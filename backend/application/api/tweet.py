from curses import ERR
from os import stat_result
from flask import request, jsonify, Blueprint, g
from config import query_yaml
from application.services import TweetService
from .login_decorator import login_required
from application.const import *
from application.util import convert_bool
import mjwt
bp_tweet = Blueprint(
    'tweet',
    __name__
)


@bp_tweet.route('/user-api/v1/tweet/create_tweet', methods=['POST'])
@login_required
def create_tweet():
    audio = request.files['audio'] if 'audio' in request.files.keys() else None
    video = request.files['video'] if 'video' in request.files.keys() else None
    image_list = []
    for i in range(9):
        image = request.files[image_form_list[i]] if image_form_list[i] in request.files.keys() else None
        if image is None:
            break
        image_list.append(image)
    try:
        tweet_type = int(request.form['type'])
        is_draft = convert_bool(request.form['is_draft'])
        content = request.form['content']
        loaction = request.form['location'] if 'location' in request.form.keys() else None
        title = request.form['title']
    except Exception as e:
        print(e)
        return jsonify({
            ERRCODE:1,
            ERRMSG: BadArguments
        }), 200
    msg, status = TweetService.create_tweet(
        userid=g.userid,
        is_draft=is_draft,
        tweet_type=tweet_type,
        content=content,
        location=loaction,
        audio=audio,
        video=video,
        image_list=image_list,
        title = title
    )
    if not status:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: msg
        }), 200
    else:
        return jsonify({
            ERRCODE: 0,
            "tweet_id": msg
        }), 200


@bp_tweet.route('/user-api/v1/tweet/delete_tweet', methods=['POST'])
@login_required
def delete_tweet():
    try:
        tweet_id = request.json['tweet_id']
    except:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: BadArguments
        }), 200
    msg, status = TweetService.delete_tweet(tweet_id)
    if not status:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: msg
        }), 200
    else:
        return jsonify({
            ERRCODE: 0
        }), 200


@bp_tweet.route('/user-api/v1/tweet/get_tweet_list', methods=['POST'])
@login_required
def get_tweet_list():
    try:
        block = request.json['block']
        _of = request.json['of']
        if _of == TWEET_USER:
            userid = request.json['userid']
        else:
            userid = None
        _order_by = request.json['order_by']
        tweet_type = request.json['tweet_type'] if 'tweet_type' in request.json.keys() else None
        search_str = request.json['search_str'] if 'search_str' in request.json.keys() else None
    except: 
        return jsonify({
            ERRCODE: 1,
            ERRMSG: BadArguments
        }), 200
    
    msg, status = TweetService.get_tweet_list(
        block=block,
        _of = _of,
        userid=userid,
        _order_by = _order_by,
        tweet_type=tweet_type,
        search_str=search_str
    )
    if not status:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: msg
        }), 200
    else:
        tweet_list = []
        for tweet in msg:
            tweet_json = {}
            tweet_json['tweet_id'] = tweet.tweet_id
            tweet_json['userid'] = tweet.userid
            tweet_json['type'] = tweet.tweet_type
            tweet_json['content'] = tweet.content
            if tweet.tweet_type == TYPE_AUDIO:
                tweet_json['audio_url'] = tweet.audio_url
            elif tweet.tweet_type == TYPE_IMAGE:
                tweet_json['image_url_list'] = tweet.image_url_list
            elif tweet.tweet_type == TYPE_VIDEO:
                tweet_json['video_url'] = tweet.video_url
            tweet_json['location'] = tweet.location
            tweet_json['last_modified'] = tweet.last_modified
            tweet_json['like_count'] = tweet.like_count
            tweet_json['comment_count'] = tweet.comment_count
            tweet_json['nickname'] = tweet.nickname
            tweet_json['headshot'] = tweet.headshot
            tweet_json['is_follow'] = tweet.is_follow
            tweet_json['title'] = tweet.title
            tweet_json['is_like'] = tweet.is_like
            tweet_list.append(tweet_json)
        return jsonify({
            ERRCODE: 0,
            "tweet_list": tweet_list
        }), 200
    

@bp_tweet.route('/user-api/v1/tweet/get_draft_list', methods=['POST'])
@login_required
def get_draft_list():
    try:
        block = request.json['block']
    except:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: BadArguments
        }), 200
    msg, status = TweetService.get_draft_list(block)
    tweet_list = []
    if status:
        for tweet in msg:
            tweet_list.append({
                "tweet_id": tweet.tweet_id,
                "type": tweet.tweet_type,
                "content": tweet.content,
                "location": tweet.location,
                "last_modified": tweet.last_modified,
                "title": tweet.title
            })
        return jsonify({
            ERRCODE: 0,
            "tweet_list": tweet_list
        }), 200
    else:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: "未知错误"
        }), 200


