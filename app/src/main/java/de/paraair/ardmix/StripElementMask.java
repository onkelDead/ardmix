package de.paraair.ardmix;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by onkel on 21.11.16.
 */

public class StripElementMask {
    public boolean bTitle = true;
    public boolean bFX = true;
    public boolean bSend = true;
    public boolean bRecord = true;

    public boolean bMeter = true;
    public boolean bMute = true;
    public boolean bSolo = true;

    public boolean bPan = true;
    public boolean bFader = true;

    public void config(AppCompatActivity context) {
        StripMaskDialogFragment sdlg = new StripMaskDialogFragment();
        Bundle settingsBundle = new Bundle();

        settingsBundle.putBoolean("title", bTitle);
        settingsBundle.putBoolean("fx", bFX);
        settingsBundle.putBoolean("send", bSend);
        settingsBundle.putBoolean("record", bRecord);
        settingsBundle.putBoolean("meter", bMeter);
        settingsBundle.putBoolean("mute", bMute);
        settingsBundle.putBoolean("solo", bSolo);
        settingsBundle.putBoolean("pan", bPan);
        settingsBundle.putBoolean("fader", bFader);

        sdlg.item = this;
        sdlg.setArguments(settingsBundle);

        sdlg.show(context.getSupportFragmentManager(), "Connection Settings");

    }

}
