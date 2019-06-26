package com.itheima.mobilesafer.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.itheima.mobilesafer.engine.AddressDao;

public class QueryAddressActivity extends AppCompatActivity {

    private EditText et_phone;
    private Button bt_query;
    private TextView tv_query_result;
    private String mAddress;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    tv_query_result.setText(mAddress);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_address);

        initUI();
        initData();
    }

    private void initUI() {
        et_phone = (EditText) findViewById(R.id.et_phone);
        bt_query = (Button) findViewById(R.id.bt_query);
        tv_query_result = (TextView) findViewById(R.id.tv_query_result);
    }

    private void initData() {
        //1,点查询功能,注册按钮的点击事件
        bt_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_phone.getText().toString();
                if (!phone.isEmpty()) {
                    //2,查询是耗时操作,开启子线程
                    query(phone);
                } else {
                    //抖动效果
                    Animation shake = AnimationUtils.loadAnimation(
                            getApplicationContext(), R.anim.shake);

                    et_phone.startAnimation(shake);

                    //手机振动效果(vibrator振动)
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    //振动毫秒值
//                    vibrator.vibrate(2000);
                    //规律振动(振动规则(不振动时间，振动时间，不振动时间，振动时间......),重复次数)
                    long[] pattern = new long[]{2000, 5000, 2000, 5000};
                    vibrator.vibrate(pattern, -1);
                }
            }
        });

        //实时查询(监听输入框文本变化)
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = et_phone.getText().toString();
                query(phone);
            }
        });
    }

    /**
     * 耗时操作
     * 获取电话号码归属地
     *
     * @param phone 查询电话号码
     */
    private void query(final String phone) {
        new Thread() {
            public void run() {
                mAddress = AddressDao.getAddress(phone);
                //3.消息机制，告知主线程查询结束，可以去使用查询结果
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }
}
