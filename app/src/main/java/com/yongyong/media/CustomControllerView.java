package com.yongyong.media;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yongyong.media.player.en.YyStatusEnum;
import com.yongyong.media.player.view.YyBaseControllerView;

public class CustomControllerView extends YyBaseControllerView {

    private static final String TAG = "CustomControllerView";

    private LinearLayout bottomBackground;
    private TextView currencyTextView;
    private SeekBar seekBar;
    private TextView totalTextView;
    private ImageView volumeImageView;

    public CustomControllerView(@NonNull Context context) {
        super(context);
    }

    public CustomControllerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomControllerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.custom_controller_view;
    }

    @Override
    protected void initView() {
        bottomBackground = findViewById(R.id.lwj_common_controller_bottom);
        currencyTextView = findViewById(R.id.lwj_common_controller_bottom_current);
        seekBar = findViewById(R.id.lwj_common_controller_bottom_seek_bar);
        totalTextView = findViewById(R.id.lwj_common_controller_bottom_total);
        volumeImageView = findViewById(R.id.lwj_common_controller_bottom_voice);
        post(new Runnable() {
            @Override
            public void run() {
                volumeImageView.setImageResource(isMute() ? R.drawable.multimedia_mute : R.drawable.multimedia_volume);
            }
        });

        volumeImageView.setOnClickListener(view -> {
            setMute(!isMute());
            if (isMute()){
                volumeImageView.setImageResource(R.drawable.multimedia_mute);
            }else{
                volumeImageView.setImageResource(R.drawable.multimedia_volume);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currencyTextView.setText(longTimeToString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    protected void onClick(MotionEvent event) {
        startController();
    }

    @Override
    protected void onDblClick(MotionEvent event) {
        if (isPlayer()){
            pause();
        }else {
            start();
        }
    }

    @Override
    protected void bufferUpdate(int buffer) {
        //解决缓冲进度不能100%问题
        if (buffer >= 95) {
            seekBar.setSecondaryProgress((int) getDuration());
        } else {
            seekBar.setSecondaryProgress(buffer * 10);
        }
    }

    @Override
    public void currencyPosition(long position) {
        seekBar.setProgress((int) position);
        currencyTextView.setText(longTimeToString(position));
    }

    @Override
    protected void changeStatusListener(YyStatusEnum statusEnum) {
        if (statusEnum == YyStatusEnum.STATUS_PLAYING){
            totalTextView.setText(longTimeToString(getDuration()));
            seekBar.setMax((int) getDuration());
            startController();
        }
    }

    /**
     *
     */
    private void startController(){
        if (bottomBackground.getVisibility() == View.VISIBLE){
            stopController();
        }else {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    stopController();
                }
            };
            mHandler.postDelayed(mRunnable,mControllerTime);
            startProgress();
            showControllerAnim(bottomBackground);
        }
    }

    /**
     *
     */
    private void stopController(){
        if (mHandler != null){
            mHandler.removeCallbacks(mRunnable);
            mRunnable = null;
        }
        hideControllerAnim(bottomBackground);
        stopProgress();
    }
}
