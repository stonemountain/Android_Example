package cn.smvp.sdk.demo;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Formatter;

import cn.smvp.android.sdk.SmvpClient;
import cn.smvp.android.sdk.callback.SmvpDownloadListener;
import cn.smvp.android.sdk.entries.DownloadData;
import cn.smvp.android.sdk.entries.DownloadManager;
import cn.smvp.android.sdk.util.SmvpConstants;
import cn.smvp.android.sdk.util.SmvpLogger;
import cn.smvp.android.sdk.util.SmvpVideoData;
import cn.smvp.android.sdk.view.SmvpVideoView;
import cn.smvp.sdk.demo.fragment.DetailFragment;
import cn.smvp.sdk.demo.fragment.TitleFragment;
import cn.smvp.sdk.demo.smvp.VideoService;
import cn.smvp.sdk.demo.util.MyLogger;

public class PlayVideoActivity extends Activity implements TitleFragment.TitleFragmentCallback {
    private SmvpVideoData mVideoData;
    private VideoService mVideoService;
    private SmvpVideoView videoView;
    private TextView mProgressView;
    private ProgressReceiver mProgressRecevier;

    private static final String LOG_TAG = "PlayVideoActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);

             setContentView(R.layout.activity_play_video);
            mVideoService = ((LocalApplication) this.getApplication()).getVideoService();
            mProgressView = (TextView) findViewById(R.id.display_progress);
            videoView = (SmvpVideoView) findViewById(R.id.video_view);

            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            mVideoData = bundle.getParcelable("data");
            String videoId = mVideoData.getId();

            if (videoId == null || TextUtils.isEmpty(videoId)) {
                MyLogger.w(LOG_TAG, "videoId can't be null or empty");
                finish();
            }

            boolean autoStart = intent.getBooleanExtra("autoStart", false);
            SmvpClient smvpClient = mVideoService.getSmvpClient();
            String defaultDefinition = SmvpConstants.DEFINITION_IOS_HD;
            videoView.playVideo(smvpClient, mVideoData, defaultDefinition, autoStart);
            videoView.setOnErrorListener(onErrorListener);

            initFragment();
            registerReceiver();
        } catch (Exception e) {
            MyLogger.w(LOG_TAG, "Exception:", e);
        }


    }

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            MyLogger.w(LOG_TAG, "MediaPlayer: onFailure,what=" + what + ", extra=" + extra);
            return true;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mProgressRecevier);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        try {
            MyLogger.w(LOG_TAG, "onConfigurationChanged");
            if (videoView != null) {
                if (videoView.rotatedFromBtn()) {
                    MyLogger.w(LOG_TAG, "videoView.rotatedFromBtn()=" + videoView.rotatedFromBtn());
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

    private void initFragment() {
        DetailFragment detailFragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", mVideoData);
        detailFragment.setArguments(bundle);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, detailFragment);
        transaction.commit();
    }


    @Override
    public void onEditBtnClick() {
        SmvpLogger.i(LOG_TAG, "onEditBtnClick");
    }

    @Override
    public void onCancelBtnClick() {
        SmvpLogger.i(LOG_TAG, "onCancelBtnClick");
    }

    @Override
    public void onCompleteBtnClick() {
        SmvpLogger.i(LOG_TAG, "onCompleteBtnClick");
    }

    @Override
    public void onUploadBtnClick() {
        SmvpLogger.i(LOG_TAG, "onUploadBtnClick");
    }

    @Override
    public void onDownloadBtnClick() {
        File storageDir = mVideoService.getStorageDirectory();
        DownloadManager downloadManager = mVideoService.getSmvpClient().getVideoManager().getDownloaderManager(this, storageDir);

        SmvpLogger.i(LOG_TAG, "onDownloadBtnClick");
        String videoId = mVideoData.getId();
        DownloadData downloadData = new DownloadData(videoId,
                SmvpConstants.DEFINITION_IOS_HD);
        downloadData.setDownloadListener(mDownloadListener);
        downloadManager.download(downloadData);
    }

    SmvpDownloadListener mDownloadListener = new SmvpDownloadListener() {
        @Override
        public void onSuccess() {
            showToast("下载成功");
        }

        @Override
        public void onProgressChanged(int progress) {
        }

        @Override
        public void onStatusChanged(int status) {
            showToast("状态改变：" + status);
        }

        @Override
        public void onFailure(Exception e) {
            showToast("下载失败");
        }
    };

    public void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    private void registerReceiver() {
        mProgressRecevier = new ProgressReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SmvpConstants.ACTION_PLAY_PROGRESS_CHANGED);
        registerReceiver(mProgressRecevier, filter);
    }


    private class ProgressReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (SmvpConstants.ACTION_PLAY_PROGRESS_CHANGED == action) {
                String id = intent.getStringExtra("id");
                String title = intent.getStringExtra("title");
                float progress = intent.getFloatExtra("progress", 0);
                String text = "当前播放时间：" + formatTime(progress);
                mProgressView.setText(text);
            }

        }
    }

    private String formatTime(float time) {
        if (time < 60) {
            return time + "";
        }

        int seconds = (int) (time % 60);
        int minutes = (int) ((time % 3600) / 60);
        int hours = (int) (time / 3600);

        Formatter formatter = new Formatter();
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return formatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }
}
