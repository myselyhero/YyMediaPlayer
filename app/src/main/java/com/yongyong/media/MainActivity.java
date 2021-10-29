package com.yongyong.media;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.yongyong.media.player.YyMediaPlayer;
import com.yongyong.media.player.en.YyRatioEnum;
import com.yongyong.media.player.model.YyPlayerListener;
import com.yongyong.media.player.view.YyMediaPLayerView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private YyMediaPLayerView pLayerView;
    private YyMediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4
        //http://www.yongyongwang.cn:8089/smile/voice/等一分钟.mp3
        //http://www.yongyongwang.cn:8089/smile/video/eh.mp4
        //http://www.yongyongwang.cn:8089/smile/voice/nuowei.mp3
        //http://www.yongyongwang.cn:8089/smile/video/13278561924707682.mp4

        /*mediaPlayer = new YyMediaPlayer();
        mediaPlayer.init(this);
        mediaPlayer.setLooping(true);
        mediaPlayer.setDataSource("http://www.yongyongwang.cn:8089/smile/voice/等一分钟.mp3");
        mediaPlayer.prepare();
        mediaPlayer.start();*/

        pLayerView = findViewById(R.id.main_media);
        pLayerView.setLooping(true);
        pLayerView.setDataSource("http://www.yongyongwang.cn:8089/smile/video/eh.mp4");
        pLayerView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null)
            mediaPlayer.start();
        if (pLayerView != null)
            pLayerView.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null)
            mediaPlayer.pause();
        if (pLayerView != null)
            pLayerView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null)
            mediaPlayer.release();
        if (pLayerView != null)
            pLayerView.onRelease();
    }
}