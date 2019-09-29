package com.sunday.common.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 描述：捕获异常信息，保存到本地
 * 作者: mlx
 * 创建时间： 2019/7/1
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";

    private static final boolean DEBUG = true;
    private static final String FILE_NAME = "crash";
    private static final String FILE_NAME_SUFFIX = ".txt";
    /**
     * Log目标文件夹
     */
    private static String tempName="mlxLogs";
    private static String PATH;
    private static volatile CrashHandler sInstance = new CrashHandler();

    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;

    private Context mContext;

    private CrashHandler() {
    }

    public static CrashHandler getsInstance() {
        return sInstance;
    }

    public void init(Context context, String dirName) {
        if (!TextUtils.isEmpty(dirName)) {
            tempName=dirName;
        }
        PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + tempName + "/Log/";
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
        File dir = new File(PATH);
    }

    public static String getPATH() {
        return PATH == null ? "" : PATH;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        //到处异常信息到SD卡中
        dumpExceptionToSDCard(throwable);
        if (null==thread) {
            return;
        }
        throwable.printStackTrace();
        if (null != mDefaultCrashHandler) {
            mDefaultCrashHandler.uncaughtException(thread, throwable);
        } else {
            Process.killProcess(Process.myPid());
        }
    }


    private void dumpExceptionToSDCard(Throwable ex) {
        //如果SD卡不存在或无法使用，则无法把异常信息写入SD卡
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (DEBUG) {
                Log.w(TAG, "sdcard unmounted,skip dump exception");
            }
            PATH = mContext.getCacheDir().getPath();
        }

        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(current));
        File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println(time);
            dumpPhoneInfo(pw);
            pw.println();
            ex.printStackTrace(pw);
            pw.close();
        } catch (Exception e) {
            Log.e(TAG, "dump crash info failed");
        }
    }

    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        pw.println("App Version:");
        pw.print(pi.versionName);
        pw.print('_');
        pw.println(pi.versionCode);

        //Android制造商

        pw.println("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);

        //手机制造商
        pw.println("Vendor: ");
        pw.println(Build.MANUFACTURER);

        //手机型号
        pw.println("Model: ");
        pw.println(Build.MODEL);

        //CPU架构
        pw.println("CPU ABI: ");
        pw.println(Build.CPU_ABI);


    }


}
