package cn.smvp.sdk.demo.util;

import android.util.Log;

public class MyLogger {
    private final String TAG = "smvpsdk";
    // private boolean isDebug = android.os.Build.TYPE.startsWith("user") ?
    // Log.isLoggable(TAG,
    // Log.DEBUG) : true;
    private boolean isDebug = true;
    private String mClassName;

    public MyLogger(String name) {
        mClassName = name;
    }

    public void v(String log) {
        if (isDebug) {
            Log.v(TAG, "[" + mClassName + "]:" + log);
        }
    }

    public void d(String log) {
        if (isDebug) {
            Log.d(TAG, "[" + mClassName + "]:" + log);
        }
    }

    public void i(String log) {
        if (isDebug) {
            Log.i(TAG, "[" + mClassName + "]:" + log);
        }
    }

    public void i(String log, Throwable tr) {
        if (isDebug) {
            Log.i(TAG, "[" + mClassName + "]:" + log + "\n" + Log.getStackTraceString(tr));
        }
    }

    public void w(String log) {
        if (isDebug) {
            Log.w(TAG, "[" + mClassName + "]:" + log);
        }
    }

    public void w(String log, Throwable tr) {
        if (isDebug) {
            Log.w(TAG, "[" + mClassName + "]:" + log + "\n" + Log.getStackTraceString(tr));
        }
    }

    public void e(String log) {
        if (isDebug) {
            Log.e(TAG, "[" + mClassName + "]:" + log);
        }
    }

    public void e(String log, Throwable tr) {
        if (isDebug) {
            Log.e(TAG, "[" + mClassName + "]:" + log + "\n" + Log.getStackTraceString(tr));
        }
    }
}
