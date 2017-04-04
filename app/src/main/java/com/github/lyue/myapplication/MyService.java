package com.github.lyue.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by yue_liang on 2017/3/29.
 */

public class MyService extends Service{
    private class My extends IMyAidlInterface.Stub {

        @Override
        public void basicTypes() throws RemoteException {
            Log.d("yueliang", "got service");
        }
    }
    private My my;
    @Override
    public void onCreate() {
        super.onCreate();
        my = new My();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return my.asBinder();
    }
}
