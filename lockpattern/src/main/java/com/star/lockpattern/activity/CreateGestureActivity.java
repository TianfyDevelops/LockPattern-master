package com.star.lockpattern.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.star.lockpattern.R;
import com.star.lockpattern.util.LockPatternUtil;
import com.star.lockpattern.util.cache.ACache;
import com.star.lockpattern.util.constant.Constant;
import com.star.lockpattern.widget.LockPatternIndicator;
import com.star.lockpattern.widget.LockPatternView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * create gesture activity
 * Created by Sym on 2015/12/23.
 * 创建手势界面
 */
public class CreateGestureActivity extends Activity {

    private List<LockPatternView.Cell> mChosenPattern = null;
    private ACache aCache;
    private static final long DELAYTIME = 600L;
    private static final String TAG = "CreateGestureActivity";
    private int i = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gesture);
        assignViews();
        this.init();
    }

    private LockPatternIndicator lockPatternIndicator;
    private TextView messageTv;
    private LockPatternView lockPatternView;
    private Button resetBtn;

    private void assignViews() {
        lockPatternIndicator = (LockPatternIndicator) findViewById(R.id.lockPatterIndicator);
        messageTv = (TextView) findViewById(R.id.messageTv);
        lockPatternView = (LockPatternView) findViewById(R.id.lockPatternView);
        resetBtn = (Button) findViewById(R.id.resetBtn);
    }


    private void init() {
        aCache = ACache.get(CreateGestureActivity.this);
        lockPatternView.setOnPatternListener(patternListener);
    }

    /**
     * 手势监听
     */
    private LockPatternView.OnPatternListener patternListener = new LockPatternView.OnPatternListener() {

        @Override
        public void onPatternStart() {
            lockPatternView.removePostClearPatternRunnable();
            //updateStatus(Status.DEFAULT, null);
            lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
        }

        @Override
        public void onPatternComplete(List<LockPatternView.Cell> pattern) {
            //Log.e(TAG, "--onPatternDetected--");
                if (mChosenPattern == null && pattern.size() >= 4) {//没有手势且绘制的点大于4
                    mChosenPattern = new ArrayList<>(pattern);
                    updateStatus(Status.CORRECT, pattern);
                } else if (mChosenPattern == null && pattern.size() < 4) {//没有手势且绘制的点小于4
                    updateStatus(Status.LESSERROR, pattern);
                } else if (mChosenPattern != null) {//有手势
                    if (mChosenPattern.equals(pattern)) {
                        updateStatus(Status.CONFIRMCORRECT, pattern);
                    } else {//手势绘制错误
                        updateStatus(Status.CONFIRMERROR, pattern);
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
        messageTv.setTextColor(getResources().getColor(status.colorId));
        messageTv.setText(status.strId);
        switch (status) {
            case DEFAULT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case CORRECT:
                updateLockPatternIndicator();
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case LESSERROR:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case CONFIRMERROR:
                lockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
                lockPatternView.postClearPatternRunnable(DELAYTIME);
                break;
            case CONFIRMCORRECT:
                saveChosenPattern(pattern);
                setLockPatternSuccess(pattern);
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
        }
    }

    /**
     * 更新 Indicator
     */
    private void updateLockPatternIndicator() {
        if (mChosenPattern == null)
            return;
        lockPatternIndicator.setIndicator(mChosenPattern);
    }

    /**
     * 重新设置手势
     */
//	@OnClick(R.id.resetBtn)
    public void resetLockPattern(View view) {
        mChosenPattern = null;
        lockPatternIndicator.setDefaultIndicator();
        updateStatus(Status.DEFAULT, null);
        lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
    }

    /**
     * 成功设置了手势密码(跳到首页)
     * @param pattern
     */
    private void setLockPatternSuccess(List<LockPatternView.Cell> pattern) {
//		Toast.makeText(this, "create gesture success", Toast.LENGTH_SHORT).show();
        StringBuilder builder=new StringBuilder();
        for (int j = 0; j < pattern.size(); j++) {
            LockPatternView.Cell cell = pattern.get(j);
            builder.append(cell.getNum(cell.getRow(),cell.getColumn()));
        }
        Intent mIntent = new Intent();
        mIntent.putExtra("result", builder.toString());
        setResult(GestureState.GESTURELOGIN_RESULT_GOTOCUSTOM, mIntent);
        this.finish();
    }

    /**
     * 保存手势密码
     */
    private void saveChosenPattern(List<LockPatternView.Cell> cells) {
        byte[] bytes = LockPatternUtil.patternToHash(cells);
        aCache.put(Constant.GESTURE_PASSWORD, bytes);
    }

    private enum Status {
        //默认的状态，刚开始的时候（初始化状态）
        DEFAULT(R.string.create_gesture_default, R.color.grey_a5a5a5),
        //第一次记录成功
        CORRECT(R.string.create_gesture_correct, R.color.grey_a5a5a5),
        //连接的点数小于4（二次确认的时候就不再提示连接的点数小于4，而是提示确认错误）
        LESSERROR(R.string.create_gesture_less_error, R.color.red_f4333c),
        //二次确认错误
        CONFIRMERROR(R.string.create_gesture_confirm_error, R.color.red_f4333c),
        //二次确认正确
        CONFIRMCORRECT(R.string.create_gesture_confirm_correct, R.color.grey_a5a5a5);

        private Status(int strId, int colorId) {
            this.strId = strId;
            this.colorId = colorId;
        }

        private int strId;
        private int colorId;
    }
}
