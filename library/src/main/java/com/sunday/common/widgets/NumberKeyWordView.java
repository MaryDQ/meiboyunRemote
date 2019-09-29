package com.sunday.common.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述：自定义数字键盘
 * 作者: mlx
 * 创建时间： 2019/07/05
 */
public class NumberKeyWordView extends View implements View.OnTouchListener, View.OnClickListener {

    private String[] numberArrays = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#"};
    private int mItemWidth;
    private Paint mPaint;
    private Paint mSelectPaint;
    private float mTextBaseLine;
    private int mItemHeight;
    private Map<String, Boolean> numberSelect = new HashMap<>(12);
    /**
     * 点击的坐标点
     */
    private float mClickX, mClickY;
    private boolean isClick = true;
    private OnClickNumListener mOnClickNumListener;

    public NumberKeyWordView(Context context) {
        super(context);
        init();
    }

    public NumberKeyWordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NumberKeyWordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    String curClick = "";

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mItemWidth = getWidth() / 3;
        mItemHeight = getHeight() / 4;
        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        mTextBaseLine = (mItemHeight >> 1) - metrics.descent + (metrics.bottom - metrics.top) / 2;
        int d = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                draw(canvas, i, j, d);
                ++d;
            }
        }

    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#333333"));
        mPaint.setTextSize(60);
        mSelectPaint = new Paint();
        mSelectPaint.setTextSize(60);
        mSelectPaint.setColor(Color.parseColor("#ffeef8fc"));
        setOnClickListener(this);
        setOnTouchListener(this);
        int d = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                numberSelect.put(numberArrays[d], false);
                ++d;
            }
        }
    }

    /**
     * 开始回执
     *
     * @param canvas 画布
     * @param i      当前的行
     * @param j      当前的列
     * @param d      现在是12个中的第几个
     */
    private void draw(Canvas canvas, int i, int j, int d) {
        int x = j * mItemWidth + mItemWidth / 2;
        int y = (int) (i * mItemHeight + mTextBaseLine);

        if (numberSelect.get(numberArrays[d])) {
            canvas.drawCircle(getWidth() / 6 * (2 * (j + 1) - 1) + 15, getHeight() / 8 * (i * 2 + 1), 70, mSelectPaint);
        }
        canvas.drawText(numberArrays[d], x, y, mPaint);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mClickX = event.getX();
                mClickY = event.getY();
                isClick = true;
                curClick = getSelectNumber(mClickX, mClickY);
                if (!TextUtils.isEmpty(curClick)) {
                    numberSelect.put(curClick, true);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float mDY;
                if (isClick) {
                    mDY = event.getY() - mClickY;
                    isClick = Math.abs(mDY) <= 50;
                }
                break;
            case MotionEvent.ACTION_UP:
                mClickX = event.getX();
                mClickY = event.getY();
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        numberSelect.put(curClick, false);
                        invalidate();
                    }
                }, 50L);

                break;
            default:
                break;
        }
        return false;
    }

    /**
     * 拿到当前点击的数字
     *
     * @param clickX
     * @param clickY
     */
    private String getSelectNumber(float clickX, float clickY) {
        int indexX = (int) (mClickX / mItemWidth);
        if (indexX >= 3) {
            indexX = 2;
        }
        int indexY = (int) (mClickY / mItemHeight);
        int position = indexY * 3 + indexX;
        if (position > 11 || position < 0) {
            return "";
        }
        return numberArrays[position];
    }

    @Override
    public void onClick(View v) {
        if (!isClick) {
            return;
        }
        if (null != mOnClickNumListener) {
            mOnClickNumListener.click(getSelectNumber(mClickX, mClickY));
        }
    }

    public void setOnClickNumListener(OnClickNumListener mOnClickNumListener) {
        this.mOnClickNumListener = mOnClickNumListener;
    }

    public interface OnClickNumListener {
        /**
         * 返回当前按下键盘对应的数字或者符号
         *
         * @param num 键盘上的数字、符号
         */
        void click(String num);
    }

}
