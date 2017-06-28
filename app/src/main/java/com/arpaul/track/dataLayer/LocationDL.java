package com.arpaul.track.dataLayer;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.arpaul.track.common.AppConstant;
import com.arpaul.track.dataAccess.TCPConstants;
import com.arpaul.track.dataObjects.TrackDO;

/**
 * Created by aritrapal on 27/06/17.
 */

public class LocationDL {

    public static String insertLocation(Context context, TrackDO objGroupDO) {
        Uri CONTENT_URI = TCPConstants.CONTENT_URI_TRACK;
        Uri uri = null;

        ContentValues contentValues = new ContentValues();
        contentValues.put(TCPConstants.TrackDBColumns.C_USERID, objGroupDO.user_id);
        contentValues.put(TCPConstants.TrackDBColumns.C_ACTIONID, objGroupDO.action_id);
        contentValues.put(TCPConstants.TrackDBColumns.C_LOOKUPID, objGroupDO.lookup_id);
        contentValues.put(TCPConstants.TrackDBColumns.C_ACTIONURL, objGroupDO.action_url);
        contentValues.put(TCPConstants.TrackDBColumns.C_ADDRESS, objGroupDO.address);
        contentValues.put(TCPConstants.TrackDBColumns.C_TRIPDATE, objGroupDO.trip_date);
        contentValues.put(TCPConstants.TrackDBColumns.C_TRIPTIME, objGroupDO.trip_time);
        contentValues.put(TCPConstants.TrackDBColumns.C_ESTTIME, objGroupDO.estTime);
        contentValues.put(TCPConstants.TrackDBColumns.C_CURRRENTLAT, objGroupDO.currentLat);
        contentValues.put(TCPConstants.TrackDBColumns.C_CURRRENTLONG, objGroupDO.currentLong);
        contentValues.put(TCPConstants.TrackDBColumns.C_ESTPLACELAT, objGroupDO.estPlaceLat);
        contentValues.put(TCPConstants.TrackDBColumns.C_ESTPLACELONG, objGroupDO.estPlaceLong);

        try {
            uri = context.getContentResolver().insert(CONTENT_URI, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
            uri = null;
        } finally {
            String isCreated = AppConstant.STATUS_FAILURE;
            if(uri != null)
                isCreated = AppConstant.STATUS_SUCCESS;

            return isCreated;
        }
    }
}
