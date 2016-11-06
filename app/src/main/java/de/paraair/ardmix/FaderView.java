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


    private String displayText = "AABB";

    enum Orientation {
        VERTICAL,
        HORIZONTAL
    };

    public ArdourPlugin.InputParameter param;

    private int max = 1000;
    public int meterLevel = 0;
    public float parentWidth;
    public float parentHeight;
    private float relative;
    private Orientation orientation = Orientation.VERTICAL;

    private Paint p;

    private Bitmap fader_bmp;

    private Handler myListener;
    private int progressColor = getResources().getColor(R.color.fader);

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
        int val0 = 782;


        p.setColor(progressColor);

        if( orientation == Orientation.VERTICAL) {
            float leftEdge = parentWidth / 2;
            float rightEdge = parentWidth / 2;
            float meterWidth = parentWidth / 4;
            float topEdge = 12;
            float bottomEdge = 12;
            float top0db = parentHeight - ((float)val0 / max * (parentHeight - topEdge - bottomEdge)) - topEdge;
            float topdb = parentHeight - ((float)val / max * (parentHeight - topEdge - bottomEdge)) - topEdge;
            p.setStrokeWidth(meterWidth);

            canvas.drawLine(leftEdge, parentHeight - bottomEdge , rightEdge, topdb, p);

            p.setStrokeWidth(2);
            canvas.drawLine(12, top0db, parentWidth - 12, top0db, p);
            p.setColor(0x40808080);

            p.setStrokeWidth(1);
            canvas.drawRect(leftEdge - meterWidth / 2, topEdge , rightEdge + meterWidth / 2, parentHeight - bottomEdge, p);

            canvas.drawBitmap(fader_bmp, leftEdge - 24, topdb - fader_bmp.getHeight() / 2, null);
        }
        else {
            float leftEdge = parentHeight / 2;
            float topEdge = 4;
            float bottomEdge = 4;
//            float left0db = ((float)val0 / max * (parentWidth - topEdge - bottomEdge)) - topEdge;
            p.setStrokeWidth(leftEdge);
            canvas.drawLine(0, leftEdge, (float) val / max * parentWidth, leftEdge, p);
//
//            p.setStrokeWidth(2);
//            canvas.drawLine(left0db, 2, left0db, parentHeight - 2, p);

            p.setColor(0x40808080);
            p.setStrokeWidth(1);
//            p.setStyle(Paint.Style.STROKE);
            canvas.drawRect(bottomEdge, leftEdge - leftEdge / 2, parentWidth - topEdge , parentHeight - bottomEdge, p);


            p.setColor(0xffffffff);

            if( param != null ) {
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
        displayText = text;
    }
}
