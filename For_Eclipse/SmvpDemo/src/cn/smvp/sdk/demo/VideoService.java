package cn.smvp.sdk.demo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;

import cn.smvp.android.sdk.DownloadManager;
import cn.smvp.android.sdk.SmvpClient;
import cn.smvp.android.sdk.UploadManager;
import cn.smvp.android.sdk.VideoManager;
import cn.smvp.android.sdk.callback.ResponseListener;
import cn.smvp.android.sdk.util.VideoData;
import cn.smvp.sdk.demo.util.LocalConstants;
import cn.smvp.sdk.demo.util.MyLogger;


public class VideoService extends Service {
    private SmvpClient mClient = null;
    private LocalBinder myBinder = new LocalBinder();

    private final File STORAGE_DIRECTORY = new File(Environment.getExternalStorageDirectory() + "/demo");

    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        MyLogger.i(LOG_TAG, "--------------------onCreate");
        mClient = SmvpClient.getInstance(getApplication(), LocalConstants.TOKEN);
    }

    @Override
    public IBinder onBind(Intent intent) {
        MyLogger.i(LOG_TAG, "--------------------onBind");
        return myBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);

        MyLogger.i(LOG_TAG, "--------------------onRebind");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        MyLogger.i(LOG_TAG, "--------------------onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        MyLogger.i(LOG_TAG, "--------------------onDestroy");
        if (mClient != null) {
            mClient.release();
            mClient = null;
        }

        myBinder = null;
    }

    public class LocalBinder extends Binder {
        public VideoService getService() {
            return VideoService.this;
        }
    }

    public void getVideo(String videoId, ResponseListener responseListener) {
        mClient.getVideoManager().get(videoId, responseListener);
    }


    private SmvpClient getClient() {
        return mClient == null ? mClient.getInstance(getApplication(), LocalConstants.TOKEN) : mClient;
    }

    public VideoManager getVideoManager() {
        return getClient().getVideoManager();
    }


    public UploadManager getUploadManager() {
        return getClient().getUploadManager();
    }

    public DownloadManager getDownloadManager() {
        return getClient().getDownloaderManager(STORAGE_DIRECTORY);
    }

    public void getAllPlayers(ResponseListener responseListener) {
        mClient.getVideoManager().getAllPlayers(responseListener);
    }


    public void jsonM3U8(String videoId, ResponseListener responseListener) {
        mClient.getVideoManager().jsonM3U8(videoId, responseListener);
    }

    public void list(int start, int max, String categoryId, ResponseListener responseListener) {
        mClient.getVideoManager().list(start, max, categoryId, responseListener);
    }

    public void update(VideoData videoData, ResponseListener responseListener) {
        mClient.getVideoManager().update(videoData, responseListener);
    }

}
