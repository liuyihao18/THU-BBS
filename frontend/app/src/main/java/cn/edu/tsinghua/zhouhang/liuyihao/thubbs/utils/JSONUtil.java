package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils;


import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.RelationAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.Static;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.TweetAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.NotificationAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.UserAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.CommentItemContent;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Draft;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.FollowItemContent;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.LikeItemContent;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Comment;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Tweet;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.User;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.mMessage;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.UserListItem;

public class JSONUtil {
    /**
     * 从数据创建用户
     *
     * @param data 服务器返回的JSON
     * @return 成功返回用户，失败返回空
     */
    public static User createUserFromJSON(@NonNull JSONObject data) {
        try {
            String nickname = data.getString(UserAPI.nickname);
            String headshot = Static.HeadShot.getHeadShotUrl(data.getString(UserAPI.headshot));
            String description = data.getString(UserAPI.description);
            int tweetCount = data.getInt(UserAPI.tweetCount);
            int followCount = data.getInt(UserAPI.followCount);
            int followerCount = data.getInt(UserAPI.followerCount);
            boolean isFollow = data.getBoolean(UserAPI.isFollow);
            return new User(nickname, headshot, description, isFollow,
                    tweetCount, followCount, followerCount);
        } catch (JSONException je) {
            return null;
        }
    }

