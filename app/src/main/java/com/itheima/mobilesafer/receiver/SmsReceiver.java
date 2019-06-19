package com.itheima.mobilesafer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;

import com.itheima.mobilesafer.activities.R;
import com.itheima.mobilesafer.service.LocationService;
import com.itheima.mobilesafer.utils.ConstantValue;
import com.itheima.mobilesafer.utils.DeviceAdminUtil;
import com.itheima.mobilesafer.utils.SpUtil;

public class SmsReceiver extends BroadcastReceiver {

    public static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: 短信监听开始");
        //1,判断是否开启了防盗保护
        boolean open_security = SpUtil.getBoolean(context, ConstantValue.OPEN_SECURITY, false);
        //拿去紧急联系人号码
        String contact_phone = SpUtil.getString(context, ConstantValue.CONTACT_PHONE, "");
        if (open_security) {
            //2.获取短信内容
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            //3.循环遍历短信过程
            for (Object object : objects) {
                //4.获取短信对象
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
                //5.获取短信对象的基本信息
                String originatingAddress = smsMessage.getOriginatingAddress();
                String messageBody = smsMessage.getMessageBody();

                //判断是否是
                if (originatingAddress.equals(contact_phone) && !originatingAddress.isEmpty()) {
                    //6,判断是否包含播放音乐的关键字
                    if (messageBody.contains("#*alarm*#")) {
                        //7,播放音乐(准备音乐,MediaPlayer)
                        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
                        mediaPlayer.setLooping(true);
                        mediaPlayer.start();
                    }

                    if (messageBody.contains("#*location*#")) {
                        //8,开启获取位置服务
                        context.startService(new Intent(context, LocationService.class));
                    }

                    if (messageBody.contains("#*wipedata*#")) {
                        DeviceAdminUtil deviceAdminUtil = new DeviceAdminUtil(context);
                        deviceAdminUtil.wipeData(0);
                    }

                    if (messageBody.contains("#*lockscreen*#")) {
                        DeviceAdminUtil deviceAdminUtil = new DeviceAdminUtil(context);
                        deviceAdminUtil.lockDevice();
                    }
                }
            }
        }
    }
}
