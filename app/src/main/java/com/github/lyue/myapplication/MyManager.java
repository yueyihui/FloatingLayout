package com.github.lyue.myapplication;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * Created by yue_liang on 2017/3/29.
 */

public class MyManager {
    private IMyAidlInterface mService;
    public MyManager(IBinder service) {
        mService = IMyAidlInterface.Stub.asInterface(service);
    }
    public void basicTypes() throws RemoteException {
        mService.basicTypes();
    }
    public static Intent getIntent() {
        Intent intent = new Intent();
        intent.setAction("com.github.lyue.myapplication.MyService");
        intent.setPackage("com.github.lyue.myapplication");
        return intent;
    }
}
