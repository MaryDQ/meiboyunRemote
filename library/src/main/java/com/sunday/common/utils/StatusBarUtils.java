package com.sunday.common.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.*;
import android.widget.FrameLayout;

public class StatusBarUtils {

    private static final String TAG_FAKE_STATUS_BAR_VIEW = "statusBarView";
    private static final String TAG_MARGIN_ADDED = "marginAdded";

    /**
     * 对于android4.4，系统没有提供设置状态栏颜色的方法，只用手工搞个假冒的状态栏来占坑
     *
     * @param activity       需要设置的activity
     * @param statusBarColor 需要设置的状态栏颜色
     */
    private static void setKitKatStatusBarColor(Activity activity, int statusBarColor) {
        Window window = activity.getWindow();
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        //先移除已有的冒牌状态栏
        View fakeView = decorView.findViewWithTag(TAG_FAKE_STATUS_BAR_VIEW);
        if (null != fakeView) {
            //从根试图移除旧状态栏
            decorView.removeView(decorView);
        }
        //添加新的冒牌状态栏
        View statusBarView = new View(activity);
        //这里params的height应该是当前手机的状态栏高度
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50);
        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(statusBarColor);
        statusBarView.setTag(TAG_FAKE_STATUS_BAR_VIEW);
        decorView.addView(statusBarView);
    }

    /**
     * 重置状态栏，恢复成系统默认的黑色
     *
     * @param activity 需要设置的activity
     */
    public static void reset(Activity activity) {
        setStatusBarColor(activity, Color.BLACK);
    }

    /**
     * 添加顶部间隔，留出状态栏的位置
     *
     * @param activity 需要设置的activity
     */
    private static void addMarginTop(Activity activity) {
        Window window = activity.getWindow();
        ViewGroup contentView = window.findViewById(Window.ID_ANDROID_CONTENT);
        View child = contentView.getChildAt(0);
        if (!TAG_MARGIN_ADDED.equals(child.getTag())) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) child.getLayoutParams();
            //此处添加的margin就是当前手机的状态栏高度
            params.topMargin += 50;
            child.setLayoutParams(params);
            child.setTag(TAG_MARGIN_ADDED);
        }
    }

    /**
     * 移除顶部间隔，霸占状态栏的位置
     *
     * @param activity 需要设置的activity
     */
    private static void removeMarginTop(Activity activity) {
        Window window = activity.getWindow();
        ViewGroup contentView = window.findViewById(Window.ID_ANDROID_CONTENT);
        View child = contentView.getChildAt(0);
        if (TAG_MARGIN_ADDED.equals(child.getTag())) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) child.getLayoutParams();
            //此处添加的margin就是当前手机的状态栏高度
            params.topMargin -= 50;
            child.setLayoutParams(params);
            child.setTag(null);
        }
    }

    /**
     * 设置状态栏的背景色,对于android4.4和5.0以上版本要区分处理
     *
     * @param activity
     * @param color
     */
    public static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().setStatusBarColor(color);
            } else {
                setKitKatStatusBarColor(activity, color);
            }
            //透明背景表示要悬浮状态栏
            if (color == Color.TRANSPARENT) {
                removeMarginTop(activity);
            } else {
                addMarginTop(activity);
            }
        } else {
            //4.4以下版本不做处理
        }
    }

    public static void initSystemBarColor(Activity activity, Boolean isLight, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            if (isLight) {
                window.setStatusBarColor(color);
            } else {
                window.setStatusBarColor(color);
            }

            //状态栏颜色接近于白色，文字图标变成黑色
            View decor = window.getDecorView();
            int ui = decor.getSystemUiVisibility();
            if (!isLight) {
                //light --> a|=b的意思就是把a和b按位或然后赋值给a,   按位或的意思就是先把a和b都换成2进制，然后用或操作，相当于a=a|b
                ui |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                //dark  --> &是位运算里面，与运算,  a&=b相当于 a = a&b,  ~非运算符
                ui &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decor.setSystemUiVisibility(ui);
        }
    }

}