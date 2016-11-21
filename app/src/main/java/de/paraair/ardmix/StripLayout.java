package de.paraair.ardmix;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by onkel on 06.10.16.
 */

public class StripLayout extends LinearLayout {

    public static final int MSG_WHAT_FADER_CHANGED = 20;
    public static final int MSG_WHAT_AUX_CHANGED = 25;
    public static final int MSG_WHAT_RECEIVE_CHANGED = 26;
    public static final int MSG_WHAT_PAN_CHANGED = 27;

    public static final int STRIP_WIDTH = 60;

    private Track track;
    private TextView stripName;
    private ToggleTextButton sends;
    private ToggleTextButton fxs;
    private ToggleTextButton recEnabled;
    private ToggleTextButton muteEnabled;
    private ToggleTextButton soloEnabled;
    private ToggleTextButton panEnabled;
    LinearLayout faderLayout;
    private FaderView volumeSeek = null;
    MeterImageView meterImage = null;

    private OnClickListener onClickListener;
    private Handler onChangeHandler;

    // params for senders
    private int oldVolume;
    private boolean oldMute;
    public Track.TrackType showtype;
    private int position;


    public StripLayout(Context context) {
        super(context);
    }

    public StripLayout(Context context, Track track) {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);
        this.track = track;


    }

    public void init(Context context) {

        LayoutParams switchLP = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                26);
        switchLP.setMargins(1,1,1,1);

        LayoutParams testLP = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                26);
        testLP.setMargins(1,1,1,1);
        stripName = new TextView(context);
        stripName.setPadding(1,0,1,0);
        stripName.setMaxLines(1);
        stripName.setText(track.name);
        stripName.setLayoutParams(testLP);
        stripName.setId(track.remoteId);
        stripName.setTextColor(Color.BLACK);
        stripName.setClickable(true);
        stripName.setId(track.remoteId);
        stripName.setTag("strip");
        stripName.setOnClickListener(onClickListener);
        stripName.setGravity(Gravity.CENTER_HORIZONTAL);
        switch(track.type) {
            case AUDIO:
                stripName.setBackgroundColor(0xff00FFFF);
                break;
            case BUS:
                stripName.setBackgroundColor(0xff0000FF);
                stripName.setTextColor(Color.WHITE);
                break;
            default:
                stripName.setBackgroundColor(Color.WHITE);
                break;
        }
        this.addView(stripName);

        fxs = new ToggleTextButton(context, "FX","FX", 0xffFF80FF, Color.GRAY);
        fxs.setPadding(0,0,0,0);
        fxs.setLayoutParams(switchLP);
        fxs.setToggleState(false);
        fxs.setId(track.remoteId);
        fxs.setTag("fx");
        fxs.setOnClickListener(onClickListener);