@bp_tweet.route('/user-api/v1/tweet/edit_tweet', methods=['POST'])
@login_required
def edit_tweet():
    audio = request.files['audio'] if 'audio' in request.files.keys() else None
    video = request.files['video'] if 'video' in request.files.keys() else None
    image_list = []
    for i in range(9):
        image = request.files[image_form_list[i]] if image_form_list[i] in request.files.keys() else None
        if image is None:
            break
        image_list.append(image)
    try:
        tweet_type = int(request.form['type'])
        is_draft = convert_bool(request.form['is_draft'])
        content = request.form['content']
        tweet_id = request.form['tweet_id']
        loaction = request.form['location'] if 'location' in request.form.keys() else None
        title = request.form['title']
    except Exception as e:
        print(e)
        return jsonify({
            ERRCODE:1,
            ERRMSG: BadArguments
        }), 200
    
    msg, status = TweetService.edit_tweet(
        tweet_id=tweet_id,
        tweet_type=tweet_type,
        is_draft=is_draft,
        content=content,
        title=title,
        location=loaction,
        audio=audio,
        video=video,
        image_list=image_list
    )

    if not status:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: msg
        }), 200
    else:
        return jsonify({
            ERRCODE: 0,
            "tweet_id": tweet_id
        }), 200


@bp_tweet.route('/user-api/v1/tweet/get_single_tweet', methods=['POST'])
@login_required
def get_single_tweet():
    try:
        tweet_id = request.json['tweet_id']
    except:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: BadArguments
        }), 200

    msg, status = TweetService.get_single_tweet(tweet_id)
    if not status:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: msg
        }), 200

    try:
        tweet_json = {}
        tweet = msg
        tweet_json['tweet_id'] = tweet.tweet_id
        tweet_json['userid'] = tweet.userid
        tweet_json['type'] = tweet.tweet_type
        tweet_json['content'] = tweet.content
        if tweet.tweet_type == TYPE_AUDIO:
            tweet_json['audio_url'] = tweet.audio_url
        elif tweet.tweet_type == TYPE_IMAGE:
            tweet_json['image_url_list'] = tweet.image_url_list
        elif tweet.tweet_type == TYPE_VIDEO:
            tweet_json['video_url'] = tweet.video_url
        tweet_json['location'] = tweet.location
        tweet_json['last_modified'] = tweet.last_modified
        tweet_json['like_count'] = tweet.like_count
        tweet_json['comment_count'] = tweet.comment_count
        tweet_json['nickname'] = tweet.nickname
        tweet_json['headshot'] = tweet.headshot
        tweet_json['is_follow'] = tweet.is_follow
        tweet_json['title'] = tweet.title
        tweet_json['is_like'] = tweet.is_like
        tweet_json[ERRCODE] = 0
    except:
        return jsonify({
                ERRCODE: 1,
                ERRMSG: "服务器错误"
            }), 200
    return jsonify(tweet_json), 200


@bp_tweet.route('/user-api/v1/tweet/like_tweet', methods=['POST'])
@login_required
def like_tweet():
    try:
        tweet_id = request.json['tweet_id']
    except:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: BadArguments
        }), 200
    
    msg, status = TweetService.like_tweet(tweet_id=tweet_id)
    if not status:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: msg
        }), 200
    else:
        return jsonify({
            ERRCODE: 0
        }), 200


@bp_tweet.route('/user-api/v1/tweet/comment_tweet', methods=['POST'])
@login_required
def comment_tweet():
    try:
        tweet_id = request.json['tweet_id']
        comment = request.json['comment']
    except:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: BadArguments
        }), 200
    msg, status = TweetService.comment_tweet(
        tweet_id=tweet_id,
        comment=comment
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


@bp_tweet.route('/user-api/v1/tweet/delete_comment', methods=['POST'])
@login_required
def delete_comment():
    try:
        comment_id = request.json['comment_id']
    except:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: BadArguments
        }), 200

    msg, status = TweetService.delete_comment(comment_id=comment_id)
    if not status:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: msg
        }), 200
    else:
        return jsonify({
            ERRCODE: 0
        }), 200


@bp_tweet.route('/user-api/v1/tweet/get_tweet_comment_list', methods=['POST'])
@login_required
def get_tweet_comment_list():
    try:
        tweet_id = request.json['tweet_id']
    except:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: BadArguments
        }), 200
    msg, status = TweetService.get_tweet_comment_list(tweet_id)
    if not status:
        return jsonify({
            ERRCODE: 1,
            ERRMSG: msg
        }), 200
    comment_obj_list = msg
    comment_list = []
    for comment_obj in comment_obj_list:
        comment_json = {}
        comment_json['comment_id'] = comment_obj.comment_id
        comment_json['userid'] = comment_obj.userid
        comment_json['content'] = comment_obj.content
        comment_json['nickname'] = comment_obj.nickname
        comment_json['headshot'] = comment_obj.headshot
        comment_json['comment_time'] = comment_obj.comment_time
        comment_list.append(comment_json)
    return jsonify({
        ERRCODE: 0,
        'comment_list': comment_list
    }), 200