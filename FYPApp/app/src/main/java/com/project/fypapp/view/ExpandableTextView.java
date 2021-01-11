package com.project.fypapp.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.project.fypapp.R;

public class ExpandableTextView extends androidx.appcompat.widget.AppCompatTextView {
    private static final int DEFAULT_TRIM_LENGTH = 97;
    private static final String ELLIPSIS = "... show more";

    private CharSequence originalText;
    private CharSequence trimmedText;
    private BufferType bufferType;
    private boolean trim = true;
    private int trimLength;

    public ExpandableTextView(Context context) {
        this(context, null);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
        this.trimLength = DEFAULT_TRIM_LENGTH;
        typedArray.recycle();

        setOnClickListener(v -> {
            trim = !trim;
            setText();
            // requestFocusFromTouch();
        });
    }

    private void setText() {
        super.setText(getDisplayableText(), bufferType);
    }

    private CharSequence getDisplayableText() {
        return trim ? trimmedText : originalText;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        originalText = text;
        trimmedText = getTrimmedText();
        bufferType = type;
        setText();
    }

    @SuppressLint("ResourceAsColor")
    private CharSequence getTrimmedText() {
        if (originalText != null && originalText.length() > trimLength) {
            Spannable spannable = new SpannableStringBuilder(originalText, 0, trimLength + 1).append(ELLIPSIS);
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#7C7C7C")), trimLength+1, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannable;
        } else {
            return originalText;
        }
    }

    public CharSequence getOriginalText() {
        return originalText;
    }

    public void setTrimLength(int trimLength) {
        this.trimLength = trimLength;
        trimmedText = getTrimmedText();
        setText();
    }

    public int getTrimLength() {
        return trimLength;
    }
}