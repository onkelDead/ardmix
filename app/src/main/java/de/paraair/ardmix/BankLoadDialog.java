package de.paraair.ardmix;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by onkel on 15.11.16.
 */

public class BankLoadDialog extends DialogFragment {

    private ArrayList<String> files;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle settingsBundle) {

        Bundle args = getArguments();

        files = args.getStringArrayList("files");


        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        LinearLayout view = (LinearLayout)inflater.inflate(R.layout.file_dlg, null);
        FileSelectLayout fsl = new FileSelectLayout(view.getContext());
        fsl.setFileNames(files);

        fsl.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();

                MainActivity callingActivity = (MainActivity) getActivity();
                callingActivity.LoadBankFile(tag);
            }
        };

        view.addView(fsl);

        builder.setView(view);


        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        return builder.create();
    }
}
