package com.ehi.capture;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

/**
 * @author  EdisonLi
 * @version 1.0
 * @since 2020/12/24 16:49
 * desc : 设置文件观察者
 */
public class CaptureFileObserver extends ContentObserver {

    private final Uri mContentUri;
    private final CaptureCallback mCaptureCallback;

    public CaptureFileObserver(Uri contentUri, CaptureCallback captureCallback, Handler handler) {
        super(handler);
        mCaptureCallback = captureCallback;
        mContentUri = contentUri;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        // 触发了截屏 注意这里会多次回调 要优化下
        if (mCaptureCallback != null){
            mCaptureCallback.onMediaFileChanged(mContentUri);
        }
    }

    /**
     * 内容观察者回调事件
     */
    public interface CaptureCallback {

        void onMediaFileChanged(Uri contentUri);
    }
}
