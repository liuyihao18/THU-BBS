from application.util.date import now
from application.database import db


class USER(db.Model):
    """
    用户表
    """
    __tablename__ = 'user'
    userid = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String(64), unique=True)
    nickname = db.Column(db.String(64))
    password = db.Column(db.String(128))
    headshot = db.Column(db.String(256))
    description = db.Column(db.String(512))


class RELATION(db.Model):
    """
    关注关系
    """
    def __init__(self, followee, follower) -> None:
        super().__init__()
        self.followee = followee
        self.follower = follower
        self.follow_time = now()
    __tablename__ = 'relation'
    followee = db.Column(db.Integer, db.ForeignKey("user.userid"), primary_key=True, doc='被关注者')
    follower = db.Column(db.Integer,db.ForeignKey("user.userid"), primary_key=True, doc='关注者')
    follow_time = db.Column(db.DateTime)


class BLACKLIST(db.Model):
    """
    黑名单
    """
    def __init__(self, userid, black_id) -> None:
        super().__init__()
        self.userid = userid
        self.black_id = black_id
        self.black_time = now()
    __tablename__ = 'blacklist'
    userid = db.Column(db.Integer,db.ForeignKey("user.userid"), primary_key=True)
    black_id = db.Column(db.Integer, db.ForeignKey("user.userid"), primary_key=True)
    black_time = db.Column(db.DateTime)


class TWEET(db.Model):
    """
    单条动态
    """
    def __init__(self, tweet_type, is_draft, userid, content, location, last_modified, unique_key, title) -> None:
        super().__init__()
        self.tweet_type = tweet_type
        self.is_draft = is_draft
        self.userid = userid
        self.content = content
        self.location = location
        self.last_modified = last_modified
        self.unique_key = unique_key
        self.like_count = 0
        self.comment_count = 0
        self.title = title

    __tablename__ = 'tweet'
    tweet_id = db.Column(db.Integer, primary_key=True)
    unique_key = db.Column(db.String(64), unique=True)
    tweet_type = db.Column(db.Integer)
    is_draft = db.Column(db.Boolean)
    userid = db.Column(db.Integer, db.ForeignKey("user.userid"))
    content = db.Column(db.String(512))
    location = db.Column(db.String(64))
    last_modified = db.Column(db.DateTime)
    like_count = db.Column(db.Integer)
    comment_count = db.Column(db.Integer)
    title = db.Column(db.String(64))


class LIKE(db.Model):
    """
    点赞
    """
    def __init__(self, tweet_id, userid, like_time) -> None:
        super().__init__()
        self.tweet_id = tweet_id
        self.userid = userid
        self.like_time = like_time
    __tablename__ = 'like'
    tweet_id = db.Column(db.Integer, db.ForeignKey("tweet.tweet_id"))
    userid = db.Column(db.Integer,db.ForeignKey("user.userid"))
    like_id = db.Column(db.Integer, primary_key=True)
    like_time = db.Column(db.DateTime)


class PICTURE(db.Model):
    """
    图片——动态关联
    """
    __tablename__ = 'picture'
    def __init__(self, tweet_id, path, order) -> None:
        super().__init__()
        self.tweet_id = tweet_id
        self.path = path
        self.order = order
    __tablename__ = 'picture'
    tweet_id = db.Column(db.Integer, primary_key=True)
    path = db.Column(db.String(256))
    order = db.Column(db.Integer, primary_key=True)
    __table_args__ = (
        db.ForeignKeyConstraint(
            [tweet_id],
            [TWEET.tweet_id],
            name='PICTURE_REF_TWEET'
        ),
    )


class AUDIO(db.Model):
    """
    音频——动态
    """
    def __init__(self, tweet_id, path) -> None:
        super().__init__()
        self.tweet_id = tweet_id
        self.path = path
        self.order = 0
    __tablename__ = 'audio'
    tweet_id = db.Column(db.Integer, primary_key=True)
    path = db.Column(db.String(256))
    order = db.Column(db.Integer, primary_key=True)
    __table_args__ = (
        db.ForeignKeyConstraint(
            [tweet_id],
            [TWEET.tweet_id],
            name='AUDIO_REF_TWEET'
        ),
    )


class VIDEO(db.Model):
    """
    视频——动态关联
    """
    def __init__(self, tweet_id, path) -> None:
        super().__init__()
        self.tweet_id = tweet_id
        self.path = path
        self.order = 0
    __tablename__ = 'video'
    tweet_id = db.Column(db.Integer, primary_key=True)
    path = db.Column(db.String(256))
    order = db.Column(db.Integer, primary_key=True)
    __table_args__ = (
        db.ForeignKeyConstraint(
            [tweet_id],
            [TWEET.tweet_id],
            name='VIDEO_REF_TWEET'
        ),
    )


class COMMENT(db.Model):
    """
    评价
    """
    def __init__(self, tweet_id, comment_content, userid, comment_time) -> None:
        super().__init__()
        self.tweet_id = tweet_id
        self.content = comment_content
        self.userid = userid
        self.comment_time = comment_time
    __tablename__ = 'comment'
    tweet_id = db.Column(db.Integer, db.ForeignKey("tweet.tweet_id"))
    comment_id = db.Column(db.Integer, primary_key=True)
    comment_time = db.Column(db.DateTime)
    userid = db.Column(db.Integer, db.ForeignKey("user.userid"))
    content = db.String(db.String(128))


class MESSAGE(db.Model):
    """
    通知消息
    """
    __tablename__ = 'message'
    content = db.Column(db.String(128))
    message_id = db.Column(db.Integer, primary_key=True)
    message_time = db.Column(db.DateTime)


class LIKENOTIFICATION(db.Model):
    """
    赞同类通知消息
    """
    def __init__(self,userid, like_userid, tweet_id) -> None:
        super().__init__()
        self.user_id = userid
        self.like_user_id = like_userid
        self.tweet_id = tweet_id
        self.like_time = now()
    __tablename__ = 'likenotification'
    user_id = db.Column(db.Integer, db.ForeignKey("user.userid"), doc='被点赞者')
    like_user_id = db.Column(db.Integer, db.ForeignKey("user.userid"), doc='点赞者')
    like_time = db.Column(db.DateTime)
    tweet_id = db.Column(db.Integer, db.ForeignKey("tweet.tweet_id"))
    notification_id = db.Column(db.Integer, primary_key=True)


class COMMENTNOTIFICATION(db.Model):
    """
    赞同类通知消息
    """
    def __init__(self, userid, comment_userid, comment_id, tweet_id) -> None:
        super().__init__()
        self.user_id = userid
        self.comment_user_id = comment_userid
        self.comment_id = comment_id
        self.tweet_id = tweet_id
    __tablename__ = 'commentnotification'
    user_id = db.Column(db.Integer, db.ForeignKey("user.userid"))
    comment_user_id = db.Column(db.Integer, db.ForeignKey("user.userid"))
    comment_time = db.Column(db.DateTime)
    comment_id = db.Column(db.Integer)
    tweet_id = db.Column(db.Integer, db.ForeignKey("tweet.tweet_id"))
    notification_id = db.Column(db.Integer, primary_key=True)

