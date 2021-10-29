package com.yongyong.media.player.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yongyong.media.player.YyMediaPlayer;
import com.yongyong.media.player.en.YyRatioEnum;
import com.yongyong.media.player.en.YySpeedLevelEnum;
import com.yongyong.media.player.en.YyStatusEnum;
import com.yongyong.media.player.model.YyMediaPlayerInterface;
import com.yongyong.media.player.model.YyMediaPlayerViewInterface;
import com.yongyong.media.player.model.YyPlayerListener;
import com.yongyong.media.player.model.YyRenderInterface;
import com.yongyong.media.player.util.YyAudioFocusManager;

/**
 *
 * @author myselyhero 
 * 
 * @desc: 播放器视图
 *
 * 具体方法请看{@link YyMediaPlayerViewInterface}
 * 
 * @// TODO: 2021/10/28
 */
public class YyMediaPLayerView extends FrameLayout implements YyMediaPlayerViewInterface {

    private final String TAG = YyMediaPLayerView.class.getSimpleName();

    /** 开始渲染视频画面 */
    public static final int MEDIA_INFO_VIDEO_RENDERING_START = 3;
    /** 缓冲开始 */
    public static final int MEDIA_INFO_BUFFERING_START = 701;
    /** 缓冲结束 */
    public static final int MEDIA_INFO_BUFFERING_END = 702;
    /** 视频旋转信息 */
    public static final int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;

    /** 容器 */
    private FrameLayout mFrameLayout;

    //播放器实例
    private YyMediaPlayerInterface mMediaPLayer;

    //渲染
    private YyRenderInterface mRenderInterface;

    //控制器
    private YyBaseControllerView mControllerView;

    /** 数据源 */
    private String mDataSource;

    /** 状态 */
    private YyStatusEnum mStatusEnum = YyStatusEnum.STATUS_IDLE;

    /** 尺寸 */
    private YyRatioEnum mRatioEnum = YyRatioEnum.RATIO_DEFAULT;

    //是否静音 true音量为0
    private boolean isMute;
    //是否获取音频焦点
    private boolean isFocus = true;
    private YyAudioFocusManager mFocusManager;
    //循环播放
    private boolean isLooping = false;
    //播放进度
    private long mCurrentPosition;

    public YyMediaPLayerView(@NonNull Context context) {
        super(context);
        init();
    }

