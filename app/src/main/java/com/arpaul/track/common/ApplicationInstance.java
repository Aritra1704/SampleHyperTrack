package com.arpaul.track.common;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.arpaul.track.BuildConfig;
import com.hypertrack.lib.HyperTrack;

/**
 * Created by Aritra Ranjan on 5/11/2017.
 */

public class ApplicationInstance extends MultiDexApplication {

    public static final int LOCATION_UPDATES_IN_SECONDS     = 30;

    public static final int LOADER_FETCH_ADDRESS            = 1;

    public static final double LAT_DESTINATION              = 17.461780;
    public static final double LON_DESTINATION              = 78.343061;

    public static final String BASE_URL                     = "https://api.zip.pr/v3/internal";
    public static final String REV_GEO_URL                  = "/geo/reverseGeocode";
    public static final String addresses                    = "addresses";

    public static final String LOCK_APP_DB                  = "LOCK_APP_DB";
    public static final String ACTION_TRACK_LOCATION        = "ACTION_TRACK_LOCATION";

    public static final String INTENT_LOCATION              = "INTENT_LOCATION";
    public static final String INTENT_TRACK                 = "INTENT_TRACK";

    @Override
    public void onCreate() {
        super.onCreate();
        HyperTrack.initialize(this, BuildConfig.API_KEY);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
