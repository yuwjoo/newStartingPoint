package com.example.gamecontroller.utils;

import android.content.Context;
import android.content.res.Configuration;

public class WindowState {

    /**
     * 是否时横屏
     *
     * @param context 上下文
     * @return 是否横屏
     */
    public static boolean isLandscape(Context context) {
        int orientation = context.getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}
