package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api;

import okhttp3.Callback;
import okhttp3.MultipartBody;

public class TweetAPI {
    public static final String type = "type";
    public static final String isDraft = "is_draft";
    public static final String content = "content";
    public static final String location = "location";
    public static final String audio = "audio";
    public static final String video = "video";
    public static final String image = "image";
    public static final String imageCount = "image_count";
    public static final String tweetId = "tweet_id";

    private static final String prefix = "/tweet";

    private static String getUrl(String suffix) {
        return Config.BASE_URL + APIConstant.API_PREFIX + prefix + suffix;
    }

    public static void createTweet(MultipartBody body, Callback callback) {
        BaseRequest.post(getUrl("/create_tweet"), body, callback);
    }
}
