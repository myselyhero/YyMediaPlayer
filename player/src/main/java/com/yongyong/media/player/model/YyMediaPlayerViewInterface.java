package com.yongyong.media.player.model;

import android.graphics.Bitmap;

import com.yongyong.media.player.en.YyRatioEnum;
import com.yongyong.media.player.en.YySpeedLevelEnum;
import com.yongyong.media.player.view.YyBaseControllerView;

/**
 *
 */
public interface YyMediaPlayerViewInterface {

    /**
     * 数据源
     *
     * @param url 播放地址
     */
    void setDataSource(String url);

    /**
     * 设置控制器
     */
    void setControllerView(YyBaseControllerView controllerView);

    /**
     *
     * @return
     */
    YyBaseControllerView getControllerView();

    /**
     * 设置渲染视图
     * @param render
     */
    void setRender(YyRenderInterface render);

    /**
     * 设置比例
     */
    void setRatio(YyRatioEnum ratioEnum);

    /**
     * 设置播放速度
     * @param levelEnum
     */
    void setSpeed(YySpeedLevelEnum levelEnum);

    /**
     * 获取当前进度
     * @return
     */
    long getCurrentPosition();

    /**
     * 调整进度
     * @param t
     */
    void  seekTo(int t);

    /**
     * 获取视频时长
     * @return
     */
    long getDuration();

    /**
     * 是否静音
     */
    void setMute(boolean voice);
    boolean isMute();

    /**
     * 释放获取音频焦点
     */
    void setFocus(boolean focus);
    boolean isFocus();

    /**
     * 是否循环播放
     */
    boolean isLooping();

    /**
     *
     * @param looping
     */
    void setLooping(boolean looping);

    /**
     * 是否播放
     */
    boolean isPlaying();

    /**
     * 视频截图
     * @return
     */
    Bitmap capture();

    /**
     * 播放
     */
    void onStart();

    /**
     * 暂停
     */
    void onPause();

    /**
     * 释放
     */
    void onRelease();
}
