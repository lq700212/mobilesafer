package com.itheima.mobilesafer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.itheima.mobilesafer.activities.SettingActivity;

/**
 * Desction:手机安全卫士
 * Author:ryan.lei
 * Date:2019-05-29 10:45
 */
public class SpUtil {
    private static SharedPreferences sp;

    /**
     * 读取String变量从sp
     *
     * @param context  上下文环境
     * @param key      存储节点名称
     * @param defValue 没有此节点的默认值
     * @return 默认值或者此节点读取到的结果
     */
    public static String getString(Context context, String key, String defValue) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getString(key, defValue);
    }

    /**
     * 写入String变量至sp中
     *
     * @param context 上下文环境
     * @param key     存储节点名称
     * @param value   存储节点的值string
     */
    public static void putString(Context context, String key, String value) {
        //(存储节点文件名称,读写方式)
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, value).apply();
    }

    /**
     * 读取boolean标示从sp中
     *
     * @param context  上下文环境
     * @param key      存储节点名称
     * @param defValue 没有此节点默认值
     * @return 默认值或者此节点读取到的结果
     */
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        //(存储节点文件名称,读写方式)
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defValue);
    }

    /**
     * 写入boolean变量至sp中
     *
     * @param context 上下文环境
     * @param key     存储节点名称
     * @param value   存储节点的值 boolean
     */
    public static void putBoolean(Context context, String key, boolean value) {
        //(存储节点文件名称,读写方式)
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).apply();
    }

    /**
     * 从sp中移除指定节点
     *
     * @param context 上下文环境
     * @param key     需要移除节点的名称
     */
    public static void remove(Context context, String key) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().remove(key).apply();
    }
}
