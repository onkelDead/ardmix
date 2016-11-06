package de.paraair.ardmix;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageButton;

import java.util.concurrent.atomic.AtomicBoolean;

public class ToggleImageButton extends ImageButton implements ToggleListener, Blinkable {
	
	private boolean state = false;
	private boolean autoToggle = false;
	
	private AtomicBoolean canBlink = new AtomicBoolean(false);
	
	private int untoggledResourceId;
	private int toggledResourceId;
	
	
	public ToggleImageButton(Context context) {
		super(context);
	}
	

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ToggleImageButton(Context context, AttributeSet attrs, int defStyle) {
		
		super(context, attrs, defStyle);
		
		init(context, attrs);
	}


	/**
	 * @param context
	 * @param attrs
	 */
	public ToggleImageButton(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		
		init(context, attrs);
	}


	/**
	 * @param context
	 * @param attrs
	 */
	private void init(Context context, AttributeSet attrs) {

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ToggleImageButton);

		toggledResourceId = a.getResourceId(R.styleable.ToggleImageButton_background_toggled, 0);
		untoggledResourceId = a.getResourceId(R.styleable.ToggleImageButton_background_untoggled, 0);
		
		a.recycle();
		
		this.setBackgroundResource(untoggledResourceId);
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
		
		if (state == false){
			this.setBackgroundResource(toggledResourceId);
			state = true;
		}
		else {
			this.setBackgroundResource(untoggledResourceId);
			state = false;
		}		
	}
	
	
	/**
	 * 
	 */
	public boolean getToggleState(){
		return this.state;
	}
	
	
	/**
	 * Toogle to a given state
	 * @param s
	 */
	public void setToggleState(boolean s){
		
		canBlink.set(false);
		
		if (s == true){
			state = true;
			this.setBackgroundResource(toggledResourceId);
		}
		else {
			state = false;
			this.setBackgroundResource(untoggledResourceId);
		}				
	}
	
	/**
	 * Toogle to a given state
	 * @param s
	 */
	public void setToggleState(boolean s, boolean blink){
		
		if (s == true){
			
			this.setBackgroundResource(toggledResourceId);
			
			if (blink){
				this.startBlink();
			}
			
			state = true;
		}
		else {
			this.setBackgroundResource(untoggledResourceId);
			canBlink.set(false);
			state = false;
		}				
	}
	
	public void toggleOn(){
		this.setToggleState(true);
	}
	
	public void toggleOnAndBlink(){
		this.setToggleState(true, true);
	}
	
	public void toggleOff(){
		//this.stopBlink();
		this.setToggleState(false);
	}
	

	/**
	 * @return the untoggledResourceId
	 */
	public int getUntoggledResourceId() {
		return untoggledResourceId;
	}



	/**
	 * @param untoggledResourceId the untoggledResourceId to set
	 */
	public void setUntoggledResourceId(int untoggledResourceId) {
		this.untoggledResourceId = untoggledResourceId;
	}



	/**
	 * @return the toggledResourceId
	 */
	public int getToggledResourceId() {
		return toggledResourceId;
	}



	/**
	 * @param toggledResourceId the toggledResourceId to set
	 */
	public void setToggledResourceId(int toggledResourceId) {
		this.toggledResourceId = toggledResourceId;
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


	@Override
	public void blink() {
		if (canBlink.get()){
			this.toggle();
		}
	}

	
	@Override
	public void startBlink() {
		canBlink.set(true);
	}

	
//	@Override
//	public void stopBlink() {
//
//		canBlink.set(false);
//
//		if (state){
//			setToggleState(true);
//		}
//		else {
//			setToggleState(false);
//		}
//	}
//
//	@Override
//	public boolean shouldBlink() {
//		return canBlink.get();
//	}
}
