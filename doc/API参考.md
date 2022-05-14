# API

root path = "http://47.93.89.166"

## 登录

@bp_user.route('/user-api/v1/user/register', methods=['POST'])

params:

```json
{
  "email": email,
  "password": password
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

@bp_user.route('/user-api/v1/user/login', methods=['POST'])

params:

```json
{
  "email": email,
  "password": password
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0,
  "jwt": jwt,
  "userid": userid
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

@bp_user.route('/user-api/v1/user/get_profile', methods=['POST'])

@login_required

params:

```json
{
  "userid": userid
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0,
  "nickname": nickname,
  "headshot": url,
  "description": description
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

@bp_user.route('/user-api/v1/user/get_minimum_profile', methods=['POST'])

@login_required

params:

```json
{
  "userid": userid
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0,
  "nickname": nickname,
  "headshot": url
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

@bp_user.route('/user-api/v1/user/edit_profile', methods=['POST'])

@login_required

params: **form-data**

```json
{
  "nickname": nickname,
  "headshot": file,
  "description": description
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

@bp_user.route('/user-api/v1/user/edit_password', methods=['POST'])

@login_required

params:

```json
{
  "old_password": old_password,
  "new_password": new_password
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

@bp_user.route('/user-api/v1/relation/follow', methods=['POST'])

@login_required

params:

```json
{
  "follow_id": follow_id
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

@bp_user.route('/user-api/v1/relation/unfollow', methods=['POST'])

@login_required

params:

```json
{
  "follow_id": follow_id
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

@bp_user.route('/user-api/v1/relation/black', methods=['POST'])

@login_required

params:

```json
{
  "black_id": black_id
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

@bp_user.route('/user-api/v1/relation/white', methods=['POST'])

@login_required

params:

```json
{
  "white_id": white_id
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

## 推文

@bp_user.route('/user-api/v1/tweet/create_tweet', methods=['POST'])

@login_required

params: **form-data**

```json
{
  "type": type,
  "is_draft": is_draft,
  "content": content,
  "location": location,
  "audio": audio_file,    /* optional */
  "video": video_file,    /* optional */
  "image_count": image_count,
  "image1": image_file1,  /* optional */
  ...
  "image9": image_file9   /* optional */
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0
  "tweet_id": tweet_id
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

@bp_user.route('/user-api/v1/tweet/edit_tweet', methods=['POST'])

@login_required

params: **form-data**

```json
{
  "tweet_id": tweet_id,
  "type": type,
  "is_draft": is_draft,
  "content": content,
  "location": location,
  "audio": audio_file,    /* optional */
  "video": video_file     /* optional */
  "image_count": image_count,
  "image1": image_file1,  /* optional */
  ...
  "image9": image_file9   /* optional */
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0
  "tweet_id": tweet_id
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

@bp_user.route('/user-api/v1/tweet/delete_tweet', methods=['POST'])

@login_required

params:

```json
{
  "tweet_id": tweet_id
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

@bp_user.route('/user-api/v1/tweet/get_tweet_list', methods=['POST'])

@login_required

params:

```json
{
  "start": start
  "order_by": "time"
  or
  "likes"
  "type": type
  "search_str": search_str
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0
  "tweet_list": [
    {
      "tweet_id": tweet_id,
      "userid": userid,
      "type": type,
      "content": content,
      "audio_url": audio_url,
      "video_url": video_url,
      "image_url_list": image_url_list
      "location": location,
      "last_modified": last_modified,
      "like_count": like_count,
      "comment_count": comment_count
    },
    ...
  ]
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

@bp_user.route('/user-api/v1/tweet/get_single_tweet', methods=['POST'])

@login_required

params:

```json
{
  "tweet_id": tweet_id
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0
  "userid": userid,
  "type": type,
  "content": content,
  "audio_url": audio_url,
  "video_url": video_url,
  "image_url_list": image_url_list
  "location": location,
  "last_modified": last_modified,
  "like_count": like_count,
  "comment_count": comment_count
}
/* 失败情形 */
{
  "errCode": 1,
  "errMsg": errMsg
}
```

@bp_user.route('/user-api/v1/tweet/like_tweet', methods=['POST'])

@login_required

params:

```json
{
  "tweet_id": tweet_id
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

@bp_user.route('/user-api/v1/tweet/comment_tweet', methods=['POST'])

@login_required

params:

```json
{
  "tweet_id": tweet_id,
  "comment": comment
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

@bp_user.route('/user-api/v1/tweet/get_tweet_comment_list', methods=['POST'])

@login_required

params:

```json
{
  "tweet_id": tweet_id
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0
  "comment_list": [
    {
      "userid": userid,
      "comment": comment
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
  "start": start
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0
  "notification_list": [
    {
      "message_id": notification_id,
      "message_time": like_time
      "content": content
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
  "start": start
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0
  "notification_list": [
    {
      "notification_id": notification_id,
      "userid": userid,
      "tweet_id": tweet_id,
      "like_time": like_time
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
  "notification_id": notification_id
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
  "start": start
}
```

ret:

```json
/* 成功情形 */
{
  "errCode": 0
  "notification_list": [
    {
      "notification_id": notification_id,
      "userid": userid,
      "tweet_id": tweet_id,
      "comment_time": like_time,
      "comment": comment
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
  "notification_id": notification_id
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

