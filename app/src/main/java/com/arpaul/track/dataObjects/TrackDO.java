package com.arpaul.track.dataObjects;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by preetambhosle on 12/02/16.
 */
public class TrackDO implements Parcelable {

    @SerializedName("track_id")
    @Expose
    public int track_id;

    @SerializedName("user_id")
    @Expose
    public String user_id;

    @SerializedName("action_id")
    @Expose
    public String action_id;

    @SerializedName("lookup_id")
    @Expose
    public String lookup_id;

    @SerializedName("action_url")
    @Expose
    public String action_url;

    @SerializedName("address")
    @Expose
    public String address;

    @SerializedName("trip_date")
    @Expose
    public String trip_date;

    @SerializedName("trip_time")
    @Expose
    public String trip_time;

    @SerializedName("estTime")
    @Expose
    public String estTime;

    @SerializedName("currentLat")
    @Expose
    public double currentLat;

    @SerializedName("currentLong")
    @Expose
    public double currentLong;

    @SerializedName("estPlaceLat")
    @Expose
    public double estPlaceLat;

    @SerializedName("estPlaceLong")
    @Expose
    public double estPlaceLong;

    protected TrackDO(Parcel in) {
        track_id = in.readInt();
        user_id = in.readString();
        action_id = in.readString();
        lookup_id = in.readString();
        action_url = in.readString();
        address = in.readString();
        trip_date = in.readString();
        trip_time = in.readString();
        estTime = in.readString();
        currentLat = in.readDouble();
        currentLong = in.readDouble();
        estPlaceLat = in.readDouble();
        estPlaceLong = in.readDouble();
    }

    public TrackDO() {
    }

    public static final Creator<TrackDO> CREATOR = new Creator<TrackDO>() {
        @Override
        public TrackDO createFromParcel(Parcel in) {
            return new TrackDO(in);
        }

        @Override
        public TrackDO[] newArray(int size) {
            return new TrackDO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(track_id);
        dest.writeString(user_id);
        dest.writeString(action_id);
        dest.writeString(lookup_id);
        dest.writeString(action_url);
        dest.writeString(address);
        dest.writeString(trip_date);
        dest.writeString(trip_time);
        dest.writeString(estTime);
        dest.writeDouble(currentLat);
        dest.writeDouble(currentLong);
        dest.writeDouble(estPlaceLat);
        dest.writeDouble(estPlaceLong);
    }
}
