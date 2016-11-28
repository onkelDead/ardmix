package de.paraair.ardmix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.SeekBar;

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
    public int meterLevel = 0;
    public float parentWidth;
    public float parentHeight;
    private float relative;
    private Orientation orientation = Orientation.VERTICAL;

    private boolean bTopText = false;
    private String strTopText = "";
    private boolean bBottomText = false;
    private String strBottomText = "";

    public int val0 = 0;

    private Paint p;
    private Bitmap fader_bmp;

    private Handler myListener;
    private int progressColor = getResources().getColor(R.color.fader, null);

    public FaderView(Context context) {
        super(context);
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.setOnTouchListener(this);

        fader_bmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.fader_image);

    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public void setMax(int max) {
        this.max = max;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


//        int ledheight =  (int)parentHeight / 14;

        int val = meterLevel;

        p.setColor(progressColor);

        if( orientation == Orientation.VERTICAL) {
            float leftEdge = parentWidth / 2;
            float rightEdge = parentWidth / 2;
            float meterWidth = parentWidth / 4;
            float topEdge = 12 + (bTopText ? 24 : 0);
            float bottomEdge = 12 + (bBottomText ? 24 : 0);

            float fullheight = parentHeight - topEdge - bottomEdge;

            float topdb = fullheight - ((float)val / max * (fullheight)) + topEdge;
            p.setStrokeWidth(meterWidth);

            // bright bg till volume
            canvas.drawLine(leftEdge, topdb, rightEdge , fullheight + topEdge , p);

            if( val0 > 0 ) {
                // 0dB line
                float top0db = fullheight - ((float)val0 / max * (fullheight)) + topEdge;
                p.setStrokeWidth(2);
                canvas.drawLine(12, top0db, parentWidth - 12, top0db, p);
            }

            // full range background
            p.setColor(0x40808080);
            p.setStrokeWidth(1);
            canvas.drawRect(leftEdge - meterWidth / 2, topEdge , rightEdge + meterWidth / 2, parentHeight - bottomEdge, p);

            // the bitmap
            canvas.drawBitmap(fader_bmp, leftEdge - 24, topdb - fader_bmp.getHeight() / 2, null);

            // top text
            if( bTopText ) {
                p.setColor(0xffffbb33);
                p.setTextSize(12);
                canvas.drawText(strTopText, 12, 18, p);
            }
            // bottom text
            if( bBottomText ) {
                p.setColor(0xffffbb33);
                p.setTextSize(12);
                canvas.drawText(strBottomText, 12, parentHeight - bottomEdge + 30, p);
            }
        }
        else {
            float leftEdge = parentHeight / 2;
            float topEdge = 0;
            float bottomEdge = 0;
            p.setStrokeWidth(leftEdge);
            canvas.drawLine(0, leftEdge, (float) val / max * parentWidth, leftEdge, p);
//
            if( val0 > 0 ) {
                float left0db = ((float)val0 / max * (parentWidth - topEdge - bottomEdge)) - topEdge;
                p.setStrokeWidth(2);
                canvas.drawLine(left0db, 4, left0db, parentHeight - 2, p);
            }

            p.setColor(0x40808080);
            p.setStrokeWidth(1);
//            p.setStyle(Paint.Style.STROKE);
            canvas.drawRect(bottomEdge, leftEdge - leftEdge / 2, parentWidth - topEdge , parentHeight - leftEdge / 2, p);

            if( param != null ) {
                p.setColor(0xffffffff);
                p.setTextSize(12);
                canvas.drawText(param.getTextFromCurrent(), 5, leftEdge+5, p);
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

    int getProgress() {
        return meterLevel;
    }

    int getMax() {
        return max;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Message msg;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(myListener!=null) {
                    getParent().requestDisallowInterceptTouchEvent(true);

                    Float p =  (float)getProgress() / (float)getMax();
                    if( orientation == Orientation.VERTICAL)
                        relative = (getHeight() - p * getHeight() - event.getY());
                    else
                        relative = (event.getX() - p * getWidth());
                    msg = myListener.obtainMessage(10);
                    myListener.sendMessage(msg);
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
                    setDisplayText(String.format("%d", Math.round(newVal)));
                }
                msg = myListener.obtainMessage(20, this.getId(), this.getProgress());
                myListener.sendMessage(msg);

                break;
            case MotionEvent.ACTION_UP:
                getParent().getParent().requestDisallowInterceptTouchEvent(false);
                msg = myListener.obtainMessage(30, this.getId(), this.getProgress());
                myListener.sendMessage(msg);
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }


    public void setOnChangeHandler(Handler thisHandler) {
        this.myListener = thisHandler;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    void setDisplayText(String text) {
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


