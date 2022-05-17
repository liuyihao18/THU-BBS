package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api;

import org.json.JSONObject;

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
    public static final String title = "title";
    public static final String block = "block";
    public static final String of = "of";
    public static final String userId = "userid";
    public static final String orderBy = "order_by";
    public static final String tweetType = "tweet_type";
    public static final String searchStr = "search_str";
    public static final String tweetList = "tweet_list";
    public static final String audioUrl = "audio_url";
    public static final String videoUrl = "video_url";
    public static final String imageUrlList = "image_url_list";
    public static final String lastModified = "last_modified";
    public static final String likeCount = "like_count";
    public static final String commentCount = "comment_count";
    public static final String nickname = "nickname";
    public static final String headshot = "headshot";
    public static final String isFollow = "is_follow";
    public static final String isLike = "is_like";

    private static final String prefix = "/tweet";

    private static String getUrl(String suffix) {
        return Config.BASE_URL + APIConstant.API_PREFIX + prefix + suffix;
    }

    public static void createTweet(MultipartBody body, Callback callback) {
        BaseRequest.post(getUrl("/create_tweet"), body, callback);
    }

    public static void getTweetList(JSONObject data, Callback callback) {
        BaseRequest.post(getUrl("/get_tweet_list"), data, callback);
    }
}
