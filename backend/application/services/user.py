import os

from flask import g
from application.const import *
from sqlalchemy.sql.expression import desc
from application.database import db
from application.models import USER
from application.models.model import RELATION, TWEET
from application.util import encrypt_password
from application.const import HeadshotRootPath
from application.util.date import now, unique_str

class UserService():
    def __init__(self) -> None:
        pass

    @staticmethod
    def create_user(email, password):
        try:
            pwd = encrypt_password(password)
            new_user = USER(
                email=email,
                password=encrypt_password(password),
                nickname="张三",
                headshot="default_headshot.jpg",
                description=""
            )
            db.session.add(new_user)
            db.session.commit()
            return 'ok', True
        except Exception as e:
            print(e)
            db.session.rollback()
            return e, False

    @staticmethod
    def get_user(userid):
        try:
            u = USER.query.filter(USER.userid == userid).first()
            if u is None:
                return u, False
            else:
                return u, True
        except Exception as e:
            print(e)
            return e, False

    
    @staticmethod
    def get_profile(userid):
        u = USER.query.filter(USER.userid == userid).first()
        if u is None:
            return "用户不存在", False
        follow_count = RELATION.query.filter(RELATION.follower==userid).count()
        follower_count = RELATION.query.filter(RELATION.followee==userid).count()
        tweet_count = TWEET.query.filter(TWEET.userid==userid, TWEET.is_draft==False).count()
        if userid == g.userid:
            is_follow = True
        else:
            relation = RELATION.query.filter(
                RELATION.followee==userid,
                RELATION.follower==g.userid
            ).first()
            if relation is None:
                is_follow = False
            else:
                is_follow = True
        return {
            ERRCODE: 0,
            "nickname": u.nickname,
            "headshot": u.headshot,
            "description": u.description,
            "tweet_count": tweet_count,
            "follow_count": follow_count,
            "follower_count": follower_count,
            "is_follow": is_follow
        }, True

    
    @staticmethod
    def get_minimun_profile(userid):
        u = USER.query.filter(USER.userid == userid).first()
        if u is None:
            return "用户不存在", False
        if userid == g.userid:
            is_follow = True
        else:
            relation = RELATION.query.filter(
                RELATION.followee==userid,
                RELATION.follower==g.userid
            ).first()
            if relation is None:
                is_follow = False
            else:
                is_follow = True
        return {
            ERRCODE: 0,
            "nickname": u.nickname,
            "headshot": u.headshot,
            "is_follow": is_follow
        }, True      
    
    @staticmethod
    def verify_user(userid, password):
        try:
            u = USER.query.filter(USER.userid == userid, USER.password == encrypt_password(password)).first()
            if u is None:
                return False
            else:
                return True
        except:
            return False

    @staticmethod
    def verify_user_email(email, password):
        try:
            u = USER.query.filter(USER.email == email, USER.password == encrypt_password(password)).first()
            if u is None:
                return None, False
            else:
                return u, True
        except:
            return None, False

    @staticmethod
    def edit_profile(userid, headshot, nickname, description):
        try:
            user = USER.query.filter(USER.userid==userid).first()
            if headshot is not None:
                image_name = unique_str() + '_' + headshot.filename
                old_image_name = user.headshot
                if old_image_name != "default_headshot.jpg":
                    try:
                        os.remove(HeadshotRootPath + old_image_name)
                    except:
                        pass
                user.headshot = image_name
                headshot.save(HeadshotRootPath + image_name)
            user.nickname = nickname
            user.description = description
            try:
                db.session.commit()
                return None, True
            except:
                db.session.rollback()
                return "头像保存失败", False      
        except:
            return "服务器错误", False
    

    @staticmethod
    def edit_password(userid, password):
        try:
            user = USER.query.filter(USER.userid==userid).first()
            if user is None:
                return False
            user.password = encrypt_password(password)
            try:
                db.session.commit()
                return True
            except:
                db.session.rollback()
                return False
        except:
            return False