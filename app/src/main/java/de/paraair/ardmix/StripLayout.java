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

    public static final int STRIP_FADER_CHANGED = 20;
    public static final int AUX_CHANGED = 25;
    public static final int RECEIVE_CHANGED = 26;
    public static final int PAN_CHANGED = 27;

    public static final int STRIP_SMALL_WIDTH = 52;
    public static final int STRIP_MEDIUM_WIDTH = 72;
    public static final int STRIP_WIDE_WIDTH = 96;
    private static final int METER_IMAGE_HIGHT = 160;

    private Track track;
    private TextView tvStripName;
    private ToggleTextButton ttbSends;
    private ToggleTextButton ttbFX;
    private ToggleTextButton ttbRecord;
    private ToggleTextButton ttbInput;
    private ToggleTextButton ttbMute;
    private ToggleTextButton ttbSolo;
    private ToggleTextButton ttbSoloIso;
    private ToggleTextButton ttbSoloSafe;
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

    private int strip_wide;

    public StripLayout(Context context) {
        super(context);
    }

    public StripLayout(Context context, Track track) {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(getResources().getColor(R.color.fader,null));
        this.track = track;
    }


    public void init(Context context, StripElementMask mask) {

        removeAllViews();

        switch( mask.stripSize ) {
            case 0:
                strip_wide = STRIP_SMALL_WIDTH;
                break;
            case 1:
                strip_wide = STRIP_MEDIUM_WIDTH;
                break;
            case 2:
                strip_wide = STRIP_WIDE_WIDTH;
                break;

        }
        this.getLayoutParams().width = strip_wide;

        LayoutParams switchLP = new LayoutParams(
                strip_wide,
                32);

        LayoutParams switchWLP = new LayoutParams(
                strip_wide / 2,
                32);

        switchLP.setMargins(1,1,0,0);
        switchWLP.setMargins(1,1,0,0);

        LayoutParams testLP = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                26);
        testLP.setMargins(1,1,0,0);
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
                tvStripName.setBackgroundColor(getResources().getColor(R.color.AUDIO_STRIP_BACKGROUND, null));
                setBackgroundColor(getResources().getColor(R.color.AUDIO_STRIP_BACKGROUND, null));
                break;
            case BUS:
                tvStripName.setBackgroundColor(getResources().getColor(R.color.BUS_AUX_BACKGROUND, null));
                setBackgroundColor(getResources().getColor(R.color.BUS_AUX_BACKGROUND, null));
                break;
            default:
                tvStripName.setBackgroundColor(Color.WHITE);
                break;
        }
        if( mask.bTitle )
            this.addView(tvStripName);


        LayoutParams meterParam = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                METER_IMAGE_HIGHT);
        meterParam.setMargins(1,1,0,0);
        meterImage = new MeterImageView(context);
        meterImage.setLayoutParams(meterParam);
        meterImage.setId(track.remoteId);
        meterImage.setBackgroundColor(getResources().getColor(R.color.VeryDark, null));

        if (mask.bMeter)
            this.addView(meterImage);

        ttbFX = new ToggleTextButton(context, "FX","FX", getResources().getColor(R.color.BUTTON_FX, null), R.color.VeryDark);
        ttbFX.setPadding(0,0,0,0);
        ttbFX.setLayoutParams(switchLP);
        ttbFX.setToggleState(false);
        ttbFX.setId(track.remoteId);
        ttbFX.setTag("fx");
        ttbFX.setOnClickListener(onClickListener);
        ttbFX.setAutoToggle(true);


        ttbSends = new ToggleTextButton(context, "SEND","SEND", getResources().getColor(R.color.BUTTON_SEND, null), R.color.VeryDark);
        ttbSends.setPadding(0,0,0,0);
        ttbSends.setLayoutParams(switchLP);
        ttbSends.setToggleState(false);
        ttbSends.setId(track.remoteId);
        ttbSends.setTag("aux");
        ttbSends.setOnClickListener(onClickListener);
        ttbSends.setAutoToggle(true);

        if( mask.stripSize == 2) {
            LinearLayout send_fx = new LinearLayout(context);
            send_fx.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            send_fx.setOrientation(HORIZONTAL);
            if( track.type != Track.TrackType.MASTER) {
                ttbFX.setLayoutParams(switchWLP);
                ttbSends.setLayoutParams(switchWLP);
                if (mask.bFX)
                    send_fx.addView(ttbFX);
                if (mask.bSend && track.type != Track.TrackType.MASTER)
                    send_fx.addView(ttbSends);
            }
            else {
                ttbFX.setLayoutParams(switchLP);
                if (mask.bFX)
                    send_fx.addView(ttbFX);
            }
            this.addView(send_fx);
        }
        else {
            if (mask.bFX)
                this.addView(ttbFX);
            if (mask.bSend && track.type != Track.TrackType.MASTER)
                this.addView(ttbSends);
        }

        if( track.type != Track.TrackType.MASTER ) {
            ttbRecord = new ToggleTextButton(context, "REC", "REC", getResources().getColor(R.color.BUTTON_RECORD, null), R.color.VeryDark);
            ttbRecord.setPadding(0, 0, 0, 0);
            ttbRecord.setLayoutParams(switchLP);
            ttbRecord.setId(track.remoteId);
            if (track.type == Track.TrackType.AUDIO || track.type == Track.TrackType.MIDI ) {
                ttbRecord.setTag("rec");
                ttbRecord.setToggleState(track.recEnabled);
                ttbRecord.setOnClickListener(onClickListener);
            } else if (track.type == Track.TrackType.BUS) {
                ttbRecord.setAllText("Receive");
                ttbRecord.onColor = getResources().getColor(R.color.BUS_AUX_BACKGROUND,null);
                ttbRecord.setOffColor(R.color.VeryDark);
                ttbRecord.setTag("in");
                ttbRecord.setToggleState(false);
                ttbRecord.setOnClickListener(onClickListener);
                ttbRecord.setAutoToggle(true);
            }


            ttbInput = new ToggleTextButton(context, "INPUT", "INPUT", getResources().getColor(R.color.BUTTON_INPUT, null), R.color.VeryDark);
            ttbInput.setPadding(0, 0, 0, 0);
            ttbInput.setLayoutParams(switchLP);
            ttbInput.setTag("input");
            ttbInput.setId(track.remoteId);
            ttbInput.setToggleState(track.stripIn);
            ttbInput.setOnClickListener(onClickListener);
            if (mask.stripSize == 2) {
                LinearLayout rec_in = new LinearLayout(context);
                rec_in.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                rec_in.setOrientation(HORIZONTAL);
                if (track.type == Track.TrackType.AUDIO) {
                    ttbRecord.setLayoutParams(switchWLP);
                    ttbInput.setLayoutParams(switchWLP);
                    if (mask.bRecord)
                        rec_in.addView(ttbRecord);
                    if (mask.bInput)
                        rec_in.addView(ttbInput);
                } else if (track.type == Track.TrackType.BUS) {
                    ttbRecord.setLayoutParams(switchLP);
                    if (mask.bRecord)
                        rec_in.addView(ttbRecord);
                }
                this.addView(rec_in);
            } else {
                if (mask.bRecord && track.type != Track.TrackType.MASTER)
                    this.addView(ttbRecord);
                if (mask.bInput && track.type == Track.TrackType.AUDIO)
                    this.addView(ttbInput);
            }
        }


        ttbMute = new ToggleTextButton(context, "MUTE", "MUTE", getResources().getColor(R.color.BUTTON_MUTE, null), R.color.VeryDark);
        ttbMute.setPadding(0,0,0,0);
        ttbMute.setLayoutParams(switchLP);
        ttbMute.setTag("mute");
        ttbMute.setId(track.remoteId);
        ttbMute.setToggleState(track.muteEnabled );
        ttbMute.setOnClickListener(onClickListener);

        ttbSolo = new ToggleTextButton(context, "SOLO", "SOLO", getResources().getColor(R.color.BUTTON_SOLO, null), R.color.VeryDark);
        ttbSolo.setPadding(0,0,0,0);
        ttbSolo.setLayoutParams(switchLP);
        ttbSolo.setTag("solo");
        ttbSolo.setId(track.remoteId);
        ttbSolo.setToggleState(track.soloEnabled);
        ttbSolo.setOnClickListener(onClickListener);

        if( mask.stripSize == 2) {
            LinearLayout mute_solo = new LinearLayout(context);
            mute_solo.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            mute_solo.setOrientation(HORIZONTAL);
            if( track.type != Track.TrackType.MASTER) {
                ttbMute.setLayoutParams(switchWLP);
                ttbSolo.setLayoutParams(switchWLP);
                if (mask.bMute)
                    mute_solo.addView(ttbMute);
                if (mask.bSolo && track.type != Track.TrackType.MASTER)
                    mute_solo.addView(ttbSolo);
            }
            else {
                ttbMute.setLayoutParams(switchLP);
                if (mask.bMute)
                    mute_solo.addView(ttbMute);
            }
            this.addView(mute_solo);
        }
        else {
            if (mask.bMute)
                this.addView(ttbMute);
            if (mask.bSolo && track.type != Track.TrackType.MASTER)
                this.addView(ttbSolo);
        }

        if( track.type != Track.TrackType.MASTER ) {
            ttbSoloIso = new ToggleTextButton(context, "Iso", "Iso", getResources().getColor(R.color.BUTTON_SOLO, null), R.color.VeryDark);
            ttbSoloIso.setPadding(0, 0, 0, 0);
            ttbSoloIso.setLayoutParams(switchLP);
            ttbSoloIso.setTag("soloiso");
            ttbSoloIso.setId(track.remoteId);
            ttbSoloIso.setToggleState(track.soloIsolateEnabled);
            ttbSoloIso.setOnClickListener(onClickListener);
            if (track.type == Track.TrackType.MASTER) {
                ttbSoloIso.setEnabled(track.soloIsolateEnabled);
                ttbSoloIso.setUntoggledText("");
            }

            ttbSoloSafe = new ToggleTextButton(context, "Lock", "Lock", getResources().getColor(R.color.BUTTON_SOLO, null), R.color.VeryDark);
            ttbSoloSafe.setPadding(0, 0, 0, 0);
            ttbSoloSafe.setLayoutParams(switchLP);
            ttbSoloSafe.setTag("solosafe");
            ttbSoloSafe.setId(track.remoteId);
            ttbSoloSafe.setToggleState(track.soloSafeEnabled);
            ttbSoloSafe.setOnClickListener(onClickListener);
            if (track.type == Track.TrackType.MASTER) {
                ttbSoloSafe.setEnabled(track.soloSafeEnabled);
                ttbSoloSafe.setUntoggledText("");
            }

            if (mask.stripSize == 2) {
                LinearLayout iso_safe = new LinearLayout(context);
                iso_safe.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                iso_safe.setOrientation(HORIZONTAL);
                ttbSoloIso.setLayoutParams(switchWLP);
                ttbSoloSafe.setLayoutParams(switchWLP);
                if (mask.bSoloIso)
                    iso_safe.addView(ttbSoloIso);
                if (mask.bSoloSafe)
                    iso_safe.addView(ttbSoloSafe);
                this.addView(iso_safe);
            } else {
                if (mask.bSoloIso)
                    this.addView(ttbSoloIso);
                if (mask.bSoloSafe)
                    this.addView(ttbSoloSafe);
            }
        }

        if( track.type != Track.TrackType.MASTER ) {
            ttbPan = new ToggleTextButton(context, "PAN", "PAN", getResources().getColor(R.color.BUTTON_PAN, null), R.color.VeryDark);
            ttbPan.setPadding(0, 0, 0, 0);
            ttbPan.setLayoutParams(switchLP);
            ttbPan.setTag("pan");
            ttbPan.setId(track.remoteId);
            ttbPan.setToggleState(false);
            ttbPan.setOnClickListener(onClickListener);
            ttbPan.setAutoToggle(true);
            if (track.type == Track.TrackType.MASTER) {
                ttbPan.setEnabled(false);
                ttbPan.setUntoggledText("");
            }
            if (mask.bPan)
                this.addView(ttbPan);
        }

        LayoutParams fwLP = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        fwLP.setMargins(1,1,0,0);
        fwVolume = new FaderView(context);
        fwVolume.setLayoutParams(fwLP);
        fwVolume.setBackgroundColor(getResources().getColor(R.color.VeryDark, null));
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

    public void soloSafeChanged() {
        if( track.soloSafeEnabled)
            ttbSolo.setEnabled(false);
        else
            ttbSolo.setEnabled(true);
        ttbSoloSafe.setToggleState(track.soloSafeEnabled);
    }

    public void soloIsoChanged() {
        ttbSoloIso.setToggleState(track.soloIsolateEnabled);
    }


    public void inputChanged() {
        ttbInput.setToggleState(track.stripIn);
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
                        onChangeHandler.sendMessage(onChangeHandler.obtainMessage(RECEIVE_CHANGED, msg.arg1, track.trackVolume));
                    }
                    else if(showtype == Track.TrackType.SEND) {
                        track.trackVolume = msg.arg2;
                        onChangeHandler.sendMessage(onChangeHandler.obtainMessage(AUX_CHANGED, msg.arg1, track.trackVolume));
                    }
                    else if(showtype == Track.TrackType.PAN) {
                        track.panPosition = (float)msg.arg2 / 1000;
                        onChangeHandler.sendMessage(onChangeHandler.obtainMessage(PAN_CHANGED, msg.arg1, msg.arg2));
                    }
                    else {
                        track.trackVolume = msg.arg2;
                        onChangeHandler.sendMessage(onChangeHandler.obtainMessage(STRIP_FADER_CHANGED, msg.arg1, track.trackVolume));
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
            fwVolume.setProgressColor(getResources().getColor(R.color.BUS_AUX_BACKGROUND, null));
            oldVolume = track.trackVolume;
            oldMute = track.muteEnabled;
            track.muteEnabled = enabled;
            track.trackVolume = (int)(sendVolume * 1000);
            fwVolume.setProgress(track.trackVolume);
            ttbSolo.setEnabled(false);
            ttbMute.setAllText("Off");
            ttbMute.setToggledText("On");
            ttbMute.onColor = getResources().getColor(R.color.BUS_AUX_BACKGROUND, null);
            ttbMute.setOffColor(R.color.VeryDark);
            ttbMute.setToggleState(enabled);
            ttbMute.setAutoToggle(true);
        }
        else if(type == Track.TrackType.SEND) {
            track.source_id = sendId;
            fwVolume.setProgressColor(getResources().getColor(R.color.BUS_AUX_BACKGROUND, null));
            oldVolume = track.trackVolume;
            oldMute = track.muteEnabled;
            track.trackVolume = (int)(sendVolume * 1000);
            fwVolume.setProgress(track.trackVolume);
            ttbSolo.setEnabled(false);
            ttbMute.setAllText("Off");
            ttbMute.setToggledText("On");
            ttbMute.onColor = getResources().getColor(R.color.BUS_AUX_BACKGROUND, null);
            ttbMute.setOffColor(R.color.VeryDark);
            ttbMute.setToggleState(enabled);
            ttbMute.setAutoToggle(true);
        }
        else if(type == Track.TrackType.PAN ) {
            fwVolume.setStrTopText("right");
            fwVolume.setStrBottomText("left");
            fwVolume.setMin(500);
            fwVolume.setProgressColor(getResources().getColor(R.color.BUTTON_PAN, null));
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
            ttbMute.onColor = getResources().getColor(R.color.BUTTON_MUTE, null);
            ttbMute.setOffColor(R.color.VeryDark);
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
        fwVolume.setMin(0);
        fwVolume.setProgressColor(getResources().getColor(R.color.AUDIO_STRIP_BACKGROUND, null));
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
        onChangeHandler.sendMessage(onChangeHandler.obtainMessage(RECEIVE_CHANGED, track.remoteId, track.trackVolume));
    }

    public void resetBackground() {
        if( track.type == Track.TrackType.AUDIO)
            setBackgroundColor(getResources().getColor(R.color.fader, null));
        if( track.type == Track.TrackType.BUS)
            setBackgroundColor(getResources().getColor(R.color.BUS_AUX_BACKGROUND, null));
        if( track.type == Track.TrackType.MASTER)
            setBackgroundColor(getResources().getColor(R.color.fader, null));
    }


}