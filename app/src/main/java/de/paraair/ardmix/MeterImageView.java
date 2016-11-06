package de.paraair.ardmix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by onkel on 08.02.16.
 */
public class MeterImageView extends ImageView {

    public float parentWidth;
    public float parentHeight;

    public int meterLevel = 0;
    private float peak = 0;
    private int peakCount = 0;


    private Paint p;

    public MeterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public MeterImageView(Context context) {
        super(context);
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int ledheight =  (int)parentHeight / 13;

        int val = meterLevel;

        float leftEdge = 15;
        float rightEdge = 15;
        float meterWidth = 12;

        p.setStrokeWidth(meterWidth);
        for( int i = 0; i < 13; i++) {
            int  b = val & 1;

            int c = Color.rgb(0,128,0);

            if( i > 11)
                c = Color.RED;
            else if( i > 10)
                c = Color.argb(255,255,0xbb,0x33);
            else if( i > 9 )
                c = Color.YELLOW;
            else if( i > 6 )
                c = Color.GREEN;

            if( b == 1 ) {
                p.setColor(c);
                canvas.drawLine(leftEdge, parentHeight - (i+1) * ledheight, rightEdge, parentHeight - (i) * ledheight - 2, p);
            }
            else
                break;
            val = val >> 1;

        }

//        float db0 = meterLevel / parentHeight;
//
//        float cyanBorder = parentHeight * .6F;
//        float greenBorder = parentHeight * .8F;
//        float yellowBorder = parentHeight * .9F;
//
//
//        p.setStrokeWidth(meterWidth);
//        p.setColor(Color.rgb(0,128,0));
//        canvas.drawLine(leftEdge, parentHeight, rightEdge, parentHeight - Math.min(meterLevel, cyanBorder), p);
//        if (meterLevel > cyanBorder){
//            p.setColor(Color.GREEN);
//            canvas.drawLine(leftEdge, parentHeight - cyanBorder, rightEdge, parentHeight - Math.min(meterLevel, greenBorder), p);
//        }
//        if (meterLevel > greenBorder){
//            p.setColor(Color.YELLOW);
//            canvas.drawLine(leftEdge, parentHeight - greenBorder, rightEdge, parentHeight - Math.min(meterLevel, yellowBorder), p);
//        }
//        if (meterLevel > yellowBorder){
//            p.setColor(Color.RED);
//            canvas.drawLine(leftEdge, parentHeight - yellowBorder, rightEdge, parentHeight - meterLevel, p);
//        }
//
//
//// peak indicator
//        if( peakCount > 0 ) {
//            p.setStrokeWidth(4);
//            Float elta =  meterWidth - (parentWidth/4);
//            canvas.drawLine(elta, parentHeight - peak, parentWidth- elta, parentHeight - peak, p);
//            peakCount--;
//        }
//        else
//            peak = 0;


    }

    public void setProgress(int val) {
        meterLevel = val;
//        for( int i = 0; i < 16; i++) {
//            int  b = val & 1;
//            if( b == 1 )
//                meterLevel += 100;
//            val = val >> 1;
//        }
//        Float factor = val/32767F;
//        Float meterFloat = parentHeight * factor;
//        meterLevel = meterFloat.intValue();
        if( meterLevel > peak ) {
            peak = meterLevel;
            peakCount = 40;
        }

        this.invalidate();
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


}

