package com.ehi.screencapturemonitor;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("onCreate", "onCreate");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d("onAttach", "onAttach");
    }
}
