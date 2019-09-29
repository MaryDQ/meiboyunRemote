package com.microsys.imb.remote.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.microsys.imb.remote.R;
import com.microsys.imb.remote.app.MyApplication;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;

import java.io.ByteArrayOutputStream;

/**
 * 描述：微信分享管理类
 * 作者: mlx
 * 创建时间： 2019/7/8
 */
public class WechatShareUtils {
    public static final int FRIEND = 1;
    public static final int MOMENT = 2;
    private static String shareUrl = "https://api.imbcloud.cn/h5/share/channel?hls=1&share_type=wx&channel_id=自己应用的APPID";

    /**
     * @param shareType 1分享给朋友,2分享给朋友圈
     */
    public static void share(Context context, int shareType) {
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_app_desk);

        if (FRIEND == shareType) {

            WXWebpageObject webpageToFriend = new WXWebpageObject();
            webpageToFriend.webpageUrl = shareUrl;

            WXMediaMessage msgToFriend = new WXMediaMessage(webpageToFriend);
            msgToFriend.title = "某某和某某的婚礼";
            msgToFriend.description = "快来观看某某和某某的婚礼吧";

            msgToFriend.thumbData = bmpToByteArray(bmp);

            SendMessageToWX.Req reqToFriend = new SendMessageToWX.Req();
            reqToFriend.transaction = buildTransaction("webpage");
            reqToFriend.message = msgToFriend;
            reqToFriend.scene = SendMessageToWX.Req.WXSceneSession;
//                SendMessageToWX.Req.WXSceneTimeline ;
            MyApplication.getSInstance().getApi().sendReq(reqToFriend);

        } else if (MOMENT == shareType) {

            WXWebpageObject webpageToPyq = new WXWebpageObject();
            webpageToPyq.webpageUrl = shareUrl;
            WXMediaMessage msgToPyq = new WXMediaMessage(webpageToPyq);
            msgToPyq.title = "某某和某某的婚礼";
            msgToPyq.description = "快来观看某某和某某的婚礼吧";
            msgToPyq.thumbData = bmpToByteArray(bmp);

            SendMessageToWX.Req reqToPyq = new SendMessageToWX.Req();
            reqToPyq.transaction = buildTransaction("webpage");
            reqToPyq.message = msgToPyq;
//                req.scene = SendMessageToWX.Req.WXSceneSession;
            reqToPyq.scene = SendMessageToWX.Req.WXSceneTimeline;
            MyApplication.getSInstance().getApi().sendReq(reqToPyq);
        }
    }

    private static byte[] bmpToByteArray(final Bitmap bmp) {

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        bmp.recycle();

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
