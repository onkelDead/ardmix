package de.paraair.ardmix;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by onkel on 03.11.16.
 */

public class BankSettingDialogFragment extends DialogFragment {

    private Bank bank;
    private EditText txtBankName;
    private int bankIndex;
    private HashMap<Integer, Track> routes;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle settingsBundle) {

        Bundle args = getArguments();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        LinearLayout view = (LinearLayout)inflater.inflate(R.layout.bank_dlg, null);

        bankIndex = args.getInt("bankIndex");

        MainActivity callingActivity = (MainActivity) getActivity();
        final Bank orgBank = callingActivity.getBank(bankIndex);
        bank = orgBank.GetClone();
        txtBankName = (EditText) view.findViewById(R.id.bankname);
        txtBankName.setText(bank.getName());
        routes = callingActivity.getRoutes();
        StripSelectLayout ssl = new StripSelectLayout(view.getContext());
        ssl.setRoutes(routes,bank);

        ssl.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();

                int id = (int)v.getTag();
                CheckBox selector = (CheckBox)v;
                if (!selector.isChecked()) {
                    bank.remove(id);
                }
                else {
                    Track t = routes.get(id);
                    bank.add(t.name, id, true);
                }
            }
        };
        view.addView(ssl);

        builder.setView(view);

        builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String name = txtBankName.getText().toString();
                bank.setName(name);

                MainActivity callingActivity = (MainActivity) getActivity();
                callingActivity.onBankDlg(bankIndex, bank);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        if( orgBank.button != null ) {
            builder.setNeutralButton(R.string.action_removebank, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    MainActivity callingActivity = (MainActivity) getActivity();
                    callingActivity.RemoveBank(bankIndex);
                }
            });
        }
        else {
            builder.setNeutralButton(R.string.action_loadbanks, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    MainActivity callingActivity = (MainActivity) getActivity();
                    callingActivity.LoadBank();
                }
            });
        }
        return builder.create();
    }

}
