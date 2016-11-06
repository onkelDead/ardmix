package de.paraair.ardmix;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by onkel on 28.09.16.
 */

public class ToggleTextButton extends Button implements ToggleListener {

    private int state = 2;
    private boolean autoToggle = true;

    private String untoggledText = "Off";
    private String toggledText = "On";

    public int onColor = Color.rgb(255,187,51);
    public int offColor = Color.rgb(243,243,243);

    public ToggleTextButton(Context context) {
        super(context);
    }
    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public ToggleTextButton(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

        init(context, attrs);
    }

    public final void setAllText(String text) {
        setUntoggledText(text);
        setToggledText(text);
    }

    /**
     * @param context
     * @param attrs
     */
    public ToggleTextButton(Context context, AttributeSet attrs) {

        super(context, attrs);

        init(context, attrs);
    }

    public ToggleTextButton(Context context, String offText, String onText, int onColor, int offColor) {
        super(context);
        untoggledText = offText;
        toggledText  = onText;
        this.onColor = onColor;
        this.offColor = offColor;
    }

    /**
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {

        this.setText(untoggledText);

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
//        if (state == false){
//            this.setText(toggledText);
//            state = true;
//        }
//        else {
//            this.setText(untoggledText);
//            state = false;
//        }
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
    public synchronized void  setToggleState(boolean s){

        if( s &&  state == 1) {
            return;
        }
        state = s ? 1 : 0;
        if (state == 1){
            this.setText(toggledText);
            this.setBackgroundColor(onColor);
            this.setTextColor(offColor);
        }
        else {
            this.setText(untoggledText);
            this.setBackgroundColor(offColor);
            this.setTextColor(onColor);
        }
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

}
