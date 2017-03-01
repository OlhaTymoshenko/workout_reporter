package ua.com.amicablesoft.android.wr.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.com.amicablesoft.android.wr.R;
import ua.com.amicablesoft.android.wr.models.Specification;

/**
 * Created by olha on 2/16/17.
 */

class DrawerAdapter extends BaseAdapter {

    interface ButtonClickListener {
        void onButtonClick(Specification specification);
    }

    private static final int TITLE = 0;
    private static final int CHECKBOX = 1;
    private static final int RADIO_BUTTON = 2;
    private static final int BUTTON = 3;
    private static final int COUNT = 4;
    private List<String> items = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private List<RadioButton> radioButtons = new ArrayList<>();
    private ButtonClickListener buttonClickListener;
    private Specification specification;

    DrawerAdapter(Context context) {
        layoutInflater = (LayoutInflater.from(context));
        specification = new Specification();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == 4) {
            return TITLE;
        } else if (position == 1 || position == 2 || position == 3) {
            return CHECKBOX;
        } else if (position == getCount() - 1) {
            return BUTTON;
        } else {
            return RADIO_BUTTON;
        }
    }

    @Override
    public int getViewTypeCount() {
        return COUNT;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            switch (type) {
                case TITLE:
                    convertView = layoutInflater.inflate(R.layout.drawer_title, parent, false);
                    convertView.setEnabled(false);
                    viewHolder.textView = (TextView) convertView.findViewById(R.id.title_text_view);
                    viewHolder.textView.setText(items.get(position));
                    break;
                case CHECKBOX:
                    convertView = layoutInflater.inflate(R.layout.drawer_checkbox, parent, false);
                    viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.check_box);
                    viewHolder.checkBox.setText(items.get(position));
                    viewHolder.checkBox.setChecked(true);
                    final ViewHolder finalViewHolder1 = viewHolder;
                    viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (finalViewHolder1.checkBox.isChecked()) {
                                if (position == 1) {
                                    specification.setSquats(true);
                                } else if (position == 2) {
                                    specification.setBenchPress(true);
                                } else if (position == 3) {
                                    specification.setDeadLift(true);
                                }
                            } else {
                                if (position == 1) {
                                    specification.setSquats(false);
                                } else if (position == 2) {
                                    specification.setBenchPress(false);
                                } else if (position == 3) {
                                    specification.setDeadLift(false);
                                }
                            }
                        }
                    });
                    break;
                case RADIO_BUTTON:
                    convertView = layoutInflater.inflate(R.layout.drawer_radio_button, parent, false);
                    viewHolder.radioButton = (RadioButton) convertView.findViewById(R.id.radio_button);
                    viewHolder.radioButton.setText(items.get(position));
                    radioButtons.add(viewHolder.radioButton);
                    radioButtons.get(0).setChecked(true);
                    final ViewHolder finalViewHolder = viewHolder;
                    viewHolder.radioButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (finalViewHolder.radioButton.isChecked()) {
                                for (int i = 0; i < radioButtons.size(); i++) {
                                    RadioButton radioButton = radioButtons.get(i);
                                    if (!radioButton.equals(finalViewHolder.radioButton)) {
                                        radioButton.setChecked(false);
                                    }
                                }
                                specification.setCompetition(finalViewHolder.radioButton.getText().toString());
                            }
                        }
                    });
                    break;
                case BUTTON:
                    convertView = layoutInflater.inflate(R.layout.drawer_button, parent, false);
                    viewHolder.button = (Button) convertView.findViewById(R.id.button);
                    viewHolder.button.setText(items.get(position));
                    viewHolder.button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonClickListener.onButtonClick(specification);
                        }
                    });
                    break;
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    void setButtonClickListener(ButtonClickListener buttonClickListener) {
        this.buttonClickListener = buttonClickListener;
    }

    void setListItems(List<String> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView textView;
        CheckBox checkBox;
        RadioButton radioButton;
        Button button;
    }
}
