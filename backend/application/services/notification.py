import os

from flask import g
from sqlalchemy.sql.expression import desc
from application.database import db
from application.models import MESSAGE, LIKENOTIFICATION, COMMENTNOTIFICATION, COMMENT
from application.util import encrypt_password
from application.const import BLOCK_SIZE, HeadshotRootPath
from application.util.date import now


class Comment_Notification_Full:
    def __init__(self, commentNotification) -> None:
        self.notification_id = commentNotification.notification_id
        self.userid = commentNotification.comment_user_id
        self.tweet_id = commentNotification.tweet_id
        self.comment_time = commentNotification.comment_time
        related_comment = COMMENT.query.filter(
            COMMENT.comment_id==commentNotification.comment_id
        ).first()
        if related_comment is None:
            self.content = "评论已被删除"
        else:
            self.content = related_comment.content

class NotificationService():
    def __init__(self) -> None:
        pass

    
    @staticmethod
    def get_message_list(block):
        message_list = MESSAGE.query.order_by(
            MESSAGE.message_time.desc()
        ).all()
        return message_list[block * (BLOCK_SIZE), (block + 1) * (BLOCK_SIZE)]
    

    @staticmethod
    def get_like_notification_list(block):
        like_list = LIKENOTIFICATION.query.filter(
            LIKENOTIFICATION.user_id==g.userid
        ).order_by(
            LIKENOTIFICATION.like_time.desc()
        ).all()
        return like_list[block * (BLOCK_SIZE), (block + 1) * (BLOCK_SIZE)]

    
    @staticmethod
    def get_comment_notification_list(block):
        comment_notification_list = COMMENTNOTIFICATION.query.filter(
            COMMENTNOTIFICATION.user_id==g.userid
        ).order_by(
            COMMENTNOTIFICATION.comment_time.desc()
        ).all()
        comment_notification_list = [Comment_Notification_Full(x) for x in comment_notification_list]
        return comment_notification_list[block * (BLOCK_SIZE), (block + 1) * (BLOCK_SIZE)]
    

    @staticmethod
    def delete_like_notification(notification_id):
        target_notification = LIKENOTIFICATION.query.filter(
            LIKENOTIFICATION.notification_id==notification_id
        ).first()
        if target_notification is None:
            return "通知不存在", False
        try:
            db.session.delete(target_notification)
            db.session.commit()
            return None, True
        except:
            db.session.rollback()
            return "删除失败", False