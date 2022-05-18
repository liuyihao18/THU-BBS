package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils;


import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.Static;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.TweetAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.UserAPI;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.Tweet;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.model.User;

public class JSONUtil {
    public static User createUserFromJSON(@NonNull JSONObject data) {
        try {
            String nickname = data.getString(UserAPI.nickname);
            String headshot = Static.HeadShot.getHeadShotUrl(data.getString(UserAPI.headshot));
            String description = data.getString(UserAPI.description);
            int tweetCount = data.getInt(UserAPI.tweetCount);
            int followCount = data.getInt(UserAPI.followCount);
            int followerCount = data.getInt(UserAPI.followerCount);
            return new User(nickname, headshot, description, false,
                    tweetCount, followCount, followerCount);
        } catch (JSONException je) {
            return null;
        }
    }

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
            je.printStackTrace();
            return null;
        }
    }
}
