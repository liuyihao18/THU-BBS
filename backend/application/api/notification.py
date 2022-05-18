from curses.ascii import US
from flask import request, jsonify, Blueprint, g
from config import query_yaml
from application.services import NotificationService
from .login_decorator import login_required
from application.const import *
import mjwt
bp_notification = Blueprint(
    'notification',
    __name__
)


@bp_notification.route('/user-api/v1/notification/get_message_list', methods=['POST'])
@login_required
def get_message_list():
    try:
        block = request.json['block']
    except:
        return jsonify({
            ERRCODE: 0,
            ERRMSG: BadArguments
        }), 200
    
    msg_list = NotificationService.get_message_list(block)
    notification_list_json = []
    for message in msg_list:
        notification_list_json.append({
            "message_id": message.message_id,
            "message_time": message.message_time,
            "content": message.content
        })
    return jsonify({
        ERRCODE: 0,
        "notification_list": notification_list_json
    }), 200


@bp_notification.route('/user-api/v1/notification/get_like_notification_list', methods=['POST'])
@login_required
def get_like_notification_list():
    try:
        block = request.json['block']
    except:
        return jsonify({
            ERRCODE: 0,
            ERRMSG: BadArguments
        }), 200
    
    like_list = NotificationService.get_like_notification_list(block)
    like_list_json = []
    for like_notification in like_list:
        like_list_json.append({
            "notification_id": like_notification.like_id,
            "userid": like_notification.like_user_id,
            "tweet_id": like_notification.tweet_id,
            "like_time": like_notification.like_time
        })
    return jsonify({
        ERRCODE: 0,
        "notification_list": like_list_json
    }), 200


@bp_notification.route('/user-api/v1/notification/get_comment_notification_list', methods=['POST'])
@login_required
def get_comment_notification_list():
    try:
        block = request.json['block']
    except:
        return jsonify({
            ERRCODE: 0,
            ERRMSG: BadArguments
        }), 200
    
    comment_notification_list = NotificationService.get_comment_notification_list(block)
    comment_list_json = []
    for comment_notification in comment_notification_list:
        comment_list_json.append({
            "notification_id": comment_notification.notification_id,
            "userid": comment_notification.userid,
            "tweet_id": comment_notification.tweet_id,
            "comment_time": comment_notification.comment_time,
            "content": comment_notification.content
        })
    return jsonify({
        ERRCODE: 0,
        "notification_list": comment_list_json
    }), 200


@bp_notification.route('/user-api/v1/notification/delete_like_notification', methods=['POST'])
@login_required
def delete_like_notification():
    try:
        notification_id = request.json['notification_id']
    except:
        return jsonify({
            ERRCODE: 0,
            ERRMSG: BadArguments
        }), 200
    
    msg, status = NotificationService.delete_like_notification(notification_id)
    if not status:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: msg
        }), 200
    else:
        return jsonify({
            ERRCODE: 0
        }), 200