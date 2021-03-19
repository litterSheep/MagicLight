package com.ly.magiclight.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import java.lang.reflect.Method;

/**
 * Created by ly on 2017/2/10 15:34.
 */

public class ScreenUtil {

    public static int getStatusBarHeight(@NonNull Context context) {
        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = context.getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = context.getApplicationContext().getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight1;
    }

    /**
     * 获取底部导航栏高度
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        //获取NavigationBar的高度
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 判断是否存在NavigationBar
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            hasNavigationBar = !hasMenuKey && !hasBackKey;//没有物理菜单、返回键则认为有虚拟导航栏
        }
        return hasNavigationBar;
    }

    public static int getScreenWidth(@NonNull Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (wm != null) {
                DisplayMetrics outMetrics = new DisplayMetrics();
                wm.getDefaultDisplay().getRealMetrics(outMetrics);
                return outMetrics.widthPixels;
            }
        }
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(@NonNull Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (wm != null) {
                DisplayMetrics outMetrics = new DisplayMetrics();
                wm.getDefaultDisplay().getRealMetrics(outMetrics);
                return outMetrics.heightPixels;
            }
        }
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenDpi(@NonNull Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }


    public static boolean isAllScreenDevice(@NonNull Context context) {
        // 低于 API 21的，都不会是全面屏。。。
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        return getScreenHeight(context) * 1f / getScreenWidth(context) >= 1.97f;
    }

    /**
     * 设置当前界面亮度值
     */
    public static void setCurrentScreenBrightness(Activity activity, @FloatRange(from = 0.0f, to = 1.0f) float percent) {
        WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
        layoutParams.screenBrightness = percent;
        activity.getWindow().setAttributes(layoutParams);
    }

    public static float getCurrentScreenBrightness(Activity activity) {
        int brightness = Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255 / 2);
        return brightness / 255f;
    }
}
