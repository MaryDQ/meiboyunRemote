package com.sunday.common.utils;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2015/6/23.
 */
public class StringUtils {
    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
        if(TextUtils.isEmpty(mobiles)){
            return false;
        }

        if(mobiles.length() != 11){
            return false;
        }

        if(mobiles.charAt(0)!='1'){
            return false;
        }

        return true;
    }

    public static String listToString(List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String string : stringList) {
            if (flag) {
                result.append(",");
            } else {
                flag = true;
            }
            result.append(string);
        }
        return result.toString();
    }

    public static String listToString(Set<String> stringList) {
        if (stringList == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String string : stringList) {
            if (flag) {
                result.append(",");
            } else {
                flag = true;
            }
            result.append(string);
        }
        return result.toString();
    }

    public static String listToString2(List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0;i<stringList.size();i++) {
            if(i<stringList.size()-1){
                result.append(stringList.get(i));
                result.append(",");
            }else{
                result.append(stringList.get(i));
            }
        }
        return result.toString();
    }

    public static String getlistInMap(List<String> stringList, Map<String, String> stringMap) {
        if (stringList == null || stringMap == null) {
            return null;
        }

        String result = "";
        for (int i = 0;i<stringList.size();i++) {
            if(i<stringList.size()-1){
                result = result + stringMap.get(stringList.get(i));
                result = result + ",";
            }else{
                result = result + stringMap.get(stringList.get(i));
            }
        }
        return result;
    }

    public String inputStreamToString(InputStream in) {
        StringBuffer out = new StringBuffer();
        try {
            byte[] b = new byte[4096];
            for (int n; (n = in.read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return out.toString();
        }
        return out.toString();
    }


    public   static   String   inputStream2String(InputStream   is)   throws   IOException{
        ByteArrayOutputStream baos   =   new   ByteArrayOutputStream();
        int   i=-1;
        while((i=is.read())!=-1){
            baos.write(i);
        }
        return   baos.toString();
    }
}
