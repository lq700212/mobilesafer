package com.itheima.mobilesafer.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.itheima.mobilesafer.utils.ConstantValue;
import com.itheima.mobilesafer.utils.SpUtil;
import com.itheima.mobilesafer.utils.ToastUtil;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "重启手机成功了,并且监听到了相应的广播......");
        //1,获取开机后手机的sim卡的序列号
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String curSimNumber = null;
        String sim_number = null;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "权限问题:用户未授予权限READ_PHONE_STATE");
            return;
        } else {
            curSimNumber = telephonyManager.getSimSerialNumber();
            Log.d(TAG, "curSimNumber: " + curSimNumber);
//            curSimNumber = curSimNumber + "xxx";    //for test
            //2,sp中存储的序列卡号
            sim_number = SpUtil.getString(context, ConstantValue.SIM_NUMBER, "");
            Log.d(TAG, "curSimNumber: " + curSimNumber + ", sim_number = " + sim_number);
        }

        //3,比对不一致
        if (!curSimNumber.equals(sim_number)) {
            String contact_phone = SpUtil.getString(context, ConstantValue.CONTACT_PHONE, "");
            if (!TextUtils.isEmpty(contact_phone)) {
                //4,发送短信给选中联系人号码
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(contact_phone, null, "SIM卡有变化", null, null);
            } else {
                ToastUtil.show(context, "紧急联系人电话号码获取失败");
            }
        }
    }
}