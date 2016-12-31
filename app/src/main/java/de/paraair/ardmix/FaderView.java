package de.paraair.ardmix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by onkel on 06.10.16.
 */

public class FaderView extends ImageView implements View.OnTouchListener {

    enum Orientation {
        VERTICAL,
        HORIZONTAL
    }

    public ArdourPlugin.InputParameter param;

    private int max = 1000;
    private int min = 0;
    private int meterLevel = 0;
    private float parentWidth;
    private float parentHeight;
    private float relative;
    private Orientation orientation = Orientation.VERTICAL;

    private boolean bTopText = false;
    private String strTopText = "";
    private boolean bBottomText = false;
    private String strBottomText = "";

    public int val0 = 0;

    private final Paint p;
    private final Bitmap fader_bmp_vertical;
    private final Bitmap fader_bmp_horizontal;


    interface FaderViewListener {
        void onFader(int id, int pos);
        void onStartFade();
        void onStopFade(int id, int pos);
    }

    private FaderViewListener m_listener;

    public void SetListener(FaderViewListener l) {
        m_listener = l;
    }

    private int progressColor = getResources().getColor(R.color.fader, null);

    public FaderView(Context context) {
        super(context);
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.setOnTouchListener(this);

        fader_bmp_vertical = BitmapFactory.decodeResource(getResources(),
                R.drawable.fader_image);

        fader_bmp_horizontal = BitmapFactory.decodeResource(getResources(),
                R.drawable.fader_image_horizontal);

    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setMin(int min) {
        this.min = min;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


//        int ledheight =  (int)parentHeight / 14;

        int val = meterLevel;

        if( orientation == Orientation.VERTICAL) {
            float leftEdge = parentWidth / 2;
            float rightEdge = parentWidth / 2;
            float meterWidth = parentWidth / 6;
            float topEdge = 12 + (bTopText ? 24 : 0);
            float bottomEdge = 12 + (bBottomText ? 24 : 0);

            float fullheight = parentHeight - topEdge - bottomEdge;

            float topdb = fullheight - ((float)val / max * (fullheight)) + topEdge;

            // full range background
            p.setColor(progressColor);
            p.setStrokeWidth(1);
            p.setStyle(Paint.Style.STROKE);
            canvas.drawRect(leftEdge - meterWidth / 6, topEdge , rightEdge + meterWidth / 6, parentHeight - bottomEdge, p);

            // bright bg till volume
            p.setColor(progressColor);
            p.setStrokeWidth(meterWidth);
            canvas.drawLine(leftEdge, topdb, rightEdge , fullheight + topEdge - ((float)min  / (float)max) * fullheight  , p);

            if( val0 > 0 ) {
                // 0dB line
                float top0db = fullheight - ((float)val0 / max * (fullheight)) + topEdge;
                p.setStrokeWidth(2);
                canvas.drawLine(12, top0db, parentWidth - 12, top0db, p);
            }

            // the bitmap
            canvas.drawBitmap(fader_bmp_vertical, leftEdge - fader_bmp_vertical.getWidth() / 2, topdb - fader_bmp_vertical.getHeight() / 2, null);

            p.setStrokeWidth(1);
            // top text
            if( bTopText ) {
                p.setColor(getResources().getColor(R.color.BUTTON_PAN, null));
                p.setTextSize(14);
                canvas.drawText(strTopText, 12, 18, p);
            }
            // bottom text
            if( bBottomText ) {
                p.setColor(getResources().getColor(R.color.BUTTON_PAN, null));
                p.setTextSize(14);
                canvas.drawText(strBottomText, 12, parentHeight - bottomEdge + 30, p);
            }
        }
        else {
            float vCenter = parentHeight / 2;
            float leftEdge = 12;
            float rightEdge = 12;

            float fullWidth = parentWidth - leftEdge - rightEdge;
            float leftdb = ((float)val / max * (fullWidth)) + leftEdge;

            // full range background
            p.setColor(progressColor);
            p.setStrokeWidth(1);
            p.setStyle(Paint.Style.STROKE);
            canvas.drawRect(leftEdge, vCenter - 2 , fullWidth + leftEdge , vCenter + 2, p);

            // bright bg till volume
            p.setColor(this.progressColor);
            p.setStrokeWidth(vCenter / 2);
            canvas.drawLine(leftEdge, vCenter, leftdb, vCenter, p);

            if( val0 > 0 ) {
                // 0dB line
                float left0db = ((float)val0 / max * (fullWidth)) + leftEdge;
                p.setStrokeWidth(2);
                canvas.drawLine(left0db, 4, left0db, parentHeight - 2, p);
            }

            // the bitmap
            canvas.drawBitmap(fader_bmp_horizontal, leftdb - fader_bmp_horizontal.getWidth() / 2, vCenter - fader_bmp_horizontal.getHeight() /2, null);

            if( param != null ) {
                p.setColor(Color.WHITE);
                p.setStrokeWidth(0);
                p.setTextSize(12);
                canvas.drawText(param.getTextFromCurrent(), leftEdge + 5, vCenter+5, p);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentW = MeasureSpec.getSize(widthMeasureSpec);
        int parentH = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentW, parentH);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        parentWidth = parentW;
        parentHeight = parentH;

    }

    public void setProgress(int val) {
        meterLevel = val;


        this.invalidate();
    }

    private int getProgress() {
        return meterLevel;
    }

    private int getMax() {
        return max;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Message msg;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);

                Float p =  (float)getProgress() / (float)getMax();
                if( orientation == Orientation.VERTICAL)
                    relative = (getHeight() - p * getHeight() - event.getY());
                else
                    relative = (event.getX() - p * getWidth());
                if(m_listener!=null) {
                    m_listener.onStartFade();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if( orientation == Orientation.VERTICAL) {
                    int newVal = getMax() - (int) (getMax() * (event.getY() + relative) / getHeight());
                    if (newVal < 0)
                        newVal = 0;
                    if (newVal > max)
                        newVal = max;
                    setProgress(newVal);
                    onSizeChanged(getWidth(), getHeight(), 0, 0);
                }
                else {
                    int newVal = (int) (getMax() * (event.getX() - relative) / getWidth()) ;
                    if (newVal < 0)
                        newVal = 0;
                    if (newVal > max)
                        newVal = max;
                    setProgress(newVal);
                    onSizeChanged(getWidth(), getHeight(), 0, 0);
                }
                if(m_listener!=null) {
                    m_listener.onFader(this.getId(), this.getProgress());
                }
                break;
            case MotionEvent.ACTION_UP:
                getParent().getParent().requestDisallowInterceptTouchEvent(false);
                if(m_listener!=null) {
                    m_listener.onStopFade(this.getId(), this.getProgress());
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public void setbTopText(boolean bTopText) {
        this.bTopText = bTopText;
    }

    public void setStrTopText(String strTopText) {
        this.strTopText = strTopText;
        this.bTopText = !strTopText.equals("");
    }

    public void setbBottomText(boolean bBottomText) {
        this.bBottomText = bBottomText;
    }

    public void setStrBottomText(String strBottomText) {
        this.strBottomText = strBottomText;
        this.bBottomText = !strBottomText.equals("");
    }
}


