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
    private TextView tvStripName;
    private ToggleTextButton ttbSends;
    private ToggleTextButton ttbFX;
    private ToggleTextButton ttbRecord;
    private ToggleTextButton ttbMute;
    private ToggleTextButton ttbSolo;
    private ToggleTextButton ttbPan;
    LinearLayout faderLayout;
    private FaderView fwVolume = null;
    MeterImageView meterImage = null;

    private OnClickListener onClickListener;
    private Handler onChangeHandler;

    // params for senders
    private int oldVolume;
    private boolean oldMute;
    public Track.TrackType showtype;
    private int iPosition;


    public StripLayout(Context context) {
        super(context);
    }

    public StripLayout(Context context, Track track) {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);
        this.track = track;
    }

    public void init(Context context, StripElementMask mask) {

        removeAllViews();

        LayoutParams switchLP = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                26);
        switchLP.setMargins(1,1,1,1);

        LayoutParams testLP = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                26);
        testLP.setMargins(1,1,1,1);
        tvStripName = new TextView(context);
        tvStripName.setPadding(1,0,1,0);
        tvStripName.setMaxLines(1);
        tvStripName.setText(track.name);
        tvStripName.setLayoutParams(testLP);
        tvStripName.setId(track.remoteId);
        tvStripName.setTextColor(Color.BLACK);
        tvStripName.setClickable(true);
        tvStripName.setId(track.remoteId);
        tvStripName.setTag("strip");
        tvStripName.setOnClickListener(onClickListener);
        tvStripName.setGravity(Gravity.CENTER_HORIZONTAL);
        switch(track.type) {
            case AUDIO:
                tvStripName.setBackgroundColor(0xff00FFFF);
                break;
            case BUS:
                tvStripName.setBackgroundColor(Color.BLUE);
                setBackgroundColor(0x200000FF);
                tvStripName.setTextColor(Color.WHITE);
                break;
            default:
                tvStripName.setBackgroundColor(Color.WHITE);
                break;
        }
        if( mask.bTitle )
            this.addView(tvStripName);

        ttbFX = new ToggleTextButton(context, "FX","FX", 0xffFF80FF, Color.GRAY);
        ttbFX.setPadding(0,0,0,0);
        ttbFX.setLayoutParams(switchLP);
        ttbFX.setToggleState(false);
        ttbFX.setId(track.remoteId);
        ttbFX.setTag("fx");
        ttbFX.setOnClickListener(onClickListener);
        if( mask.bFX)
            this.addView(ttbFX);
        ttbFX.setAutoToggle(true);

        ttbSends = new ToggleTextButton(context, "SEND","SEND", Color.CYAN, Color.GRAY);
        ttbSends.setPadding(0,0,0,0);
        ttbSends.setLayoutParams(switchLP);
        ttbSends.setToggleState(false);
        ttbSends.setId(track.remoteId);
        ttbSends.setTag("aux");
        ttbSends.setOnClickListener(onClickListener);
        if (track.type == Track.TrackType.MASTER) {
            ttbSends.setEnabled(false);
            ttbSends.setUntoggledText("");
        }
        if (mask.bSend)
            this.addView(ttbSends);
        ttbSends.setAutoToggle(true);

        ttbRecord = new ToggleTextButton(context, "REC","REC", Color.RED, Color.GRAY);
        ttbRecord.setPadding(0,0,0,0);
        ttbRecord.setLayoutParams(switchLP);
        ttbRecord.setId(track.remoteId);
        if (track.type == Track.TrackType.AUDIO) {
            ttbRecord.setTag("rec");
            ttbRecord.setToggleState(track.recEnabled);
            ttbRecord.setOnClickListener(onClickListener);
        }
        else if (track.type == Track.TrackType.MASTER) {
            ttbRecord.setEnabled(false);

            ttbRecord.setUntoggledText("");
            recChanged();
        }
        else {
            ttbRecord.setAllText("Receive");
            ttbRecord.onColor = Color.BLUE;
            ttbRecord.setOffColor(Color.GRAY);
            ttbRecord.setTag("in");
            ttbRecord.setToggleState(false);
            ttbRecord.setOnClickListener(onClickListener);
            ttbRecord.setAutoToggle(true);
        }

        if( mask.bRecord)
            this.addView(ttbRecord);


        LayoutParams meterParam = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                160);
        meterParam.setMargins(0,0,0,0);
        meterImage = new MeterImageView(context);
        meterImage.setLayoutParams(meterParam);
        meterImage.setId(track.remoteId);
        meterImage.setBackgroundColor(Color.BLACK);
        meterImage.setBackgroundResource(R.drawable.gain_image);

        if (mask.bMeter)
            this.addView(meterImage);

        ttbMute = new ToggleTextButton(context, "MUTE", "MUTE", Color.YELLOW, Color.GRAY);
        ttbMute.setPadding(0,0,0,0);
        ttbMute.setLayoutParams(switchLP);
        ttbMute.setTag("mute");
        ttbMute.setId(track.remoteId);
        ttbMute.setToggleState(track.muteEnabled );
        if (mask.bMute) {
            this.addView(ttbMute);
        }
        ttbMute.setOnClickListener(onClickListener);


        ttbSolo = new ToggleTextButton(context, "SOLO", "SOLO", Color.GREEN, Color.GRAY);
        ttbSolo.setPadding(0,0,0,0);
        ttbSolo.setLayoutParams(switchLP);
        ttbSolo.setTag("solo");
        ttbSolo.setId(track.remoteId);
        ttbSolo.setToggleState(track.soloEnabled);
        ttbSolo.setOnClickListener(onClickListener);
        if (track.type == Track.TrackType.MASTER) {
            ttbSolo.setEnabled(track.soloEnabled);
            ttbSolo.setUntoggledText("");
        }
        if (mask.bSolo)
            this.addView(ttbSolo);

        ttbPan = new ToggleTextButton(context, "PAN", "PAN", 0xffffbb33, Color.GRAY);
        ttbPan.setPadding(0,0,0,0);
        ttbPan.setLayoutParams(switchLP);
        ttbPan.setTag("pan");
        ttbPan.setId(track.remoteId);
        ttbPan.setToggleState(false);
        ttbPan.setOnClickListener(onClickListener);
        if (track.type == Track.TrackType.MASTER) {
            ttbPan.setEnabled(false);
            ttbPan.setUntoggledText("");
        }
        if (mask.bPan)
            this.addView(ttbPan);
        ttbPan.setAutoToggle(true);

        fwVolume = new FaderView(context);
        fwVolume.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        fwVolume.setTag("volume");
        fwVolume.setId(track.remoteId);
        fwVolume.setOnChangeHandler(mHandler);
        fwVolume.val0 = 782;
        fwVolume.setProgress(track.trackVolume);
        if (mask.bFader)
            this.addView(fwVolume);

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnChangeHandler(Handler myHandler) {
        this.onChangeHandler= myHandler;
    }

    public void nameChanged() {
        tvStripName.setText(track.name);
    }

    public void recChanged() {
//        System.out.printf("rec changed on %d\n", track.remoteId);
        ttbRecord.setToggleState(track.recEnabled);
    }

    public void muteChanged() {
        ttbMute.setToggleState(track.muteEnabled);
    }

    public void soloChanged() {
        ttbSolo.setToggleState(track.soloEnabled);
    }

    public void panChanged() {
        if( showtype == Track.TrackType.PAN)
            fwVolume.setProgress((int) (track.panPosition * 1000));
    }

    public void volumeChanged() {
        if (fwVolume == null) {
            fwVolume = new FaderView(this.getContext());
            fwVolume.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
            fwVolume.setTag("volume");
            fwVolume.setId(track.remoteId);
            fwVolume.setClickable(true);

            fwVolume.setOnChangeHandler(mHandler);

            faderLayout.addView(fwVolume);
        }
        fwVolume.setProgress(track.trackVolume);
    }

    private Handler mHandler = new Handler() {

        /* (non-Javadoc)
         * @see android.os.Handler#handleMessage(android.os.Message)
         */
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 10:
                    track.setTrackVolumeOnSeekBar(true);
                    break;
                case 20:
                    if(showtype == Track.TrackType.RECEIVE) {
                        track.trackVolume = msg.arg2;
                        onChangeHandler.sendMessage(onChangeHandler.obtainMessage(MSG_WHAT_RECEIVE_CHANGED, msg.arg1, track.trackVolume));
                    }
                    else if(showtype == Track.TrackType.SEND) {
                        track.trackVolume = msg.arg2;
                        onChangeHandler.sendMessage(onChangeHandler.obtainMessage(MSG_WHAT_AUX_CHANGED, msg.arg1, track.trackVolume));
                    }
                    else if(showtype == Track.TrackType.PAN) {
                        track.panPosition = (float)msg.arg2 / 1000;
                        onChangeHandler.sendMessage(onChangeHandler.obtainMessage(MSG_WHAT_PAN_CHANGED, msg.arg1, msg.arg2));
                    }
                    else {
                        track.trackVolume = msg.arg2;
                        onChangeHandler.sendMessage(onChangeHandler.obtainMessage(MSG_WHAT_FADER_CHANGED, msg.arg1, track.trackVolume));
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
            fwVolume.setProgressColor(0xA000FFFF);
            oldVolume = track.trackVolume;
            oldMute = track.muteEnabled;
            track.muteEnabled = enabled;
            track.trackVolume = (int)(sendVolume * 1000);
            fwVolume.setProgress(track.trackVolume);
            ttbSolo.setEnabled(false);
            ttbMute.setToggleState(!enabled);
        }
        else if(type == Track.TrackType.SEND) {
            track.source_id = sendId;
            fwVolume.setProgressColor(Color.argb(255,160,160,255));
            oldVolume = track.trackVolume;
            oldMute = track.muteEnabled;
            track.trackVolume = (int)(sendVolume * 1000);
            fwVolume.setProgress(track.trackVolume);
            ttbSolo.setEnabled(false);
            ttbMute.setAllText("Off");
            ttbMute.setToggledText("On");
            ttbMute.onColor = Color.CYAN;
            ttbMute.setOffColor(Color.GRAY);
            ttbMute.setToggleState(enabled);
            ttbMute.setAutoToggle(true);
        }
        else if(type == Track.TrackType.PAN ) {
            fwVolume.setStrTopText("right");
            fwVolume.setStrBottomText("left");
            fwVolume.val0 = 500;
            oldVolume = track.trackVolume;

        }
        else {
            track.source_id = -1;
            fwVolume.setProgressColor(getResources().getColor(R.color.fader, null));
            fwVolume.setbTopText(false);
            track.trackVolume = oldVolume;
            fwVolume.setProgress(track.trackVolume);
            ttbSolo.setEnabled(true);
            ttbMute.setAllText("Mute");
            ttbMute.onColor = Color.YELLOW;
            ttbMute.setOffColor(Color.GRAY);
            ttbMute.setToggleState(oldMute);
            ttbMute.setAutoToggle(false);
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
        ttbSends.setToggleState(state);
    }

    public void ResetPan() {
        ttbPan.setToggleState(false);
        fwVolume.setbTopText(false);
        fwVolume.setbBottomText(false);
        fwVolume.val0 = 782;
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
        return iPosition;
    }

    public void setPosition(int position) {
        this.iPosition = position;
    }

    public void fxOff() {
        ttbFX.setToggleState(false);
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
        onChangeHandler.sendMessage(onChangeHandler.obtainMessage(MSG_WHAT_RECEIVE_CHANGED, track.remoteId, track.trackVolume));
    }

    public void resetBackground() {
        if( track.type == Track.TrackType.AUDIO)
            setBackgroundColor(getResources().getColor(R.color.VeryDark, null));
        if( track.type == Track.TrackType.BUS)
            setBackgroundColor(0x200000FF);
    }
}