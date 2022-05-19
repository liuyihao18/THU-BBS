import os

from flask import g
from sqlalchemy.sql.expression import desc
from application.database import db
from application.models import USER, RELATION
from application.models.model import BLACKLIST
from application.util import encrypt_password
from application.const import BLOCK_SIZE, HeadshotRootPath
from application.util.date import now

class RelationService():
    def __init__(self) -> None:
        pass

    @staticmethod
    def add_follow_relation(userid, follow_id):
        new_relation = RELATION(follower=userid, followee=follow_id)
        try:
            db.session.add(new_relation)
            db.session.commit()
            return True
        except:
            db.session.rollback()
            return False
    
    @staticmethod
    def drop_follow_relation(userid, follow_id):
        relation = RELATION.query.filter(
            RELATION.follower==userid,
            RELATION.followee==follow_id
        ).first()
        if relation is None:
            return "尚未关注", False
        try:
            db.session.delete(relation)
            db.session.commit()
            return None, True
        except:
            db.session.rollback()
            return "取消关注失败", True

    
    @staticmethod
    def add_black_relation(black_id):
        black_relation = BLACKLIST.query.filter(
            BLACKLIST.black_id == black_id,
            BLACKLIST.userid == g.userid
        ).first()
        if black_relation is not None:
            return "已经被拉黑", False
        new_black_relation = BLACKLIST(
            userid=g.userid,
            black_id=black_id
        )
        try:
            db.session.add(new_black_relation)
            db.session.commit()
            return None, True
        except:
            db.session.rollback()
            return "拉黑失败", False
        
    
    @staticmethod
    def drop_black_relation(black_id):
        black_relation = BLACKLIST.query.filter(
            BLACKLIST.black_id == black_id,
            BLACKLIST.userid == g.userid
        ).first()
        if black_relation is None:
            return "未被拉黑", False
        try:
            db.session.delete(black_relation)
            db.session.commit()
            return None, True
        except:
            db.session.rollback()
            return "解除拉黑失败", False
    

    @staticmethod
    def get_follow_list(block):
        follow_list = RELATION.query.filter(
            RELATION.follower==g.userid
        ).order_by(RELATION.follow_time.desc()).all()
        follow_list = follow_list[block * BLOCK_SIZE, (block + 1) * BLOCK_SIZE]
        follow_json_list = []
        for follow_relation in follow_list:
            followee = USER.query.filter(
                USER.userid==follow_relation.followee
            ).first()
            follow_json_list.append({
                "userid": followee.userid,
                "nickname": followee.nickname,
                "headshot": followee.headshot,
                "description": followee.description
            })
        return follow_json_list, True


    @staticmethod
    def get_black_list(block):
        black_list = BLACKLIST.query.filter(
            BLACKLIST.userid==g.userid
        ).order_by(BLACKLIST.black_time.desc()).all()
        black_list = black_list[block * BLOCK_SIZE, (block + 1) * BLOCK_SIZE]
        black_json_list = []
        for black_relation in black_list:
            blacker = USER.query.filter(
                USER.userid==black_relation.black_id
            ).first()
            black_json_list.append({
                "userid": blacker.userid,
                "nickname": blacker.nickname,
                "headshot": blacker.headshot
            })
        return black_json_list, True

    
    @staticmethod
    def get_fan_list(block):
        follow_list = RELATION.query.filter(
            RELATION.followee==g.userid
        ).order_by(RELATION.follow_time.desc()).all()
        follow_list = follow_list[block * BLOCK_SIZE, (block + 1) * BLOCK_SIZE]
        follow_json_list = []
        for follow_relation in follow_list:
            follower = USER.query.filter(
                USER.userid==follow_relation.follower
            ).first()
            follow_json_list.append({
                "userid": follower.userid,
                "nickname": follower.nickname,
                "headshot": follower.headshot,
                "description": follower.description
            })
        return follow_json_list, True
    
        