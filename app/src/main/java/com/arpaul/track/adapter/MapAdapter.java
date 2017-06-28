package com.arpaul.track.adapter;

import android.content.Context;

import com.hypertrack.lib.HyperTrackMapAdapter;

/**
 * Created by Aman Jain on 30/05/17.
 */

public class MapAdapter extends HyperTrackMapAdapter {

    private Context mContext;

    public MapAdapter(Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }

    /*
    * Show the trailing polyline of driver
    * */
    @Override
    public boolean showTrailingPolyline() {
        return true;
    }

}