//        if (track.type == Track.TrackType.MASTER) {
//            fxs.setEnabled(false);
//            fxs.setUntoggledText("");
//        }
        this.addView(fxs);

        sends = new ToggleTextButton(context, "SEND","SEND", Color.CYAN, Color.GRAY);
        sends.setPadding(0,0,0,0);
        sends.setLayoutParams(switchLP);
        sends.setToggleState(false);
        sends.setId(track.remoteId);
        sends.setTag("aux");
        sends.setOnClickListener(onClickListener);
        if (track.type == Track.TrackType.MASTER) {
            sends.setEnabled(false);
            sends.setUntoggledText("");
        }
        this.addView(sends);



        recEnabled = new ToggleTextButton(context, "REC","REC", Color.RED, Color.GRAY);
        recEnabled.setPadding(0,0,0,0);
        recEnabled.setLayoutParams(switchLP);
        recEnabled.setId(track.remoteId);
        if (track.type == Track.TrackType.AUDIO) {
            recEnabled.setTag("rec");
            recEnabled.setOnClickListener(onClickListener);
        }
        else if (track.type == Track.TrackType.MASTER) {
            recEnabled.setEnabled(false);
            recEnabled.setUntoggledText("");
            recChanged();
        }
        else {
            recEnabled.setAllText("Receive");
            recEnabled.setTextColor(Color.argb(255,120,120,255));
            recEnabled.onColor = Color.BLUE;
            recEnabled.offColor = Color.GRAY;
            recEnabled.setTag("in");
            recEnabled.setToggleState(false);
            recEnabled.setOnClickListener(onClickListener);
        }

        this.addView(recEnabled);


        LayoutParams meterParam = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                160);
        meterParam.setMargins(0,0,0,0);
        meterImage = new MeterImageView(context);
        meterImage.setLayoutParams(meterParam);
        meterImage.setId(track.remoteId);
        meterImage.setBackgroundColor(Color.BLACK);
        meterImage.setBackgroundResource(R.drawable.gain_image);
        this.addView(meterImage);

        muteEnabled = new ToggleTextButton(context, "MUTE", "MUTE", Color.YELLOW, Color.GRAY);
        muteEnabled.setPadding(0,0,0,0);
        muteEnabled.setLayoutParams(switchLP);
        muteEnabled.setTag("mute");
        muteEnabled.setId(track.remoteId);
        muteEnabled.setToggleState(track.muteEnabled );
        muteEnabled.setOnClickListener(onClickListener);
        this.addView(muteEnabled);


        soloEnabled = new ToggleTextButton(context, "SOLO", "SOLO", Color.GREEN, Color.GRAY);
        soloEnabled.setPadding(0,0,0,0);
        soloEnabled.setLayoutParams(switchLP);
        soloEnabled.setTag("solo");
        soloEnabled.setId(track.remoteId);
        soloEnabled.setToggleState(track.soloEnabled);
        soloEnabled.setOnClickListener(onClickListener);
        if (track.type == Track.TrackType.MASTER) {
            soloEnabled.setEnabled(false);
            soloEnabled.setUntoggledText("");
        }
        this.addView(soloEnabled);

        panEnabled = new ToggleTextButton(context, "PAN", "PAN", 0xffffbb33, Color.GRAY);
        panEnabled.setPadding(0,0,0,0);
        panEnabled.setLayoutParams(switchLP);
        panEnabled.setTag("pan");
        panEnabled.setId(track.remoteId);
        panEnabled.setOnClickListener(onClickListener);
        if (track.type == Track.TrackType.MASTER) {
            panEnabled.setEnabled(false);
            panEnabled.setUntoggledText("");
        }
        this.addView(panEnabled);
        panEnabled.setToggleState(false);

        volumeSeek = new FaderView(context);
        volumeSeek.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        volumeSeek.setTag("volume");
        volumeSeek.setId(track.remoteId);
        volumeSeek.setOnChangeHandler(mHandler);
        this.addView(volumeSeek);

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnChangeHandler(Handler myHandler) {
        this.onChangeHandler= myHandler;
    }

    public void nameChanged() {
        stripName.setText(track.name);
    }

    public void recChanged() {
//        System.out.printf("rec changed on %d\n", track.remoteId);
        recEnabled.setToggleState(track.recEnabled);
    }

    public void muteChanged() {
        muteEnabled.setToggleState(track.muteEnabled);
    }

    public void soloChanged() {
        soloEnabled.setToggleState(track.soloEnabled);
    }

    public void panChanged() {
        if( showtype == Track.TrackType.PAN)
            volumeSeek.setProgress((int) (track.panPosition * 1000));
    }

    public void volumeChanged() {
        if (volumeSeek == null) {
            volumeSeek = new FaderView(this.getContext());
            volumeSeek.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
            volumeSeek.setTag("volume");
            volumeSeek.setId(track.remoteId);
            volumeSeek.setClickable(true);

            volumeSeek.setOnChangeHandler(mHandler);

            faderLayout.addView(volumeSeek);
        }
        volumeSeek.setProgress(track.trackVolume);
    }

    private Handler mHandler = new Handler() {

        /* (non-Javadoc)
         * @see android.os.Handler#handleMessage(android.os.Message)
         */
        @Override
        public void handleMessage(Message msg) {

            int index;
            StripLayout tl;

            switch (msg.what) {
                case 10:
                    track.setTrackVolumeOnSeekBar(true);
                    break;
                case 20:
                    if(showtype == Track.TrackType.RECEIVE) {
                        track.trackVolume = msg.arg2;
                        Message fm = onChangeHandler.obtainMessage(MSG_WHAT_RECEIVE_CHANGED, msg.arg1, track.trackVolume);
                        onChangeHandler.sendMessage(fm);
                    }
                    else if(showtype == Track.TrackType.SEND) {
                        track.trackVolume = msg.arg2;
                        Message fm = onChangeHandler.obtainMessage(MSG_WHAT_AUX_CHANGED, msg.arg1, track.trackVolume);
                        onChangeHandler.sendMessage(fm);
                    }
                    else if(showtype == Track.TrackType.PAN) {
                        track.panPosition = (float)msg.arg2 / 1000;

                        Message fm = onChangeHandler.obtainMessage(MSG_WHAT_PAN_CHANGED, msg.arg1, msg.arg2);
                        onChangeHandler.sendMessage(fm);
                    }
                    else {
                        track.trackVolume = msg.arg2;
                        Message fm = onChangeHandler.obtainMessage(MSG_WHAT_FADER_CHANGED, msg.arg1, track.trackVolume);
                        onChangeHandler.sendMessage(fm);
                    }
                    break;
                case 30:
                    track.setTrackVolumeOnSeekBar(false);
            }
        }
    };

    public void setType(Track.TrackType type, Float sendVolume, int sendId, boolean enabled) {
        if(type == Track.TrackType.RECEIVE) {
            track.source_id = sendId;
//            volumeSeek.setBackgroundColor(Color.BLUE);
            volumeSeek.setProgressColor(0xA000FFFF);
            oldVolume = track.trackVolume;
            oldMute = track.muteEnabled;
            track.muteEnabled = enabled;
            track.trackVolume = (int)(sendVolume * 1000);
            volumeSeek.setProgress(track.trackVolume);
            soloEnabled.setEnabled(false);
            muteEnabled.setToggleState(!enabled);
        }
        else if(type == Track.TrackType.SEND) {
            track.source_id = sendId;
//            volumeSeek.setBackgroundColor(Color.BLUE);
            volumeSeek.setProgressColor(Color.argb(255,160,160,255));
            oldVolume = track.trackVolume;
            oldMute = track.muteEnabled;
            track.trackVolume = (int)(sendVolume * 1000);
            volumeSeek.setProgress(track.trackVolume);
            soloEnabled.setEnabled(false);
            muteEnabled.setToggleState(!enabled);
//            muteEnabled.setEnabled(false);
        }
        else if(type == Track.TrackType.PAN ) {
            volumeSeek.setStrTopText("right");
            volumeSeek.setStrBottomText("left");
            volumeSeek.val0 = 500;
            oldVolume = track.trackVolume;

        }
        else {
//            volumeSeek.setBackgroundColor(getResources().getColor(R.color.VeryDark));
            track.source_id = -1;
            volumeSeek.setProgressColor(getResources().getColor(R.color.fader));
            volumeSeek.setbTopText(false);
            track.trackVolume = oldVolume;
            volumeSeek.setProgress(track.trackVolume);
            soloEnabled.setEnabled(true);
            muteEnabled.setToggleState(oldMute);
            track.muteEnabled = oldMute;
        }
        showtype = type;
    }

    public void ResetType() {
        setType(track.type, 0f, 0, track.muteEnabled);
    }

    public Track.TrackType getShowType() {
        return showtype;
    }

    public void meterChange() {
        meterImage.setProgress(track.meter);
    }

    public void sendChanged(boolean state) {
        sends.setToggleState(state);
    }

    public void ResetPan() {
        panEnabled.setToggleState(false);
        volumeSeek.setbTopText(false);
        volumeSeek.setbBottomText(false);
        volumeSeek.val0 = 782;
        showtype = track.type;
    }

    public Track.TrackType getTrackType() {
        return track.type;
    }

    public Track getTrack() {
        return track;
    }

    public int getRemoteId() {
        return track.remoteId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void fxOff() {
        fxs.setToggleState(false);
    }

    public void pushVolume() {
        oldVolume = track.trackVolume;
    }

    public void pullVolume() {
        track.trackVolume = oldVolume;
        volumeChanged();
    }

    public int getOldVolume() {
        return oldVolume;
    }

    public void changeVolume(int delta) {
        track.trackVolume += delta;
        if(track.trackVolume < 0)
            track.trackVolume = 0;
        volumeChanged();
        Message fm = onChangeHandler.obtainMessage(MSG_WHAT_RECEIVE_CHANGED, track.remoteId, track.trackVolume);
        onChangeHandler.sendMessage(fm);
    }
}