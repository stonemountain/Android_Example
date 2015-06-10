
package cn.smvp.sdk.demo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.smvp.android.sdk.callback.ResponseListener;
import cn.smvp.android.sdk.util.VideoData;
import cn.smvp.sdk.demo.LocalApplication;
import cn.smvp.sdk.demo.PlayVideoActivity;
import cn.smvp.sdk.demo.R;
import cn.smvp.sdk.demo.VideoService;
import cn.smvp.sdk.demo.adapter.VideoInfoAdapter;
import cn.smvp.sdk.demo.util.MyLogger;


public class VideoFragment extends Fragment {
    private final int LOAD_COUNTS = 5;
    private VideoInfoAdapter videoInfoAdapter;
    private LocalApplication mApplication;
    private PullToRefreshGridView mRefreshGridView;
    private View mLoadingView;
    private ArrayList<VideoData> videoList = new ArrayList<VideoData>();
    private final String LOG_TAG = VideoFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_video, container, false);
        initView(root);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mApplication = (LocalApplication) getActivity().getApplication();
        onRefresh();
    }

    private void initView(View rootView) {
        mLoadingView = rootView.findViewById(R.id.loading_view);

        mRefreshGridView = (PullToRefreshGridView) rootView.findViewById(R.id.refresh_gridview);
        TextView emptyView = (TextView) rootView.findViewById(R.id.empty);
        mRefreshGridView.setEmptyView(emptyView);
        videoInfoAdapter = new VideoInfoAdapter(getActivity(), videoList);
        mRefreshGridView.setAdapter(videoInfoAdapter);

        mRefreshGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoData videoData = videoList.get(position);
                if (videoData == null)
                    return;

                Intent intent = new Intent();
                intent.setClass(getActivity(), PlayVideoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", videoData);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                String label = DateUtils.formatDateTime(
                        mApplication, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                // 显示最后更新的时间
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                onRefresh();
                MyLogger.i(LOG_TAG, "onPullDownToRefresh");

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                loadMore();
                MyLogger.i(LOG_TAG, "onPullUpToRefresh");
            }
        });


        initIndicator();
    }

    private void initIndicator() {
        ILoadingLayout startLabels = mRefreshGridView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel(getString(R.string.pull_down_refresh));
        startLabels.setRefreshingLabel(getString(R.string.loading));
        startLabels.setReleaseLabel(getString(R.string.release_to_refresh1));

        ILoadingLayout endLabels = mRefreshGridView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel(getString(R.string.pull_up_refresh));
        endLabels.setReleaseLabel(getString(R.string.release_to_refresh2));
        endLabels.setRefreshingLabel(getString(R.string.loading));
    }

    private LocalApplication.ServiceListener refreshListener = new LocalApplication.ServiceListener() {
        @Override
        public void onServiceDisconnected(VideoService service) {
            service.list(0, LOAD_COUNTS, null, new ResponseListener() {
                @Override
                public void onSuccess(String response) {
                    MyLogger.i(LOG_TAG, "onSuccess");
                    videoList.clear();
                    videoList = getVideoList(response);
                    if (videoInfoAdapter != null) {
                        videoInfoAdapter.setData(videoList);
                    }

                    mLoadingView.setVisibility(View.GONE);
                    mRefreshGridView.setVisibility(View.VISIBLE);
                    mRefreshGridView.onRefreshComplete();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    MyLogger.e(LOG_TAG, "onFailure,load failed", throwable);
                    mLoadingView.setVisibility(View.GONE);
                    mRefreshGridView.setVisibility(View.VISIBLE);
                    mRefreshGridView.onRefreshComplete();
                }
            });
        }
    };

    private LocalApplication.ServiceListener loadMoreListener = new LocalApplication.ServiceListener() {
        @Override
        public void onServiceDisconnected(VideoService service) {
            int first = videoInfoAdapter.getCount();
            service.getVideoManager().list(first, LOAD_COUNTS, null, new ResponseListener() {
                @Override
                public void onSuccess(String response) {
                    ArrayList<VideoData> moreData = getVideoList(response);
                    videoList.addAll(moreData);
                    if (videoInfoAdapter != null) {
                        videoInfoAdapter.notifyDataSetChanged();
                    }

                    mRefreshGridView.onRefreshComplete();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    mRefreshGridView.onRefreshComplete();
                }
            });
        }
    };

    private void onRefresh() {
        MyLogger.i(LOG_TAG, "onRefresh");
        mApplication.getVideoService(refreshListener);
    }

    private void loadMore() {
        MyLogger.i(LOG_TAG, "loadMore");
        mApplication.getVideoService(loadMoreListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLogger.i(LOG_TAG, "onDestroy");
    }

    public ArrayList<VideoData> getVideoList(String jsonString) {
        try {
            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray("items");
            Gson gson = new Gson();
            Type type = new TypeToken<List<VideoData>>() {
            }.getType();
            ArrayList<VideoData> videoList = gson.fromJson(jsonArray.toString(), type);

            return videoList;
        } catch (Exception e) {
            MyLogger.e(LOG_TAG, "Exception occurred while load more video", e);
        }

        return null;
    }

}
