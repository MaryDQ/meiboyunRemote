package com.sunday.common.utils;

import android.content.Context;
import android.text.style.ClickableSpan;
import android.view.View;

public class MyClickableSpan extends ClickableSpan {
    private String str;

    private Context context;


    public MyClickableSpan(String str, Context context) {
        this.str = str;
        this.context = context;
    }

    @Override
    public void onClick(View widget) {

    }
}
