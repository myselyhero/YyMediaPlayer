package com.yongyong.media.player.model;

import android.graphics.Bitmap;
import android.view.View;

import com.yongyong.media.player.en.YyRatioEnum;

/**
 * @author myselyhero 
 * 
 * @dssc: 渲染接口
 * 
 * @// TODO: 2021/10/28
 */
public interface YyRenderInterface {

    /**
     * 绑定播放器
     * @param playerInterface
     */
    void attach(YyMediaPlayerInterface playerInterface);

    /**
     * 设置视频宽高
     * @param width
     * @param height
     */
    void setVideoSize(int width,int height);

    /**
     * 设置视频旋转
     * @param degree
     */
    void setDegree(int degree);

    /**
     * 设置视频比例
     * @param ratioEnum
     */
    void setRatio(YyRatioEnum ratioEnum);

    /**
     *
     * @return
     */
    View getView();

    /**
     * 视频截图
     * @return
     */
    Bitmap capture();

    /**
     * 释放
     */
    void release();
}
