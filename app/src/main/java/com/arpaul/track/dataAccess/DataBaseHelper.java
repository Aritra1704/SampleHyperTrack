package com.arpaul.track.dataAccess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ARPaul on 09-05-2016.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    /**
     * Database specific constant declarations
     */
    private SQLiteDatabase db;


    static final String C_DB_T_TRACK =
            " CREATE TABLE IF NOT EXISTS " + TCPConstants.T_TRACK +
                    " (" +
                    TCPConstants.TrackDBColumns.C_TRACKID       + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TCPConstants.TrackDBColumns.C_USERID        + " VARCHAR NOT NULL, " +
                    TCPConstants.TrackDBColumns.C_ACTIONID      + " VARCHAR , " +
                    TCPConstants.TrackDBColumns.C_LOOKUPID      + " VARCHAR , " +
                    TCPConstants.TrackDBColumns.C_ACTIONURL     + " VARCHAR , " +
                    TCPConstants.TrackDBColumns.C_ADDRESS       + " VARCHAR , " +
                    TCPConstants.TrackDBColumns.C_TRIPDATE      + " DATETIME , " +
                    TCPConstants.TrackDBColumns.C_TRIPTIME      + " DATETIME , " +
                    TCPConstants.TrackDBColumns.C_ESTTIME       + " DATETIME , " +
                    TCPConstants.TrackDBColumns.C_CURRRENTLAT   + " DOUBLE , " +
                    TCPConstants.TrackDBColumns.C_CURRRENTLONG  + " DOUBLE , " +
                    TCPConstants.TrackDBColumns.C_ESTPLACELAT   + " DOUBLE , " +
                    TCPConstants.TrackDBColumns.C_ESTPLACELONG  + " DOUBLE  " +
                    ");";

    DataBaseHelper(Context context){
        super(context, TCPConstants.DATABASE_NAME, null, TCPConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(C_DB_T_TRACK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TCPConstants.T_TRACK);
        onCreate(db);
    }
}
