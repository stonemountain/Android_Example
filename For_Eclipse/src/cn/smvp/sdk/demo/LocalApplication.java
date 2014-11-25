package cn.smvp.sdk.demo;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import cn.smvp.sdk.demo.smvp.VideoService;
import cn.smvp.sdk.demo.util.ImageDownLoader;
import cn.smvp.sdk.demo.util.MyLogger;


/**
 * Created by shangsong on 14-9-23.
 */
public class LocalApplication extends Application {

    private final MyLogger myLogger = new MyLogger(this.getClass().getSimpleName());

    private VideoService videoService=null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        clearImageCache();
        myLogger.d("onLowMemory:clear cache");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        clearImageCache();
        myLogger.d("onTerminate:clear cache");
    }

    private void clearImageCache() {
         ImageDownLoader.getInstance().clear();
    }

    public void onActivityCreate(){
        Intent intent=new Intent(this,VideoService.class);
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            myLogger.i("onServiceConnected");
            videoService=((VideoService.LocalBinder)iBinder).getService();
            videoService.getVideoList();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myLogger.w("service disconnected");

            unbindService(this);
            videoService=null;
        }
    };

    public VideoService getVideoService(){
        return  videoService;
    }

    public void unBindService(){
        if(videoService!=null){
            videoService.cancel();
            unbindService(serviceConnection);
        }

    }

}
