# API

root path = "http://47.93.89.166"

## 登录

- [x] @bp_user.route('/user-api/v1/user/register', methods=['POST'])

params:

```json
{
  "email": string,
  "password": string
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

- [x] @bp_user.route('/user-api/v1/user/login', methods=['POST'])

params:

```json
{
  "email": string,
  "password": string
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0,
  "jwt": string,
  "userid": int
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

- [x] @bp_user.route('/user-api/v1/user/get_profile', methods=['POST'])

@login_required

params:

```json
{
  "userid": int
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0,
  "nickname": string,
  "headshot": url,
  "description": string,
  "tweet_count": int,
  "follow_count": int, /* 关注 */
  "follower_count": int, /* 粉丝 */
  "is_follow": boolean /* 是否关注,如果是自己返回已关注 */
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

- [x] @bp_user.route('/user-api/v1/user/get_minimum_profile', methods=['POST'])

@login_required

params:

```json
{
  "userid": int
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0,
  "nickname": string,
  "headshot": url,
  "is_follow": boolean
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

- [x] @bp_user.route('/user-api/v1/user/edit_profile', methods=['POST'])

@login_required

params: **form-data**

```json
{
  "nickname": string,
  "headshot": file, /* optional */
  "description": string
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

- [x] @bp_user.route('/user-api/v1/user/edit_password', methods=['POST'])

@login_required

params:

```json
{
  "old_password": string,
  "new_password": string
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

## 关系

- [x] @bp_user.route('/user-api/v1/relation/follow', methods=['POST'])

@login_required

params:

```json
{
  "follow_id": int
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

- [x] @bp_user.route('/user-api/v1/relation/unfollow', methods=['POST'])

@login_required

params:

```json
{
  "follow_id": int
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

- [x] @bp_user.route('/user-api/v1/relation/black', methods=['POST'])

@login_required

params:

```json
{
  "black_id": int
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

- [x] @bp_user.route('/user-api/v1/relation/white', methods=['POST'])

@login_required

params:

```json
{
  "white_id": int
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

@bp_user.route('/user-api/v1/relation/get_follow_list', methods=['POST'])

@login_required

params:

```json
{
  "block": int
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0,
  "follow_list": [
    {
      "userid": int,
      "nickname": string,
      "headshot": file,
      "description": string
    }
  ]
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

@bp_user.route('/user-api/v1/relation/get_black_list', methods=['POST'])

@login_required

params:

```json
{
  "block": int
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0,
  "black_list": [
    {
      "userid": int,
      "nickname": string,
      "headshot": file
    }
  ]
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

@bp_user.route('/user-api/v1/relation/get_fan_list', methods=['POST'])

@login_required

params:

```json
{
  "block": int
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0,
  "fan_list": [
    {
      "userid": int,
      "nickname": string,
      "headshot": file
    }
  ]
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```


## 推文

- [x] @bp_user.route('/user-api/v1/tweet/create_tweet', methods=['POST'])

@login_required

params: **form-data**

```json
{
  "type": int,
  "title": string,
  "is_draft": boolean,
  "content": string,
  "location": string, /* optional */
  "audio": file,      /* optional */
  "video": file,      /* optional */
  "image_count": int, /* optional */
  "image0": file,     /* optional */
  ...
  "image8": file      /* optional */
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0,
  "tweet_id": int
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

- [x] @bp_user.route('/user-api/v1/tweet/edit_tweet', methods=['POST'])

@login_required

params: **form-data**

```json
{
  "tweet_id": int,
  "type": int,
  "title": string,
  "is_draft": boolean,
  "content": string,
  "location": string, /* optional */
  "audio": file,      /* optional */
  "video": file,      /* optional */
  "image_count": int, /* optional */
  "image0": file,     /* optional */
  ...
  "image8": file      /* optional */
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0,
  "tweet_id": int
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

- [x] @bp_user.route('/user-api/v1/tweet/delete_tweet', methods=['POST'])

@login_required

params:

```json
{
  "tweet_id": int
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

- [x] @bp_user.route('/user-api/v1/tweet/get_tweet_list', methods=['POST'])

@login_required

params:

```json
{
  "block": int, /* 从0开始， 一次10个*/
  "of": int, /* TWEET_ALL = 1 表示全部，TWEET_FOLLOW = 2 表示我关注的，TWEET_USER = 3 表示某个用户的 */
  "userid": int, /* 如果 TWEET_USER = 3，则必须有这一项 */
  "order_by": string, /* "time" or "likes" */
  "tweet_type": int, /* TYPE_TEXT = 0, TYPE_IMAGE = 1, TYPE_AUDIO = 2, TYPE_VIDEO = 3 */	//optional
  "search_str": string  // optional
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0,
  "tweet_list": [
    {
      "tweet_id": int,
      "userid": int,
      "type": int,
      "title": string,        
      "content": string,
      "audio_url": url,
      "video_url": url,
      "image_url_list": [ url ],
      "location": string,
      "last_modified": string,
      "like_count": int,
      "comment_count": int,
      "nickname": string,
      "headshot": url,
      "is_follow": boolean,
      "is_like": boolean
    }
    ...
  ]
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

- [x] @bp_user.route('/user-api/v1/tweet/get_draft_list', methods=['POST'])

@login_required

params:

```json
{
  "block": int /* 按照修改时间反向排序 */
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0,
  "tweet_list": [
    {
      "tweet_id": int,
      "type": int,
      "title": string,
      "content": string,
      "location": string,
      "last_modified": string
    }
    ...
  ]
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

> 因为太难处理网络url和本地uri的转化（总不能上传的时候混合一起上传），所以草稿只保存文字吧

- [x] @bp_user.route('/user-api/v1/tweet/get_single_tweet', methods=['POST'])

@login_required

params:

```json
{
  "tweet_id": int
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0,
  "userid": int,
  "type": int,
  "title": string,
  "content": string,
  "audio_url": url,
  "video_url": url,
  "image_url_list": [ url ],
  "location": string,
  "last_modified": string,
  "like_count": int,
  "comment_count": int,
  "nickname": string,
  "headshot": url,
  "is_follow": boolean,
  "is_like": boolean
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

- [x] @bp_user.route('/user-api/v1/tweet/like_tweet', methods=['POST'])

@login_required

params:

```json
{
  "tweet_id": int
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

- [x] @bp_user.route('/user-api/v1/tweet/comment_tweet', methods=['POST'])

@login_required

params:

```json
{
  "tweet_id": int,
  "comment": string
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

- [x] @bp_user.route('/user-api/v1/tweet/delete_comment', methods=['POST'])

@login_required

params:

```json
{
  "comment_id": int
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

- [x] @bp_user.route('/user-api/v1/tweet/get_tweet_comment_list', methods=['POST'])

@login_required

params:

```json
{
  "tweet_id": int
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0,
  "comment_list": [
    {
      "comment_id": int,
      "userid": int,
      "content": string,
      "nickname": string,
      "headshot": url,
      "comment_time": string
    }
  ]
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

## 通知

@bp_user.route('/user-api/v1/notification/get_message_list', methods=['POST'])

@login_required

params:

```json
{
  "start": int
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0,
  "notification_list": [
    {
      "message_id": int,
      "message_time": string,
      "content": string
    }
  ]
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

@bp_user.route('/user-api/v1/notification/get_like_notification_list', methods=['POST'])

@login_required

params:

```json
{
  "start": int
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0,
  "notification_list": [
    {
      "notification_id": int,
      "userid": int,
      "tweet_id": int,
      "like_time": string
    }
  ]
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

@bp_user.route('/user-api/v1/notification/delete_like_notification', methods=['POST'])

@login_required

params:

```json
{
  "notification_id": int
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

@bp_user.route('/user-api/v1/notification/get_comment_notification_list', methods=['POST'])

@login_required

params:

```json
{
  "start": int
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0,
  "notification_list": [
    {
      "notification_id": int,
      "userid": int,
      "tweet_id": int,
      "comment_time": string,
      "content": string
    }
  ]
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

@bp_user.route('/user-api/v1/notification/delete_comment_notification', methods=['POST'])

@login_required

params:

```json
{
  "notification_id": int
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

