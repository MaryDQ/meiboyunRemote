package com.sunday.common.widgets.gridview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Wangran on 2015/7/1.
 */
public class NoScrollGridView extends GridView {
        public NoScrollGridView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        public NoScrollGridView(Context context, AttributeSet attrs) {
            super(context, attrs);
            // TODO Auto-generated constructor stub
        }

        public NoScrollGridView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            // TODO Auto-generated method stub
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                    MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        }

}
