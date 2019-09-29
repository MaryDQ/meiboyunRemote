package com.sunday.common.widgets;


import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.sunday.common.R;

/**
 * 自定义控件实现ip地址特殊输入
 *
 * @author mlx
 * <p>
 * 2016-8-4
 */
public class IPEditText extends FrameLayout {

    private TextView mTvOne;
    private TextView mTvTwo;
    private TextView mTvThree;
    private TextView mTvFour;
    private EditText mEtPwd;


    private String mText1;
    private String mText2;
    private String mText3;
    private String mText4;


    private IPEditTextListener ipEditTextListener;

    public IPEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        /**
         * 初始化控件
         */
        View view = LayoutInflater.from(context).inflate(
                R.layout.ip_edittext, this);
        mTvOne = view.findViewById(R.id.tvOne);
        mTvTwo = view.findViewById(R.id.tvTwo);
        mTvThree = view.findViewById(R.id.tvThree);
        mTvFour = view.findViewById(R.id.tvFour);
        mEtPwd = view.findViewById(R.id.etInputPwd);

        mEtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String curText = s.toString();
                if (TextUtils.isEmpty(curText)) {
                    clearText();
                    return;
                }

                if (curText.length() == 1) {
                    mText1 = curText.substring(0, 1);
                    mTvOne.setText(mText1);
                    mTvTwo.setText("");
                    mTvThree.setText("");
                    mTvFour.setText("");
                } else if (curText.length() == 2) {
                    mText1 = curText.substring(0, 1);
                    mText2 = curText.substring(1);
                    mTvOne.setText(mText1);
                    mTvTwo.setText(mText2);
                    mTvThree.setText("");
                    mTvFour.setText("");
                } else if (curText.length() == 3) {
                    mText1 = curText.substring(0, 1);
                    mText2 = curText.substring(1, 2);
                    mText3 = curText.substring(2);
                    mTvOne.setText(mText1);
                    mTvTwo.setText(mText2);
                    mTvThree.setText(mText3);
                    mTvFour.setText("");
                } else if (curText.length() == 4) {
                    mText1 = curText.substring(0, 1);
                    mText2 = curText.substring(1, 2);
                    mText3 = curText.substring(2, 3);
                    mText4 = curText.substring(3);
                    mTvOne.setText(mText1);
                    mTvTwo.setText(mText2);
                    mTvThree.setText(mText3);
                    mTvFour.setText(mText4);
                    ipEditTextListener.onRefresh();
                }
            }
        });

    }


    public void clearText() {
        mTvOne.setText("");
        mTvTwo.setText("");
        mTvThree.setText("");
        mTvFour.setText("");
    }

    public String getText() {
        return mEtPwd.getText().toString();
    }

    public void setTextClear() {
        mTvOne.setText("");
        mTvTwo.setText("");
        mTvThree.setText("");
        mTvFour.setText("");
        mEtPwd.setText("");
    }

    public void setIpEditTextListener(IPEditTextListener listener) {
        this.ipEditTextListener = listener;
    }

    public interface IPEditTextListener {
        void onRefresh();
    }

    public EditText getmEtPwd() {
        return mEtPwd;
    }
}
