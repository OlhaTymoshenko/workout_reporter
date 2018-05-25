/*
 * Created by Olha Tymoshenko on 5/24/18 9:22 PM.
 * Copyright (c) 2018. All rights reserved.
 */

package ua.com.amicablesoft.android.wr.ui.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ua.com.amicablesoft.android.wr.R
import ua.com.amicablesoft.android.wr.dal.Specification
import java.util.*

class DrawerAdapter(context: Context) : BaseAdapter() {

    companion object {
        private val TITLE = 0
        private val CHECKBOX = 1
        private val RADIO_BUTTON = 2
        private val BUTTON = 3
        private val COUNT = 4
    }

    private val items = ArrayList<String>()
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val radioButtons = ArrayList<RadioButton>()
    private var buttonClickListener: ButtonClickListener? = null
    private val specification: Specification = Specification()

    interface ButtonClickListener {
        fun onButtonClick(specification: Specification)
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 || position == 4) {
            TITLE
        } else if (position == 1 || position == 2 || position == 3) {
            CHECKBOX
        } else if (position == count - 1) {
            BUTTON
        } else {
            RADIO_BUTTON
        }
    }

    override fun getViewTypeCount(): Int {
        return COUNT
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val viewHolder: ViewHolder?
        val type = getItemViewType(position)
        if (view == null) {
            viewHolder = ViewHolder()
            when (type) {
                TITLE -> {
                    view = layoutInflater.inflate(R.layout.drawer_title, parent, false)
                    view!!.isEnabled = false
                    viewHolder.textView = view.findViewById<View>(R.id.title_text_view) as TextView
                    viewHolder.textView!!.text = items[position]
                }
                CHECKBOX -> {
                    view = layoutInflater.inflate(R.layout.drawer_checkbox, parent, false)
                    viewHolder.checkBox = view!!.findViewById<View>(R.id.check_box) as CheckBox
                    viewHolder.checkBox!!.text = items[position]
                    viewHolder.checkBox!!.isChecked = true
                    viewHolder.checkBox!!.setOnClickListener {
                        if (viewHolder.checkBox!!.isChecked) {
                            when (position) {
                                1 -> specification.isSquats = true
                                2 -> specification.isBenchPress = true
                                3 -> specification.isDeadLift = true
                            }
                        } else {
                            when (position) {
                                1 -> specification.isSquats = false
                                2 -> specification.isBenchPress = false
                                3 -> specification.isDeadLift = false
                            }
                        }
                    }
                }
                RADIO_BUTTON -> {
                    view = layoutInflater.inflate(R.layout.drawer_radio_button, parent, false)
                    viewHolder.radioButton = view!!.findViewById<View>(R.id.radio_button) as RadioButton
                    viewHolder.radioButton!!.text = items[position]
                    radioButtons.add(viewHolder.radioButton!!)
                    radioButtons[0].isChecked = true
                    val finalViewHolder = viewHolder
                    viewHolder.radioButton!!.setOnClickListener {
                        if (finalViewHolder.radioButton!!.isChecked) {
                            for (i in radioButtons.indices) {
                                val radioButton = radioButtons[i]
                                if (radioButton != finalViewHolder.radioButton) {
                                    radioButton.isChecked = false
                                }
                            }
                            specification.competition = finalViewHolder.radioButton!!.text.toString()
                        }
                    }
                }
                BUTTON -> {
                    view = layoutInflater.inflate(R.layout.drawer_button, parent, false)
                    viewHolder.button = view!!.findViewById<View>(R.id.button) as Button
                    viewHolder.button!!.text = items[position]
                    viewHolder.button!!.setOnClickListener { buttonClickListener!!.onButtonClick(specification) }
                }
            }
            view!!.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        return view
    }

    fun setButtonClickListener(buttonClickListener: ButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }

    fun setListItems(items: List<String>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    private class ViewHolder {
        internal var textView: TextView? = null
        internal var checkBox: CheckBox? = null
        internal var radioButton: RadioButton? = null
        internal var button: Button? = null
    }
}
