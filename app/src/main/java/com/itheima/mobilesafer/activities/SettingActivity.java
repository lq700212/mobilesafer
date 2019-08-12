package com.itheima.mobilesafer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.itheima.mobilesafer.service.AddressService;
import com.itheima.mobilesafer.utils.ConstantValue;
import com.itheima.mobilesafer.utils.ServiceUtil;
import com.itheima.mobilesafer.utils.SpUtil;
import com.itheima.mobilesafer.view.SettingItemView;

/**
 * Desction:手机安全卫士
 * Author:ryan.lei
 * Date:2019-05-28 17:40
 */
public class SettingActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initUpdate();
        initAddress();
    }

    /**
     * 版本更新开关
     */
    private void initUpdate() {
        final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);

        //获取已有的开关状态,用作显示
        boolean open_update = SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false);
        //是否选中,根据上一次存储的结果去做决定
        siv_update.setCheck(open_update);

        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果之前是选中的,点击过后,变成未选中
                //如果之前是未选中的,点击过后,变成选中

                //获取之前的选中状态
                boolean isCheck = siv_update.isChecked();
                //将原有状态取反,等同上诉的两部操作
                siv_update.setCheck(!isCheck);
                //将取反后的状态存储到相应sp中
                SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, !isCheck);
            }
        });
    }

    /**
     * 是否显示电话号码归属地的方法
     */
    private void initAddress() {
        final SettingItemView siv_address = (SettingItemView) findViewById(R.id.siv_address);
        //对服务是否开的状态做显示
//        boolean isRunning = ServiceUtil.isRunning(this, "com.itheima.mobilesafe74.service.AddressService");
//        siv_address.setCheck(isRunning);
        //点击过程中,状态(是否开启电话号码归属地)的切换过程
        siv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回点击前的选中状态
                boolean isCheck = siv_address.isChecked();
                siv_address.setCheck(!isCheck);
                if (isCheck) {
                    //关闭服务，不需要显示吐司
                    stopService(new Intent(getApplicationContext(), AddressService.class));
                } else {
                    //开启服务，管理吐司
                    startService(new Intent(getApplicationContext(), AddressService.class));
                }
            }
        });
    }
}
