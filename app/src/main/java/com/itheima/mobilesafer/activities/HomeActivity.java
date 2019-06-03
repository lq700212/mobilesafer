package com.itheima.mobilesafer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.mobilesafer.encrypt.Md5Util;
import com.itheima.mobilesafer.utils.ConstantValue;
import com.itheima.mobilesafer.utils.SpUtil;
import com.itheima.mobilesafer.utils.ToastUtil;

/**
 * Desction:手机安全卫士
 * Author:ryan.lei
 * Date:2019-05-16 17:22
 */
public class HomeActivity extends AppCompatActivity {

    private GridView gv_home;
    private String[] mTitleStrs;
    private int[] mDrawableIds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initUI();
        //初始化数据的方法
        initData();
    }

    private void initUI() {
        gv_home = (GridView) findViewById(R.id.gv_home);
    }

    private void initData() {
        //准备数据(文字(9组),图片(9张))
        mTitleStrs = new String[]{
                "手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"
        };

        mDrawableIds = new int[]{
                R.mipmap.home_safe, R.mipmap.home_callmsgsafe,
                R.mipmap.home_apps, R.mipmap.home_taskmanager,
                R.mipmap.home_netmanager, R.mipmap.home_trojan,
                R.mipmap.home_sysoptimize, R.mipmap.home_tools, R.mipmap.home_settings
        };

        //九宫格控件设置数据适配器(等同ListView数据适配器)
        gv_home.setAdapter(new MyAdapter());
        //注册九宫格单个条目点击事件
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //开启对话框
                        showDialog();
                        break;
                    case 8:
                        Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void showDialog() {
        //判断本地是否有存储密码(sp 字符串)
        String psd = SpUtil.getString(this, ConstantValue.MOBILE_SAFE_PSD, "");
        if (TextUtils.isEmpty(psd)) {
            //1,初始设置密码对话框
            showSetPsdDialog();
        } else {
            //2,确认密码对话框
            showConfirmPsdDialog();
        }
    }

    private void showSetPsdDialog() {
        //因为需要去自己定义对话框的展示样式,所以需要调用dialog.setView(view);
        //view是由自己编写的xml转换成的view对象xml----->view
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        View view = View.inflate(this, R.layout.dialog_set_psd, null);
        final EditText et_set_psd = (EditText) view.findViewById(R.id.et_set_psd);
        final EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String psd = et_set_psd.getText().toString();
                String confirmPsd = et_confirm_psd.getText().toString();
                if (!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(confirmPsd)) {
                    if (psd.equals(confirmPsd)) {
                        //进入应用手机防盗模块,开启一个新的activity
                        Intent intent = new Intent(HomeActivity.this, Setup1Activity.class);
                        startActivity(intent);
                        //跳转到新的界面以后需要去隐藏对话框
                        alertDialog.dismiss();
                        //密码加密存储,加盐
                        psd = Md5Util.encoder(psd + "com.itheima.mobilesafer");
                        SpUtil.putString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, psd);
                    } else {
                        et_set_psd.setText("");
                        et_confirm_psd.setText("");
                        ToastUtil.show(getApplicationContext(), "确认密码错误");
                    }
                } else {
                    //提示用户密码输入有为空的情况
                    ToastUtil.show(getApplicationContext(), "请输入密码");
                }
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setView(view, 0, 0, 0, 0);

        alertDialog.show();
    }

    private void showConfirmPsdDialog() {
        //因为需要去自己定义对话框的展示样式,所以需要调用dialog.setView(view);
        //view是由自己编写的xml转换成的view对象xml----->view
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        View view = View.inflate(this, R.layout.dialog_confirm_psd, null);
        //让对话框显示一个自己定义的对话框界面效果
        alertDialog.setView(view, 0, 0, 0, 0);
        alertDialog.show();

        final EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
        final String psd = SpUtil.getString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, "");
        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String confirmPsd = et_confirm_psd.getText().toString();
                confirmPsd = Md5Util.encoder(confirmPsd + "com.itheima.mobilesafer");
                if (!TextUtils.isEmpty(confirmPsd)) {
                    if (psd.equals(confirmPsd)) {
                        //进入应用手机防盗模块,开启一个新的activity
                        Intent intent = new Intent(HomeActivity.this, Setup1Activity.class);
                        startActivity(intent);
                        //跳转到新的界面以后需要去隐藏对话框
                        alertDialog.dismiss();
                    } else {
                        ToastUtil.show(getApplicationContext(), "确认密码错误");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入密码");
                }
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    class MyAdapter extends BaseAdapter {
        class ViewHolder {
            ImageView iv_icon;
            TextView tv_title;

            public ViewHolder(View view) {
                iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                tv_title = (TextView) view.findViewById(R.id.tv_title);
            }
        }

        @Override
        public int getCount() {
            //条目的总数	文字组数 == 图片张数
            return mTitleStrs.length;
        }

        @Override
        public Object getItem(int position) {
            return mTitleStrs[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();    //重新获取ViewHolder
            }

            viewHolder.iv_icon.setImageResource(mDrawableIds[position]);
            viewHolder.tv_title.setText(mTitleStrs[position]);

            return view;
        }
    }
}
