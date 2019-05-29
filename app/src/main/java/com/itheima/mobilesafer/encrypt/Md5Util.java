package com.itheima.mobilesafer.encrypt;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Desction:手机安全卫士
 * Author:ryan.lei
 * Date:2019-05-29 19:03
 */
public class Md5Util {
    public static final String TAG = "Md5Util";

    /**
     * 给指定字符串按照md5算法去加密
     *
     * @param psd 需要加密的密码
     * @return 返回加密后的字符串
     */
    public static String encoder(String psd) {
        try {
            //1,指定加密算法类型
            MessageDigest digest = MessageDigest.getInstance("MD5");
            //2,将需要加密的字符串中转换成byte类型的数组,然后进行随机哈希过程
            byte[] bs = digest.digest(psd.getBytes());
//			System.out.println(bs.length);
            //3,循环遍历bs,然后让其生成32位字符串,固定写法
            //4,拼接字符串过程
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : bs) {
                int i = b & 0xff;
                //int类型的i需要转换成16机制字符
                String hexString = Integer.toHexString(i);
//				System.out.println(hexString);
                if (hexString.length() < 2) {
                    hexString = "0" + hexString;
                }
                stringBuilder.append(hexString);
            }
            //5,打印测试
            Log.d(TAG, "encoder: " + stringBuilder.toString());

            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
