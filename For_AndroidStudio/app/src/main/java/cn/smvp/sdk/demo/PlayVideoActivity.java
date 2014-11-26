package cn.smvp.sdk.demo;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;

import cn.smvp.android.sdk.SmvpClient;
import cn.smvp.android.sdk.component.SmvpVideoView;
import cn.smvp.android.sdk.util.SmvpConstants;
import cn.smvp.sdk.demo.util.MyLogger;

public class PlayVideoActivity extends Activity {
    private LocalApplication mApplication;
    private SmvpVideoView videoView;
    private static final String LOG_TAG = "PlayVideoActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);

            setContentView(R.layout.activity_play_video);

            mApplication = (LocalApplication) getApplication();
            videoView = (SmvpVideoView) findViewById(R.id.video_view);

            Intent intent = getIntent();
            String videoId = intent.getStringExtra("videoId");
            if (videoId == null || TextUtils.isEmpty(videoId)) {
                MyLogger.w(LOG_TAG, "videoId can't be null or empty");
                finish();
            }

            boolean autoStart = true;
            SmvpClient smvpClient = mApplication.getVideoService().getSmvpClient();
            String defaultDefinition = SmvpConstants.DEFINITION_IOS_HD;

            videoView.playVideo(smvpClient, videoId, autoStart, defaultDefinition);
            videoView.setOnErrorListener(onErrorListener);
        } catch (Exception e) {
            MyLogger.w(LOG_TAG, "Exception:", e);
        }


    }

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {

            return true;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        try {
            MyLogger.w(LOG_TAG, "onConfigurationChanged");
            if (videoView != null) {
                if (videoView.rotateFromBtn()) {
                    MyLogger.w(LOG_TAG, "videoView.rotateFromBtn()=" + videoView.rotateFromBtn());
                    return;
                } else {
                    MyLogger.w(LOG_TAG, "orientation=" + getResources().getConfiguration().orientation);
                    if (videoView.isFullScreen()) {
                        videoView.toSmallScreen();
                        MyLogger.w(LOG_TAG, "toFullScreen");
                    } else {
                        videoView.toFullScreen();
                        MyLogger.w(LOG_TAG, "toSmallScreen");
                    }
                }
            }
        } catch (Exception e) {
            MyLogger.w(LOG_TAG, "onConfigurationChanged exception");
        }
    }


}
