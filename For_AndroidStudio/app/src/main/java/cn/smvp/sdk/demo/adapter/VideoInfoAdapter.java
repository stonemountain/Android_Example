package cn.smvp.sdk.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.smvp.android.sdk.util.SmvpVideoData;
import cn.smvp.sdk.demo.R;
import cn.smvp.sdk.demo.util.ImageDownLoader;

/**
 * Created by shangsong on 14-9-23.
 */
public class VideoInfoAdapter extends BaseAdapter {
    private Context context;
    private List<SmvpVideoData> videoList = null;
    private LayoutInflater layoutInflater;
    private final ImageDownLoader imageLoader = ImageDownLoader.getInstance();

    public VideoInfoAdapter(Context context, List<SmvpVideoData> videoList) {
        this.context = context;
        this.videoList = videoList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.videoList.size();
    }

    @Override
    public Object getItem(int position) {
        return videoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.gridview_item, parent, false);

            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            holder.title = (TextView) convertView.findViewById(R.id.title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SmvpVideoData smvpVideoData = videoList.get(position);
        holder.title.setText(smvpVideoData.getTitle());

        String imageUrl = smvpVideoData.getThumbnail_url();
        imageLoader.downloadImage(imageUrl, holder.imageView);

        return convertView;
    }

    public void setData(List<SmvpVideoData> videoList) {
        this.videoList = videoList;
        notifyDataSetChanged();
    }

    private final class ViewHolder {
        ImageView imageView;
        TextView title;
    }
}