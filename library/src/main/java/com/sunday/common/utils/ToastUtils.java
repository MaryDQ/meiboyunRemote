package com.sunday.common.utils;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.widget.Toast;
import com.sunday.common.MlxLibraryApp;


/**
 * Created by mlx on 17-10-27.
 */
public class ToastUtils {

    private static final boolean IS_DEBUG = false;

    private static Toast mToast;

    public static void show(int resId) {
        show(MlxLibraryApp.getInstance().getString(resId));
    }

    @SuppressLint("ShowToast")
    public static void show(String msg) {
        if (null == mToast) {
            mToast = Toast.makeText(MlxLibraryApp.getInstance(), msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }

        mToast.setGravity(Gravity.CENTER, 0, 0);

        mToast.show();
    }

    @SuppressLint("ShowToast")
    public static void debugShow(String msg) {
        if (IS_DEBUG) {
            return;
        }
        if (null == mToast) {
            mToast = Toast.makeText(MlxLibraryApp.getInstance(), msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }

        mToast.show();
    }


}
