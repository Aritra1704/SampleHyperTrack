package com.arpaul.track.dataAccess;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;

/**
 * Created by ARPaul on 07-01-2016.
 */
public class TCPConstants {
    public static final String CONTENT_AUTHORITY = "com.arpaul.track.dataAccess.ContentProviderHelper";

    public static final String DATABASE_NAME                    = "Track.sqlite";

    public static final String T_TRACK                          = "tblTrack";

    public static final String AS_T_TRACK                       = " tT";

    public static final int DATABASE_VERSION                    = 1;

    public static final String PATH_RELATIONSHIP_JOIN           = "relationship_join";

    public static final String DELIMITER = "/";
    public static final String TABLE_ID    = "_id";

    public static final String T_LEFT_OUTER_JOIN = " LEFT OUTER JOIN ";
    public static final String T_ON = " ON ";
    public static final String T_DOT = ".";
    public static final String T_EQUAL = " = ";
    public static final String T_AND = " AND ";
    public static final String T_QUES  = " = ? ";
    public static final String T_LIKE  = " LIKE ? ";
    public static final String T_ORDER_BY  = " ORDER BY ";
    public static final String T_DESC  = " DESC ";
    public static final String T_FTTIME  = " strftime('%H %M', ";
    public static final String T_IN_ENDBRACKET  = " ) ";

    public static final String CONTENT = "content://";
    public static final Uri BASE_CONTENT_URI = Uri.parse(CONTENT + CONTENT_AUTHORITY);

    public static final Uri CONTENT_URI_TRACK = Uri.parse(CONTENT + CONTENT_AUTHORITY + DELIMITER + T_TRACK);

    public static final Uri CONTENT_URI_RELATIONSHIP_JOIN = Uri.parse(CONTENT + CONTENT_AUTHORITY + DELIMITER + PATH_RELATIONSHIP_JOIN);

    public static final String PROVIDER_NAME = CONTENT_AUTHORITY;

    // create cursor of base type directory for multiple entries
    public static final String CONTENT_MULTIPLE_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + DELIMITER + CONTENT_AUTHORITY + DELIMITER + DATABASE_NAME;
    // create cursor of base type item for single entry
    public static final String CONTENT_BASE_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + DELIMITER + CONTENT_AUTHORITY + DELIMITER + DATABASE_NAME;

    public static Uri buildLocationUri(long id){
        return ContentUris.withAppendedId(CONTENT_URI_TRACK, id);
    }

    public class TrackDBColumns {
        public static final String C_TRACKID        = "TRACKID";
        public static final String C_USERID         = "USERID";
        public static final String C_ACTIONID       = "ACTIONID";
        public static final String C_LOOKUPID       = "LOOKUPID";
        public static final String C_ACTIONURL      = "ACTIONURL";
        public static final String C_ADDRESS        = "ADDRESS";
        public static final String C_TRIPDATE       = "TRIPDATE";
        public static final String C_TRIPTIME       = "TRIPTIME";
        public static final String C_ESTTIME        = "ESTTIME";
        public static final String C_CURRRENTLAT    = "CURRRENTLAT";
        public static final String C_CURRRENTLONG   = "CURRRENTLONG";
        public static final String C_ESTPLACELAT    = "ESTPLACELAT";
        public static final String C_ESTPLACELONG   = "ESTPLACELONG";
    }
}
