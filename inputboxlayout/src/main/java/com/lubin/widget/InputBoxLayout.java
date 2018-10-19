package com.lubin.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lubin
 * @version 1.0 ·2018/10/19
 * 类似方框的输入EditText管理布局
 */
public class InputBoxLayout extends LinearLayout implements TextWatcher, View.OnKeyListener {
    private final String TAG = InputBoxLayout.class.getSimpleName();
    private final static String INPUT_TYPE_NUMBER = "number";
    private final static String INPUT_TYPE_TEXT = "text";
    private final static String INPUT_TYPE_PASSWORD = "password";

    private int boxAccount = 4;
    private int boxWidth = 50;
    private int boxHeight = 50;
    private int boxTextSize = 30;
    private int boxPadding = 0;
    private int boxMargin = 0;
    private int boxMarginRight = 0;
    private int boxMarginLeft = 0;
    private int boxPosition = 0;
    private int boxTextColor;
    private String boxInputType;
    private int boxBackground;

    private List<EditText> editTexts;
    private OnBoxListener onBoxListener;

    public InputBoxLayout(Context context) {
        super(context);
    }

    public InputBoxLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBoxView(context, attrs);
    }

    public InputBoxLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBoxView(context, attrs);
    }

    private void initBoxView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.InputBoxLayout);
        boxAccount = typedArray.getInt(R.styleable.InputBoxLayout_box_account, boxAccount);
        boxBackground = typedArray.getResourceId(R.styleable.InputBoxLayout_box_drawable, R.drawable.bg_box_default_frame);
        boxWidth = (int) typedArray.getDimension(R.styleable.InputBoxLayout_box_width, boxWidth);
        boxHeight = (int) typedArray.getDimension(R.styleable.InputBoxLayout_box_height, boxHeight);
        boxTextSize = (int) typedArray.getDimension(R.styleable.InputBoxLayout_box_textSize, boxTextSize);
        boxPadding = (int) typedArray.getDimension(R.styleable.InputBoxLayout_box_padding, boxPadding);
        boxMargin = (int) typedArray.getDimension(R.styleable.InputBoxLayout_box_margin, boxMargin);
        boxInputType = typedArray.getString(R.styleable.InputBoxLayout_box_inputType);
        if (boxInputType == null) {
            boxInputType = INPUT_TYPE_NUMBER;
        }
        boxMarginRight = (int) typedArray.getDimension(R.styleable.InputBoxLayout_box_marginRight, boxMarginRight);
        boxMarginLeft = (int) typedArray.getDimension(R.styleable.InputBoxLayout_box_marginLeft, boxMarginLeft);
        boxTextColor = typedArray.getColor(R.styleable.InputBoxLayout_box_textCoclor, getResources().getColor(R.color.defaulet_color));
        initEditText();
        typedArray.recycle();
    }

    private void initEditText() {
        editTexts = new ArrayList<>();
        for (int i = 0; i < boxAccount; i++) {
            EditText editText = new EditText(getContext());
            LayoutParams layoutParams = new LayoutParams(boxWidth, boxHeight);
            layoutParams.gravity = Gravity.CENTER;
            if (boxMargin > 0) {
                layoutParams.rightMargin = boxMargin;
                layoutParams.leftMargin = boxMargin;
                layoutParams.topMargin = boxMargin;
                layoutParams.bottomMargin = boxMargin;
            } else {
                layoutParams.rightMargin = boxMarginRight;
                layoutParams.leftMargin = boxMarginLeft;
            }
            editText.setLayoutParams(layoutParams);
            editText.setTextColor(boxTextColor);
            editText.setGravity(Gravity.CENTER);
            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, boxTextSize);
            editText.setPadding(boxPadding, boxPadding, boxPadding, boxPadding);
            editText.setId(i);
            editText.setEms(1);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});

            editText.setBackground(getResources().getDrawable(boxBackground));

            switch (boxInputType) {
                case INPUT_TYPE_NUMBER:
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
                case INPUT_TYPE_TEXT:
                    editText.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                case INPUT_TYPE_PASSWORD:
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    break;
                default:
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
            }
            editText.addTextChangedListener(this);
            editText.setOnKeyListener(this);
            addView(editText, i);
            editTexts.add(editText);
        }
    }

    /**
     * 焦点获取
     */
    private void setFocus() {
        int childCount = getChildCount();
        EditText editText;
        for (int i = 0; i < childCount; i++) {
            editText = (EditText) getChildAt(i);
            if (editText.getText().toString().length() < 1) {
                editText.requestFocus();
                return;
            }
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (start == 0 && count >= 1 && boxPosition < getChildCount() - 1) {
            boxPosition++;
            getChildAt(boxPosition).requestFocus();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            setFocus();
        }
        textChanged();
        Log.e(TAG, boxTextSize + " " + boxWidth);
    }

    /**
     * 删除监听
     *
     * @param v       editText
     * @param keyCode int
     * @param event   KeyEvent
     * @return false
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        EditText editText = (EditText) v;
        if (keyCode == KeyEvent.KEYCODE_DEL && editText.getText().length() == 0) {
            if (boxPosition != 0 && event.getAction() == KeyEvent.ACTION_DOWN) {
                boxPosition--;
                ((EditText) getChildAt(boxPosition)).setText("");
                getChildAt(boxPosition).requestFocus();
            }
        }
        return false;
    }

    private void textChanged() {
        StringBuffer buffer = new StringBuffer();
        boolean isComplete = false;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            EditText editText = (EditText) getChildAt(i);
            String code = editText.getText().toString();
            if (code.length() > 0) {
                buffer.append(code);
                isComplete = true;
            } else {
                isComplete = false;
            }
        }
        if (onBoxListener != null) {
            onBoxListener.onIsOrNotComplete(isComplete, buffer.toString());
        }
        buffer = null;
    }

    public interface OnBoxListener {

        void onIsOrNotComplete(boolean isComplete, String text);
    }

    public void setOnBoxListener(OnBoxListener onBoxListener) {
        this.onBoxListener = onBoxListener;
    }
}
