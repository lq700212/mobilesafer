package com.itheima.mobilesafer.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.itheima.mobilesafer.utils.ConstantValue;
import com.itheima.mobilesafer.utils.SpUtil;
import com.itheima.mobilesafer.utils.ToastUtil;

public class Setup3Activity extends BaseSetupActivity {

    public static final String TAG = "Setup3Activity";

    private EditText et_phone_number;
    private Button bt_select_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        initUI();
        initData();
    }

    @Override
    protected void showNextPage() {
        //点击按钮以后,需要获取输入框中的联系人,再做下一页操作
        String phone = et_phone_number.getText().toString();
        if (!TextUtils.isEmpty(phone)) {
            Intent intent = new Intent(Setup3Activity.this, Setup4Activity.class);
            startActivity(intent);
            finish();

            //如果现在是输入电话号码,则需要去保存
            SpUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);

            //开启平移动画
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            ToastUtil.show(this, "请设置安全号码");
        }
    }

    @Override
    protected void showPrePage() {
        Intent intent = new Intent(Setup3Activity.this, Setup2Activity.class);
        startActivity(intent);
        finish();

        //开启平移动画
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    private void initUI() {
        et_phone_number = (EditText) findViewById(R.id.et_phone_number);
        bt_select_number = (Button) findViewById(R.id.bt_select_number);
    }

    private void initData() {
        //获取联系人电话号码回显过程
        String phone = SpUtil.getString(this, ConstantValue.CONTACT_PHONE, "");
        et_phone_number.setText(phone);

        bt_select_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setup3Activity.this, ContactListActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    //1,返回到当前界面的时候,接受结果的方法
                    String name = data.getStringExtra("name");
                    String phone = data.getStringExtra("phone");

                    //2,将特殊字符过滤(中划线转换成空字符串)
                    phone = phone.replace("-", "").replace(" ", "").trim();

                    Log.d(TAG, "onActivityResult: name = " + name + ", phone = " + phone);

                    et_phone_number.setText(phone);

                    //3,存储联系人至sp中
                    SpUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);
                }
                break;

            default:

                break;
        }
    }
}