    /**
     * 从数据创建动态
     *
     * @param data 服务器返回的JSON
     * @return 成功返回动态，失败返回空
     */
    public static Tweet createTweetFromJSON(@NonNull JSONObject data) {
        try {
            int tweetId = data.getInt(TweetAPI.tweetId);
            int userId = data.getInt(TweetAPI.userId);
            int type = data.getInt(TweetAPI.type);
            String title = data.getString(TweetAPI.title);
            String content = data.getString(TweetAPI.content);
            String audioUrl = null;
            String videoUrl = null;
            ArrayList<String> imageList = null;
            switch (type) {
                case Tweet.TYPE_TEXT:
                    break;
                case Tweet.TYPE_IMAGE:
                    JSONArray array = data.getJSONArray(TweetAPI.imageUrlList);
                    imageList = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        imageList.add(Static.Image.getImageUrl(array.getString(i)));
                    }
                    break;
                case Tweet.TYPE_AUDIO:
                    audioUrl = Static.Audio.getAudioUrl(data.getString(TweetAPI.audioUrl));
                    break;
                case Tweet.TYPE_VIDEO:
                    videoUrl = Static.Video.getVideoUrl(data.getString(TweetAPI.videoUrl));
                    break;
            }
            String location = data.getString(TweetAPI.location);
            String lastModified = data.getString(TweetAPI.lastModified);
            int likeCount = data.getInt(TweetAPI.likeCount);
            int commentCount = data.getInt(TweetAPI.commentCount);
            String nickName = data.getString(TweetAPI.nickname);
            String headshot = Static.HeadShot.getHeadShotUrl(data.getString(TweetAPI.headshot));
            boolean isFollow = data.getBoolean(TweetAPI.isFollow);
            boolean isLike = data.getBoolean(TweetAPI.isLike);
            return new Tweet(tweetId, userId, type, title, content, location, lastModified, commentCount,
                    likeCount, imageList, audioUrl, videoUrl, nickName, headshot, isFollow, isLike);
        } catch (JSONException je) {
            return null;
        }
    }

    /**
     * 从数据创建评论
     *
     * @param data 服务器返回的JSON
     * @return 成功返回动态，失败返回空
     */
    public static Comment createCommentFromJSON(@NotNull JSONObject data) {
        try {
            int commentId = data.getInt(TweetAPI.commentId);
            int userId = data.getInt(TweetAPI.userId);
            String content = data.getString(TweetAPI.content);
            String nickname = data.getString(TweetAPI.nickname);
            String headshot = Static.HeadShot.getHeadShotUrl(data.getString(TweetAPI.headshot));
            String commentTime = data.getString(TweetAPI.commentTime);
            return new Comment(commentId, userId, nickname, headshot, content, commentTime);
        } catch (JSONException je) {
            return null;
        }
    }

    /**
     * 从数据创建用户列表项
     *
     * @param data 服务器返沪的JSON
     * @return 成功返回用户列表项，失败返回空
     */
    public static UserListItem createUserListItemFromJSON(@NonNull JSONObject data) {
        try {
            int userId = data.getInt(RelationAPI.userId);
            String nickname = data.getString(RelationAPI.nickname);
            String headshot = Static.HeadShot.getHeadShotUrl(data.getString(RelationAPI.headshot));
            String description = data.getString(RelationAPI.description);
            return new UserListItem(userId, nickname, headshot, description);
        } catch (JSONException je) {
            return null;
        }
    }

    /**
     * 从数据创建草稿
     *
     * @param data 服务器返回的JSON
     * @return 成功返回动态，失败返回空
     */
    public static Draft createDraftFromJSON(@NonNull JSONObject data) {
        try {
            int tweetId = data.getInt(TweetAPI.tweetId);
            // int type = data.getInt(TweetAPI.type);
            String title = data.getString(TweetAPI.title);
            String content = data.getString(TweetAPI.content);
            String location = data.getString(TweetAPI.location);
            String lastModified = data.getString(TweetAPI.lastModified);
            return new Draft(tweetId, title, content, location, lastModified);
        } catch (JSONException je) {
            return null;
        }
    }

    public static LikeItemContent createLikeFromJSON(@NonNull JSONObject data) {
        try {
            int tweet_id = data.getInt(NotificationAPI.tweet_id);
            String like_time = data.getString(NotificationAPI.like_time);
            int userid = data.getInt(NotificationAPI.userid);
            int notification_id = data.getInt(NotificationAPI.notification_id);
            String tweet_content = data.getString(NotificationAPI.tweet_content);
            String headshot = Static.HeadShot.getHeadShotUrl(data.getString(NotificationAPI.headshot));
            String like_user_name = data.getString(NotificationAPI.like_user_name);
            return new LikeItemContent(
                    headshot,
                    like_user_name,
                    like_time,
                    tweet_id,
                    tweet_content,
                    userid,
                    notification_id
            );
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
            return null;
        }
    }

    public static CommentItemContent createCommentNotificationFromJson(@NonNull JSONObject data) {
        try {
            int tweet_id = data.getInt(NotificationAPI.tweet_id);
            String headshot = Static.HeadShot.getHeadShotUrl(data.getString(NotificationAPI.headshot));
            int userid = data.getInt(NotificationAPI.userid);
            int notification_id = data.getInt(NotificationAPI.notification_id);
            String content = data.getString(NotificationAPI.content);
            String comment_user_name = data.getString(NotificationAPI.comment_user_name);
            String comment_time = data.getString(NotificationAPI.comment_time);
            String tweet_content = data.getString(NotificationAPI.tweet_content);
            return new CommentItemContent(
                    headshot,
                    comment_user_name,
                    comment_time,
                    tweet_id,
                    content,
                    userid,
                    notification_id,
                    tweet_content
            );
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
            return null;
        }
    }

    public static mMessage createMessageFromJson(@NonNull JSONObject data) {
        try {
            int message_id = data.getInt("message_id");
            String message_time = data.getString("message_time");
            String message_content = data.getString("content");
            String message_title = data.getString("title");
            String headshot = Static.HeadShot.getHeadShotUrl(data.getString(NotificationAPI.headshot));
            return new mMessage(
                    message_title,
                    message_content,
                    message_time,
                    message_id,
                    headshot
            );
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
            return null;
        }
    }

    public static FollowItemContent createFollowFromJSON(@NonNull JSONObject data) {
        try {
            int tweet_id = data.getInt(NotificationAPI.tweet_id);
            String notification_time = data.getString(NotificationAPI.notification_time);
            int userid = data.getInt(NotificationAPI.userid);
            int notification_id = data.getInt(NotificationAPI.notification_id);
            String tweet_content = data.getString(NotificationAPI.tweet_content);
            String headshot = Static.HeadShot.getHeadShotUrl(data.getString(NotificationAPI.headshot));
            String followee_user_name = data.getString(NotificationAPI.followee_user_name);
            return new FollowItemContent(
                    headshot,
                    followee_user_name,
                    notification_time,
                    tweet_id,
                    tweet_content,
                    userid,
                    notification_id
            );
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
            return null;
        }
    }
}
