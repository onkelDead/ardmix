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

                String name = txtStripName.getText().toString();

                MainActivity callingActivity = (MainActivity) getActivity();
                callingActivity.onStripDlg(stripIndex, name, cbStripIn.isChecked());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        txtStripName = (EditText) view.findViewById(R.id.stripname);
        cbStripIn = (CheckBox) view.findViewById(R.id.sripin);

        txtStripName.setText(args.getString("stripName"));
        if (args.keySet().contains("stripIn"))
            cbStripIn.setChecked(args.getBoolean("stripIn"));
        else
            cbStripIn.setEnabled(false);
        stripIndex = args.getInt("stripIndex");

        return builder.create();
    }

}
