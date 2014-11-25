
package cn.smvp.sdk.demo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.smvp.sdk.demo.smvp.JsonParser;
import cn.smvp.sdk.demo.smvp.SmvpVideo;
import cn.smvp.sdk.demo.util.LocalConstants;
import cn.smvp.sdk.demo.util.MyLogger;


public class MainActivity extends Activity {
    private VideoInfoAdapter videoInfoAdapter;

    private InfoReceiver infoReceiver;
    private LocalApplication smvpApplication;
    private List<SmvpVideo> videoList = new ArrayList<SmvpVideo>();
    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        infoReceiver = new InfoReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LocalConstants.ACTION_GET_ALL_VIDEOS_COMPLETED);
        this.registerReceiver(infoReceiver, intentFilter);

        smvpApplication = (LocalApplication) getApplication();
        smvpApplication.onActivityCreate();

        initListView();
        initUpload();
        initDownload();
        initGetVideo();
        initUpdateVideoInfo();
        initCancel();
        initPlay();
    }

    private void initListView() {
        final ListView listView = (ListView) findViewById(R.id.listview);
        TextView emptyView = (TextView) findViewById(R.id.empty);
        listView.setEmptyView(emptyView);
        videoInfoAdapter = new VideoInfoAdapter(this, videoList);
        listView.setAdapter(videoInfoAdapter);
    }

    private void initUpload() {
        Button uploadBtn = (Button) findViewById(R.id.upload);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smvpApplication.getVideoService().uploadVideo();
            }
        });
    }

    private void initDownload() {
        Button downloadBtn = (Button) findViewById(R.id.download);
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smvpApplication.getVideoService().downloadVideo();

            }
        });
    }

    private void initGetVideo() {
        Button downloadBtn = (Button) findViewById(R.id.getItem);
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smvpApplication.getVideoService().getVideo();
            }
        });
    }

    private void initUpdateVideoInfo() {
        Button downloadBtn = (Button) findViewById(R.id.update);
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smvpApplication.getVideoService().updateVideo();
            }
        });
    }

    private void initCancel() {
        Button downloadBtn = (Button) findViewById(R.id.cancel);
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyLogger.d(LOG_TAG, "cancel upload or download");
                smvpApplication.getVideoService().cancel();
            }
        });
    }

    private void initPlay() {
        try {
            Button playBtn = (Button) findViewById(R.id.play_video);
            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyLogger.d(LOG_TAG, "playBtn onClick");
//                smvpApplication.getVideoService().playMP4Video();
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, PlayVideoActivity.class);
                    intent.putExtra("videoId", "648153037914795602");
                    startActivity(intent);
                }
            });

        } catch (Exception e) {
            MyLogger.e(LOG_TAG, "play video exception: ", e);
        }
    }


    private class InfoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (LocalConstants.ACTION_GET_ALL_VIDEOS_COMPLETED.equals(intent.getAction())) {
                String jsonObject = intent.getStringExtra("result");
                videoList.clear();
                videoList = JsonParser.getInstance().parseJsonStringToObject(jsonObject);
                if (videoInfoAdapter != null) {
                    videoInfoAdapter.setData(videoList);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        smvpApplication.unBindService();
        unregisterReceiver(infoReceiver);
    }

}
