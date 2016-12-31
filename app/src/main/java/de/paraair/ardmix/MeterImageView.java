package de.paraair.ardmix;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by onkel on 08.02.16.
 */
public class MeterImageView extends ImageView {

    private float parentWidth;
    private float parentHeight;

    private int meterLevel = 0;
    private final Paint p;

    private final static float meterWidth = 10;

    private final static String[] dbs = {"-50", "-40", "-37", "-32", "-27", "-25", "-20", "-17", "-13", "-10", "-5", "-2", "0" };

    public MeterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public MeterImageView(Context context) {
        super(context);
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.setBackgroundColor(getResources().getColor(R.color.VeryDark, null));

        p.setTextAlign(Paint.Align.RIGHT);
        p.setStrokeWidth(meterWidth);

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float leftEdge = parentWidth / 2-12;
        float rightEdge = parentWidth / 2-12;

        int ledHeight =  (int)parentHeight / 13;

        int val = meterLevel;
        int lower = 1;

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

            p.setColor(c);
            if( b == 1 && lower == 1) {
                canvas.drawLine(leftEdge, parentHeight - (i+1) * ledHeight, rightEdge, parentHeight - (i) * ledHeight - 2, p);
            }

            canvas.drawText(dbs[i], rightEdge+30 , parentHeight - (i) * ledHeight - 2, p);
            val = val >> 1;
            lower = b;
        }


    }

    public void setProgress(int val) {
        meterLevel = val;

        this.invalidate(0, (int)(parentWidth / 2 - meterWidth), (int)(parentWidth / 2+meterWidth), (int)parentHeight);
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

