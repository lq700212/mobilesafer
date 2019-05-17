package com.itheima.mobilesafer.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Desction:手机安全卫士
 * Author:ryan.lei
 * Date:2019-05-16 17:29
 */
public class ToastUtil {
    public static void show(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }
}
