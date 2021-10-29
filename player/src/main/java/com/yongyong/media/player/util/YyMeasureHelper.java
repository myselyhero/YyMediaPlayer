package com.yongyong.media.player.util;

import android.view.View;

import com.yongyong.media.player.en.YyRatioEnum;

/**
 *
 */
public class YyMeasureHelper {

    private static YyMeasureHelper instance;

    private int mDegree;
    private int mWidth,mHeight;

    private YyRatioEnum mRatioEnum = YyRatioEnum.RATIO_DEFAULT;

    /**
     *
     * @return
     */
    public static YyMeasureHelper getInstance() {
        if (instance == null){
            synchronized (YyMeasureHelper.class){
                instance = new YyMeasureHelper();
            }
        }
        return instance;
    }

    /**
     *
     * @param degree
     */
    public void setDegree(int degree) {
        mDegree = degree;
    }

    /**
     *
     * @param width
     * @param height
     */
    public void setVideoSize(int width,int height){
        mWidth = width;
        mHeight = height;
    }

    /**
     *
     * @param ratioEnum
     */
    public void setRatioEnum(YyRatioEnum ratioEnum) {
        mRatioEnum = ratioEnum;
    }

    /**
     *
     * @param videoWidth
     * @param videoHeight
     * @return
     */
    public int[] onMeasure(int videoWidth,int videoHeight){
        /** 软解码时处理旋转信息，交换宽高 */
        if (mDegree == 90 || mDegree == 270) {
            videoWidth = videoWidth + videoHeight;
            videoHeight = videoWidth - videoHeight;
            videoWidth = videoWidth - videoHeight;
        }

        int width = View.MeasureSpec.getSize(videoWidth);
        int height = View.MeasureSpec.getSize(videoHeight);

        if (mWidth == 0 || mHeight == 0) {
            return new int[]{width, height};
        }

        if (mHeight > mWidth){
            mRatioEnum = YyRatioEnum.RATIO_FULL;
        }

        /** 设置了宽高比例 */
        switch (mRatioEnum){
            case RATIO_DEFAULT://默认使用原视频高宽
            default:
                int widthSpecMode = View.MeasureSpec.getMode(videoWidth);
                int widthSpecSize = View.MeasureSpec.getSize(videoWidth);
                int heightSpecMode = View.MeasureSpec.getMode(videoHeight);
                int heightSpecSize = View.MeasureSpec.getSize(videoHeight);

                if (widthSpecMode == View.MeasureSpec.EXACTLY && heightSpecMode == View.MeasureSpec.EXACTLY) {
                    // the size is fixed
                    width = widthSpecSize;
                    height = heightSpecSize;

                    // for compatibility, we adjust size based on aspect ratio
                    if (mWidth * height < width * mHeight) {
                        //Log.i("@@@", "image too wide, correcting");
                        width = height * mWidth / mHeight;
                    } else if (mWidth * height > width * mHeight) {
                        //Log.i("@@@", "image too tall, correcting");
                        height = width * mHeight / mWidth;
                    }
                } else if (widthSpecMode == View.MeasureSpec.EXACTLY) {
                    // only the width is fixed, adjust the height to match aspect ratio if possible
                    width = widthSpecSize;
                    height = width * mHeight / mWidth;
                    if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                        // couldn't match aspect ratio within the constraints
                        height = heightSpecSize;
                    }
                } else if (heightSpecMode == View.MeasureSpec.EXACTLY) {
                    // only the height is fixed, adjust the width to match aspect ratio if possible
                    height = heightSpecSize;
                    width = height * mWidth / mHeight;
                    if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                        // couldn't match aspect ratio within the constraints
                        width = widthSpecSize;
                    }
                } else {
                    // neither the width nor the height are fixed, try to use actual video size
                    width = mWidth;
                    height = mHeight;
                    if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                        // too tall, decrease both width and height
                        height = heightSpecSize;
                        width = height * mWidth / mHeight;
                    }
                    if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                        // too wide, decrease both width and height
                        width = widthSpecSize;
                        height = width * mHeight / mWidth;
                    }
                }
                break;
            case RATIO_CROP://居中
                if (mWidth * height > width * mHeight) {
                    height = width * mHeight / mWidth;
                }
                /*if (mWidth * height > width * mHeight) {
                    width = height * mWidth / mHeight;
                } else {
                    height = width * mHeight / mWidth;
                }*/
                break;
            case RATIO_FULL://全屏
                width = videoWidth;
                height = videoHeight;
                break;
            case RATIO_16_9://
                if (height > width / 16 * 9) {
                    height = width / 16 * 9;
                } else {
                    width = height / 9 * 16;
                }
                break;
            case RATIO_4_3://
                if (height > width / 4 * 3) {
                    height = width / 4 * 3;
                } else {
                    width = height / 3 * 4;
                }
                break;
        }
        return new int[]{width, height};
    }
}
