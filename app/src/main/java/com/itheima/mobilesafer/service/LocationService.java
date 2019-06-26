package com.itheima.mobilesafer.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;

import com.itheima.mobilesafer.utils.ConstantValue;
import com.itheima.mobilesafer.utils.SpUtil;
import com.itheima.mobilesafer.utils.ToastUtil;

public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private String contact_phone;

    @Override
    public void onCreate() {
        super.onCreate();
        //获取手机的经纬度
        //1.获取位置管理者对象
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        //2.以最优的方式获取经纬度坐标
        Criteria criteria = new Criteria();
        //允许花费(因为可能会使用流量)
        criteria.setCostAllowed(true);
        //指定获取经纬度的精确度
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String bestProvider = lm.getBestProvider(criteria, true);
        //获取紧急联系人号码
        contact_phone = SpUtil.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
        //3.在一定时间间隔，移动一定距离后获取经纬度坐标
        MyLocationListener myLocationListener = new MyLocationListener();

        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lm.requestLocationUpdates(bestProvider, 0, 0, myLocationListener);
        } else {
            ToastUtil.show(getApplicationContext(), "未授予权限，得到位置信息失败");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            //经度
            double longitude = location.getLongitude();
            //纬度
            double latitude = location.getLatitude();
            //4.发送短信(添加权限)
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(contact_phone, null,
                    "longitude = " + longitude + ", latitude = " + latitude, null, null);
            //停止位置服务
            stopSelf();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
