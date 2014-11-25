package cn.smvp.sdk.demo;


import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Toast;
import cn.smvp.android.sdk.callback.SmvpJsonHttpResponseHandler;
import cn.smvp.android.sdk.component.SmvpVideoView;
import cn.smvp.android.sdk.util.SmvpConstants;
import cn.smvp.sdk.demo.util.SmvpLogger;

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
                SmvpLogger.w(LOG_TAG, "videoId can't be null or empty");
                finish();
            }

            playM3U8Video(videoId);
        } catch (Exception e) {
            SmvpLogger.w(LOG_TAG, "Exception:", e);
        }


    }


    public void playM3U8Video(String videoId) {

        SmvpJsonHttpResponseHandler jsonHttpResponseHandler = new SmvpJsonHttpResponseHandler() {
            @Override
            public void onFailure(Throwable throwable) {
                SmvpLogger.w(LOG_TAG, "download the video in M3U8 format failed: ", throwable);
                if (SmvpConstants.ERROR_NO_AVAILABLE_BITRATE.equals(throwable.getMessage())) {
                    Toast.makeText(PlayVideoActivity.this, R.string.no_available_video, Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray videoArray = response.getJSONArray("entries");
                    JSONObject jsonObject = (JSONObject) videoArray.get(0);
                    JSONArray renditions = jsonObject.getJSONArray("renditions");
                    videoView.setVideoPath(SmvpConstants.RENDITIONS_IOS_HD, renditions);
                    videoView.requestFocus();
                    videoView.start();
                } catch (Exception e) {
                    SmvpLogger.w(LOG_TAG, "error occured when download the video in M3U8 format: ", e);

                }
            }
        };

        mApplication.getVideoService().playM3U8Video(videoId, jsonHttpResponseHandler);
//        mApplication.getVideoService().playMP4Video(videoId, jsonHttpResponseHandler);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        try {
            SmvpLogger.w(LOG_TAG, "onConfigurationChanged");
            if (videoView != null) {
                if (videoView.rotateFromBtn()) {
                    SmvpLogger.w(LOG_TAG, "videoView.rotateFromBtn()=" + videoView.rotateFromBtn());
                    return;
                } else {
                    SmvpLogger.w(LOG_TAG, "orientation=" + getResources().getConfiguration().orientation);
                    if (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT == getResources().getConfiguration().orientation) {
                        videoView.toSmallScreen();
                        SmvpLogger.w(LOG_TAG, "toFullScreen");
                    } else {
                        videoView.toFullScreen();
                        SmvpLogger.w(LOG_TAG, "toSmallScreen");
                    }
                }
            }
        } catch (Exception e) {
            SmvpLogger.i(LOG_TAG, "onConfigurationChanged exception");
        }


    }
}
