package cn.smvp.sdk.demo.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.smvp.android.sdk.util.SmvpVideoData;
import cn.smvp.sdk.demo.R;

public class DetailFragment extends ListFragment {
    private SmvpVideoData mVideoData;

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mVideoData = getArguments().getParcelable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setData();
    }

    private void setData() {
        ArrayList<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>(5);
        Map<String, Object> idMap = new HashMap<String, Object>(2);
        idMap.put("name", "id");
        idMap.put("value", mVideoData.getId());
        dataList.add(idMap);

        Map<String, Object> titleMap = new HashMap<String, Object>(2);
        titleMap.put("name", "title");
        titleMap.put("value", mVideoData.getTitle());
        dataList.add(titleMap);

        Map<String, Object> descriptionMap = new HashMap<String, Object>(2);
        descriptionMap.put("name", "description");
        descriptionMap.put("value", mVideoData.getDescription());
        dataList.add(descriptionMap);


        StringBuilder builder = new StringBuilder();
        if (mVideoData.getTags() != null) {
            String[] tags = mVideoData.getTags();
            for (int index = 0; index < tags.length; index++) {
                if (index != 0) {
                    builder.append(',');
                }
                builder.append(tags[index]);
            }
        }
        Map<String, Object> tagMap = new HashMap<String, Object>(2);
        tagMap.put("name", "tags");
        tagMap.put("value", builder.toString());
        dataList.add(tagMap);

        Map<String, Object> activatedMap = new HashMap<String, Object>(2);
        activatedMap.put("name", "activated");
        activatedMap.put("value", mVideoData.isActivated());
        dataList.add(activatedMap);

        String[] from = new String[]{
                "name", "value"
        };

        int[] to = new int[]{
                R.id.name, R.id.value
        };

        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), dataList,
                R.layout.detail_listview_item, from, to);
        getListView().setAdapter(simpleAdapter);
    }
}
