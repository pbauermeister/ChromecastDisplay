package digital.bauermeister.chromecastdisplay;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by pascal on 2/24/16.
 */
public class TextAutoscrollView extends TextView {

    private static final String TAG = "TextAutoscrollView";

    private float textHeight = 1f;
    private float textWidth;

    private String text = "";

    private float scrollXPosition;
    private float scrollYPosition;
    private float scrollStep;
    private int initialGravity;


    public TextAutoscrollView(Context context) {
        super(context);
        init();
    }

    public TextAutoscrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextAutoscrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/LLPIXEL3.ttf" // ***-
//                "fonts/Clubland.ttf" // ****
//                "fonts/Famirids.ttf" // ***-
//                "fonts/TRANA___.TTF" // **--
//                "fonts/PICHABS_.ttf" // *---
        );
        setTypeface(tf);
        setSingleLine(true);
        initialGravity = getGravity();
        post(new Runnable() {
            @Override
            public void run() {
                sizeTextView();
            }
        });
    }

    private void sizeTextView() {
        float height = (float) getHeight();
        textHeight = height * 0.8f;
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textHeight);
    }

    public void setText2(String text) {
        if (this.text.equals(text)) return;
        this.text = text;
        setText(text);
        setTextColor(0xff << 24);

        // scrolling
        removeCallbacks(scroller);
        textWidth = getTextWidth(text);
        if (textWidth > getWidth()) {
            // scroll
            scrollXPosition = 0;
            scrollYPosition = -getHeight();
            scrollStep = textHeight / 8f;
            setGravity((initialGravity & ~Gravity.RIGHT) | Gravity.LEFT);
            scroll();
        } else {
            // no scroll
            setGravity(initialGravity);
            setPadding(0, 0, getPaddingRight(), getPaddingBottom());
        }
    }

    private float getTextWidth(String text) {
        Paint testPaint = new Paint();
        testPaint.set(getPaint());
        float textWidth = testPaint.measureText(text);
        return textWidth;
    }

    private Runnable scroller = new Runnable() {
        @Override
        public void run() {
            scroll();
        }
    };

    private void scroll() {
        int delay;
        if (scrollYPosition < 0f) {
            scrollYPosition += scrollStep;
            delay = scrollYPosition < 0 ? Config.SCROLL_Y_DELAY_MS : Config.SCROLL_TWEEN_PAUSE;
        } else {
            scrollXPosition -= scrollStep;
            if (scrollXPosition < -textWidth) {
                scrollXPosition = 0;
                scrollYPosition = -getHeight();
                setTextColor(0xff << 24);
            }

            float remWidth = scrollXPosition + textWidth;
            if (remWidth < getWidth()) {
                float fade = remWidth / getWidth();
                int alpha = (int) (fade * 0xff + 0.5f);
                setTextColor(alpha << 24);
            }

            delay = Config.SCROLL_X_DELAY_MS;
        }
        setPadding((int) scrollXPosition, (int) -scrollYPosition, getPaddingRight(), getPaddingBottom());
        postDelayed(scroller, delay);
    }
}

