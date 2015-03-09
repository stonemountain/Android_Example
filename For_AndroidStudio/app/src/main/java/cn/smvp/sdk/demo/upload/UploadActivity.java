package cn.smvp.sdk.demo.upload;


import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import cn.smvp.android.sdk.callback.SmvpUploadListener;
import cn.smvp.android.sdk.entries.UploadData;
import cn.smvp.android.sdk.entries.UploadManager;
import cn.smvp.android.sdk.entries.UploadTask;
import cn.smvp.sdk.demo.LocalApplication;
import cn.smvp.sdk.demo.R;
import cn.smvp.sdk.demo.smvp.VideoService;
import cn.smvp.sdk.demo.util.MyLogger;

public class UploadActivity extends Activity {
    private Button mUploadBtn;
    private List<UploadTask> mTaskList;
    private UploadManager mUploadManager;
    private UploadAdapter mUploadAdapter;

    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);

        VideoService mVideoService = ((LocalApplication) this.getApplication()).getVideoService();
        mUploadManager = mVideoService.getSmvpClient().getVideoManager().getUploadManager(this);
        mTaskList = mUploadManager.getTaskList();
        Iterator<UploadTask> iterator = mTaskList.iterator();
        while (iterator.hasNext()) {
            iterator.next().setUploadListener(mUploadListener);
        }

        ListView listView = (ListView) findViewById(R.id.upload_listview);
        mUploadAdapter = new UploadAdapter(this, mTaskList);
        mUploadAdapter.setUploadManager(mUploadManager);
        listView.setAdapter(mUploadAdapter);

        mUploadBtn = (Button) findViewById(R.id.upload);
        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setData() {
//        File directory = Environment.getExternalStoragePublicDirectory("UploadTest");
        File testFile = new File("/sdcard1/UploadTest/smvpTest.mp4");
        UploadData uploadData1 = new UploadData("test1", "description1", testFile);
        uploadData1.setUploadListener(mUploadListener);

        UploadData uploadData2 = new UploadData("test2", "description2", new String[]{"tag1,tag2"},
                "687476371219309847", true, testFile);
        uploadData2.setUploadListener(mUploadListener);

        UploadData uploadData3 = new UploadData("test3", "description3", testFile);
        uploadData3.setUploadListener(mUploadListener);

        UploadData uploadData4 = new UploadData("test4", "description4", new String[]{"tag3,tag4,tag5"},
                "687476371219309847", true, testFile);
        uploadData4.setUploadListener(mUploadListener);

        mUploadManager.upload(uploadData1);
        mUploadManager.upload(uploadData2);
        mUploadManager.upload(uploadData3);
        mUploadManager.upload(uploadData4);

        mTaskList = mUploadManager.getTaskList();
        mUploadAdapter.setData(mTaskList);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        MyLogger.i(LOG_TAG, "onConfigurationChanged");
    }

    SmvpUploadListener mUploadListener = new SmvpUploadListener() {
        @Override
        public void onSuccess(JSONObject response) {
            MyLogger.i(LOG_TAG, "Upload onSuccess response=" + response);
            mUploadAdapter.notifyDataSetChanged();
            showToast("上传成功");
        }

        @Override
        public void onProgressChanged(int progress) {
            mUploadAdapter.notifyDataSetChanged();
        }

        @Override
        public void onStatusChanged(int status) {
            mUploadAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(Exception e) {
            MyLogger.w(LOG_TAG, "upload failure: ", e);
            mUploadAdapter.notifyDataSetChanged();
            showToast("上传失败");
        }
    };

    public void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }


}
