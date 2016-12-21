package ua.com.amicablesoft.android.wr.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ua.com.amicablesoft.android.wr.R;

/**
 * Created by olha on 12/18/16.
 */

public class CommonProcessDialogFragment extends BaseTitleLessDialogFragment {

    private static final String ARG_KEY__MESSAGE_RES_ID = "arg.messageResId";
    private static final String ARG_KEY__IS_CANCELABLE = "arg.isCancelable";

    private int messageId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageId = getArguments().getInt(ARG_KEY__MESSAGE_RES_ID);
        boolean isCancelable = getArguments().getBoolean(ARG_KEY__IS_CANCELABLE, true);

        setCancelable(isCancelable);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_common_process, container, false);
        TextView messageLabel = (TextView) view.findViewById(R.id.common_process_dialog_label_message);
        messageLabel.setText(messageId);
        return view;
    }

    public static void show(FragmentManager fm, int dialogId) {
        show(fm, dialogId, R.string.common_label_one_moment);
    }

    public static void show(FragmentManager fm, int dialogId, @StringRes int messageResId) {
        Bundle args = new Bundle();
        args.putInt(ARG_KEY__MESSAGE_RES_ID, messageResId);

        CommonProcessDialogFragment newDialog = new CommonProcessDialogFragment();
        newDialog.setArguments(args);
        newDialog.show(fm, String.valueOf(dialogId));
    }

    public static void dismiss(FragmentManager fm, int dialogId) {
        Fragment foundedDialog = fm.findFragmentByTag(String.valueOf(dialogId));
        if (foundedDialog instanceof CommonProcessDialogFragment) {
            CommonProcessDialogFragment processDialog = (CommonProcessDialogFragment) foundedDialog;
            processDialog.dismissAllowingStateLoss();
        }
    }
}

