from curses.ascii import US
from flask import request, jsonify, Blueprint, g
from config import query_yaml
from application.services import RelationService
from .login_decorator import login_required
from application.const import *
import mjwt
bp_relation = Blueprint(
    'relation',
    __name__
)

@bp_relation.route('/user-api/v1/relation/follow', methods=['POST'])
@login_required
def follow():
    try:
        follow_id = request.json['follow_id']
    except:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: BadArguments
        }), 200
    
    status = RelationService.add_follow_relation(
        g.userid, 
        follow_id
    )
    if status:
        return jsonify({
            ERRCODE: 0
        }), 200
    else:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: "关注失败"
        }), 200

@bp_relation.route('/user-api/v1/relation/unfollow', methods=['POST'])
@login_required
def unfollow():
    try:
        follow_id = request.json['follow_id']
    except:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: BadArguments
        }), 200
    
    msg, status = RelationService.drop_follow_relation(
        g.userid,
        follow_id
    )
    if not status:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: msg
        }), 200
    return jsonify({
        ERRCODE: 0
    }), 200


@bp_relation.route('/user-api/v1/relation/black', methods=['POST'])
@login_required
def black():
    try:
        black_id = request.json['black_id']
    except:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: BadArguments
        }), 200      
    msg, status = RelationService.add_black_relation(
        black_id
    )
    if not status:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: msg
        }), 200
    return jsonify({
        ERRCODE: 0
    }), 200


@bp_relation.route('/user-api/v1/relation/white', methods=['POST'])
@login_required
def white():
    try:
        white_id = request.json['white_id']
    except:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: BadArguments
        }), 200 
    msg, status = RelationService.drop_black_relation(
        white_id
    )
    if not status:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: msg
        }), 200
    return jsonify({
        ERRCODE: 0
    }), 200


@bp_relation.route('/user-api/v1/relation/get_follow_list', methods=['POST'])
@login_required
def get_follow_list():
    try:
        block = request.json['block']
    except:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: BadArguments
        }), 200 
    
    msg, status = RelationService.get_follow_list(block)
    if not status:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: msg
        }), 200
    else:
        return jsonify({
            ERRCODE: 0,
            "follow_list": msg
        }), 200
    

@bp_relation.route('/user-api/v1/relation/get_black_list', methods=['POST'])
@login_required
def get_black_list():
    try:
        block = request.json['block']
    except:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: BadArguments
        }), 200 
    
    msg, status = RelationService.get_black_list(block)
    if not status:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: msg
        }), 200
    else:
        return jsonify({
            ERRCODE: 0,
            "black_list": msg
        }), 200


@bp_relation.route('/user-api/v1/relation/get_fan_list', methods=['POST'])
@login_required
def get_fan_list():
    try:
        block = request.json['block']
    except:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: BadArguments
        }), 200 
    
    msg, status = RelationService.get_fan_list(block)
    if not status:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: msg
        }), 200
    else:
        return jsonify({
            ERRCODE: 0,
            "black_list": msg
        }), 200