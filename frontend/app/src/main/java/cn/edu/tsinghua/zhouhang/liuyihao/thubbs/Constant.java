package cn.edu.tsinghua.zhouhang.liuyihao.thubbs;

public class Constant {
    public static final String SHARED_PREFERENCES = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.constant.prefFile";

    public static final String JWT = "jwt";
    public static final String USER_ID = "userid";
    public static final String HEADSHOT = "headshot";
    public static final String TMP_DIR = "tmp";

    /* Permission */
    public static final int LOCATION_PERMISSION = 0;
    public static final int RECORD_PERMISSION = 1;

    /* Edit Constant */
    public static final int MAX_IMAGE_COUNT = 9;
    public static final int MAX_AUDIO_COUNT = 1;
    public static final int MAX_VIDEO_COUNT = 1;

    /* Tweet Fragment */
    public static final String TWEETS_TYPE = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant.TWEETS_TYPE";
    public static final int TWEETS_EMPTY = 0; // 空
    public static final int TWEETS_ALL = 1; // 全部推送
    public static final int TWEETS_FOLLOW = 2; // 关注的人的推送
    public static final int TWEETS_USER = 3; // 用户的推送

    /* Intent Constant */
    public static final String EXTRA_EMAIL = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.constant.EXTRA_EMAIL";
    public static final String DETAIL_HAVE_DATA = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant.DETAIL_HAVE_DATA";
    public static final String DETAIL_NO_DATA = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant.DETAIL_NO_DATA";
    public static final String EDIT_FROM_DRAFT = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant.EDIT_FROM_DRAFT";
    public static final String EDIT_FROM_BLANK = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant.EDIT_FROM_BLANK";
    public static final String EXTRA_TWEET = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant.EXTRA_TWEET";
    public static final String EXTRA_TWEET_ID = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant.EXTRA_TWEET_ID";
    public static final String EXTRA_TWEET_CONTENT = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant.EXTRA_TWEET_CONTENT";
    public static final String EXTRA_IMAGE_URL = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant.EXTRA_IMAGE_URL";
    public static final String EXTRA_USER_ID = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant.EXTRA_USER_ID";
}
