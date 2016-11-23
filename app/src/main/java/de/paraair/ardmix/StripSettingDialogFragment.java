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
import android.widget.EditText;

/**
 * Created by onkel on 03.11.16.
 */

public class StripSettingDialogFragment extends DialogFragment {

    private int stripIndex;
    private EditText txtStripName;
    private CheckBox cbStripIn;
    private CheckBox cbStripRecord;
    private CheckBox cbStripMute;
    private CheckBox cbStripSolo;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle settingsBundle) {

        Bundle args = getArguments();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.strip_dlg, null);
        builder.setView(view);

        builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                MainActivity callingActivity = (MainActivity) getActivity();
                callingActivity.onStripDlg(stripIndex, txtStripName.getText().toString(), cbStripIn.isChecked(), cbStripRecord.isChecked(), cbStripMute.isChecked(), cbStripSolo.isChecked());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        txtStripName = (EditText) view.findViewById(R.id.stripname);
        txtStripName.setText(args.getString("stripName"));

        cbStripIn = (CheckBox) view.findViewById(R.id.sripin);
        if (args.keySet().contains("stripIn"))
            cbStripIn.setChecked(args.getBoolean("stripIn"));
        else
            cbStripIn.setEnabled(false);

        cbStripRecord = (CheckBox) view.findViewById(R.id.sriprecord);
        if (args.keySet().contains("stripRecord"))
            cbStripRecord.setChecked(args.getBoolean("stripRecord"));
        else
            cbStripRecord.setEnabled(false);

        cbStripMute = (CheckBox) view.findViewById(R.id.sripmute);
        if (args.keySet().contains("stripMute"))
            cbStripMute.setChecked(args.getBoolean("stripMute"));
        else
            cbStripMute.setEnabled(false);

        cbStripSolo = (CheckBox) view.findViewById(R.id.sripsolo);
        if (args.keySet().contains("stripSolo"))
            cbStripSolo.setChecked(args.getBoolean("stripSolo"));
        else
            cbStripSolo.setEnabled(false);

        stripIndex = args.getInt("stripIndex");

        return builder.create();
    }

}
