package com.star.lockpattern;

import android.content.Intent;

import com.star.lockpattern.activity.CreateGestureActivity;
import com.star.lockpattern.activity.GestureLoginActivity;
import com.star.lockpattern.activity.GestureState;
import com.star.lockpattern.util.cache.ACache;
import com.star.lockpattern.util.constant.Constant;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONException;
import org.json.JSONObject;

import static com.star.lockpattern.activity.GestureState.GESTURELOGIN_REQUEST_CODE;
import static com.star.lockpattern.activity.GestureState.GESTURELOGIN_RESULT_GOTOCUSTOM;

/**
 * @name LockPattern-master
 * @class name：com.star.lockpattern
 * @class describe
 * @anthor tianfy
 * @time 2018/3/12 16:04
 * @change 
 * @chang time
 * @class describe
 */

public class APIModuleDemo extends UZModule {
    private ACache aCache;
    private UZModuleContext mUZModuleContext;

    public APIModuleDemo(UZWebView webView) {
        super(webView);
        aCache = ACache.get(getContext());
    }

    //打开手势登录界面
    public void jsmethod_openGestureLoginActivity(final UZModuleContext moduleContext) {
        this.mUZModuleContext = moduleContext;
        String gesturePassword = aCache.getAsString(Constant.GESTURE_PASSWORD);
        if (gesturePassword == null || "".equals(gesturePassword)) {
            Intent intent = new Intent(getContext(), CreateGestureActivity.class);
            startActivityForResult(intent, GestureState.GESTURELOGIN_REQUEST_CODE);
        } else {
            Intent intent = new Intent(getContext(), GestureLoginActivity.class);
            startActivityForResult(intent, GestureState.GESTURELOGIN_REQUEST_CODE);
        }
    }

    //打开创建手势界面
        public void jsmethod_openCreateGestureActivity(final UZModuleContext moduleContext) {
            this.mUZModuleContext = moduleContext;
            Intent intent = new Intent(getContext(), CreateGestureActivity.class);
            startActivityForResult(intent, GestureState.GESTURELOGIN_REQUEST_CODE);
        }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GESTURELOGIN_REQUEST_CODE:
                    try {
                        if (resultCode == GestureState.GESTURELOGIN_RESULT_GOTOMAIN) {//去主页
                            jumpActivity(data);
                        } else if (resultCode == GestureState.GESTURELOGIN_RESULT_GOTOLOGIN) {//去登录页
                            jumpActivity(data);
                        } else if (resultCode == GESTURELOGIN_RESULT_GOTOCUSTOM) {//去自定义界面
                            jumpActivity(data);
                        }
                        mUZModuleContext = null;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                break;
        }
    }

    private void jumpActivity(Intent data) throws JSONException {
        String result= data.getStringExtra("result");
        JSONObject ret = new JSONObject();
        ret.put("result", result);
        mUZModuleContext.success(ret, true);
    }

    @Override
    protected void onClean() {
        if (null != mUZModuleContext) {
            mUZModuleContext = null;
        }
    }
}
