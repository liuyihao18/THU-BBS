from curses.ascii import US
from flask import request, jsonify, Blueprint, g
from config import query_yaml
from application.services import UserService
from .login_decorator import login_required
from application.const import *
import mjwt
bp_user = Blueprint(
    'user',
    __name__
)


@bp_user.route('/user-api/v1/user/register', methods=['POST'])
def register():
    try:
        email = request.json["email"]
        password = request.json["password"]
    except:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: "bad arguments"
        }), 200
    msg, status = UserService.create_user(email=email, password=password)
    if status:
        print("注册成功")
        return jsonify({
            ERRCODE: 0
        }), 200
    else:
        return jsonify({
            ERRCODE: 2,
            ERRMSG: "该邮箱已被注册"
        }), 200
    

@bp_user.route('/user-api/v1/user/login', methods=['POST'])
def login():
    try:
        email = request.json["email"]
        password = request.json["password"]
    except:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: "bad arguments"
        }), 200
    user, status = UserService.verify_user_email(email, password)
    if not status:
        print("验证失败")
        return jsonify({
            ERRCODE: 1,
            ERRMSG: "邮箱或密码错误"
        }), 200
    else:
        print("登录成功")
        userjwt = mjwt.generate_jwt({
            "userid": user.userid
        })
        return jsonify({
            "jwt": userjwt,
            "userid": user.userid,
            ERRCODE: 0
        }), 200


@bp_user.route('/user-api/v1/user/getLoginStatus', methods=['GET'])
@login_required
def getLoginStatus():
    return jsonify({
        ERRCODE: 0
    }), 200


@bp_user.route('/user-api/v1/user/get_profile', methods=['POST'])
@login_required
def get_profile():
    try:
        userid = request.json["userid"]
    except:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: "bad arguments"
        }), 200
    msg, status = UserService.get_profile(userid)
    if status:
        return jsonify(msg), 200
    else:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: msg
        }), 200


@bp_user.route('/user-api/v1/user/get_minimum_profile', methods=['POST'])
@login_required
def get_minimum_profile():
    try:
        userid = request.json["userid"]
    except:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: "bad arguments"
        }), 200
    msg, status = UserService.get_minimun_profile(userid)
    if status:
        return jsonify(msg), 200
    else:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: msg
        }), 200


    
@bp_user.route('/user-api/v1/user/edit_profile', methods=['POST'])
@login_required
def edit_profile():
    headshot = request.files["headshot"] if "headshot" in request.files.keys() else None
    try:
        nickname = request.form["nickname"]
        description = request.form["description"]
    except:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: BadArguments
        })
    
    msg, status = UserService.edit_profile(
        g.userid,
        headshot,
        nickname,
        description
    )

    if not status:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: msg
        }), 200
    else:
        return jsonify({
            ERRCODE: 0
        }), 200


@bp_user.route('/user-api/v1/user/edit_password', methods=['POST'])
@login_required
def edit_password():
    try:
        old_password = request.json['old_password']
        new_password = request.json['new_password']
    except:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: BadArguments
        }), 200
    if UserService.verify_user(g.userid, old_password):
        status = UserService.edit_password(g.userid, new_password)
        if status:
            return jsonify({
                ERRCODE: 0,
            }), 200
        else:
            return jsonify({
                ERRCODE: 1,
                ERRMSG: "修改失败，服务器错误"
            }), 200
    else:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: "密码错误"
        }), 200


