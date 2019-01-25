package com.magewr.sample.lib.textswitcher;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.magewr.sample.lib.R;

public class AdvTextSwitcher extends TextSwitcher
{
    private Context mContext;
    private String[] mTexts = {};
    private int currentPos;
    private int row;
    private int column;
    private Callback mCallback = new Callback(){
        @Override
        public void onTextChanged(String text) {}

        @Override
        public void onSpinStarted(int row) {}

        @Override
        public void onSpinStopped(int row) {}
    };

    public AdvTextSwitcher(Context context, int row, int column, int textSize)
    {
        super(context);
        this.mContext = context;
        this.row = row;
        this.column = column;
        final int textColor = Color.WHITE;
        final int animInRes = R.anim.fade_in_slide_in;
        final int animOutRes = R.anim.fade_out_slide_out;
        final int gravity = Gravity.CENTER;
        this.setFactory(() -> {
            TextView innerText = new TextView(mContext);
            innerText.setGravity(gravity);
            innerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            innerText.setTextColor(textColor);
            innerText.setBackground(getResources().getDrawable(R.drawable.card_bg));
            return innerText;
        });

        Animation animIn = AnimationUtils.loadAnimation(mContext, animInRes);
        Animation animOut = AnimationUtils.loadAnimation(mContext, animOutRes);

        this.setInAnimation(animIn);
        this.setOutAnimation(animOut);
    }

    public void overrideText(String text){
        this.setText(text);
        if (text.equals(" "))
            this.currentPos = 0;
    }

    public void setAnim(int animInRes, int animOutRes){
        Animation animIn = AnimationUtils.loadAnimation(mContext, animInRes);
        Animation animOut = AnimationUtils.loadAnimation(mContext, animOutRes);
        this.setInAnimation(animIn);
        this.setOutAnimation(animOut);
    }

    public void setAnim(Animation animIn, Animation animOut){
        this.setInAnimation(animIn);
        this.setOutAnimation(animOut);
    }

    public void setTexts(String[] texts)
    {
        if (texts.length > 0)
        {
            this.mTexts = texts;
            this.currentPos = 0;
        }
        updateDisp();
    }

    public void setCallback(Callback callback){
        this.mCallback = callback;
    }

    public void next()
    {
        if (mTexts.length > 0)
        {
            if (currentPos < mTexts.length - 1)
            {
                currentPos++;
            }
            else
            {
                currentPos = 0;
            }
            updateDisp();
        }
    }

    public void previous()
    {
        if (mTexts.length > 0)
        {
            if (currentPos > 0)
            {
                currentPos--;
            }
            else
            {
                currentPos = mTexts.length - 1;
            }
            updateDisp();
        }
    }

    public interface Callback{
        void onTextChanged(String text);
        void onSpinStarted(int row);
        void onSpinStopped(int row);
    }

    public String getCurrentText () {
        return mTexts[currentPos];
    }


    private void updateDisp()
    {
        this.setText(mTexts[ currentPos ]);
        mCallback.onTextChanged(mTexts[currentPos]);
    }

    public void onSpinStarted() {
        mCallback.onSpinStarted(row);
    }

    public void onSpinStopped() {
        mCallback.onSpinStopped(row);
    }
}