/*
 * Created by Olha Tymoshenko on 5/25/18 3:25 PM.
 * Copyright (c) 2018. All rights reserved.
 */

package ua.com.amicablesoft.android.wr.ui.processing

import android.app.FragmentManager
import android.os.Bundle
import android.support.annotation.StringRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ua.com.amicablesoft.android.wr.R

class CommonProcessDialogFragment : BaseTitleLessDialogFragment() {

    private val ARG_KEY__MESSAGE_RES_ID = "arg.messageResId"
    private val ARG_KEY__IS_CANCELABLE = "arg.isCancelable"
    private var messageId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        messageId = arguments.getInt(ARG_KEY__MESSAGE_RES_ID)
        val isCancelable = arguments.getBoolean(ARG_KEY__IS_CANCELABLE, true)
        setCancelable(isCancelable)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dialog_common_process, container, false)
        val messageLabel = view.findViewById<View>(R.id.common_process_dialog_label_message) as TextView
        messageLabel.setText(messageId)
        return view
    }

    fun show(fm: FragmentManager, dialogId: Int) {
        show(fm, dialogId, R.string.common_label_one_moment)
    }

    private fun show(fm: FragmentManager, dialogId: Int, @StringRes messageResId: Int) {
        val args = Bundle()
        args.putInt(ARG_KEY__MESSAGE_RES_ID, messageResId)
        val newDialog = CommonProcessDialogFragment()
        newDialog.arguments = args
        newDialog.show(fm, dialogId.toString())
    }

    fun dismiss(fm: FragmentManager, dialogId: Int) {
        val foundedDialog = fm.findFragmentByTag(dialogId.toString())
        if (foundedDialog is CommonProcessDialogFragment) {
            foundedDialog.dismissAllowingStateLoss()
        }
    }
}