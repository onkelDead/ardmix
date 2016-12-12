package de.paraair.ardmix;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by onkel on 28.09.16.
 */

public class ToggleTextButton extends Button implements ToggleListener {


    public float parentWidth;
    public float parentHeight;

    private int state = 2;
    private boolean autoToggle = false;

    private String untoggledText = "Off";
    private String toggledText = "On";

    public int onColor = Color.WHITE;
    private int offColor = Color.BLACK;


    private Paint p;

    public ToggleTextButton(Context context) {
        super(context);
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
    }
    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public ToggleTextButton(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    /**
     * @param context
     * @param attrs
     */
    public ToggleTextButton(Context context, AttributeSet attrs) {

        super(context, attrs);
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public ToggleTextButton(Context context, String offText, String onText, int onColor, int offColor) {
        super(context);
        untoggledText = offText;
        toggledText  = onText;
        this.onColor = onColor;
        this.offColor = offColor;
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);

        p.setStyle(Paint.Style.FILL);
        if( state == 0 )
            p.setColor(offColor);
        else
            p.setColor(onColor);
        canvas.drawRoundRect(1, 1, parentWidth - 1, parentHeight -1, 3, 3, p);

//        p.setStyle(Paint.Style.STROKE);
        p.setColor(onColor);
//        canvas.drawRoundRect(1, 1, parentWidth - 1, parentHeight -1, 3, 3, p);

        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((p.descent() + p.ascent()) / 2)) ;
        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.

        String text = state == 0 ? untoggledText : toggledText;
        if( text != null) {
            float width = p.measureText(text, 0, text.length());

            if (state == 1)
                p.setColor(offColor);
            else if (!isEnabled())
                p.setColor(0);
            canvas.drawText(state == 0 ? untoggledText : toggledText, xPos - width / 2, yPos, p);
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


    /**
     *
     *  Toggle state on click and change the background image
     *
     */
    @Override
    public boolean performClick() {

        if (autoToggle){
            toggle();
        }

        return super.performClick();
    }

    /**
     * Method to toggle the state of the button
     */
    public void toggle(){

        this.setToggleState(state != 1);

    }
    /**
     *
     */
    public boolean getToggleState(){
        return this.state == 1;
    }
    
    /**
     * Toogle to a given state
     * @param s
     */
    public void  setToggleState(boolean s){

        if( s &&  state == 1) {
            return;
        }
        state = s ? 1 : 0;

        invalidate();
    }

    public void toggleOn(){
        this.setToggleState(true);
    }
    public void toggleOff(){
        //this.stopBlink();
        this.setToggleState(false);
    }

    /**
     * @return the untoggledText
     */
    public String getUntoggledText() {
        return untoggledText;
    }

    /**
     * @param untoggledText the untoggledText to set
     */
    public void setUntoggledText(String untoggledText) {
        this.untoggledText = untoggledText;
        if (state == 1)
            this.setText(untoggledText);
    }

    /**
     * @return the toggledText
     */
    public String getToggledText() {
        return toggledText;
    }

    /**
     * @param toggledText the toggledText to set
     */
    public void setToggledText(String toggledText) {
        this.toggledText = toggledText;
        if (state == 1)
            this.setText(toggledText);
    }

    public final void setAllText(String text) {
        setUntoggledText(text);
        setToggledText(text);
        setToggleState(state == 1);
    }

    /**
     * @return the autoToggle
     */
    public boolean isAutoToggle() {
        return autoToggle;
    }
    /**
     * @param autoToggle the autoToggle to set
     */
    public void setAutoToggle(boolean autoToggle) {
        this.autoToggle = autoToggle;
    }

    public void setOffColor(int offColor) {
        this.offColor = offColor;
//        this.setBackgroundColor(offColor);
    }

}

