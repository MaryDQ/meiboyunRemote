package com.microsys.imb.remote.wxapi;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.microsys.imb.remote.app.MyApplication;
import com.sunday.common.net.YRequest;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by microsys on 2018/4/20.
 */

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private static final int WECHAT_LOGIN = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getSInstance().getApi().handleIntent(getIntent(), this); //处理微信传回的Intent,当然你也可以在别的地方处理
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {//在这个方法中处理微信传回的数据
        //形参resp 有下面两个个属性比较重要
        //1.resp.errCode
        //2.resp.transaction则是在分享数据的时候手动指定的字符创,用来分辨是那次分享(参照4.中req.transaction)

        if (baseResp.getType() == WECHAT_LOGIN) {

            SendAuth.Resp resp = (SendAuth.Resp) baseResp;

            //根据需要的情况进行处理
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    String code = String.valueOf(resp.code);
                    getAccessToken(code);
                    finish();
                    //正确返回
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    finish();
                    //用户取消
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    finish();
                    //认证被否决
                    break;
                case BaseResp.ErrCode.ERR_SENT_FAILED:
                    finish();
                    //发送失败
                    break;
                case BaseResp.ErrCode.ERR_UNSUPPORT:
                    finish();
                    //不支持错误
                    break;
                case BaseResp.ErrCode.ERR_COMM:
                    finish();
                    //一般错误
                    break;
                default:
                    //其他不可名状的情况
                    break;
            }
        } else {
            //根据需要的情况进行处理
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    finish();
                    //正确返回
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    finish();
                    //用户取消
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    finish();
                    //认证被否决
                    break;
                case BaseResp.ErrCode.ERR_SENT_FAILED:
                    finish();
                    //发送失败
                    break;
                case BaseResp.ErrCode.ERR_UNSUPPORT:
                    finish();
                    //不支持错误
                    break;
                case BaseResp.ErrCode.ERR_COMM:
                    finish();
                    //一般错误
                    break;
                default:
                    //其他不可名状的情况
                    break;
            }
        }
    }

    private void getAccessToken(String code) {

//        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + Constants.WECHAT_APP_ID + "&secret=" + Constants.WECHAT_APP_SECRET + "&code=" + code + "&grant_type=authorization_code";
        String url = "";
        HashMap<String, String> paramsMap = new HashMap<>(1);

        YRequest.getInstance().get(url, paramsMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                String access = null;
                String openId = null;
                String refreshToken = null;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    access = jsonObject.getString("access_token");
                    openId = jsonObject.getString("openid");
                    refreshToken = jsonObject.getString("refresh_token");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
