package com.itheima.mobilesafer.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.itheima.mobilesafer.utils.ConstantValue;
import com.itheima.mobilesafer.utils.SpUtil;
import com.itheima.mobilesafer.utils.ToastUtil;
import com.itheima.mobilesafer.view.SettingItemView;

/**
 * Desction:手机安全卫士
 * Author:ryan.lei
 * Date:2019-06-04 19:12
 */
public class Setup2Activity extends BaseSetupActivity {

    public static final String TAG = "Setup2Activity";

    private SettingItemView siv_sim_bound;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        initUI();
        initData();
    }

    @Override
    protected void showNextPage() {
        if (siv_sim_bound.isChecked()) {
            String simSerialNumber = SpUtil.getString(this, ConstantValue.SIM_NUMBER, "");
            if (!TextUtils.isEmpty(simSerialNumber)) {
                Intent intent = new Intent(Setup2Activity.this, Setup3Activity.class);
                startActivity(intent);
                finish();

                //开启平移动画
                overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
            } else {
                ToastUtil.show(this, "请绑定SIM卡");
            }
        } else {
            ToastUtil.show(this, "请绑定SIM卡");
        }
    }

    @Override
    protected void showPrePage() {
        Intent intent = new Intent(Setup2Activity.this, Setup1Activity.class);
        startActivity(intent);
        finish();

        //开启平移动画
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    private void initUI() {
        siv_sim_bound = (SettingItemView) findViewById(R.id.siv_sim_bound);
    }

    private void initData() {
        //1,回显(读取已有的绑定状态,用作显示,sp中是否存储了sim卡的序列号)
        String sim_number = SpUtil.getString(this, ConstantValue.SIM_NUMBER, "");
        //2,判断是否序列卡号为""
        if (TextUtils.isEmpty(sim_number)) {
            siv_sim_bound.setCheck(false);
        } else {
            siv_sim_bound.setCheck(true);
        }

        siv_sim_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //3,获取原有的状态
                boolean isChecked = siv_sim_bound.isChecked();
                //4,将原有状态取反
                //5,状态设置给当前条目
                siv_sim_bound.setCheck(!isChecked);
                if (!isChecked) {
                    //检测运行时权限
                    if (ActivityCompat.checkSelfPermission(Setup2Activity.this,
                            Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
//                        //6,存储(序列卡号)
//                        //6.1获取sim卡序列号TelephoneManager
//                        TelephonyManager manager = (TelephonyManager)
//                                getSystemService(Context.TELEPHONY_SERVICE);
//                        //6.2获取sim卡的序列卡号
//                        String simSerialNumber = manager.getSimSerialNumber();
                        //6.3存储
                        String simSerialNumber = getSimSerialNumber();
                        Log.d(TAG, "onClick: simSerialNumber = " + simSerialNumber);
                        SpUtil.putString(getApplicationContext(), ConstantValue.SIM_NUMBER, simSerialNumber);
                    } else {
                        ActivityCompat.requestPermissions(Setup2Activity.this,
                                new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                    }
                } else {
                    //7,将存储序列卡号的节点,从sp中删除掉
                    SpUtil.remove(Setup2Activity.this, ConstantValue.SIM_NUMBER);
                }
            }
        });
    }

    public String getSimSerialNumber() {
        //6,存储(序列卡号)
        //6.1获取sim卡序列号TelephoneManager
        TelephonyManager manager = (TelephonyManager)
                getSystemService(Context.TELEPHONY_SERVICE);
        //6.2获取sim卡的序列卡号
        String simSerialNumber = manager.getSimSerialNumber();

        return simSerialNumber;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String simSerialNumber = getSimSerialNumber();
                if (!TextUtils.isEmpty(simSerialNumber)) {
                    Intent intent = new Intent(Setup2Activity.this, Setup3Activity.class);
                    startActivity(intent);
                    finish();

                    //开启平移动画
                    overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
                }
            } else {
                siv_sim_bound.setCheck(false);
                ToastUtil.show(this, "授权失败,未能成功绑定SIM卡");
            }
        }
    }
}
