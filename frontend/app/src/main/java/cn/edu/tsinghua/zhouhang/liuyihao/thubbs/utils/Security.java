package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Security {
    private final static String SALT = "&Y&^%#(!";

    private static String byte2HexStr(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        String temp;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xff);
            if (temp.length() == 1) {
                stringBuilder.append("0");
            }
            stringBuilder.append(temp);
        }
        return stringBuilder.toString();
    }

    public static String encode(String s) {
        MessageDigest algorithm;
        try {
            algorithm = MessageDigest.getInstance("SHA");
            algorithm.reset();
            algorithm.update((s + SALT).getBytes());
            return byte2HexStr(algorithm.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
