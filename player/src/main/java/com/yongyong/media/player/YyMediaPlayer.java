package com.yongyong.media.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import com.yongyong.media.player.en.YySpeedLevelEnum;
import com.yongyong.media.player.model.YyMediaPlayerInterface;
import com.yongyong.media.player.model.YyPlayerListener;

import java.io.IOException;

/**
 *
 * @author myselyhero
 *
 * @desc: 系统播放器
 *
 * {@link YyMediaPlayerInterface}
 *
 * @// TODO: 2021/10/28
 */
public class YyMediaPlayer implements YyMediaPlayerInterface {

    private Context mContext;

    private MediaPlayer mMediaPlayer;

    private YyPlayerListener mPlayerListener;

    @Override
    public void init(Context context) {
        mContext = context;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //监听
        mMediaPlayer.setOnBufferingUpdateListener(bufferingUpdateListener);
        mMediaPlayer.setOnCompletionListener(completionListener);
        mMediaPlayer.setOnErrorListener(errorListener);
        mMediaPlayer.setOnInfoListener(infoListener);
        mMediaPlayer.setOnPreparedListener(preparedListener);
        mMediaPlayer.setOnVideoSizeChangedListener(videoSizeChangedListener);
    }

    @Override
    public void setDataSource(String url) {
        if (mMediaPlayer == null || TextUtils.isEmpty(url))
            return;
        try {
            mMediaPlayer.setDataSource(mContext, Uri.parse(url));
        } catch (IOException e) {
            e.printStackTrace();
            if (mPlayerListener != null)
                mPlayerListener.onError();
        }
    }

    @Override
    public void prepareAsync() {
        if (mMediaPlayer == null)
            return;
        try{
            mMediaPlayer.prepareAsync();
        }catch (IllegalStateException e){
            e.printStackTrace();
            if (mPlayerListener != null)
                mPlayerListener.onError();
        }
    }

    @Override
    public void prepare() {
        if (mMediaPlayer == null)
            return;
        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            if (mPlayerListener != null)
                mPlayerListener.onError();
        }
    }

    @Override
    public void start() {
        if (mMediaPlayer == null || mMediaPlayer.isPlaying())
            return;
        mMediaPlayer.start();
    }

    @Override
    public void pause() {
        if (mMediaPlayer == null)
            return;
        mMediaPlayer.pause();
    }

    @Override
    public void stop() {
        if (mMediaPlayer == null)
            return;
        pause();
        mMediaPlayer.stop();
    }

    @Override
    public void reset() {
        if (mMediaPlayer == null)
            return;
        if (mMediaPlayer.isPlaying())
            stop();

        mMediaPlayer.reset();
        mMediaPlayer.setSurface(null);
    }

    @Override
    public void release() {
        if (mMediaPlayer == null)
            return;
        reset();

        mMediaPlayer.setOnErrorListener(null);
        mMediaPlayer.setOnCompletionListener(null);
        mMediaPlayer.setOnInfoListener(null);
        mMediaPlayer.setOnBufferingUpdateListener(null);
        mMediaPlayer.setOnPreparedListener(null);
        mMediaPlayer.setOnVideoSizeChangedListener(null);
        try {
            mMediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPlayer() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(int t) {
        if (mMediaPlayer != null)
            mMediaPlayer.seekTo(t);
    }

    @Override
    public long getCurrentPosition() {
        return mMediaPlayer == null ? 0 : mMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mMediaPlayer == null ? 0 : mMediaPlayer.getDuration();
    }

    @Override
    public void setSurface(Surface surface) {
        if (mMediaPlayer != null)
            mMediaPlayer.setSurface(surface);
    }

    @Override
    public void setVolume(float l, float r) {
        if (mMediaPlayer != null)
            mMediaPlayer.setVolume(l,r);
    }

    @Override
    public void setLooping(boolean looping) {
        if (mMediaPlayer != null)
            mMediaPlayer.setLooping(looping);
    }

    @Override
    public boolean isLooping() {
        return mMediaPlayer != null && mMediaPlayer.isLooping();
    }

    @Override
    public void setSpeed(YySpeedLevelEnum speed) {
        if (mMediaPlayer == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PlaybackParams params = mMediaPlayer.getPlaybackParams();
            params.setSpeed(speed.getSpeed());
            mMediaPlayer.setPlaybackParams(params);
        }
    }

    @Override
    public int[] getVideoSize() {
        if (mMediaPlayer == null)
            return new int[]{0,0};
        return new int[]{mMediaPlayer.getVideoWidth(),mMediaPlayer.getVideoHeight()};
    }

    @Override
    public void addPlayerListener(YyPlayerListener listener) {
        mPlayerListener = listener;
    }

    /* 播放器监听 */

    /**
     * 准备监听
     */
    private MediaPlayer.OnPreparedListener preparedListener = mp -> {
        if (mPlayerListener != null)
            mPlayerListener.onPreparedEnd();
    };

    /**
     * 缓存监听
     */
    private MediaPlayer.OnBufferingUpdateListener bufferingUpdateListener = (mp, percent) -> {
        if (mPlayerListener != null)
            mPlayerListener.onBuffering(percent);
    };

    /**
     * 加载、渲染监听
     */
    private MediaPlayer.OnInfoListener infoListener = (mp, what, extra) -> {
        if (mPlayerListener != null)
            mPlayerListener.onInfo(what,extra);
        return false;
    };

    /**
     * 错误监听
     */
    private MediaPlayer.OnErrorListener errorListener = (mp, what, extra) -> {
        if (what != -38) {
            if (mPlayerListener != null)
                mPlayerListener.onError();
            return false;
        }else {
            return true;
        }
    };

    /**
     * 播放结束
     */
    private MediaPlayer.OnCompletionListener completionListener = mp -> {
        if (mPlayerListener != null)
            mPlayerListener.onCompletion();
    };

    /**
     * 视频大小监听
     */
    private MediaPlayer.OnVideoSizeChangedListener videoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            if (mPlayerListener != null)
                mPlayerListener.onSizeChanged(mp.getVideoWidth(),mp.getVideoHeight());
        }
    };
}
