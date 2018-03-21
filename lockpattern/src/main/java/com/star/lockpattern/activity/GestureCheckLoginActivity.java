package com.star.lockpattern.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.star.lockpattern.R;
import com.star.lockpattern.util.LockPatternUtil;
import com.star.lockpattern.util.cache.ACache;
import com.star.lockpattern.util.constant.Constant;
import com.star.lockpattern.widget.LockPatternView;

import java.util.List;


/**
 * Created by Sym on 2015/12/24.
 * 手势登录界面
 */
public class GestureCheckLoginActivity extends Activity {

    private static final String TAG = "LoginGestureActivity";

//    @Bind(R.id.lockPatternView)
//    LockPatternView lockPatternView;
//    @Bind(R.id.messageTv)
//    TextView messageTv;
//    @Bind(R.id.forgetGestureBtn)
//    Button forgetGestureBtn;

    private ACache aCache;
    private static final long DELAYTIME = 600l;
//    private byte[] gesturePassword;
    private Intent mIntent;
    private int i = 0;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_login);
//        ButterKnife.bind(this);
        mIntent = new Intent();
        assignViews();
        this.init();
    }

    private TextView messageTv;
    private LockPatternView lockPatternView;
    private Button forgetGestureBtn;

    private void assignViews() {
        messageTv = (TextView) findViewById(R.id.messageTv);
        lockPatternView = (LockPatternView) findViewById(R.id.lockPatternView);
        forgetGestureBtn = (Button) findViewById(R.id.forgetGestureBtn);
    }

    private void init() {
        aCache = ACache.get(GestureCheckLoginActivity.this);
        //得到设置的密码
        mPassword = getIntent().getStringExtra("password");
        
        //得到当前用户的手势密码
//        gesturePassword = aCache.getAsBinary(Constant.GESTURE_PASSWORD);
        lockPatternView.setOnPatternListener(patternListener);
//        updateStatus(Status.DEFAULT, pattern);
    }

    private LockPatternView.OnPatternListener patternListener = new LockPatternView.OnPatternListener() {

        @Override
        public void onPatternStart() {
            i++;
            lockPatternView.removePostClearPatternRunnable();
        }

        @Override
        public void onPatternComplete(List<LockPatternView.Cell> pattern) {
            if (pattern != null) {//手势不为空 判断手势是否正确
                if (LockPatternUtil.checkPassword(pattern, mPassword)) {//正确
                    updateStatus(Status.CORRECT,pattern);
                } else {//错误
                    if (i < 3) {//错误小于3次
                        if (i == 1) {
                            updateStatus(Status.ERRORTWO, pattern);
                        } else {
                            updateStatus(Status.ERRORONE, pattern);
                        }
//                        updateStatus(Status.ERROR);
                    } else {//大于3次 跳转到登录界面
                        i = 0;
                        mIntent.putExtra("result", "GOTOLOGIN");
                        GestureCheckLoginActivity.this.setResult(GestureState.GESTURELOGIN_RESULT_GOTOLOGIN, mIntent);
                        GestureCheckLoginActivity.this.finish();
                    }
                }
            }
        }
    };

    /**
     * 更新状态
     *
     * @param status
     * @param pattern
     */
    private void updateStatus(Status status, List<LockPatternView.Cell> pattern) {
        messageTv.setText(status.strId);
        messageTv.setTextColor(getResources().getColor(status.colorId));
        switch (status) {
            case DEFAULT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case ERROR:
                lockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
                lockPatternView.postClearPatternRunnable(DELAYTIME);
                break;
            case ERRORONE://新增
                lockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
                lockPatternView.postClearPatternRunnable(DELAYTIME);
                break;
            case ERRORTWO://新增
                lockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
                lockPatternView.postClearPatternRunnable(DELAYTIME);
                break;
            case CORRECT:
                saveChosenPattern(pattern);
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                loginGestureSuccess();
                break;
        }
    }
    /**
     * 保存手势密码
     */
    private void saveChosenPattern(List<LockPatternView.Cell> cells) {
        byte[] bytes = LockPatternUtil.patternToHash(cells);
        aCache.put(Constant.GESTURE_PASSWORD, bytes);
    }
    /**
     * 手势登录成功（去首页）
     */
    private void loginGestureSuccess() {
//        Toast.makeText(GestureLoginActivity.this, "success", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(GestureLoginActivity.this, MainActivity.class);
//        startActivity(intent);
        mIntent.putExtra("result", "GOTOMAIN");
        setResult(GestureState.GESTURELOGIN_RESULT_GOTOMAIN, mIntent);
        this.finish();
    }

    /**
     * 忘记手势密码（去账号登录界面）
     */
//    @OnClick(R.id.forgetGestureBtn)
    public void forgetGesturePasswrod(View view) {
//        Intent intent = new Intent(GestureLoginActivity.this, CreateGestureActivity.class);
//        startActivity(intent);
        mIntent.putExtra("result", "GOTOLOGIN");
        setResult(GestureState.GESTURELOGIN_RESULT_GOTOLOGIN, mIntent);
        this.finish();
    }

    private enum Status {
        //默认的状态
        DEFAULT(R.string.gesture_default, R.color.grey_a5a5a5),
        //密码输入错误
        ERROR(R.string.gesture_error, R.color.red_f4333c),
        //密码错误剩余验证次数1次
        ERRORONE(R.string.gesture_error_one, R.color.red_f4333c),
        //密码错误剩余验证次数2次
        ERRORTWO(R.string.gesture_error_two, R.color.red_f4333c),
        //密码输入正确
        CORRECT(R.string.gesture_correct, R.color.grey_a5a5a5);

        private Status(int strId, int colorId) {
            this.strId = strId;
            this.colorId = colorId;
        }

        private int strId;
        private int colorId;
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
