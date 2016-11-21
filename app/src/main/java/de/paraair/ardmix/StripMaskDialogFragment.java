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

/**
 * Created by onkel on 21.11.16.
 */

public class StripMaskDialogFragment extends DialogFragment {

    private int stripIndex;
    private CheckBox cbStripTitle;
    private CheckBox cbStripFX;
    private CheckBox cbStripSend;
    private CheckBox cbStripRecord;
    private CheckBox cbStripMeter;
    private CheckBox cbStripMute;
    private CheckBox cbStripSolo;
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
                item.bMeter = cbStripMeter.isChecked();
                item.bMute = cbStripMute.isChecked();
                item.bSolo = cbStripSolo.isChecked();
                item.bPan = cbStripPan.isChecked();
                item.bFader = cbStripFader.isChecked();

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

        cbStripMeter = (CheckBox) view.findViewById(R.id.chkMaskMeter);
        cbStripMeter.setChecked(args.getBoolean("meter"));

        cbStripMute = (CheckBox) view.findViewById(R.id.chkMaskMute);
        cbStripMute.setChecked(args.getBoolean("mute"));

        cbStripSolo = (CheckBox) view.findViewById(R.id.chkMaskSolo);
        cbStripSolo.setChecked(args.getBoolean("solo"));

        cbStripPan = (CheckBox) view.findViewById(R.id.chkMaskPan);
        cbStripPan.setChecked(args.getBoolean("pan"));

        cbStripFader = (CheckBox) view.findViewById(R.id.chkMaskFader);
        cbStripFader.setChecked(args.getBoolean("fader"));

        builder.setNeutralButton(R.string.action_maskall, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                item.bTitle = true;
                item.bFX = true;
                item.bSend = true;
                item.bRecord = true;
                item.bMeter = true;
                item.bMute = true;
                item.bSolo = true;
                item.bPan = true;
                item.bFader = true;

                MainActivity callingActivity = (MainActivity) getActivity();
                callingActivity.onStripMaskDlg();
            }
        });

        return builder.create();
    }
}
