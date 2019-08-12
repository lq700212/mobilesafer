package com.itheima.mobilesafer;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Desction:手机安全卫士
 * Author:ryan.lei
 * Date:2019-05-16 19:15
 */
public class MyApplication extends TinkerApplication {
    public MyApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "com.itheima.mobilesafer.MyApplicationLike",
                "com.tencent.tinker.loader.TinkerLoader", false);
    }
}