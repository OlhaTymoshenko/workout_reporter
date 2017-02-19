package ua.com.amicablesoft.android.wr.ui;

import java.util.ArrayList;

import ua.com.amicablesoft.android.wr.models.Powerlifter;

/**
 * Created by olha on 2/16/17.
 */

interface GalleryView {

    void setListPowerlifters(ArrayList<Powerlifter> list);
    void setPowerlifter(int position);
}
