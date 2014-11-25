package cn.smvp.sdk.demo.smvp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;

import cn.smvp.android.sdk.SmvpClient;
import cn.smvp.android.sdk.callback.SmvpDownloadVideoCallback;
import cn.smvp.android.sdk.callback.SmvpJsonHttpResponseHandler;
import cn.smvp.android.sdk.callback.SmvpUploadVideoCallback;
import cn.smvp.android.sdk.util.SmvpConstants;
import cn.smvp.sdk.demo.LocalApplication;
import cn.smvp.sdk.demo.R;
import cn.smvp.sdk.demo.util.LocalConstants;
import cn.smvp.sdk.demo.util.MyLogger;


public class VideoService extends Service {
    private SmvpClient smvpClient = null;
    private LocalApplication application = null;

    private LocalBinder myBinder = new LocalBinder();
    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        application = (LocalApplication) getApplication();
        smvpClient = SmvpClient.getInstance(getApplicationContext(), LocalConstants.SMVP_TOKEN);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        MyLogger.d(LOG_TAG,"onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        MyLogger.d(LOG_TAG,"onDestroy");
        super.onDestroy();

        smvpClient.cancel();
        smvpClient = null;
        application = null;
        myBinder = null;
    }

    public class LocalBinder extends Binder {
        public VideoService getService() {
            return VideoService.this;
        }
    }


    public void getVideoList() {
        smvpClient.entries.list(0, 5, null, new SmvpJsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                Intent intent = new Intent(LocalConstants.ACTION_GET_ALL_VIDEOS_COMPLETED);
                intent.putExtra("result", response.toString());
                sendBroadcast(intent);
            }

            @Override
            public void onFailure(Throwable e) {
                MyLogger.d(LOG_TAG,"SmvpJsonHttpResponseHandler: onFailure");
            }

        });
    }

    public void uploadVideoSimple() {
        File EXTERNAL_PATH = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);

        try {
            File uploadFile = new File(EXTERNAL_PATH, "test3.mp4");
            if (!uploadFile.exists()) {
                MyLogger.d(LOG_TAG,"file not exist: filepath=" + uploadFile.getAbsolutePath());
                return;
            }

            MyLogger.d(LOG_TAG,"start upload,file=" + uploadFile.getAbsolutePath());
            smvpClient.entries.upload("UpLoadDameo", "test upload", uploadFile,
                    new SmvpUploadVideoCallback() {

                        @Override
                        public void onSuccess(JSONObject jsonObject) {
                            MyLogger.d(LOG_TAG,"upload success,jsonobject=" + jsonObject);
                            Toast.makeText(application, getResources().
                                    getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            MyLogger.w(LOG_TAG,"upload failed", throwable);
                            Toast.makeText(application, getResources().
                                    getString(R.string.upload_failure), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onProgressChanged(int progress) {
                            MyLogger.d(LOG_TAG,"upload progress=" + progress);
                        }

                        @Override
                        public void onCancel() {
                            super.onCancel();

                            Toast.makeText(application, getResources().
                                    getString(R.string.cancel_upload), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            MyLogger.w(LOG_TAG,"upload video failed:", e);
        }

    }

    public void uploadVideo() {
        File EXTERNAL_PATH = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);

        try {
            String[] tags = new String[]{
                    "test", "test1", "test2"
            };
            String categoryId = "640378515748843249";
            File uploadFile = new File(EXTERNAL_PATH, "test3.mp4");
            if (!uploadFile.exists()) {
                MyLogger.d(LOG_TAG,"file not exist: filepath=" + uploadFile.getAbsolutePath());
                return;
            }

            MyLogger.d(LOG_TAG,"start upload,file=" + uploadFile.getAbsolutePath());
            smvpClient.entries.upload("UpLoadDameo", "test upload", tags, categoryId, true, uploadFile,
                    new SmvpUploadVideoCallback() {

                        @Override
                        public void onSuccess(JSONObject jsonObject) {
                            MyLogger.d(LOG_TAG,"onSuccess: upload success,jsonobject=" + jsonObject);
                            Toast.makeText(application, getResources().
                                    getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            MyLogger.w(LOG_TAG,"onFailure: upload success", throwable);
                            Toast.makeText(application, getResources().
                                    getString(R.string.upload_failure), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onProgressChanged(int progress) {
                            MyLogger.d(LOG_TAG,"upload progress=" + progress);
                        }

                        @Override
                        public void onCancel() {
                            super.onCancel();

                            MyLogger.d(LOG_TAG,"cancel the upload success");
                            Toast.makeText(application, getResources().
                                    getString(R.string.cancel_upload), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            MyLogger.w(LOG_TAG,"upload video failed:", e);
        }

    }

    public void downloadVideo() {
        File storeFile = new File("/sdcard/smvp/test2.mp4");
        if (storeFile.exists()) {
            storeFile.delete();
        }
        String videoId = "647026622611877784";

        smvpClient.entries.downloadMP4Video(videoId, SmvpConstants.RENDITIONS_ANDROID_SMOOTH,
                new SmvpDownloadVideoCallback(storeFile, false) {
                    @Override
                    public void onProgressChanged(int progress) {
                        MyLogger.d(LOG_TAG,"download progress: " + progress);
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();

                        MyLogger.d(LOG_TAG,"cancel the download success");
                        Toast.makeText(application, getResources().
                                getString(R.string.cancel_download), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        MyLogger.d(LOG_TAG,"download success");
                        Toast.makeText(application, getResources().
                                getString(R.string.download_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        if (throwable.getMessage().equals("NO_RENDITION_AVAILABLE")) {
                            MyLogger.w(LOG_TAG,"download failed: NO_RENDITION_AVAILABLE");
                            Toast.makeText(application, "没有对应的码率，请转码后重试。", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(application, getResources().
                                    getString(R.string.download_failure), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    public void getVideo() {
        String videoId = "647026622611877784";
        smvpClient.entries.get(videoId, new SmvpJsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                MyLogger.d(LOG_TAG,"get: onSuccess,response=" + response);
                Toast.makeText(application, "获取单个视频信息成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(application, "获取单个视频信息失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateVideo() {
        smvpClient.entries.update("647443938814226137", "update video", "test update", new String[]{"update1", "update2"},
                false, new SmvpJsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        MyLogger.d(LOG_TAG,"update video success");
                        Toast.makeText(application, "更新视频信息成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        MyLogger.d(LOG_TAG,"update video failed");
                        Toast.makeText(application, "更新视频信息失败", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void playMP4Video(String videoId, SmvpJsonHttpResponseHandler smvpJsonHttpResponseHandler) {
        smvpClient.entries.jsonMP4(videoId, smvpJsonHttpResponseHandler);
    }

    public void playM3U8Video(String videoId, SmvpJsonHttpResponseHandler smvpJsonHttpResponseHandler) {
        smvpClient.entries.jsonM3U8(videoId, smvpJsonHttpResponseHandler);
    }

    public void cancel() {
        smvpClient.entries.cancel();
    }

}
