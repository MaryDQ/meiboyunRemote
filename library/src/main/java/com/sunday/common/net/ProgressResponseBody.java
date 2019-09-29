package com.sunday.common.net;

import com.sunday.common.logger.Logger;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.*;

import java.io.IOException;

/**
 * Created by Ace on 2016/12/6.
 */

public class ProgressResponseBody extends ResponseBody {
    private final ResponseBody responseBody;
    private final ProgressResponseListener mListener;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, ProgressResponseListener listener) {
        this.responseBody = responseBody;
        this.mListener = listener;
        if (mListener != null) {
//            mListener.onPreExecute(contentLength());
        }
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {

        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                mListener.onResponseProgress(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                Logger.d("当前需要下载" + responseBody.contentLength() + "字节,已经下载了" + totalBytesRead + "字节");
                return bytesRead;
            }
        };
    }

    public interface ProgressResponseListener {
        void onResponseProgress(long bytesRead, long contentLength, boolean done);
//        void onPreExecute(long contentLength);
    }
}
