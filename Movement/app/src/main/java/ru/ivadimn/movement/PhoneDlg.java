package ru.ivadimn.movement;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import android.widget.EditText;

import ru.ivadimn.movement.interfaces.Dlgable;

/**
 * Created by vadim on 19.11.2016.
 */

public class PhoneDlg extends AppCompatDialogFragment {

    public static final String TAG = "PHONE_DIALOG";
    public static final String TITLE = "TITLE";
    private EditText edtPhoneNumber;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Bundle bundle = getArguments();
        builder.setTitle(R.string.set_phone_number);
        builder.setIcon(android.R.drawable.edit_text);

        final View v = getActivity().getLayoutInflater().inflate(R.layout.phone_number_dlg, null);
        edtPhoneNumber = (EditText) v.findViewById(R.id.ed_phone_number_id);
        edtPhoneNumber.setText(bundle.getString(MainActivity.TAG_PHONE_NUMBER));
        builder.setView(v);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((Dlgable) getActivity()).onOkClick(edtPhoneNumber.getText().toString());
            }
        });
        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });

        return builder.create();
    }
}
