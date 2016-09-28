package ua.com.amicablesoft.android.wr;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lapa on 26.09.16.
 */

public class CustomAdapter extends ArrayAdapter<Powerlifter> {

    private ArrayList<Powerlifter> powerlifters;

    public CustomAdapter(Context context, int resource, ArrayList<Powerlifter> objects) {
        super(context, resource, objects);
        powerlifters = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        String lastNameAndName = powerlifters.get(position).getLastName() + " " +
                powerlifters.get(position).getName();
        textView.setText(lastNameAndName);
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
        String lastNameAndName = powerlifters.get(position).getLastName() + " " +
                powerlifters.get(position).getName();
        textView.setText(lastNameAndName);
        return textView;
    }
}
