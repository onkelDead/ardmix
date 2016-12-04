package de.paraair.ardmix;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
    public boolean bInput = true;

    public boolean bMeter = true;
    public boolean bMute = true;
    public boolean bSolo = true;

    public boolean bPan = true;
    public boolean bFader = true;
    public boolean bSoloIso = false;
    public boolean bSoloSafe = false;

    public int stripSize = 1;

    public void config(AppCompatActivity context) {
        StripMaskDialogFragment sdlg = new StripMaskDialogFragment();
        Bundle settingsBundle = new Bundle();

        settingsBundle.putInt("stripSize", stripSize);
        settingsBundle.putBoolean("title", bTitle);
        settingsBundle.putBoolean("fx", bFX);
        settingsBundle.putBoolean("send", bSend);
        settingsBundle.putBoolean("record", bRecord);
        settingsBundle.putBoolean("input", bInput);
        settingsBundle.putBoolean("meter", bMeter);
        settingsBundle.putBoolean("mute", bMute);
        settingsBundle.putBoolean("solo", bSolo);
        settingsBundle.putBoolean("soloiso", bSoloIso);
        settingsBundle.putBoolean("solosafe", bSoloSafe);
        settingsBundle.putBoolean("pan", bPan);
        settingsBundle.putBoolean("fader", bFader);

        sdlg.item = this;
        sdlg.setArguments(settingsBundle);

        sdlg.show(context.getSupportFragmentManager(), "Connection Settings");

    }

    public void loadSetting(SharedPreferences settings) {
        bTitle = settings.getBoolean("mskTitle", true);
        bFX = settings.getBoolean("mskFx", true);
        bSend = settings.getBoolean("mskSend", true);
        bRecord = settings.getBoolean("mskRecord", true);
        bInput = settings.getBoolean("mskInput", true);

        bMeter = settings.getBoolean("mskMeter", true);
        bMute = settings.getBoolean("mskMute", true);
        bSolo = settings.getBoolean("mskSolo", true);
        bSoloIso = settings.getBoolean("mskSoloIso", true);
        bSoloSafe = settings.getBoolean("mskSoloSafe", true);
        bPan = settings.getBoolean("mskPan", true);
        bFader = settings.getBoolean("mskFader", true);

        stripSize = settings.getInt("strip_wide", 1);

    }
}
