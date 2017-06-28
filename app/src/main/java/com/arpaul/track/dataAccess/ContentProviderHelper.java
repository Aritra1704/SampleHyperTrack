package com.arpaul.track.dataAccess;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.arpaul.track.common.ApplicationInstance;
import com.arpaul.track.dataObjects.TrackDO;
import com.arpaul.utilitieslib.LogUtils;

import java.util.ArrayList;

/**
 * Created by ARPaul on 04-01-2016.
 */
public class ContentProviderHelper extends ContentProvider {

    public static final int TRACK                                   = 1;
    public static final int SYNCLOG                                 = 16;

    public static final int RELATIONSHIP_JOIN                       = 100;

    public static final String TAG_ID_INT = "/#";
    public static final String TAG_ID_ALL = "/*";

    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(TCPConstants.PROVIDER_NAME, TCPConstants.T_TRACK, TRACK);
        uriMatcher.addURI(TCPConstants.PROVIDER_NAME, TCPConstants.PATH_RELATIONSHIP_JOIN, RELATIONSHIP_JOIN);
    }

    private DataBaseHelper mOpenHelper;

    public static Uri getContentUri(int type) {
        String URL = TCPConstants.CONTENT + TCPConstants.PROVIDER_NAME;
        switch (type) {
            case RELATIONSHIP_JOIN:
                URL += "";
                break;
            case SYNCLOG:
                URL += "/"+ TCPConstants.TrackDBColumns.C_TRACKID;
                break;
        }
        return Uri.parse(URL);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DataBaseHelper(getContext());
        return (mOpenHelper == null) ? false : true;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values){
        synchronized (ApplicationInstance.LOCK_APP_DB) {

            int numInserted = 0;
            String table = getTableName(uri);

            SQLiteDatabase sqlDB = mOpenHelper.getWritableDatabase();
            sqlDB.beginTransaction();
            try {
                for (ContentValues cv : values) {
                    long newID = sqlDB.insertOrThrow(table, null, cv);
                    if (newID <= 0) {
                        throw new SQLException("Failed to insert row into " + uri);
                    }
                }
                sqlDB.setTransactionSuccessful();
                getContext().getContentResolver().notifyChange(uri, null);
                numInserted = values.length;
                LogUtils.infoLog("bulk_Insert", table + " inserted: " + numInserted);
            } finally {
                sqlDB.endTransaction();
            }
            return numInserted;
        }
    }

    /**
     * Create a write able database which will trigger its
     * creation if it doesn't already exist.
     *//*
        mOpenHelper = dbHelper.getWritableDatabase();
    }*/
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        synchronized (ApplicationInstance.LOCK_APP_DB) {

            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

            Uri returnURI;
            String table = getTableName(uri);

            long _id = db.insert(table, null, values);
            if (_id > 0) {
                returnURI = TCPConstants.buildLocationUri(_id);
            } else {
                throw new SQLException("Failed to insert row into: " + uri);
            }

            getContext().getContentResolver().notifyChange(uri, null);
            return returnURI;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        synchronized (ApplicationInstance.LOCK_APP_DB) {

            Cursor retCursor;
            String query = "";
            String table = "";
            switch (uriMatcher.match(uri)) {
                case TRACK:
                    table = TCPConstants.T_TRACK;
                    break;
                case RELATIONSHIP_JOIN:
                    table = selection;
                    selection = null;
                    break;
                default: {
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
                }
            }
            retCursor = mOpenHelper.getReadableDatabase().query(
                    table,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);

            retCursor.setNotificationUri(getContext().getContentResolver(),uri);
            return retCursor;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        synchronized (ApplicationInstance.LOCK_APP_DB) {

            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            int count = 0;

            String table = getTableName(uri);

            try {
                if(!TextUtils.isEmpty(table)){
                    count = db.delete(table, selection, selectionArgs);
                }
            } catch (Exception ex){
                ex.printStackTrace();
            } finally {
                getContext().getContentResolver().notifyChange(uri, null);

                return count;
            }
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        synchronized (ApplicationInstance.LOCK_APP_DB) {

            int count = 0;
            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

            String table = getTableName(uri);

            count = db.update(table, values, selection, selectionArgs);

            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
    }

    private String getTableName(Uri uri){
        String table = TCPConstants.T_TRACK;
        int uriType = uriMatcher.match(uri);

        switch (uriType) {
            case TRACK:
                table = TCPConstants.T_TRACK;
                break;
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        return table;
    }

    @Override
    public String getType(Uri uri) {

        final int match = uriMatcher.match(uri);
        switch (match) {
            case TRACK:
                return TCPConstants.CONTENT_BASE_TYPE;
            case SYNCLOG:
                return TCPConstants.CONTENT_BASE_TYPE;
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    //http://www.grokkingandroid.com/better-performance-with-contentprovideroperation/
    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {

        ContentProviderResult[] result = new ContentProviderResult[operations.size()];
        int i = 0;
        // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        // Begin a transaction
        db.beginTransaction();
        try {
            for (ContentProviderOperation operation : operations) {
                // Chain the result for back references
                result[i++] = operation.apply(this, result, i);
            }

            db.setTransactionSuccessful();
        } catch (OperationApplicationException e) {
            Log.d("applyBatch", "batch failed: " + e.getLocalizedMessage());
            result = null;
        } finally {
            db.endTransaction();
        }

        return result;
    }
}