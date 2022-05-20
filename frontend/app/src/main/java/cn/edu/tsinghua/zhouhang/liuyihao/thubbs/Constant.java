package cn.edu.tsinghua.zhouhang.liuyihao.thubbs;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api.Static;

public class Constant {
    /* 本地文件 */
    public static final String SHARED_PREFERENCES = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.constant.prefFile";

    /* 状态常量 */
    public static final String JWT = "jwt";
    public static final String USER_ID = "userid";
    public static final String DEFAULT_HEADSHOT = Static.HeadShot.getHeadShotUrl("default_headshot.jpg");
    public static final String TMP_DIR = "tmp";
    public static final int LOGIN_OK = 0;
    public static final int DELAY_TIME = 50;

    /* 权限 */
    public static final int LOCATION_PERMISSION = 0;
    public static final int RECORD_PERMISSION = 1;

    /* 编辑相关常量 */
    public static final int MAX_IMAGE_COUNT = 9;
    public static final int MAX_AUDIO_COUNT = 1;
    public static final int MAX_VIDEO_COUNT = 1;

    /* 动态列表相关常量 */
    public static final String TWEETS_TYPE = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant.TWEETS_TYPE";
    public static final int TWEETS_EMPTY = 0; // 空
    public static final int TWEETS_ALL = 1; // 全部推送
    public static final int TWEETS_FOLLOW = 2; // 关注的人的推送
    public static final int TWEETS_USER = 3; // 用户的推送
    public static final int TWEETS_DETAIL = 4; // 用户的推送

    /* 用户列表相关常量 */
    public static final String USER_LIST_TYPE = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant.USER_LIST_TYPE";
    public static final int EMPTY_LIST = 0; // 空
    public static final int FOLLOW_LIST = 1; // 关注列表
    public static final int FAN_LIST = 2; // 粉丝列表
    public static final int BLACK_LIST = 3; // 黑名单

    /* Intent Constant */
    // Action
    public static final String EDIT_FROM_DRAFT = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant.EDIT_FROM_DRAFT";
    public static final String EDIT_FROM_BLANK = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant.EDIT_FROM_BLANK";
    // Extra
    public static final String EXTRA_EMAIL = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.constant.EXTRA_EMAIL";
    public static final String EXTRA_TWEET = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant.EXTRA_TWEET";
    public static final String EXTRA_TWEET_ID = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant.EXTRA_TWEET_ID";
    public static final String EXTRA_TWEET_CONTENT = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant.EXTRA_TWEET_CONTENT";
    public static final String EXTRA_IMAGE_URL = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant.EXTRA_IMAGE_URL";
    public static final String EXTRA_USER_ID = "cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant.EXTRA_USER_ID";
}
