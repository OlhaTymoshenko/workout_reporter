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
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        textView.setText(powerlifters.get(position).getName());
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
        textView.setText(powerlifters.get(position).getName());
        return textView;
    }
}
