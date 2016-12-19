package de.paraair.ardmix;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Class which provides settings dialog fragment
 * Created by onkel on 06.10.16.
 */

public class SettingsDialogFragment extends android.support.v4.app.DialogFragment {

    private EditText hostText;
    private EditText portText;
    private CheckBox useOSCbridge;
    private EditText bankSizeText;
    private CheckBox useSendsLayoutCheckbox;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle settingsBundle) {


        Bundle args = getArguments();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.settings_dlg, null);
        builder.setView(view);

        builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String host = hostText.getText().toString();
                int port, bankSize;

                try {
                    port = Integer.parseInt(portText.getText().toString());
                    bankSize = Integer.parseInt(bankSizeText.getText().toString());
                } catch(NumberFormatException e){
                    port = 0;
                    bankSize = 0;
                }

                MainActivity callingActivity = (MainActivity) getActivity();
                callingActivity.onSettingDlg(host, port, bankSize, useSendsLayoutCheckbox.isChecked(), useOSCbridge.isChecked());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        hostText = (EditText) view.findViewById(R.id.host);
        portText = (EditText) view.findViewById(R.id.port);
        bankSizeText = (EditText) view.findViewById(R.id.bankSize);
        useSendsLayoutCheckbox = (CheckBox) view.findViewById(R.id.useSendsLayout);
        useOSCbridge = (CheckBox) view.findViewById(R.id.chkOSCbridge);

        hostText.setText(args.getString("host"));
        portText.setText(String.valueOf(args.getInt("port")));
        bankSizeText.setText(String.valueOf(args.getInt("bankSize")));
        useSendsLayoutCheckbox.setChecked(args.getBoolean("useOSCbridge"));
        useOSCbridge.setChecked(args.getBoolean("useSendsLayout"));

        // Create the AlertDialog object and return it
        return builder.create();
    }


}

