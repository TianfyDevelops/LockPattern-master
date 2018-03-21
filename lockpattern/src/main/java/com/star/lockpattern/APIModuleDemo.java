package com.star.lockpattern;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.star.lockpattern.activity.CreateGestureActivity;
import com.star.lockpattern.activity.GestureCheckLoginActivity;
import com.star.lockpattern.activity.GestureLoginActivity;
import com.star.lockpattern.activity.GestureState;
import com.star.lockpattern.activity.ResetGestureActivity;
import com.star.lockpattern.util.cache.ACache;
import com.star.lockpattern.util.constant.Constant;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;
import static com.star.lockpattern.activity.GestureState.GESTURELOGIN_REQUEST_CODE;
import static com.star.lockpattern.activity.GestureState.GESTURELOGIN_RESULT_GOTOCUSTOM;
import static com.uzmap.pkg.uzcore.UZResourcesIDFinder.array;

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

    //打开手势登录界面(用于第一次登陆)
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
    //打开重置手势界面(用于用户修改用户名密码之后调用)
    public void jsmethod_openCreateGestureActivity(final UZModuleContext moduleContext){
        this.mUZModuleContext = moduleContext;
        Intent intent = new Intent(getContext(), CreateGestureActivity.class);
        startActivityForResult(intent, GestureState.GESTURELOGIN_REQUEST_CODE);
    }
    //打开修改手势界面(用于进入应用修改手势)
    public void jsmethod_openResetGestureActivity(final UZModuleContext moduleContext) {
        this.mUZModuleContext = moduleContext;
        Intent intent = new Intent(getContext(), ResetGestureActivity.class);
        startActivityForResult(intent, GestureState.GESTURELOGIN_REQUEST_CODE);
    }

    //打开验证手势界面(用于有手势密码的情况下传递进来checkpassword,直接开启手势解锁界面)
    public void jsmethod_openCheckPwdGestureActivity(final UZModuleContext moduleContext){
        this.mUZModuleContext=moduleContext;
        String password = mUZModuleContext.optString("checkpassword");
        Intent intent=new Intent(getContext(), GestureCheckLoginActivity.class);
        intent.putExtra("password",password);
        startActivityForResult(intent,GestureState.GESTURELOGIN_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == GestureState.GESTURELOGIN_REQUEST_CODE && resultCode == GestureState.GESTURELOGIN_RESULT_GOTOMAIN) {//去主页
                jumpActivity(data);
            } else if (requestCode == GestureState.GESTURELOGIN_REQUEST_CODE && resultCode == GestureState.GESTURELOGIN_RESULT_GOTOLOGIN) {//去登录页
                jumpActivity(data);
            } else if (requestCode == GestureState.GESTURELOGIN_REQUEST_CODE && resultCode == GESTURELOGIN_RESULT_GOTOCUSTOM) {//去自定义界面
                jumpActivity(data);
            }
            mUZModuleContext = null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void jumpActivity(Intent data) throws JSONException {
        String result = data.getStringExtra("result");
        JSONObject ret = new JSONObject();
        ret.put("result", result);
        ret.put("status", true);
        ret.put("msg", "回掉成功");
        mUZModuleContext.success(ret, true);
    }

    @Override
    protected void onClean() {
        if (null != mUZModuleContext) {
            mUZModuleContext = null;
        }
    }
}
