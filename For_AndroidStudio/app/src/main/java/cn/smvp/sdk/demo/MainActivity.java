
package cn.smvp.sdk.demo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cn.smvp.android.sdk.util.SmvpVideoData;
import cn.smvp.sdk.demo.adapter.VideoInfoAdapter;
import cn.smvp.sdk.demo.smvp.JsonParser;
import cn.smvp.sdk.demo.util.LocalConstants;
import cn.smvp.sdk.demo.util.MyLogger;


public class MainActivity extends Activity {
    private VideoInfoAdapter videoInfoAdapter;

    private InfoReceiver infoReceiver;
    private LocalApplication smvpApplication;
    private List<SmvpVideoData> videoList = new ArrayList<SmvpVideoData>();
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

        try {
            initListView();
            initUpload();
            initDownload();
            initGetVideo();
            initUpdateVideoInfo();
        } catch (Exception e) {
            MyLogger.e(LOG_TAG, "exception: ", e);
        }

    }

    private void initListView() {
        final GridView gridView = (GridView) findViewById(R.id.gridview);
        TextView emptyView = (TextView) findViewById(R.id.empty);
        gridView.setEmptyView(emptyView);
        videoInfoAdapter = new VideoInfoAdapter(this, videoList);
        gridView.setAdapter(videoInfoAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SmvpVideoData smvpVideoData = videoList.get(position);

                Gson gson = new Gson();
                String videoInfo = gson.toJson(smvpVideoData);
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PlayVideoActivity.class);

                Bundle bundle = new Bundle();
                bundle.putParcelable("data", smvpVideoData);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initUpload() {
        Button uploadBtn = (Button) findViewById(R.id.upload);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smvpApplication.getVideoService().upload(MainActivity.this);
            }
        });
    }

    private void initDownload() {
        Button downloadBtn = (Button) findViewById(R.id.download);
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smvpApplication.getVideoService().download(MainActivity.this);

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

    private class InfoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (LocalConstants.ACTION_GET_ALL_VIDEOS_COMPLETED.equals(intent.getAction())) {
                String jsonObject = intent.getStringExtra("result");
//                MyLogger.i(LOG_TAG, "result=" + jsonObject);

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

        unregisterReceiver(infoReceiver);
        smvpApplication.clear();
    }

}
