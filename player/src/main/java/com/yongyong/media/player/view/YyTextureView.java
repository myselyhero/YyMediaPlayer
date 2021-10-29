package com.yongyong.media.player.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yongyong.media.player.en.YyRatioEnum;
import com.yongyong.media.player.model.YyMediaPlayerInterface;
import com.yongyong.media.player.model.YyRenderInterface;
import com.yongyong.media.player.util.YyMeasureHelper;

/**
 * @author myselyhero 
 * 
 * @desc: 渲染视图
 * 
 * @// TODO: 2021/10/28
 */
public class YyTextureView extends TextureView implements YyRenderInterface, TextureView.SurfaceTextureListener {

    private YyMediaPlayerInterface mMediaPLayer;

    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;

    public YyTextureView(@NonNull Context context) {
        super(context);
        init();
    }

    public YyTextureView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public YyTextureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        if (mSurfaceTexture != null) {
            setSurfaceTexture(mSurfaceTexture);
        } else {
            mSurfaceTexture = surfaceTexture;
            mSurface = new Surface(surfaceTexture);
            if (mMediaPLayer != null)
                mMediaPLayer.setSurface(mSurface);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] measuredSize = YyMeasureHelper.getInstance().onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measuredSize[0], measuredSize[1]);
    }

    @Override
    public void attach(YyMediaPlayerInterface playerInterface) {
        mMediaPLayer = playerInterface;
        if (mMediaPLayer != null && mSurface != null)
            mMediaPLayer.setSurface(mSurface);
    }

    @Override
    public void setVideoSize(int width, int height) {
        if (width > 0 && height > 0) {
            YyMeasureHelper.getInstance().setVideoSize(width,height);
            requestLayout();
        }
    }

    @Override
    public void setDegree(int degree) {
        YyMeasureHelper.getInstance().setDegree(degree);
        requestLayout();
    }

    @Override
    public void setRatio(YyRatioEnum ratioEnum) {
        YyMeasureHelper.getInstance().setRatioEnum(ratioEnum);
        requestLayout();
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public Bitmap capture() {
        return getBitmap();
    }

    @Override
    public void release() {
        if (mSurface != null)
            mSurface.release();
        if (mSurfaceTexture != null)
            mSurfaceTexture.release();
    }
}
