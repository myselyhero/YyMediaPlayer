package com.yongyong.media.player.model;

import android.content.Context;
import android.view.Surface;

import com.yongyong.media.player.en.YySpeedLevelEnum;

/**
 *
 * @author myselyhero
 *
 * @desc: 播放器接口
 *
 * @// TODO: 2021/10/28
 */
public interface YyMediaPlayerInterface {

    /**
     * 初始化
     * @param context
     */
    void init(Context context);


    /**
     * 设置数据源
     * @param url
     */
    void setDataSource(String url);

    /**
     * 异步准备 如使用异步方法需在准备结束的回调中进行播放否则播放失败
     */
    void prepareAsync();

    /**
     * 准备
     */
    void prepare();

    /**
     * 开始播放
     */
    void start();

    /**
     * 暂停
     */
    void pause();

    /**
     * 停止
     */
    void stop();

    /**
     * 重置
     */
    void reset();

    /**
     * 释放
     */
    void release();

    /**
     * 释放在播放
     * @return
     */
    boolean isPlayer();

    /**
     * 设置进度
     * @param t
     */
    void seekTo(int t);

    /**
     * 当前播放位置
     * @return
     */
    long getCurrentPosition();

    /**
     * 总时长
     * @return
     */
    long getDuration();

    /**
     * 渲染视图
     * @param surface
     */
    void setSurface(Surface surface);

    /**
     * 音量
     * @param l
     * @param r
     */
    void setVolume(float l,float r);

    /**
     * 循环播放
     * @param looping
     */
    void setLooping(boolean looping);
    boolean isLooping();

    /**
     * 播放速度
     * @param speed
     */
    void setSpeed(YySpeedLevelEnum speed);

    /**
     * 视频宽高
     * @return
     */
    int[] getVideoSize();

    /**
     * 设置播放器监听
     * @param listener
     */
    void addPlayerListener(YyPlayerListener listener);
}
