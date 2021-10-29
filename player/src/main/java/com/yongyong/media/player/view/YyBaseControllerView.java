package com.yongyong.media.player.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yongyong.media.player.en.YySpeedLevelEnum;
import com.yongyong.media.player.en.YyStatusEnum;
import com.yongyong.media.player.util.LuminanceUtils;
import com.yongyong.media.player.util.ScreenUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author myselyhero 
 * 
 * @desc: 播放器控制器
 * 
 * @// TODO: 2021/10/28
 */
public abstract class YyBaseControllerView extends FrameLayout implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener,
        View.OnTouchListener {

    private final String TAG = "YyBaseControllerView";

    private static final float STEP_PROGRESS = 6f;// 进度滑动时的步长
    private static final float STEP_VOLUME = 2f;// 音量滑动时的步长
    private static final float STEP_LIGHT = 2f;// 亮度滑动时的步长
    private static final int GESTURE_PROGRESS = 1;//进度
    private static final int GESTURE_VOLUME = 2;//音量
    private static final int GESTURE_BRIGHTNESS = 3;//亮度

    /**
     * 播放器
     */
    private YyMediaPLayerView mPLayerView;

    /**  */
    private GestureDetector mGestureDetector;
    /** 是否关闭手势 */
    private boolean isGesture;
    /** 是否全屏 */
    private boolean isFull;

    /** 多长时间后隐藏控制器 */
    protected int mControllerTime = 5000;
    /** 控制器隐藏线程 */
    protected Handler mHandler = new Handler(Looper.getMainLooper());
    protected Runnable mRunnable;

    /** 刷新进度 */
    protected int progressDefault = 1000;
    protected Timer progressTimer;

    public YyBaseControllerView(@NonNull Context context) {
        super(context);
        init();
    }

    public YyBaseControllerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public YyBaseControllerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     *
     */
    private void init(){
        mGestureDetector = new GestureDetector(getContext(), this);
        setOnTouchListener(this);

        int resId = getLayoutId();
        if (resId > 0){
            LayoutInflater.from(getContext()).inflate(resId,this);
            initView();
        }
    }

    /**
     * 是否可以点击
     * @param gesture
     */
    public void setGesture(boolean gesture) {
        isGesture = gesture;
    }

    /**
     * 绑定播放器视图
     * @param view
     */
    public void attachView(YyMediaPLayerView view){
        mPLayerView = view;
    }

    /**
     * 状态改变了
     * @param statusEnum
     */
    public void changeStatus(YyStatusEnum statusEnum){
        changeStatusListener(statusEnum);
    }

    /**
     * 释放
     */
    public void release(){
        if (mHandler != null && mRunnable != null)
            mHandler.removeCallbacks(mRunnable);
        stopProgress();
    }

    /* 抽象方法 */

    /**
     * 获取布局ID
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 调用此方法初始化子类控件
     */
    protected abstract void initView();

    /**
     * 单击
     * @param event
     */
    protected abstract void onClick(MotionEvent event);

    /**
     * 双击
     * @param event
     */
    protected abstract void onDblClick(MotionEvent event);

    /**
     * 更新缓冲值
     * @param buffer
     */
    protected abstract void bufferUpdate(int buffer);

    /**
     * 获取播放进度、控制器可见时自动回调，每秒一次（用于更新seekBar）
     * @param position
     */
    public abstract void currencyPosition(long position);

    /**
     *
     * @param statusEnum
     */
    protected abstract void changeStatusListener(YyStatusEnum statusEnum);

    /**
     * 是否正在播放
     * @return
     */
    protected boolean isPlayer(){
        return mPLayerView != null && mPLayerView.isPlaying();
    }

    /**
     * 调整播放速度
     * @param levelEnum
     */
    protected void setSpeed(YySpeedLevelEnum levelEnum){
        if (mPLayerView != null)
            mPLayerView.setSpeed(levelEnum);
    }

    /**
     * 是否静音
     * @return
     */
    protected boolean isMute(){
        return mPLayerView != null && mPLayerView.isMute();
    }

    /**
     * 设置静音
     * @param mute
     */
    protected void setMute(boolean mute){
        if (mPLayerView != null)
            mPLayerView.setMute(mute);
    }

    /**
     * 暂停
     */
    protected void pause(){
        if (mPLayerView != null){
            mPLayerView.onPause();
        }
    }

    /**
     * 播放
     */
    protected void start(){
        if (mPLayerView != null){
            mPLayerView.onStart();
        }
    }

    /**
     * 获取播放进度
     * @return
     */
    protected long getCurrentPosition(){
        return mPLayerView == null ? 0 : mPLayerView.getCurrentPosition();
    }

    /**
     * 视频总时长
     * @return
     */
    protected long getDuration(){
        return mPLayerView == null ? 0 : mPLayerView.getDuration();
    }

    /**
     * 调整进度
     * @param t
     */
    protected void seekTo(long t){
        if (mPLayerView != null)
            mPLayerView.seekTo((int) t);
    }

    /**
     * 开始计时刷新进度s
     */
    protected void startProgress(){
        stopProgress();

        /** 如果是拉流不进行刷新 */
        if (mPLayerView == null)
            return;

        progressTimer = new Timer();
        progressTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        currencyPosition(mPLayerView.getCurrentPosition());
                    }
                });
            }
        },0,progressDefault);
    }

    /**
     * 停止刷新进度的线程
     */
    protected void stopProgress(){
        if (progressTimer != null){
            progressTimer.cancel();
            progressTimer = null;
        }
    }

    /**
     * 转换毫秒数成“分、秒”，如“01:53”。若超过60分钟则显示“时、分、秒”，如“01:01:30
     * @param time
     * @return
     */
    protected String longTimeToString(long time) {
        int second = 1000;
        int minute = second * 60;
        int hour = minute * 60;

        long hourTime = (time) / hour;
        long minuteTime = (time - hourTime * hour) / minute;
        long secondTime = (time - hourTime * hour - minuteTime * minute) / second;

        String strHour = hourTime < 10 ? "0" + hourTime : "" + hourTime;
        String strMinute = minuteTime < 10 ? "0" + minuteTime : "" + minuteTime;
        String strSecond = secondTime < 10 ? "0" + secondTime : "" + secondTime;
        if (hourTime > 0) {
            return strHour + ":" + strMinute + ":" + strSecond;
        } else {
            return strMinute + ":" + strSecond;
        }
    }

    /**
     *
     */
    protected void showControllerAnim(View view){
        if (view.getVisibility() == View.VISIBLE)
            return;

        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(null);
    }

    /**
     *
     * @param view
     */
    protected void hideControllerAnim(View view){
        if (view.getVisibility() == View.GONE)
            return;

        view.animate()
                .alpha(0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }

    /* impl */

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        onClick(motionEvent);
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        onDblClick(motionEvent);
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return !isGesture;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float distanceX, float distanceY) {

        /*if (!isFull)
            return false;

        int flag;
        // 横向的距离变化大则调整进度，纵向的变化大则调整音量
        if (Math.abs(distanceX) >= Math.abs(distanceY)) {
            flag = GESTURE_PROGRESS;
        } else {
            //右边是音量,左边是亮度
            flag = (int)motionEvent.getX() > ScreenUtils.getScreenWidth(getContext()) / 2 ? GESTURE_VOLUME : GESTURE_BRIGHTNESS;
        }

        switch (flag){
            case GESTURE_PROGRESS:
                if (mPLayerView == null || !mPLayerView.isPlaying())
                    return false;
                //表示是横向滑动,可以添加快进
                // distanceX=lastScrollPositionX-currentScrollPositionX，因此为正时是快进
                if (Math.abs(distanceX) > Math.abs(distanceY)) {// 横向移动大于纵向移动
                    if (distanceX >= ScreenUtils.dip2px(getContext(), STEP_PROGRESS)) {// 快退，用步长控制改变速度，可微调
                        Log.e(TAG, "onScroll: 快退");
                        if (mPLayerView.getCurrentPosition() > 3 * 1000) {// 避免为负
                        } else {

                        }
                    } else if (distanceX <= -ScreenUtils.dip2px(getContext(), STEP_PROGRESS)) {// 快进
                        Log.e(TAG, "onScroll: 快进");
                        if (mPLayerView.getCurrentPosition() < mPLayerView.getDuration() - 5 * 1000) {// 避免超过总时长
                        }
                    }
                }
                break;
            case GESTURE_VOLUME:
                //右边是音量
                if (Math.abs(distanceY) > Math.abs(distanceX)) {// 纵向移动大于横向移动
                    if (distanceY >= ScreenUtils.dip2px(getContext(), STEP_VOLUME)) {// 音量调大,注意横屏时的坐标体系,尽管左上角是原点，但横向向上滑动时distanceY为正
                        Log.e(TAG, "onScroll: 加音量");
                    } else if (distanceY <= -ScreenUtils.dip2px(getContext(), STEP_VOLUME)) {// 音量调小
                        Log.e(TAG, "onScroll: 减音量");
                    }
                }
                break;
            case GESTURE_BRIGHTNESS:
                if (Math.abs(distanceY) > Math.abs(distanceX)) {// 纵向移动大于横向移动
                    // 亮度调大,注意横屏时的坐标体系,尽管左上角是原点，但横向向上滑动时distanceY为正
                    int mLight = LuminanceUtils.GetLightness((Activity) getContext());
                    if (mLight >= 0 && mLight <= 255) {
                        if (distanceY >= ScreenUtils.dip2px(getContext(), STEP_LIGHT)) {
                            Log.e(TAG, "onScroll: 增加亮度");
                            if (mLight > 245) {
                                LuminanceUtils.SetLightness((Activity) getContext(), 255);
                            } else {
                                LuminanceUtils.SetLightness((Activity) getContext(), mLight + 10);
                            }
                        } else if (distanceY <= -ScreenUtils.dip2px(getContext(), STEP_LIGHT)) {// 亮度调小
                            Log.e(TAG, "onScroll: 减小亮度");
                            if (mLight < 10) {
                                LuminanceUtils.SetLightness((Activity) getContext(), 0);
                            } else {
                                LuminanceUtils.SetLightness((Activity) getContext(), mLight - 10);
                            }
                        }
                    }
                }
                break;
        }*/
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return mGestureDetector.onTouchEvent(motionEvent);
    }
}
