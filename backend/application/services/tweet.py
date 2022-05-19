import os
from sqlalchemy import and_, or_
from sqlalchemy.sql.expression import desc
from flask import g
from application.database import db
from application.models import TWEET, PICTURE, AUDIO, VIDEO, RELATION, USER, LIKE, COMMENT, LIKENOTIFICATION, COMMENTNOTIFICATION
from application.services import user
from application.util import encrypt_password
from application.const import HeadshotRootPath
from application.util.date import now, unique_str
from application.const import *


class Tweet_Full:
    def __init__(self, tweet_raw) -> None:
        self.tweet_type = tweet_raw.tweet_type
        self.is_draft = tweet_raw.is_draft
        self.userid = tweet_raw.userid
        self.content = tweet_raw.content
        self.location = tweet_raw.location
        self.last_modified = tweet_raw.last_modified
        self.unique_key = tweet_raw.unique_key
        self.like_count = tweet_raw.like_count
        self.comment_count = tweet_raw.comment_count
        self.tweet_id = tweet_raw.tweet_id
        self.title = tweet_raw.title
        
        like_record = LIKE.query.filter(
            LIKE.tweet_id == self.tweet_id,
            LIKE.userid == g.userid
        ).first()
        if like_record is None:
            self.is_like = False
        else:
            self.is_like = True

        if self.tweet_type == TYPE_AUDIO:
            audio_obj = AUDIO.query.filter(
                AUDIO.tweet_id == self.tweet_id
            ).first()
            if audio_obj is None:
                self.audio_url = None
            else:
                self.audio_url = audio_obj.path
        elif self.tweet_type == TYPE_VIDEO:
            video_obj = VIDEO.query.filter(
                VIDEO.tweet_id == self.tweet_id
            ).first()
            if video_obj is None:
                self.video_url = None
            else:
                self.video_url = video_obj.path
        elif self.tweet_type == TYPE_IMAGE:
            image_obj_list = PICTURE.query.filter(
                PICTURE.tweet_id == self.tweet_id
            ).all()
            self.image_url_list = [x.path for x in (image_obj_list)]
        user = USER.query.filter(
            USER.userid == self.userid
        ).first()
        self.nickname = user.nickname
        self.headshot = user.headshot
        _relation = RELATION.query.filter(
            RELATION.follower==g.userid,
            RELATION.followee==self.userid
        ).first()
        if _relation is None:
            self.is_follow = False
        else:
            self.is_follow = True


class Comment_Full:
    def __init__(self, comment_raw) -> None:
        self.comment_id = comment_raw.comment_id
        self.userid = comment_raw.userid
        self.content = comment_raw.content
        comment_user = USER.query.filter(
            USER.userid == self.userid
        ).first()
        self.nickname = comment_user.nickname
        self.headshot = comment_user.headshot
        self.comment_time = comment_raw.comment_time
            
            
        
