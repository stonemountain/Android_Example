package cn.smvp.sdk.demo.smvp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;

import cn.smvp.android.sdk.SmvpClient;
import cn.smvp.android.sdk.callback.SmvpJsonHttpResponseHandler;
import cn.smvp.sdk.demo.download.DownloadActivity;
import cn.smvp.sdk.demo.upload.UploadActivity;
import cn.smvp.sdk.demo.util.LocalConstants;
import cn.smvp.sdk.demo.util.MyLogger;


public class VideoService extends Service {
    private SmvpClient mSmvpClient = null;
    private LocalBinder myBinder = new LocalBinder();

    private final File STORAGE_DIRECTORY = new File(Environment.getExternalStorageDirectory() + "/smvpdemo");

    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        mSmvpClient = SmvpClient.getInstance(LocalConstants.SMVP_TOKEN);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        MyLogger.d(LOG_TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        MyLogger.d(LOG_TAG, "onDestroy");
        super.onDestroy();

        if (mSmvpClient != null) {
            mSmvpClient.release();
            mSmvpClient = null;
        }

        myBinder = null;
    }

    public class LocalBinder extends Binder {
        public VideoService getService() {
            return VideoService.this;
        }
    }


    public void getVideoList() {
        mSmvpClient.getVideoManager().list(0, 5, null, new SmvpJsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                Intent intent = new Intent(LocalConstants.ACTION_GET_ALL_VIDEOS_COMPLETED);
                intent.putExtra("result", response.toString());
                sendBroadcast(intent);
            }

            @Override
            public void onFailure(Throwable e) {
                MyLogger.d(LOG_TAG, "SmvpJsonHttpResponseHandler: onFailure");
            }

        });
    }

    public void upload(Context context) {
        startActivity(context, UploadActivity.class);
    }

    public void download(Context context) {
        startActivity(context, DownloadActivity.class);
    }

    private void startActivity(Context context, Class targetActivity) {
        Intent intent = new Intent();
        intent.setClass(context, targetActivity);
        context.startActivity(intent);
    }


    public void getVideo() {
        String videoId = "647026622611877784";
        mSmvpClient.getVideoManager().get(videoId, new SmvpJsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                Toast.makeText(VideoService.this, "获取单个视频信息成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(VideoService.this, "获取单个视频信息失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateVideo() {
        mSmvpClient.getVideoManager().update("647443938814226137", "update video", "test update", new String[]{"update1", "update2"},
                false, new SmvpJsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Toast.makeText(VideoService.this, "更新视频信息成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        MyLogger.w(LOG_TAG, "update video failed", throwable);
                        Toast.makeText(VideoService.this, "更新视频信息失败", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public SmvpClient getSmvpClient() {
        return mSmvpClient;
    }

    public File getStorageDirectory() {
        return STORAGE_DIRECTORY;
    }


 }
