package com.sunday.common;

import android.app.Application;
import com.sunday.common.utils.SharedPerencesUtil;

public class MlxLibraryApp {
    private static Application mInstance;

    public static void init(Application application) {
        mInstance = application;
        SharedPerencesUtil.getSharedPreference();

    }

    public static Application getInstance() {
        if (null == mInstance) {
            throw new IllegalArgumentException("please init first");
        }
        return mInstance;
    }
}
