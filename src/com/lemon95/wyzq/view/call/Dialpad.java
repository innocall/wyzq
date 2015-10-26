package com.lemon95.wyzq.view.call;

import com.lemon95.wyzq.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class Dialpad extends LinearLayout implements OnClickListener {

    private OnDialKeyListener mDialKeyListener;
    private static final SparseArray<String> DIGITS_NAMES = new SparseArray<String>();

    static {
        DIGITS_NAMES.put(R.id.button0, "0");
        DIGITS_NAMES.put(R.id.button1, "1");
        DIGITS_NAMES.put(R.id.button2, "2");
        DIGITS_NAMES.put(R.id.button3, "3");
        DIGITS_NAMES.put(R.id.button4, "4");
        DIGITS_NAMES.put(R.id.button5, "5");
        DIGITS_NAMES.put(R.id.button6, "6");
        DIGITS_NAMES.put(R.id.button7, "7");
        DIGITS_NAMES.put(R.id.button8, "8");
        DIGITS_NAMES.put(R.id.button9, "9");
        DIGITS_NAMES.put(R.id.buttonpound, "#");
        DIGITS_NAMES.put(R.id.buttonstar, "*");
    }

    public Dialpad(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.call_dialpad, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        int size = DIGITS_NAMES.size();
        for (int i = 0; i < size; i++) {
            int buttonId = DIGITS_NAMES.keyAt(i);
            ImageButton button = (ImageButton) findViewById(buttonId);
            button.setOnClickListener(this);
        }
    }

    /**
     * Registers a callback to be invoked when the user triggers an event.
     *
     * @param listener the OnTriggerListener to attach to this view
     */
    public void setOnDialKeyListener(OnDialKeyListener listener) {
        mDialKeyListener = listener;
    }

    @Override
    public void onClick(View v) {
        String dtmf = DIGITS_NAMES.get(v.getId());
        mDialKeyListener.onTrigger(dtmf.charAt(0));
    }

    /**
     * Interface definition for a callback to be invoked when a tab is triggered
     * by moving it beyond a target zone.
     */
    public interface OnDialKeyListener {

        /**
         * Called when the user make an action
         *
         * @param keyCode keyCode pressed
         * @param dialTone corresponding dialtone
         */
        void onTrigger(char dtmf);
    }

}