class TweetService():
    def __init__(self) -> None:
        pass

    @staticmethod
    def create_tweet(userid, tweet_type, is_draft, content, title, location="", audio=None, video=None, image_list = None):
        unique_key= unique_str()+ '_' + str(userid)
        new_tweet = TWEET(
            userid=userid,
            tweet_type=tweet_type,
            is_draft=is_draft,
            content=content,
            location=location,
            last_modified=now(),
            unique_key= unique_key,
            title=title
        )
        if tweet_type == TYPE_TEXT:
            try:
                db.session.add(new_tweet)
                db.session.commit()
                new_tweet = TWEET.query.filter(
                TWEET.unique_key==unique_key
                ).first()
                return new_tweet.tweet_id, True
            except Exception as e:
                print(e)
                db.session.rollback()
                return "创建动态失败", False
        
        elif tweet_type == TYPE_IMAGE:
            try:
                db.session.add(new_tweet)
                db.session.commit()
            except:
                return "创建动态失败", False
            new_tweet = TWEET.query.filter(
                TWEET.unique_key==unique_key
            ).first()
            if new_tweet is None:
                return "创建动态失败", False
            save_path = []
            print(image_list)
            for i, image in enumerate(image_list):
                image_name = unique_str() + '_' + str(i) + '_' + image.filename
                new_image = PICTURE(
                    tweet_id=new_tweet.tweet_id,
                    path = image_name,
                    order=i
                )
                save_path.append(ImageRootPath + image_name)
                db.session.add(new_image)
            try:
                db.session.commit()
                for i, image in enumerate(image_list):
                    image.save(save_path[i])
                return new_tweet.tweet_id, True
            except:
                db.session.rollback()
                db.session.delete(new_tweet)
                db.session.commit()
                return "图片保存失败", False
        
        elif tweet_type == TYPE_AUDIO:
            if audio is None:
                return BadArguments,False
            try:
                db.session.add(new_tweet)
                db.session.commit()
            except:
                return "创建动态失败", False
            new_tweet = TWEET.query.filter(
                TWEET.unique_key==unique_key
            ).first()
            if new_tweet is None:
                return "创建动态失败", False
            new_audio = AUDIO(
                tweet_id=new_tweet.tweet_id,
                path = unique_str() + '_' + str(userid) + '_' + audio.filename
            )
            try:
                db.session.add(new_audio)
                db.session.commit()
                audio.save(AudioRootPath + new_audio.path)
                return new_tweet.tweet_id, True
            except:
                db.session.rollback()
                db.session.delete(new_tweet)
                db.session.commit()
                return "音频保存失败", False
        
        elif tweet_type == TYPE_VIDEO:
            if video is None:
                return BadArguments, False
            try:
                db.session.add(new_tweet)
                db.session.commit()
            except:
                return "创建动态失败", False
            new_tweet = TWEET.query.filter(
                TWEET.unique_key==unique_key
            ).first()
            if new_tweet is None:
                return "创建动态失败", False
            new_video = VIDEO(
                tweet_id=new_tweet.tweet_id,
                path = unique_str() + '_' + str(userid) + '_' + video.filename
            )
            try:
                db.session.add(new_video)
                db.session.commit()
                video.save(VideoRootPath + new_video.path)
                return new_tweet.tweet_id, True
            except:
                db.session.rollback()
                db.session.delete(new_tweet)
                db.session.commit()
                return "视频保存失败", False
        
        else:
            return BadArguments,False

    
    @staticmethod
    def delete_tweet(tweet_id):
        target_tweet = TWEET.query.filter(
            TWEET.tweet_id==tweet_id,
            TWEET.userid==g.userid
        ).first()
        if target_tweet is None:
            return "此动态不存在", False
        
        if target_tweet.tweet_type == TYPE_TEXT:
            try:
                db.session.delete(target_tweet)
                db.session.commit()
                return None, True
            except:
                db.session.rollback()
                return "删除动态失败；未知错误", False
            
        elif target_tweet.tweet_type == TYPE_AUDIO:
            audio_list = AUDIO.query.filter(
                AUDIO.tweet_id==tweet_id
            ).all()
            for audio in audio_list:
                try:
                    db.session.delete(audio)
                    db.session.commit()
                except:
                    db.session.rollback()
                    return "删除音频记录失败；未知错误", False
                try:
                    os.remove(AudioRootPath + audio.path)
                except Exception as e:
                    print(e)

            try:
                db.session.delete(target_tweet)
                db.session.commit()
                return None, True
            except:
                db.session.rollback()
                return "删除动态失败；未知错误", False
        
        elif target_tweet.tweet_type == TYPE_IMAGE:
            image_list = PICTURE.query.filter(
                PICTURE.tweet_id==tweet_id
            ).all()
            for image in image_list:
                try:
                    db.session.delete(image)
                    db.session.commit()
                except:
                    db.session.rollback()
                    return "删除音频记录失败；未知错误", False
                try:
                    os.remove(ImageRootPath + image.path)
                except Exception as e:
                    print(e)

            try:
                db.session.delete(target_tweet)
                db.session.commit()
                return None, True
            except:
                db.session.rollback()
                return "删除动态失败；未知错误", False

        elif target_tweet.tweet_type == TYPE_VIDEO:
            video_list = VIDEO.query.filter(
                VIDEO.tweet_id==tweet_id
            ).all()
            for video in video_list:
                try:
                    db.session.delete(video)
                    db.session.commit()
                except:
                    db.session.rollback()
                    return "删除音频记录失败；未知错误", False
                try:
                    os.remove(VideoRootPath + video.path)
                except Exception as e:
                    print(e)

            try:
                db.session.delete(target_tweet)
                db.session.commit()
                return None, True
            except:
                db.session.rollback()
                return "删除动态失败；未知错误", False
        
        else:
            return "服务器错误", False


    @staticmethod
    def get_tweet_list(block, _of, userid, _order_by, tweet_type, search_str):
        query_condition = and_()
        if tweet_type is not None:
            query_condition = and_(
                query_condition,
                TWEET.tweet_type==tweet_type
            )
        if search_str is not None:
            query_condition = and_(
                query_condition,
                TWEET.content.like('%' + search_str + '%')
            )
        if _of == TWEET_FOLLOW:
            followee_list = RELATION.query.filter(
                RELATION.follower==g.userid
            ).all()
            if len(followee_list)==0:
                query_condition = and_(
                    query_condition,
                    False
                )
            else:
                followee_condition = or_()
                for followee in followee_list:
                    followee_condition = or_(
                        followee_condition,
                        TWEET.userid==followee.followee
                    )
                query_condition = and_(
                    query_condition,
                    followee_condition
                )
        elif _of == TWEET_USER:
            if userid is None:
                return BadArguments, False
            targer_user = USER.query.filter(
                USER.userid==userid
            ).first()
            if targer_user is None:
                return BadArguments, False
            query_condition = and_(
                query_condition,
                TWEET.userid==userid
            )
        
        query_condition = and_(
            query_condition,
            TWEET.is_draft==False
        )

        if _order_by == 'time':
            tweet_list = TWEET.query.filter(
                query_condition
            ).order_by(TWEET.last_modified.desc()).all()
        elif _order_by == 'likes':
            tweet_list = TWEET.query.filter(
                query_condition
            ).order_by(TWEET.like_count.desc()).all()
        
        tweet_list = [Tweet_Full(x) for x in tweet_list]

        if block * BLOCK_SIZE > len(tweet_list):
            return [], True
        else:
            return tweet_list[block * BLOCK_SIZE: (block + 1) * BLOCK_SIZE], True
                
            
    @staticmethod
    def get_draft_list(block):
        draft_list = TWEET.query.filter(
            TWEET.userid == g.userid,
            TWEET.is_draft == True
        ).order_by(TWEET.last_modified.desc()).all()
        return draft_list[block * BLOCK_SIZE:(block + 1) * BLOCK_SIZE], True

    
    @staticmethod
    def edit_tweet(tweet_id, tweet_type, is_draft, content, title, location="", audio=None, video=None, image_list = None):
        target_tweet = TWEET.query.filter(
            TWEET.tweet_id==tweet_id
        ).first()
        if target_tweet is None:
            return "这条草稿不存在", False
        target_tweet.tweet_type = tweet_type
        target_tweet.is_draft = is_draft
        target_tweet.content.content = content
        target_tweet.location = location
        target_tweet.title = title
        if tweet_type==TYPE_TEXT:
            try:
                db.session.commit()
                return None, True
            except:
                db.session.rollback()
                return "修改动态失败", False
        elif tweet_type==TYPE_AUDIO:
            new_audio = AUDIO(
                tweet_id=target_tweet.tweet_id,
                path = unique_str() + '_' + str(g.userid) + '_' + audio.filename
            )
            try:
                db.session.add(new_audio)
                db.session.commit()
                audio.save(AudioRootPath + new_audio.path)
                return target_tweet.tweet_id, True
            except:
                db.session.rollback()
                return "动态保存失败", False

        elif tweet_type == TYPE_VIDEO:
            new_video = VIDEO(
                tweet_id=target_tweet.tweet_id,
                path = unique_str() + '_' + str(g.userid) + '_' + video.filename
            )
            try:
                db.session.add(new_video)
                db.session.commit()
                video.save(VideoRootPath + new_video.path)
                return target_tweet.tweet_id, True
            except:
                db.session.rollback()
                return "动态保存失败", False
        
        elif tweet_type == TYPE_IMAGE:
            save_path = []
            for i, image in enumerate(image_list):
                image_name = unique_str() + '_' + str(i) + '_' + image.filename
                new_image = PICTURE(
                    tweet_id=target_tweet.tweet_id,
                    path = image_name,
                    order=i
                )
                save_path.append(ImageRootPath + image_name)
                db.session.add(new_image)
            try:
                db.session.commit()
                for i, image in enumerate(image_list):
                    image.save(save_path[i])
                return target_tweet.tweet_id, True
            except:
                db.session.rollback()
                return "动态保存失败", False


    @staticmethod
    def get_single_tweet(tweet_id):
        target_tweet = TWEET.query.filter(
            TWEET.tweet_id==tweet_id
        ).first()
        if target_tweet is None:
            return "该动态不存在", False
        tweet_res = Tweet_Full(target_tweet)
        return tweet_res, True
        
    
    @staticmethod
    def like_tweet(tweet_id):
        target_tweet = TWEET.query.filter(
            TWEET.tweet_id==tweet_id
        ).first()
        if target_tweet is None:
            return "该动态不存在", False
        target_tweet.like_count += 1
        new_like_record = LIKE(
            tweet_id=tweet_id,
            userid=g.userid,
            like_time=now()
        )

        new_like_notification = LIKENOTIFICATION(
            userid = target_tweet.userid,
            like_userid=g.userid,
            tweet_id=tweet_id
        )

        try:
            db.session.add(new_like_record)
            db.session.add(new_like_notification)
            db.session.commit()
            return None, True
        except:
            db.session.rollback()
            return "数据库错误", False

    
    @staticmethod
    def comment_tweet(tweet_id, comment):
        target_tweet = TWEET.query.filter(
            TWEET.tweet_id==tweet_id
        ).first()
        if target_tweet is None:
            return "该动态不存在", False
        target_tweet.comment_count += 1
        new_comment_record = COMMENT(
            tweet_id=tweet_id,
            userid=g.userid,
            comment_time=now(),
            comment_content=comment
        )
        try:
            db.session.add(new_comment_record)
            db.session.commit()
        except:
            db.session.rollback()
            return "数据库错误", False
        
        new_comment_notification = COMMENTNOTIFICATION(
            userid=target_tweet.userid,
            comment_userid=g.userid,
            comment_id=new_comment_record.comment_id,
            tweet_id=tweet_id
        )
        try:
            db.session.add(new_comment_notification)
            db.session.commit()
            return None, True
        except:
            db.session.rollback()
            return "未知错误", None


    @staticmethod
    def delete_comment(comment_id):
        target_comment = COMMENT.query.filter(
            COMMENT.comment_id == comment_id,
            COMMENT.userid == g.userid
        ).first()
        if target_comment is None:
            return "评论不存在或非自己的评论", False

        tweet = TWEET.query.filter(
            TWEET.tweet_id == COMMENT.tweet_id
        ).first()
        if tweet is None:
            return "数据库错误", False
        tweet.comment_count -= 1
        try:
            db.session.delete(target_comment)
            db.session.commit()
            return None, True
        except:
            db.session.rollback()
            return "删除评论失败", False


    @staticmethod
    def get_tweet_comment_list(tweet_id):
        target_tweet = TWEET.query.filter(
            TWEET.tweet_id == tweet_id
        ).first()
        if not target_tweet:
            return "该动态不存在", False
        comment_list = COMMENT.query.filter(
            COMMENT.tweet_id == tweet_id
        ).all()

        comment_list = [Comment_Full(x) for x in comment_list]
        return comment_list, True

