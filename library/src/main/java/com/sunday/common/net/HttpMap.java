package com.sunday.common.net;


import com.sunday.common.utils.CryptUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 对请求参数进行统一的管理，同时可以根据类型来动态生成相应的请求map
 * Created by Ace on 2016/6/16.
 */
public class HttpMap {
    public enum MapType {
        NORMAL, //标准类型，不对参数做任何处理
        SIGN    //对请求map进行签名（预留）
    }

    /**
     * 根据MapType生成相应的HttMap
     * @param mapType   HttpMap类型
     * @param action    HttpMap操作
     * @return  根据类型和处理后的HttpMap
     */
    public static HashMap<String, String> getMap(MapType mapType, Action action) {
        HashMap<String, String> map = new HashMap<>();
        if(action != null) {
            action.addParams(map);
        }
        switch (mapType) {
            case NORMAL:
                break;
            case SIGN:
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    map.put(entry.getKey(), CryptUtil.aesEncryptParameter(entry.getValue()));
                }
                break;
            default:
                break;
        }

        return map;
    }

    /**
     * 生成默认类型STANDARD的HttpMap
     * @param action    HttpMap操作
     * @return  根据类型和处理后的HttpMap
     */
    public static HashMap<String, String> getMap(Action action) {
        return getMap(MapType.NORMAL,action);
    }

    /**
     * 根据MapType生成HttpMap,适用于不带请求参数的情况
     * @param mapType   HttpMap类型
     * @return  根据类型和处理后的HttpMap
     */
    public static HashMap<String, String> getMap(MapType mapType) {
        return getMap(mapType,null);
    }

    /**
     * 生成默认类型STANDARD的HttpMap,适用于不带请求参数的情况
     * @return  根据类型和处理后的HttpMap
     */
    public static HashMap<String, String> getMap() {
        return getMap(MapType.NORMAL,null);
    }



    /**
     * 对HttpMap进行编辑的操作
     */
    public interface Action {
        void addParams(HashMap<String, String> map);
    }
}
