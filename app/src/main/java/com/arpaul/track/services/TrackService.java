package com.arpaul.track.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.arpaul.gpslibrary.fetchAddressGeoCode.AddressConstants;
import com.arpaul.gpslibrary.fetchAddressGeoCode.AddressDO;
import com.arpaul.gpslibrary.fetchAddressGeoCode.FetchAddressLoader;
import com.arpaul.track.common.AppConstant;
import com.arpaul.track.common.ApplicationInstance;
import com.arpaul.track.dataLayer.LocationDL;
import com.arpaul.track.dataObjects.TrackDO;
import com.arpaul.track.webService.PostParamBuilder;
import com.arpaul.utilitieslib.LogUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedHashMap;

import static com.arpaul.track.common.ApplicationInstance.ACTION_TRACK_LOCATION;
import static com.arpaul.track.common.ApplicationInstance.INTENT_LOCATION;
import static com.arpaul.track.common.ApplicationInstance.INTENT_TRACK;
import static com.arpaul.track.common.ApplicationInstance.LOADER_FETCH_ADDRESS;

public class TrackService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean isGpsEnabled;
    private final String TAG = "TrackService";
    private LatLng currentLatLng = null;
    private LocalBinder mBinder = new LocalBinder();
    private TrackDO objTrackDO;

    public TrackService() {
    }

    public class LocalBinder extends Binder {
        public TrackService getService() {
            return TrackService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.infoLog(TAG, "in onStartCommand");

        if(intent.hasExtra(INTENT_TRACK))
            objTrackDO = (TrackDO) intent.getExtras().get(INTENT_TRACK);
        buildGoogleApiClient();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        buildGoogleApiClient();
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        LogUtils.infoLog(TAG, "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.infoLog(TAG, "in onUnbind");
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.infoLog(TAG, "in onDestroy");
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LogUtils.infoLog(TAG, "TrackService Connected to GoogleApiClient");

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(location != null){
            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        } //else {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(ApplicationInstance.LOCATION_UPDATES_IN_SECONDS * 1000); // Update location every second

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        } else
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LogUtils.infoLog(TAG, "GoogleApiClient connection has failed");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LogUtils.infoLog(TAG, location.toString());

        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        if(objTrackDO != null) {
            objTrackDO.currentLat = currentLatLng.latitude;
            objTrackDO.currentLong = currentLatLng.longitude;

            getAddress();
        }
//        saveLocation();
    }

    void getAddress() {
        if(currentLatLng != null) {

            AddressDO objAddressDO = new FetchAddressLoader(this, currentLatLng).getAddress();

            if(objAddressDO.code == AddressConstants.SUCCESS_RESULT)
                objTrackDO.address = objAddressDO.message;
//            else if(objAddressDO.code == AddressConstants.FAILURE_RESULT)
//                setFailureAddress(objAddressDO.message);

            saveLocation();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if(isGpsEnabled()) {
            isGpsEnabled = true;
        } else {
            isGpsEnabled = false;
        }

        if(mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    private synchronized void saveLocation() {
        String isSuccess = LocationDL.insertLocation(this, objTrackDO);
        if(isSuccess.equalsIgnoreCase(AppConstant.STATUS_SUCCESS)) {
            Intent intent = new Intent(ACTION_TRACK_LOCATION);
            intent.putExtra(INTENT_LOCATION, objTrackDO);
            sendBroadcast(intent);
        }
//            mBinder.currentLocation(objTrackDO);
    }

    private boolean isGpsEnabled() {
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isGpsProviderEnabled;
    }
}
