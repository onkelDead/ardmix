package de.paraair.ardmix;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.TextView;


import java.util.ArrayList;

import static android.widget.CompoundButton.*;

/**
 * Created by onkel on 04.11.16.
 */

public class SendsLayout extends LinearLayout implements OnClickListener {

    public static final int SEND_CHANGED = 98;
    public static final int SEND_ENABLED = 99;
    public static final int RESET_LAYOUT = 199;
    public static final int PREV_SEND_LAYOUT = 197;
    public static final int NEXT_SEND_LAYOUT = 198;
    private static final int NAVBUTTON_HEIGHT = 36;
    private Context context;

    private int iStripIndex;

    private Handler onChangeHandler;

    ArrayList<FaderView> fwSendGains = new ArrayList<>();
    ArrayList<ToggleTextButton> ttbEnables = new ArrayList<>();
    ArrayList<LinearLayout> llSends = new ArrayList<>();


    public SendsLayout(Context context) {
        super(context);
        this.context = context;
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        lp.setMargins(1,1,1,1);
        this.setLayoutParams(lp);
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(getResources().getColor(R.color.SENDS_BACKGROUND, null));
        this.setPadding(1, 0, 1, 0);

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        String vTag = (String)(v.getTag());
        Message msgButtonClick;
        switch(vTag) {
            case "close":
                msgButtonClick = onChangeHandler.obtainMessage(RESET_LAYOUT);
                onChangeHandler.sendMessage(msgButtonClick);
                break;
            case "prev":
                msgButtonClick = onChangeHandler.obtainMessage(PREV_SEND_LAYOUT);
                onChangeHandler.sendMessage(msgButtonClick);
                break;
            case "next":
                msgButtonClick = onChangeHandler.obtainMessage(NEXT_SEND_LAYOUT);
                onChangeHandler.sendMessage(msgButtonClick);
                break;

        }
    }

    public void init(StripLayout strip, Object[] sargs) {
        iStripIndex = strip.getRemoteId();

        TextView tvSendsDescription = new TextView(context);
        tvSendsDescription.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        tvSendsDescription.setTextSize(18);
        tvSendsDescription.setPadding(4,4,4,4);
        tvSendsDescription.setTextColor(Color.WHITE);
        tvSendsDescription.setTag("pluginTitle");
        tvSendsDescription.setOnClickListener(this);
        tvSendsDescription.setText("Sends of " + strip.getTrack().name);
        addView(tvSendsDescription);

        LinearLayout llButtons = new LinearLayout(context);
        llButtons.setOrientation(HORIZONTAL);
        llButtons.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        llButtons.setPadding(0,0,0,32);

        Button btnClose = new Button(context);
        LayoutParams bclp = new LayoutParams(LayoutParams.WRAP_CONTENT, NAVBUTTON_HEIGHT);
        bclp.setMargins(0,0,48,0);
        btnClose.setLayoutParams(bclp);
        btnClose.setPadding(1, 0, 1, 0);
        btnClose.setTag("close");
        btnClose.setText("Close");
        btnClose.setOnClickListener(this);
        llButtons.addView(btnClose);

        Button btnPrev = new Button(context);
        btnPrev.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, NAVBUTTON_HEIGHT));
        btnPrev.setPadding(1, 0, 1, 0);
        btnPrev.setTag("prev");
        btnPrev.setText("<");
        btnPrev.setOnClickListener(this);
        llButtons.addView(btnPrev);

        Button btnNext = new Button(context);
        btnNext.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, NAVBUTTON_HEIGHT));
        btnNext.setPadding(1, 0, 1, 0);
        btnNext.setTag("next");
        btnNext.setText(">");
        btnNext.setOnClickListener(this);
        llButtons.addView(btnNext);

        addView(llButtons);

    }

    private void addSend(int sendIndex, String name) {
        LinearLayout llSend = new LinearLayout(context);
        llSend.setOrientation(HORIZONTAL);
        llSend.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        ToggleTextButton ttbEnable = new ToggleTextButton(context, "","", Color.CYAN, Color.GRAY);
        LayoutParams slp = new LayoutParams(80, 32);
        slp.setMargins(6,2,12,2);
        ttbEnable.setPadding(0,0,0,0);
        ttbEnable.setLayoutParams(slp);
        ttbEnable.setAllText(name);
        ttbEnable.setAutoToggle(false);
        ttbEnable.setId(sendIndex);
        ttbEnable.setOnClickListener(checkedChangeListener);
        llSend.addView(ttbEnable);
        ttbEnables.add(ttbEnable);

        FaderView fwSend = new FaderView(context);
        fwSend.setLayoutParams(new LayoutParams(240, 48));
        fwSend.setMax(1000);
        fwSend.setOrientation(FaderView.Orientation.HORIZONTAL);
        fwSend.setId(sendIndex);
        fwSend.setProgressColor(getResources().getColor(R.color.BUTTON_SEND, null));
        fwSend.val0 = 782;
        fwSend.setOnChangeHandler(mHandler);
        llSend.addView(fwSend);

        fwSendGains.add(fwSend);

        addView(llSend);
        llSends.add(llSend);
    }

    private Handler mHandler = new Handler() {

        /* (non-Javadoc)
         * @see android.os.Handler#handleMessage(android.os.Message)
         */
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 10:
                    break;
                case 20:
                    int pi = msg.arg1;
                    Message fm = onChangeHandler.obtainMessage(SEND_CHANGED, iStripIndex, pi, msg.arg2);
                    onChangeHandler.sendMessage(fm);
                    break;
                case 30:
                    break;
            }
        }
    };

    public void setOnChangeHandler(Handler onChangeHandler) {
        this.onChangeHandler = onChangeHandler;
    }

    public void sendChanged(int sendIndex, float value) {
        if( sendIndex-1 < fwSendGains.size())
            fwSendGains.get(sendIndex-1).setProgress((int)(value * 1000));
    }

    public void sendEnable(int sendIndex, float state) {
        if( sendIndex-1 < ttbEnables.size()) {
            ToggleTextButton cb = ttbEnables.get(sendIndex-1);
            cb.setToggleState(state > 0);
        }
    }

    public void deinit() {
//        for( ToggleTextButton tb : ttbEnables) {
//            removeView(tb);
//        }
//        ttbEnables.clear();
//        for( FaderView fw: fwSendGains) {
//            removeView(fw);
//        }
//        fwSendGains.clear();
        for(LinearLayout ll: llSends) {
            ll.removeAllViews();
            removeView(ll);
        }
        llSends.clear();
    }

    OnClickListener checkedChangeListener = new OnClickListener() {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            ToggleTextButton tb = (ToggleTextButton)v;
            Message fm = onChangeHandler.obtainMessage(SEND_ENABLED, iStripIndex, tb.getId(), !tb.getToggleState());
            onChangeHandler.sendMessage(fm);
        }
    };

    public void sendName(int sendIndex, String name) {
        int si = sendIndex - 1;
        if( sendIndex >  ttbEnables.size() ) {
            addSend(sendIndex, name);
        }
        else {
            ttbEnables.get(si).setAllText(name);
        }

    }
}
