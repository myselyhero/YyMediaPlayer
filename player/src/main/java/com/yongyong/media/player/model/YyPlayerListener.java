package com.yongyong.media.player.model;

/**
 * @author myselyhero 
 *
 * desc:播放器监听
 *
 * @// TODO: 2021/10/28
 */
public interface YyPlayerListener {

    /**
     * 准备结束
     */
    void onPreparedEnd();

    /**
     * 缓冲值
     * @param buffer
     */
    void onBuffering(int buffer);

    /**
     * 缓存/缓冲结束
     * @param what
     * @param extra
     */
    void onInfo(int what, int extra);

    /**
     * 宽高改变了
     * @param width
     * @param height
     */
    void onSizeChanged(int width,int height);

    /**
     * 发生错误了
     */
    void onError();

    /**
     * 播放结束
     */
    void onCompletion();
}
