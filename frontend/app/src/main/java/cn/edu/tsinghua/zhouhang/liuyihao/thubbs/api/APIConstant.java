package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api;

public class APIConstant {
    public static final String AUTHORIZATION = "Authorization";

    public static final String API_PREFIX = "/user-api/v1";

    public static final String ERR_CODE = "errCode";
    public static final String ERR_MSG = "errMsg";

    public static final int REQUEST_OK = 0; // 请求成功
    public static final int REQUEST_ERROR = -1; // 服务器端返回错误信息
    public static final int NETWORK_ERROR = -2; // 网络错误
    public static final int SERVER_ERROR = -3; // 服务器错误
}
