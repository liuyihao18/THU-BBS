package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils;


import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.State;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.Static;
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
            return new User(nickname, headshot, description,
                    tweetCount, followCount, followerCount);
        } catch (JSONException je) {
            return null;
        }
    }

    public static Tweet createTweetFromJSON(@NonNull JSONObject data) {
        return null;
    }
}
