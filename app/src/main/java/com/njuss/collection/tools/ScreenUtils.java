package com.njuss.collection.tools;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;


public class ScreenUtils {
    /**
     * 获得屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        if (context == null) {
            return 0;
        }
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        return width;
    }

}
