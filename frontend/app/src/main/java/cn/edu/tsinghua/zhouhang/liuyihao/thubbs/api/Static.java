package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.api;

public class Static {
    private static final String prefix = "/static";

    private static String getUrl(String subPrefix, String name) {
        return Config.BASE_URL + prefix + subPrefix + "/" + name;
    }

    public static class Image {
        private static final String subPrefix = "/image";

        public static String getImageUrl(String name) {
            return getUrl(subPrefix, name);
        }
    }

    public static class Audio {
        private static final String subPrefix = "/audio";

        public static String getAudioUrl(String name) {
            return getUrl(subPrefix, name);
        }
    }

    public static class Video {
        private static final String subPrefix = "/video";

        public static String getVideoUrl(String name) {
            return getUrl(subPrefix, name);
        }
    }

    public static class HeadShot {
        private static final String subPrefix = "/headshot";

        public static String getHeadShotUrl(String name) {
            return getUrl(subPrefix, name);
        }
    }
}
