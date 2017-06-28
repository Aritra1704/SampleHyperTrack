package com.arpaul.track.dataAccess;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.arpaul.track.dataObjects.TrackDO;
import com.arpaul.utilitieslib.LogUtils;

import static com.arpaul.track.dataAccess.InsertDataPref.INSERT_TRACK_LOC;
import static com.arpaul.track.dataAccess.InsertDataPref.DELETE_ALL_DATA;

/**
 * Created by Aritra on 14-07-2016.
 */
public class InsertLoader extends AsyncTaskLoader {

    private Context context;
    private Object data;

    private final String TAG = "InsertLoader";
    private int dataPref;

    public final static String BUNDLE_INSERTLOADER      = "BUNDLE_INSERTLOADER";

    /**
     *
     * @param context
     */
    public InsertLoader(Context context){
        super(context);
        onContentChanged();
        this.context = context;
    }

    /**
     *
     * @param context
     * @param dataPref
     * @param bundle
     */
    public InsertLoader(Context context, int dataPref, Bundle bundle){
        super(context);
        onContentChanged();
        this.context = context;
        if(bundle != null)
            data = bundle.get(BUNDLE_INSERTLOADER);

        this.dataPref = dataPref;
    }

    /**
     *
     * @param context
     * @param dataPref
     * @param data
     */
    public InsertLoader(Context context, int dataPref, Object data){
        super(context);
        onContentChanged();
        this.context = context;
        this.data = data;
        this.dataPref = dataPref;
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged())
            forceLoad();
    }

    @Override
    public Object loadInBackground() {

        @InsertDataPref.InsertDataPreference int dataPreference = dataPref;
        switch (dataPreference) {
            case INSERT_TRACK_LOC:
                if(data != null && data instanceof TrackDO){
                    TrackDO objPrefLocationDO = (TrackDO) data;
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(TCPConstants.TrackDBColumns.C_TRACKID, objPrefLocationDO.track_id);
                    contentValues.put(TCPConstants.TrackDBColumns.C_USERID, objPrefLocationDO.user_id);
                    contentValues.put(TCPConstants.TrackDBColumns.C_ACTIONID, objPrefLocationDO.action_id);
                    contentValues.put(TCPConstants.TrackDBColumns.C_LOOKUPID, objPrefLocationDO.lookup_id);
                    contentValues.put(TCPConstants.TrackDBColumns.C_ACTIONURL, objPrefLocationDO.action_url);
                    contentValues.put(TCPConstants.TrackDBColumns.C_ADDRESS, objPrefLocationDO.address);
                    contentValues.put(TCPConstants.TrackDBColumns.C_TRIPDATE, objPrefLocationDO.trip_date);
                    contentValues.put(TCPConstants.TrackDBColumns.C_TRIPTIME, objPrefLocationDO.trip_time);
                    contentValues.put(TCPConstants.TrackDBColumns.C_ESTTIME, objPrefLocationDO.estTime);
                    contentValues.put(TCPConstants.TrackDBColumns.C_CURRRENTLAT, objPrefLocationDO.currentLat);
                    contentValues.put(TCPConstants.TrackDBColumns.C_CURRRENTLONG, objPrefLocationDO.currentLong);
                    contentValues.put(TCPConstants.TrackDBColumns.C_ESTPLACELAT, objPrefLocationDO.estPlaceLat);
                    contentValues.put(TCPConstants.TrackDBColumns.C_ESTPLACELONG, objPrefLocationDO.estPlaceLong);

                    String address = "";
                    int tryUpdate = context.getContentResolver().update(TCPConstants.CONTENT_URI_TRACK,
                            contentValues,
                            TCPConstants.TrackDBColumns.C_ACTIONID + TCPConstants.T_QUES,
                            new String[]{objPrefLocationDO.action_id + ""});

                    if (tryUpdate <= 0){
                        Uri uri = context.getContentResolver().insert(TCPConstants.CONTENT_URI_TRACK, contentValues);
                        if(uri != null)
                            address = objPrefLocationDO.address;
                    } else {
                        address = objPrefLocationDO.address;
                    }

                    return address;
                }
                break;

            case DELETE_ALL_DATA:
                break;
            default:

                break;
        }

        return null;
    }

    @Override
    protected void onStopLoading() {
        if (LogUtils.isLogEnable)
            Log.i(TAG, "+++ onStopLoading() called! +++");

        cancelLoad();
    }

    @Override
    protected void onReset() {
        if (LogUtils.isLogEnable)
            Log.i(TAG, "+++ onReset() called! +++");

        // Ensure the loader is stopped.
        onStopLoading();

    }

    @Override
    public void forceLoad() {
        if (LogUtils.isLogEnable)
            Log.i(TAG, "+++ forceLoad() called! +++");
        super.forceLoad();
    }

}
