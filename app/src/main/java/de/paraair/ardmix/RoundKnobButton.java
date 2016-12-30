package de.paraair.ardmix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

/*
File:              RoundKnobButton
Version:           1.0.0
Release Date:      November, 2013
License:           GPL v2
Description:	   A round knob button to control volume and toggle between two states

****************************************************************************
Copyright (C) 2013 Radu Motisan  <radu.motisan@gmail.com>

http://www.pocketmagic.net

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
****************************************************************************/

public class RoundKnobButton extends RelativeLayout implements View.OnTouchListener {

    private final ImageView			ivRotor;
    private int					m_nWidth = 0, m_nHeight = 0;
    private int percent;
    private int max = 100;
    private float relative;
    private float oldVal;

    interface RoundKnobButtonListener {
        void onRotate(int percentage);
        void onStartRotate();
        void onStopRotate();
    }

    private RoundKnobButtonListener m_listener;

    public void SetListener(RoundKnobButtonListener l) {
        m_listener = l;
    }

    public RoundKnobButton(Context context, int back, int rotor, final int w, final int h) {
        super(context);
        // we won't wait for our size to be calculated, we'll just store out fixed size
        m_nWidth = w;
        m_nHeight = h;
        // create stator
        this.setOnTouchListener(this);
        ImageView ivBack = new ImageView(context);
        ivBack.setImageResource(back);
        RelativeLayout.LayoutParams lp_ivBack = new RelativeLayout.LayoutParams(
                w,h);
        lp_ivBack.setMargins(1,1,0,0);

        addView(ivBack, lp_ivBack);

        Bitmap srcon = BitmapFactory.decodeResource(context.getResources(), rotor);
        float scaleWidth = ((float) w) / srcon.getWidth();
        float scaleHeight = ((float) h) / srcon.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap bmpRotor = Bitmap.createBitmap(
                srcon, 0, 0,
                srcon.getWidth(), srcon.getHeight(), matrix, true);

        // create rotor
        ivRotor = new ImageView(context);
        ivRotor.setImageBitmap(bmpRotor);
        RelativeLayout.LayoutParams lp_ivKnob = new RelativeLayout.LayoutParams(w,h);//LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp_ivKnob.setMargins(1,1,0,0);
        addView(ivRotor, lp_ivKnob);
    }

    private void setRotorPosAngle(float deg) {

        if (deg >= 210 || deg <= 150) {
            if (deg > 180) deg = deg - 360;
            Matrix matrix=new Matrix();
            ivRotor.setScaleType(ScaleType.MATRIX);
            matrix.postRotate(deg, m_nWidth/2, m_nHeight/2);
            ivRotor.setImageMatrix(matrix);
        }
    }

    private void setRotorPercentage(int percentage) {
        int posDegree = percentage * 3 - 150;
        if (posDegree < 0) posDegree = 360 + posDegree;
        setRotorPosAngle(posDegree);
    }



    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                    if (m_listener != null) m_listener.onStartRotate();
                    getParent().requestDisallowInterceptTouchEvent(true);

                    relative = event.getY();
                    oldVal = (float)percent;
                break;
            case MotionEvent.ACTION_MOVE:

                int newVal = (int) (oldVal - (event.getY() - relative) / 2) ;
                if (newVal < 0)
                    newVal = 0;
                if (newVal > max)
                    newVal = max;
                if( newVal != percent) {
                    setProgress(newVal);
                    onSizeChanged(getWidth(), getHeight(), 0, 0);
                    if (m_listener != null) m_listener.onRotate(newVal);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (m_listener != null) m_listener.onStopRotate();
                getParent().requestDisallowInterceptTouchEvent(false);
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    public void setProgress(int val) {
        percent = val;
        setRotorPercentage(percent);
    }

    private int getMax() {
        return max;
    }

    private void setMax(int val) {
        max = val;
    }

}
