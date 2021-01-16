package com.ehi.capture;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.Nullable;

public class ScreenUtil {
    @Nullable
     public static Point getRealScreenSize(Context mContext) {
        Point mPoint = null;
        try {
            mPoint = new Point();
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display defaultDisplay = windowManager.getDefaultDisplay();
            defaultDisplay.getRealSize(mPoint);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mPoint;
    }
}
