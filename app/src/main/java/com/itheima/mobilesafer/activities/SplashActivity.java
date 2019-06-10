package com.itheima.mobilesafer.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itheima.mobilesafer.utils.ConstantValue;
import com.itheima.mobilesafer.utils.ProgressDialogUtil;
import com.itheima.mobilesafer.utils.SpUtil;
import com.itheima.mobilesafer.utils.StreamUtil;
import com.itheima.mobilesafer.utils.ToastUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import io.reactivex.functions.Consumer;

/**
 * Desction:手机安全卫士
 * Author:ryan.lei
 * Date:2019-05-16 17:21
 */
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    /**
     * 更新版本的状态码
     */
    private static final int UPDATE_VERSION = 100;
    /**
     * 进入应用程序主界面状态码
     */
    private static final int ENTER_HOME = 101;
    /**
     * url地址出错状态码
     */
    private static final int URL_ERROR = 102;
    private static final int IO_ERROR = 103;
    private static final int JSON_ERROR = 104;
    private static final String SERVER_URL = "http://192.168.1.125:8080/update74.json";

    private RxPermissions mRxPermissions;

    private TextView tv_version_name;
    private int mLocalVersionCode;
    private String mVersionDesc;
    private String mDownloadUrl;
    private ProgressBar mProgressBar;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION:
                    //弹出对话框，提示用户更新
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    //进入应用程序主界面,activity跳转过程
                    enterHome();
                    break;
                case URL_ERROR:
                    ToastUtil.show(getApplicationContext(), "url异常");
                    enterHome();
                    break;
                case IO_ERROR:
                    ToastUtil.show(getApplicationContext(), "读取异常");
                    enterHome();
                    break;
                case JSON_ERROR:
                    ToastUtil.show(getApplicationContext(), "json解析异常");
                    enterHome();
                    break;
                default:
                    break;
            }
        }
    };

    private void showUpdateDialog() {
        new AlertDialog.Builder(SplashActivity.this)
                .setTitle("版本更新")
                .setIcon(R.mipmap.alert_icon)
                .setMessage(mVersionDesc)
                .setCancelable(false)
                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //下载apk,apk链接地址,downloadUrl
                        downloadApk();
                    }
                })
                .setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //取消对话框,进入主界面
                        enterHome();
                    }
                })
                .show();
    }

    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //在开启一个新的界面后,将导航界面关闭(导航界面只可见一次)
        finish();
    }

    private void downloadApk() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mProgressBar = new ProgressBar(SplashActivity.this);
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
            //mDownloadUrl为JSON从服务器端解析出来的下载地址
            RequestParams requestParams = new RequestParams(mDownloadUrl);
            //为RequestParams设置文件下载后的保存路径
            requestParams.setSaveFilePath(path);
            //下载完成后自动为文件命名
            requestParams.setAutoRename(true);
            x.http().get(requestParams, new Callback.ProgressCallback<File>() {
                @Override
                public void onSuccess(File file) {
                    Log.i(TAG, "下载成功 ");
                    //隐藏对话框
                    ProgressDialogUtil.dismiss();
                    installApk(file);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.i(TAG, "下载失败");
                    //隐藏对话框
                    ProgressDialogUtil.dismiss();
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    Log.i(TAG, "取消下载");
                    //隐藏对话框
                    ProgressDialogUtil.dismiss();
                }

                @Override
                public void onFinished() {
                    Log.i(TAG, "结束下载");
                    //隐藏对话框
                    ProgressDialogUtil.dismiss();
                }

                @Override
                public void onWaiting() {
                    //网络请求开始的时候调用
                    Log.i(TAG, "等待下载");
                    //显示对话框
                    ProgressDialogUtil.showProgressDialog(SplashActivity.this);
                }

                @Override
                public void onStarted() {
                    //下载的时候不断回调的方法
                    Log.i(TAG, "开始下载");
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    //当前的下载进度和文件总大小
                    Log.i(TAG, "正在下载......");
                }
            });
        }
    }

    private void installApk(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(SplashActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 0:
                enterHome();
                break;
            default:
                enterHome();
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //检测权限
        checkRequestPermissions();
    }

    private void checkRequestPermissions() {
        mRxPermissions = new RxPermissions(SplashActivity.this);
        mRxPermissions.setLogging(true);
        mRxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CONTACTS)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        Log.i(TAG, "accept: " + aBoolean);
                        if (aBoolean) {
                            //初始化UI
                            initUI();
                            //初始化数据
                            initData();
                        } else {
                            mHandler.sendEmptyMessage(ENTER_HOME);
                        }
                    }
                });
    }

    /**
     * 初始化UI方法
     */
    private void initUI() {
        tv_version_name = (TextView) findViewById(R.id.tv_version_name);
    }

    private void initData() {
        boolean open_update = SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false);
        //1.应用版本名称
        tv_version_name.setText("版本名称:" + getVersionName());
        //2.获取本地versionCode
        mLocalVersionCode = getVersionCode();
        if (open_update) {
            //3,获取服务器版本号(客户端发请求,服务端给响应,(json,xml))
            //http://www.oxxx.com/update74.json?key=value  返回200 请求成功,流的方式将数据读取下来
            //json中内容包含:
            /**
             * 更新版本的版本名称
             * 新版本的描述信息
             * 服务器版本号
             * 新版本apk下载地址
             */
            checkVersion();
        } else {
            //在发送消息4秒后去处理,ENTER_HOME状态码指向的消息
            mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
        }
    }

    /**
     * 检测版本号
     */
    private void checkVersion() {
        new Thread() {
            @Override
            public void run() {
                //发送请求获取数据，参数为请求json的链接地址
                //http://192.168.13.99:8080/update74.json	测试阶段不是最优
                // 仅限于模拟器访问电脑tomcat
                Message msg = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {
                    //1.封装url地址
                    //update74.json放在apach/webapps/ROOT
                    URL url = new URL(SERVER_URL);
                    //2.开启一个链接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //3.设置常见请求参数(请求头)
                    //请求超时
                    connection.setConnectTimeout(2000);
                    //读取超时
                    connection.setReadTimeout(2000);

                    //默认就是get请求方式
//                    connection.setRequestMethod("POST");
                    //4.获取请求成功响应码
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        //5.以流的形式，将数据获取下来
                        InputStream is = connection.getInputStream();
                        //6.将流转换为字符串(工具类封装)
                        String json = StreamUtil.streamToString(is);
                        Log.i(TAG, json);
                        //7.json解析
                        JSONObject jsonObject = new JSONObject(json);

                        //debug调试，解决问题
                        String versionName = jsonObject.getString("version_name");
                        mVersionDesc = jsonObject.getString("description");
                        String versionCode = jsonObject.getString("version_code");
                        mDownloadUrl = jsonObject.getString("download_url");

                        //日志打印
                        Log.i(TAG, versionName);
                        Log.i(TAG, mVersionDesc);
                        Log.i(TAG, versionCode);
                        Log.i(TAG, mDownloadUrl);

                        //8.比对版本号(服务器版本号>本地版本号，提示用户更新)
                        if (mLocalVersionCode < Integer.parseInt(versionCode)) {
                            //提示用户更新,弹出对话框(UI),消息机制
                            msg.what = UPDATE_VERSION;
                        } else {
                            //进入应用程序主界面
                            msg.what = ENTER_HOME;
                        }
                    } else {
                        Log.i(TAG, "网络请求失败responseCode = " + responseCode);
                        msg.what = ENTER_HOME;
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    msg.what = URL_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = IO_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = JSON_ERROR;
                } finally {
                    //指定睡眠时间，请求网络的时长超过4秒则不做处理
                    //请求网络的时长小于4秒，强制让其睡眠满4秒钟
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime < 4) {
                        mHandler.sendEmptyMessageDelayed(msg.what, 4000 - (endTime - startTime));
                    } else {
                        mHandler.sendEmptyMessage(msg.what);
                    }
                }
            }
        }.start();
    }

    private String getVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    private int getVersionCode() {
        return BuildConfig.VERSION_CODE;
    }
}
