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
    private Bitmap meter_bmp;
    private Paint p;

    public MeterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public MeterImageView(Context context) {
        super(context);
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.setBackgroundColor(getResources().getColor(R.color.VeryDark, null));
        meter_bmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.gain_image);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(Bitmap.createScaledBitmap(meter_bmp, (int)parentWidth, (int)parentHeight, true),0,0,p);

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


    }

    public void setProgress(int val) {
        meterLevel = val;

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

