package cn.smvp.sdk.demo.upload;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.smvp.android.sdk.entries.UploadManager;
import cn.smvp.android.sdk.entries.UploadTask;
import cn.smvp.android.sdk.util.SmvpConstants;
import cn.smvp.sdk.demo.R;

/**
 * Created by shangsong on 14-9-23.
 */
public class UploadAdapter extends BaseAdapter {
    private Context mContext;
    private List<UploadTask> data = null;
    private LayoutInflater layoutInflater;
    private UploadManager mUploadManager;

    private static final int PROGRESS_MAX = 100;
    private final String LOG_TAG = this.getClass().getSimpleName();

    public UploadAdapter(Context context, List<UploadTask> data) {
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
            convertView = layoutInflater.inflate(R.layout.upload_listview_item, parent, false);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.upload_title);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.upload_progressbar);
            holder.btnRetry = (Button) convertView.findViewById(R.id.upload_retry);
            holder.btnCancel = (Button) convertView.findViewById(R.id.upload_cancel);
            holder.progressText = (TextView) convertView.findViewById(R.id.upload_progress);
            holder.status = (TextView) convertView.findViewById(R.id.upload_status);

            holder.progressBar.setMax(PROGRESS_MAX);

            holder.onClickListener = new MyOnClickListener();
            holder.btnRetry.setOnClickListener(holder.onClickListener);
            holder.btnCancel.setOnClickListener(holder.onClickListener);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        UploadTask uploadTask = data.get(position);

        int status = uploadTask.getUploadStatus();
        if (SmvpConstants.STATUS_FAILURE == status)
            holder.btnRetry.setClickable(true);
        else {
            holder.btnRetry.setClickable(false);
        }

        holder.title.setText(uploadTask.getUploadData().getTitle());
        holder.onClickListener.setUploadTask(uploadTask);
        holder.progressBar.setProgress(uploadTask.getProgress());
        holder.progressText.setText(mContext.getString(R.string.progress, getProgress(uploadTask)) + '%');
        setStatus(holder, uploadTask.getUploadStatus());

        return convertView;
    }

    public void setData(List<UploadTask> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setUploadManager(UploadManager uploadManager) {
        this.mUploadManager = uploadManager;
    }

    public void showToast(String content) {
        Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
    }

    private void setStatus(ViewHolder holder, int status) {
        String text = null;
        switch (status) {
            case SmvpConstants.STATUS_WAIT:
                text = mContext.getString(R.string.upload_status_wait);
                break;
            case SmvpConstants.STATUS_RUNNING:
                text = mContext.getString(R.string.upload_status_running);
                break;
            case SmvpConstants.STATUS_SUCCESS:
                text = mContext.getString(R.string.upload_status_success);
                break;
            case SmvpConstants.STATUS_FAILURE:
                text = mContext.getString(R.string.upload_status_failure);
                break;

            default:
                break;
        }

        holder.status.setText(mContext.getString(R.string.status, text));
    }

    private class MyOnClickListener implements View.OnClickListener {
        private UploadTask uploadTask;

        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.upload_retry:
                    mUploadManager.upload(uploadTask.getUploadData());
                    break;

                case R.id.upload_cancel:
                    mUploadManager.cancel(uploadTask);
                    break;
                default:
                    break;
            }
        }

        public void setUploadTask(UploadTask uploadTask) {
            this.uploadTask = uploadTask;
        }
    }

    private int getProgress(UploadTask uploadTask) {
        return uploadTask.getProgress();
    }

    private final class ViewHolder {
        Button btnRetry;
        Button btnCancel;
        TextView status;
        TextView title;
        TextView progressText;
        ProgressBar progressBar;
        MyOnClickListener onClickListener;
    }

}
