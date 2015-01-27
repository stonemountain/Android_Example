package cn.smvp.sdk.demo.download;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import cn.smvp.android.sdk.callback.SmvpDownloadListener;
import cn.smvp.android.sdk.entries.DownloadManager;
import cn.smvp.android.sdk.entries.DownloadTask;
import cn.smvp.android.sdk.util.SmvpConstants;
import cn.smvp.android.sdk.view.PlayOfflineVideoActivity;
import cn.smvp.sdk.demo.R;
import cn.smvp.sdk.demo.util.MyLogger;

/**
 * Created by shangsong on 14-9-23.
 */
public class DownloadAdapter extends BaseAdapter {
    private Context mContext;
    private List<DownloadTask> data = null;
    private LayoutInflater layoutInflater;
    private DownloadManager mDownloadManager;
    private SmvpDownloadListener mDownloadListener;

    private static final int PROGRESS_MAX = 100;
    private static final int DELAY_TIME = 1000;
    private final String LOG_TAG = this.getClass().getSimpleName();

    public DownloadAdapter(Context context, List<DownloadTask> data) {
        this.mContext = context;
        this.data = data;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.download_listview_item, parent, false);

            holder = new ViewHolder();
            holder.videoId = (TextView) convertView.findViewById(R.id.download_id);
            holder.videoDefinition = (TextView) convertView.findViewById(R.id.download_defenition);

            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.download_progressbar);
            holder.btnStart = (Button) convertView.findViewById(R.id.download_start);
            holder.btnCancel = (Button) convertView.findViewById(R.id.download_cancel);
            holder.btnPlay = (Button) convertView.findViewById(R.id.download_play);
            holder.progressText = (TextView) convertView.findViewById(R.id.download_progress);
            holder.status = (TextView) convertView.findViewById(R.id.download_status);

            holder.progressBar.setMax(PROGRESS_MAX);

            holder.onClickListener = new MyOnClickListener();
            holder.btnStart.setOnClickListener(holder.onClickListener);
            holder.btnStart.setTag(true);
            holder.btnCancel.setOnClickListener(holder.onClickListener);
            holder.btnPlay.setOnClickListener(holder.onClickListener);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DownloadTask downloadTask = data.get(position);
        holder.videoId.setText(mContext.getString(R.string.video_id,
                downloadTask.getVideoId()));
        holder.videoDefinition.setText(mContext.getString(R.string.video_defenition,
                downloadTask.getDefinition()));

        if (downloadTask.isRunning()) {
            holder.btnStart.setTag(true);
            holder.btnStart.setText(mContext.getString(R.string.pause));
        } else {
            holder.btnStart.setTag(false);
            holder.btnStart.setText(mContext.getString(R.string.start));
        }

        holder.onClickListener.setDownloadTask(downloadTask);
        holder.progressBar.setTag(downloadTask);
        holder.progressBar.setProgress(downloadTask.getProgress());
        holder.progressText.setText(mContext.getString(R.string.progress, downloadTask.getProgress()) + "%");
        setStatus(holder, downloadTask.getDownloadStatus());

        return convertView;
    }

    public void setData(List<DownloadTask> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    private void setStatus(ViewHolder holder, int status) {
        String text = null;
        switch (status) {
            case SmvpConstants.STATUS_WAIT:
                text = "WAIT";
                holder.btnPlay.setClickable(false);
                break;
            case SmvpConstants.STATUS_RUNNING:
                text = "RUNNING";
                break;
            case SmvpConstants.STATUS_STOP:
                text = "PAUSE";
                break;
            case SmvpConstants.STATUS_SUCCESS:
                text = "SUCCESS";
                holder.btnPlay.setClickable(true);
                break;
            case SmvpConstants.STATUS_CANCELLED:
                text = "CANCELL";
                break;
            case SmvpConstants.STATUS_FAILURE:
                text = "ERROR";
                break;

            default:
                text = "WAIT";
                holder.btnPlay.setClickable(false);
                break;
        }

        holder.status.setText(mContext.getString(R.string.status, text));
    }

    public void setDownloadListener(SmvpDownloadListener downloadListener) {
        this.mDownloadListener = downloadListener;
    }

    private class MyOnClickListener implements View.OnClickListener {
        private DownloadTask downloadTask;

        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.download_start:
                    boolean tag = !(Boolean) view.getTag();
                    if (tag) {
                        MyLogger.w(LOG_TAG, "start,");
                        downloadTask = mDownloadManager.download(downloadTask.getDownloadData());
                        ((Button) view).setText(mContext.getString(R.string.pause));
                    } else {
                        MyLogger.w(LOG_TAG, "stop,");
                        mDownloadManager.stop(downloadTask);
                        ((Button) view).setText(mContext.getString(R.string.start));
                    }

                    view.setTag(tag);
                    break;
                case R.id.download_cancel:
                    mDownloadManager.cancel(downloadTask);
                    MyLogger.w(LOG_TAG, "download_cancel,");
                    break;

                case R.id.download_play:
                    Intent intent = new Intent();
                    intent.setClass(mContext, PlayOfflineVideoActivity.class);
                    intent.putExtra(SmvpConstants.KEY_AUTO_START, false);
                    intent.putExtra(SmvpConstants.KEY_VIDEO_DIRECTORY, downloadTask.getStorageDirectory().getAbsolutePath());
                    mContext.startActivity(intent);
                    break;
                default:
                    break;
            }
        }

        public void setDownloadTask(DownloadTask downloadTask) {
            this.downloadTask = downloadTask;
        }
    }

    public void setDownloadManager(DownloadManager downloadManager) {
        this.mDownloadManager = downloadManager;
    }

    private final class ViewHolder {
        TextView videoId;
        TextView videoDefinition;
        Button btnStart;
        Button btnCancel;
        Button btnPlay;
        TextView progressText;
        TextView status;
        ProgressBar progressBar;
        MyOnClickListener onClickListener;
    }


}
