package com.arpaul.samplehypertrack.common;

import android.app.Application;

import com.hypertrack.lib.HyperTrack;

/**
 * Created by Aritra Ranjan on 5/11/2017.
 */

public class ApplicationInstance extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HyperTrack.initialize(this, "pk_e5f3b391e0147984270fabafb177f7143b3fd20b");
    }
}
