package ua.com.amicablesoft.android.wr.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import ua.com.amicablesoft.android.wr.R;

/**
 * Created by olha on 2/11/17.
 */

public class CompetitionDialogFragment extends DialogFragment {

    CompetitionDialogListener listener;

    public interface CompetitionDialogListener {
        void onOkButtonClick (String competition);
        void onCancelButtonClick();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (CompetitionDialogListener) getActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(R.layout.dialog_add_competition)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) getDialog().findViewById(R.id.edit_text_competition);
                        String competition = editText.getText().toString();
                        listener.onOkButtonClick(competition);
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onCancelButtonClick();
                        dismissAllowingStateLoss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
