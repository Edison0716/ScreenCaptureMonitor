package com.ehi.capture;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author  EdisonLi
 * @version 1.0
 * @since 2020/12/24 17:43
 * desc : 专门用来获取媒体图片
 */
public class MediaImageObserver extends MediaFileBaseObserver {
    private static final String TAG = MediaImageObserver.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private static volatile MediaImageObserver mInstance = null;

    private static final String[] MEDIA_STORE_IMAGE = {
            MediaStore.Images.ImageColumns.DATA,
            // 时间 这里不能用 Date_ADD 因为是秒级 按时间筛选不准确
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            // 宽
            MediaStore.Images.ImageColumns.WIDTH
    };

    // 截屏关键词 随时补充
    private static final String[] KEYWORDS = {
            "screenshot", "screen_shot", "screen-shot", "screen shot",
            "screencapture", "screen_capture", "screen-capture", "screen capture",
            "screencap", "screen_cap", "screen-cap", "screen cap", "Screenshot","截屏"
    };

    // 按照日期插入的顺序取第一条
    private final static String QUERY_ORDER_SQL = ImageColumns.DATE_ADDED + " DESC LIMIT 1";
    private final Point mPoint;

    public static MediaFileBaseObserver getInstance(Application application) {
        if (mInstance == null) {
            synchronized (MediaFileBaseObserver.class) {
                if (mInstance == null) {
                    mInstance = new MediaImageObserver(application.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    public MediaImageObserver(Context context) {
        super(context);
        mPoint = ScreenUtil.getRealScreenSize(context);
    }

    @Override
    void acquireTargetFile(Uri contentUri) {
        Cursor cursor = null;

        try {
            // 查找
            cursor =  mContentResolver.query(contentUri, MEDIA_STORE_IMAGE, null, null, QUERY_ORDER_SQL);
            findImagePathByCursor(cursor);
        }catch (Exception e){
            if (e.getMessage() != null) {
                Log.e(TAG, e.getMessage());
            } else {
                e.printStackTrace();
            }
        }finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
    }

    private void findImagePathByCursor(Cursor cursor) {
        if (cursor == null) {
            return;
        }

        if (!cursor.moveToFirst()){
            Log.d(TAG,"Cannot find newest image file");
            return;
        }

        // 获取 文件索引
        int imageColumnIndexData = cursor.getColumnIndex(ImageColumns.DATA);
        int imageCreateDateIndexData = cursor.getColumnIndex(ImageColumns.DATE_TAKEN);
        int imageWidthColumnIndexData = cursor.getColumnIndex(ImageColumns.WIDTH);

        String imagePath = cursor.getString(imageColumnIndexData);
        int imageWidth = cursor.getInt(imageWidthColumnIndexData);
        long imageCreateDate = cursor.getLong(imageCreateDateIndexData);

        // 时间判断 判断截屏时间 与 截屏图片实际生成时间的差
        if (imageCreateDate < mStartListenTime || (System.currentTimeMillis() - imageCreateDate) > 2 * 1000) {
           return;
        }
        // 这里只判断width 长截屏无法判断
        if (mPoint != null && mPoint.x != imageWidth){
            return;
        }
        // path 为空
        if (TextUtils.isEmpty(imagePath)){
            return;
        }
        // 判断关键词
        String lowerCasePath = imagePath.toLowerCase();
        // 关键词比对
        for (String keyword : KEYWORDS) {
            if (lowerCasePath.contains(keyword)){
                if (mCaptureCallback != null) {
                    mCaptureCallback.capture(imagePath);
                }
                break;
            }
        }
    }
}
