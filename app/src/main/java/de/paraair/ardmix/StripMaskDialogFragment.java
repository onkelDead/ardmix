package de.paraair.ardmix;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

/**
 * Created by onkel on 21.11.16.
 */

public class StripMaskDialogFragment extends DialogFragment {

    private RadioButton rbSmall;
    private RadioButton rbMedium;
    private RadioButton rbWide;

    private CheckBox cbStripTitle;
    private CheckBox cbStripFX;
    private CheckBox cbStripSend;
    private CheckBox cbStripRecord;
    private CheckBox cbStripReceive;
    private CheckBox cbStripInput;
    private CheckBox cbStripMeter;
    private CheckBox cbStripMute;
    private CheckBox cbStripSolo;
    private CheckBox cbStripSoloIso;
    private CheckBox cbStripSoloSafe;
    private CheckBox cbStripPan;
    private CheckBox cbStripFader;

    public StripElementMask item;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle settingsBundle) {

        Bundle args = getArguments();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.stripmask_dlg, null);
        builder.setView(view);

        builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                item.bTitle = cbStripTitle.isChecked();
                item.bFX = cbStripFX.isChecked();
                item.bSend = cbStripSend.isChecked();
                item.bRecord = cbStripRecord.isChecked();
                item.bReceive = cbStripReceive.isChecked();
                item.bInput = cbStripInput.isChecked();
                item.bMeter = cbStripMeter.isChecked();
                item.bMute = cbStripMute.isChecked();
                item.bSolo = cbStripSolo.isChecked();
                item.bSoloIso = cbStripSoloIso.isChecked();
                item.bSoloSafe = cbStripSoloSafe.isChecked();
                item.bPan = cbStripPan.isChecked();
                item.bFader = cbStripFader.isChecked();

                if( rbSmall.isChecked())
                    item.stripSize = 0;
                else if( rbMedium.isChecked())
                    item.stripSize = 1;
                else if( rbWide.isChecked())
                    item.stripSize = 2;

                MainActivity callingActivity = (MainActivity) getActivity();
                callingActivity.onStripMaskDlg();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        cbStripTitle = (CheckBox) view.findViewById(R.id.chkMaskTitle);
        cbStripTitle.setChecked(args.getBoolean("title"));

        cbStripFX = (CheckBox) view.findViewById(R.id.chkMaskFX);
        cbStripFX.setChecked(args.getBoolean("fx"));

        cbStripSend = (CheckBox) view.findViewById(R.id.chkMaskSend);
        cbStripSend.setChecked(args.getBoolean("send"));

        cbStripRecord = (CheckBox) view.findViewById(R.id.chkMaskRecord);
        cbStripRecord.setChecked(args.getBoolean("record"));

        cbStripReceive = (CheckBox) view.findViewById(R.id.chkMaskReceive);
        cbStripReceive.setChecked(args.getBoolean("receive"));

        cbStripInput = (CheckBox) view.findViewById(R.id.chkMaskInput);
        cbStripInput.setChecked(args.getBoolean("input"));

        cbStripMeter = (CheckBox) view.findViewById(R.id.chkMaskMeter);
        cbStripMeter.setChecked(args.getBoolean("meter"));

        cbStripMute = (CheckBox) view.findViewById(R.id.chkMaskMute);
        cbStripMute.setChecked(args.getBoolean("mute"));

        cbStripSolo = (CheckBox) view.findViewById(R.id.chkMaskSolo);
        cbStripSolo.setChecked(args.getBoolean("solo"));

        cbStripSoloIso = (CheckBox) view.findViewById(R.id.chkMaskSoloIso);
        cbStripSoloIso.setChecked(args.getBoolean("soloiso"));

        cbStripSoloSafe = (CheckBox) view.findViewById(R.id.chkMaskSoloSafe);
        cbStripSoloSafe.setChecked(args.getBoolean("solosafe"));

        cbStripPan = (CheckBox) view.findViewById(R.id.chkMaskPan);
        cbStripPan.setChecked(args.getBoolean("pan"));

        cbStripFader = (CheckBox) view.findViewById(R.id.chkMaskFader);
        cbStripFader.setChecked(args.getBoolean("fader"));


        rbSmall = (RadioButton) view.findViewById(R.id.small_strips);
        rbMedium = (RadioButton) view.findViewById(R.id.medium_strips);
        rbWide = (RadioButton) view.findViewById(R.id.wide_strips);

        switch(args.getInt("stripSize")) {
            case 0:
                rbSmall.setChecked(true);
                break;
            case 1:
                rbMedium.setChecked(true);
                break;
            case 2:
                rbWide.setChecked(true);
                break;

        }

        builder.setNeutralButton(R.string.action_maskall, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                item.bTitle = true;
                item.bFX = true;
                item.bSend = true;
                item.bRecord = true;
                item.bReceive = true;
                item.bInput = false;
                item.bMeter = true;
                item.bMute = true;
                item.bSolo = true;
                item.bSoloIso = false;
                item.bSoloSafe = false;
                item.bPan = true;
                item.bFader = true;

                MainActivity callingActivity = (MainActivity) getActivity();
                callingActivity.onStripMaskDlg();
            }
        });

        return builder.create();
    }
}
