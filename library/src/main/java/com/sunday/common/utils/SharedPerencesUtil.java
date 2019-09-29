package com.sunday.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.sunday.common.MlxLibraryApp;

/**
 * on 2017/10/24.
 */
public class SharedPerencesUtil {
    private static final String SPNAME = "meiboyun_APP_SP";
    private static SharedPreferences sp = null;

    public static SharedPreferences.Editor getSharedPreference() {
        if (null == sp) {
            sp = MlxLibraryApp.getInstance().getSharedPreferences(SPNAME, Context.MODE_PRIVATE);
        }
        return sp.edit();
    }

    public static SharedPreferences getSharedPreference(Context context) {
        if (null == sp) {
            sp = MlxLibraryApp.getInstance().getSharedPreferences(SPNAME, Context.MODE_PRIVATE);
        }
        return sp;
    }

    public static void putString(String name, String value) {
        getSharedPreference().putString(name, value).apply();
    }

    public static void putInt(String name, int value) {
        getSharedPreference().putInt(name, value).apply();
    }

    public static String getString(String name) {
        return sp.getString(name, "");
    }

    public static int getInt(String name) {
        return sp.getInt(name, -1);
    }

}
