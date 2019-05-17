package com.itheima.mobilesafer;

import android.app.Application;

import org.xutils.BuildConfig;
import org.xutils.x;

/**
 * Desction:白骑士用户行为分析sdk
 * Author:ryan.lei
 * Date:2019-05-16 19:15
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);  //是否输出debug日志, 开启debug会影响性能
    }
}