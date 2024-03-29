package com.yongyong.media.player.en;

/**
 * @author myselyhero
 *
 * desc:播放器状态
 *
 * @// TODO: 2021/10/28
 */
public enum YyStatusEnum {

    /** 空闲 */
    STATUS_IDLE,
    /** 准备中 */
    STATUS_PREPARING,
    /** 缓冲中 */
    STATUS_BUFFERING,
    /** 缓冲结束 */
    STATUS_BUFFEEND,
    /** 停止 */
    STATUS_PAUSED,
    /** 播放中 */
    STATUS_PLAYING,
    /** 播放结束 */
    STATUS_COMPLETED,
    /** 错误 */
    STATUS_ERROR
}
