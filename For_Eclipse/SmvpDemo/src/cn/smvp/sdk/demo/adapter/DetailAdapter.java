package cn.smvp.sdk.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.smvp.android.sdk.util.SmvpVideoData;
import cn.smvp.sdk.demo.R;

/**
 * Created by shangsong on 15-2-2.
 */
public class DetailAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<SmvpVideoData> mDataList;

    public DetailAdapter(Context context, List<SmvpVideoData> data) {
        mDataList = data;
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.detail_listview_item, parent, false);

            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.name = (TextView) convertView.findViewById(R.id.value);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    public void setData(List<SmvpVideoData> data) {
        mDataList = data;
        notifyDataSetChanged();
    }

    static final class ViewHolder {
        private TextView name;
        private TextView value;
    }

}
