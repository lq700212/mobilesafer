package com.itheima.mobilesafer.utils;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.itheima.mobilesafer.activities.SetupOverActivity;
import com.itheima.mobilesafer.receiver.MyDeviceAdminReceiver;

/**
 * Desction:手机安全卫士
 * Author:ryan.lei
 * Date:2019-06-18 20:42
 */
public class DeviceAdminUtil {
    public static final String TAG = "DeviceAdminUtil";

    private ComponentName mDeviceAdminSample;
    private DevicePolicyManager mDPM;
    private Context mContext;

    /**
     * 初始化设备管理器工具类
     *
     * @param context
     */
    public DeviceAdminUtil(Context context) {
        mContext = context;
        //(上下文环境,广播接受者对应的字节码文件)
        //组件对象可以作为是否激活的判断标志
        mDeviceAdminSample = new ComponentName(mContext, MyDeviceAdminReceiver.class);
        mDPM = (DevicePolicyManager) mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    /**
     * 开启设备管理器
     */
    public void startDeviceAdmin() {
        Log.d(TAG, "startDeviceAdmin: ");
        //开启设备管理器的activity
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "设备管理器");
        mContext.startActivity(intent);
    }

    /**
     * 跳转到有点击按钮的设置界面
     */
    public void startSettingOverActivity() {
        Log.d(TAG, "startSettingOverActivity: ");
        Intent intent = new Intent(mContext, SetupOverActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    /**
     * 检测是否开启设备管理员权限
     */
    public boolean isAdminActive() {
        return mDPM.isAdminActive(mDeviceAdminSample);
    }

    /**
     * 一键锁屏功能
     */
    public void lockDevice() {
        //是否开启的判断
        if (isAdminActive()) {
            //激活--->锁屏
            mDPM.lockNow();
            //锁屏同时去设置密码
            mDPM.resetPassword("123456", 0);
        } else {
            ToastUtil.show(mContext, "请先激活设备管理器权限");
//            startDeviceAdmin();
            startSettingOverActivity();
        }
    }

    /**
     * 一键擦除数据
     *
     * @param flags 决定擦除哪里数据的选项 0表示手机本机的，WIPE_EXTERNAL_STORAGE表示sd卡的
     */
    public void wipeData(int flags) {
        if (isAdminActive()) {
            mDPM.wipeData(flags);
        } else {
            ToastUtil.show(mContext, "请先激活设备管理器权限");
//            startDeviceAdmin();
            startSettingOverActivity();
        }
    }

    /**
     * 一键卸载功能
     */
    public void uninstall() {
        if (isAdminActive()) {
            mDPM.removeActiveAdmin(mDeviceAdminSample);//删除超级管理权限
        }
        Intent intent = new Intent("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
        mContext.startActivity(intent);
    }
}
