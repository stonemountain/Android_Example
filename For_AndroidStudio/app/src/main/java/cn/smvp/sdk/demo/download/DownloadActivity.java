package cn.smvp.sdk.demo.download;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import cn.smvp.android.sdk.callback.SmvpDownloadListener;
import cn.smvp.android.sdk.entries.DownloadData;
import cn.smvp.android.sdk.entries.DownloadManager;
import cn.smvp.android.sdk.entries.DownloadTask;
import cn.smvp.android.sdk.util.SmvpConstants;
import cn.smvp.sdk.demo.LocalApplication;
import cn.smvp.sdk.demo.R;
import cn.smvp.sdk.demo.smvp.VideoService;

public class DownloadActivity extends Activity {
    private Button mStartAllBtn;
    private DownloadManager mDownloadManager;
    private DownloadAdapter mDownloadAdapter;
    private List<DownloadTask> mDownloadList;

    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download);

        VideoService mVideoService = ((LocalApplication) this.getApplication()).getVideoService();
        File storageDir = mVideoService.getStorageDirectory();
        mDownloadManager = mVideoService.getSmvpClient().getVideoManager().getDownloaderManager(this, storageDir);

        mDownloadList = mDownloadManager.getTaskList();
        Iterator<DownloadTask> iterator = mDownloadList.iterator();
        while (iterator.hasNext()) {
            iterator.next().setDownloadListener(mDownloadListener);
        }

        ListView listView = (ListView) findViewById(R.id.download_listview);
        mDownloadAdapter = new DownloadAdapter(this, mDownloadList);
        mDownloadAdapter.setDownloadManager(mDownloadManager);
        mDownloadAdapter.setDownloadListener(mDownloadListener);
        listView.setAdapter(mDownloadAdapter);

        Button downloadBtn = (Button) findViewById(R.id.download_test);
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();

                mStartAllBtn.setTag(true);
                mStartAllBtn.setText(getResources().getString(R.string.stop_all));
            }

        });

        mStartAllBtn = (Button) findViewById(R.id.download_start_all);
        mStartAllBtn.setTag(false);
        mStartAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newstatus = !(Boolean) v.getTag();
                if (newstatus) {
                    mDownloadManager.downloadAll();
                    ((TextView) v).setText(getResources().getString(R.string.stop_all));
                } else {
                    mDownloadManager.stopAll();
                    ((TextView) v).setText(getResources().getString(R.string.start_all));
                }
                v.setTag(newstatus);
            }

        });

        Button cancelAllBtn = (Button) findViewById(R.id.download_cancel_all);
        cancelAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDownloadManager.cancelAll();
            }

        });

    }

    private void setData() {
        String videoId = "687484462937695693";
        DownloadData downloadData1 = new DownloadData(videoId,
                SmvpConstants.DEFINITION_IOS_HD);
        downloadData1.setDownloadListener(mDownloadListener);
        mDownloadManager.download(downloadData1);

        String videoId2 = "687484462937695693";
        DownloadData downloadData2 = new DownloadData(videoId2,
                SmvpConstants.DEFINITION_IOS_STANDARD);
        downloadData2.setDownloadListener(mDownloadListener);
        mDownloadManager.download(downloadData2);

        String videoId3 = "687484462937695693";
        DownloadData downloadData3 = new DownloadData(videoId3,
                SmvpConstants.DEFINITION_IOS_SMOOTH);
        downloadData3.setDownloadListener(mDownloadListener);
        mDownloadManager.download(downloadData3);

        String videoId4 = "687480764970853803";
        DownloadData downloadData4 = new DownloadData(videoId4,
                SmvpConstants.DEFINITION_IOS_HD);
        downloadData4.setDownloadListener(mDownloadListener);
        mDownloadManager.download(downloadData4);

        String videoId5 = "677479559959951805";
        DownloadData downloadData5 = new DownloadData(videoId5,
                SmvpConstants.DEFINITION_IOS_HD);
        downloadData5.setDownloadListener(mDownloadListener);
//        mDownloadManager.download(downloadData5);

        String videoId6 = "677479559959951805";
        DownloadData downloadData6 = new DownloadData(videoId6,
                SmvpConstants.DEFINITION_IOS_STANDARD);
        downloadData6.setDownloadListener(mDownloadListener);
//        mDownloadManager.download(downloadData6);

        String videoId7 = "677479559959951805";
        DownloadData downloadData7 = new DownloadData(videoId6,
                SmvpConstants.DEFINITION_IOS_STANDARD);
        downloadData7.setDownloadListener(mDownloadListener);
//        mDownloadManager.download(downloadData7);

        String videoId8 = "677479559959951805";
        DownloadData downloadData8 = new DownloadData(videoId6,
                SmvpConstants.DEFINITION_IOS_STANDARD);
        downloadData8.setDownloadListener(mDownloadListener);
//        mDownloadManager.download(downloadData8);

        String videoId9 = "677479559959951805";
        DownloadData downloadData9 = new DownloadData(videoId6,
                SmvpConstants.DEFINITION_IOS_STANDARD);
        downloadData9.setDownloadListener(mDownloadListener);
//        mDownloadManager.download(downloadData9);

        mDownloadList = mDownloadManager.getTaskList();
        mDownloadAdapter.setData(mDownloadList);
    }

    SmvpDownloadListener mDownloadListener = new SmvpDownloadListener() {
        @Override
        public void onSuccess() {
            mDownloadAdapter.notifyDataSetChanged();
            showToast("下载成功");
        }

        @Override
        public void onProgressChanged(int progress) {
            mDownloadAdapter.notifyDataSetChanged();
        }

        @Override
        public void onStatusChanged(int status) {
            mDownloadAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(Exception e) {
            mDownloadAdapter.notifyDataSetChanged();
            showToast("下载失败");
        }
    };

    public void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

}