    public YyMediaPLayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public YyMediaPLayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        /** 初始化容器 */
        mFrameLayout = new FrameLayout(getContext());
        mFrameLayout.setBackgroundColor(Color.TRANSPARENT);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mFrameLayout, params);
    }

    @Override
    public void setDataSource(String url) {
        mDataSource = url;
    }

    @Override
    public void setControllerView(YyBaseControllerView controllerView) {
        removeControllerView();
        mControllerView = controllerView;
        if (mControllerView != null){
            mControllerView.attachView(this);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mFrameLayout.addView(mControllerView, params);
        }
    }

    @Override
    public YyBaseControllerView getControllerView() {
        return mControllerView;
    }

    @Override
    public void setRender(YyRenderInterface render) {
        removeRenderView();
        mRenderInterface = render;
        if (mRenderInterface != null){
            mRenderInterface.attach(mMediaPLayer);
            mRenderInterface.setRatio(mRatioEnum);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER);
            mFrameLayout.addView(mRenderInterface.getView(), 0, params);
        }
    }

    @Override
    public void setRatio(YyRatioEnum ratioEnum) {
        mRatioEnum = ratioEnum;
        if (mRenderInterface != null) {
            mRenderInterface.setRatio(ratioEnum);
        }
    }

    @Override
    public void setSpeed(YySpeedLevelEnum levelEnum) {
        if (mMediaPLayer != null)
            mMediaPLayer.setSpeed(levelEnum);
    }

    @Override
    public long getCurrentPosition() {
        return mMediaPLayer == null ? 0 : mMediaPLayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int t) {
        if (mMediaPLayer != null)
            mMediaPLayer.seekTo(t);
    }

    @Override
    public long getDuration() {
        return mMediaPLayer == null ? 0 : mMediaPLayer.getDuration();
    }

    @Override
    public boolean isMute() {
        return isMute;
    }

    @Override
    public void setMute(boolean voice) {
        isMute = voice;
        if (mMediaPLayer != null){
            float volume = isMute ? 0.0f : 1.0f;
            mMediaPLayer.setVolume(volume,volume);
        }
    }

    @Override
    public void setFocus(boolean focus) {
        isFocus = focus;
        /** 设置了取消并且如果以获取则释放 */
        if (!isFocus() && mFocusManager != null){
            mFocusManager.abandonFocus();
        }
    }

    @Override
    public boolean isFocus() {
        return isFocus;
    }

    @Override
    public boolean isLooping() {
        return mMediaPLayer != null && mMediaPLayer.isLooping();
    }

    @Override
    public void setLooping(boolean looping) {
        isLooping = looping;
        if (mMediaPLayer != null)
            mMediaPLayer.setLooping(isLooping);
    }

    @Override
    public boolean isPlaying() {
        return mMediaPLayer != null && mMediaPLayer.isPlayer();
    }

    @Override
    public Bitmap capture() {
        return mRenderInterface != null ? mRenderInterface.capture() : null;
    }

    @Override
    public void onStart() throws IllegalStateException {
        if (TextUtils.isEmpty(mDataSource)){
            throw new IllegalStateException("url cannot be empty!!!");
        }

        switch (mStatusEnum){
            case STATUS_IDLE:
            case STATUS_ERROR:
                initMediaPlayer();
                changeStatus(YyStatusEnum.STATUS_PREPARING);
                break;
            case STATUS_PAUSED:
            case STATUS_PLAYING:
            case STATUS_PREPARING:
                if (isPlaying())
                    return;
                initAudioManager();
                if (mCurrentPosition > 0)
                    mMediaPLayer.seekTo((int) mCurrentPosition);
                mMediaPLayer.start();
                changeStatus(YyStatusEnum.STATUS_PLAYING);
                break;
        }
    }

    @Override
    public void onPause() {
        if (mMediaPLayer == null)
            return;
        if (mStatusEnum != YyStatusEnum.STATUS_PLAYING)
            return;

        mCurrentPosition = mMediaPLayer.getCurrentPosition();
        mMediaPLayer.pause();
        changeStatus(YyStatusEnum.STATUS_PAUSED);
        releaseAudioManager();
    }

    @Override
    public void onRelease() {
        if (mStatusEnum == YyStatusEnum.STATUS_IDLE)
            return;
        if (mMediaPLayer != null) {
            mMediaPLayer.reset();
            mMediaPLayer.release();
            mMediaPLayer.addPlayerListener(null);
            mMediaPLayer = null;
        }
        /** 释放TextureView */
        removeRenderView();
        /** 关闭AudioFocus监听 */
        releaseAudioManager();
        /** 重置播放进度 */
        mCurrentPosition = 0;
        /** 切换转态 */
        changeStatus(YyStatusEnum.STATUS_IDLE);
    }

    /* 内部Api */

    /**
     *
     */
    private void initMediaPlayer(){
        mMediaPLayer = new YyMediaPlayer();

        mMediaPLayer.addPlayerListener(mPlayerListener);
        mMediaPLayer.init(getContext());
        mMediaPLayer.setLooping(isLooping);
        mMediaPLayer.setDataSource(mDataSource);
        setMute(isMute);
        mMediaPLayer.prepareAsync();
    }

    /**
     *
     */
    private void removeControllerView(){
        if (mControllerView != null) {
            mFrameLayout.removeView(mControllerView);
            mControllerView.release();
            mControllerView = null;
        }
    }

    /**
     *
     */
    private void removeRenderView(){
        if (mRenderInterface != null) {
            mFrameLayout.removeView(mRenderInterface.getView());
            mRenderInterface.release();
            mRenderInterface = null;
        }
    }

    /**
     *
     */
    private void initRenderView(){
        removeRenderView();
        mRenderInterface = new YyTextureView(getContext());

        mRenderInterface.attach(mMediaPLayer);
        mRenderInterface.setRatio(mRatioEnum);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        mFrameLayout.addView(mRenderInterface.getView(), 0, params);
    }

    /**
     * 初始化音频焦点
     */
    private void initAudioManager(){
        /** 已禁用或已静音则不获取 */
        if (!isFocus() || isMute())
            return;
        if (mFocusManager == null) {
            mFocusManager = new YyAudioFocusManager(getContext(), mFocusListener);
        }
        mFocusManager.requestFocus();
    }

    /**
     * 释放音频焦点
     */
    private void releaseAudioManager(){
        if (mFocusManager != null){
            mFocusManager.abandonFocus();
            mFocusManager = null;
        }
    }

    /**
     *
     * @param statusEnum
     */
    public void changeStatus(YyStatusEnum statusEnum){
        mStatusEnum = statusEnum;
        switch (mStatusEnum){
            case STATUS_PLAYING:
                mFrameLayout.setKeepScreenOn(true);
                break;
            case STATUS_IDLE:
            case STATUS_PAUSED:
            case STATUS_COMPLETED:
            case STATUS_ERROR:
                mFrameLayout.setKeepScreenOn(false);
                break;
        }
        if (mControllerView != null)
            mControllerView.changeStatus(mStatusEnum);
    }

    /**
     * 播放器回调
     */
    private final YyPlayerListener mPlayerListener = new YyPlayerListener() {
        @Override
        public void onPreparedEnd() {
            initRenderView();
            initAudioManager();
            changeStatus(YyStatusEnum.STATUS_PLAYING);
            mMediaPLayer.start();
        }

        @Override
        public void onBuffering(int buffer) {
            if (mControllerView != null)
                mControllerView.bufferUpdate(buffer);
        }

        @Override
        public void onInfo(int what, int extra) {
            switch (what) {
                case MEDIA_INFO_BUFFERING_START://缓冲
                    changeStatus(YyStatusEnum.STATUS_BUFFERING);
                    break;
                case MEDIA_INFO_BUFFERING_END://缓冲结束
                    changeStatus(YyStatusEnum.STATUS_BUFFEEND);
                    break;
                case MEDIA_INFO_VIDEO_RENDERING_START: // 视频开始渲染
                    changeStatus(YyStatusEnum.STATUS_PLAYING);
                    if (getWindowVisibility() != VISIBLE)
                        onPause();
                    break;
                case MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                    if (mRenderInterface != null)
                        mRenderInterface.setDegree(extra);
                    break;
            }
        }

        @Override
        public void onSizeChanged(int width, int height) {
            if (mRenderInterface != null) {
                mRenderInterface.setVideoSize(width, height);
            }
        }

        @Override
        public void onError() {
            changeStatus(YyStatusEnum.STATUS_ERROR);
        }

        @Override
        public void onCompletion() {
            mCurrentPosition = 0;
            changeStatus(YyStatusEnum.STATUS_COMPLETED);
        }
    };

    /**
     *
     */
    private final YyAudioFocusManager.YyAudioFocusListener mFocusListener = new YyAudioFocusManager.YyAudioFocusListener() {
        @Override
        public void onAcquire() {
            post(new Runnable() {
                @Override
                public void run() {
                    onStart();
                }
            });
            /**
             * 已禁音则不恢复音量
             */
            if (!isMute() && mMediaPLayer != null){
                mMediaPLayer.setVolume(1f,1f);
            }
        }

        @Override
        public void onLose() {
            post(new Runnable() {
                @Override
                public void run() {
                    onPause();
                }
            });
        }

        @Override
        public void onFlat() {
            if (!isMute() && mMediaPLayer != null){
                mMediaPLayer.setVolume(0.1f,0.1f);
            }
        }
    };
}
