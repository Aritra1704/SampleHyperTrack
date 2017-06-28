package com.arpaul.track;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arpaul.gpslibrary.fetchAddressGeoCode.AddressConstants;
import com.arpaul.gpslibrary.fetchAddressGeoCode.AddressDO;
import com.arpaul.gpslibrary.fetchAddressGeoCode.FetchAddressLoader;
import com.arpaul.track.webService.PostParamBuilder;
import com.arpaul.utilitieslib.CalendarUtils;
import com.arpaul.utilitieslib.LogUtils;
import com.arpaul.utilitieslib.PermissionUtils;
import com.arpaul.utilitieslib.UnCaughtException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.hypertrack.lib.HyperTrack;
import com.hypertrack.lib.callbacks.HyperTrackCallback;
import com.hypertrack.lib.models.Action;
import com.hypertrack.lib.models.ActionParamsBuilder;
import com.hypertrack.lib.models.ErrorResponse;
import com.hypertrack.lib.models.Place;
import com.hypertrack.lib.models.SuccessResponse;
import com.hypertrack.lib.models.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.arpaul.track.common.ApplicationInstance.LAT_DESTINATION;
import static com.arpaul.track.common.ApplicationInstance.LOADER_FETCH_ADDRESS;
import static com.arpaul.track.common.ApplicationInstance.LOCATION_UPDATES_IN_SECONDS;
import static com.arpaul.track.common.ApplicationInstance.LON_DESTINATION;

public class TrackActionActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveStartedListener,
        LoaderManager.LoaderCallbacks {

    private Button btnLogin;
    private EditText edtPhoneNumber, edtUsername;
    private TextView tvAddress;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private ImageView ivLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean isGpsEnabled;
    private boolean ispermissionGranted = false;
    private float mZoom = 0.0f;
    private LatLng currentLatLng;

    private String TAG = "TrackActionActivity";
    private String username = "", phoneNumber = "", lookupId = "";
    private String userid = "";
    private ArrayList<String> arrAction = new ArrayList<>();
    private ArrayList<Place> arrExpectPlace = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_action);

        initializeUiControls();

        bindControls();

        checkForLocationSettings();
    }

    private void bindControls() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (new PermissionUtils().checkPermission(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}) != PackageManager.PERMISSION_GRANTED) {
                new PermissionUtils().verifyPermission(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE});
            } else
                buildGoogleApiClient();
        } else
            buildGoogleApiClient();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginButtonClick(v);
            }
        });
    }

    public void onLoginButtonClick(View view) {
        if (TextUtils.isEmpty(edtPhoneNumber.getText().toString())) {
            Toast.makeText(this, "Phone number absent.",Toast.LENGTH_SHORT).show();
            return;
        }

        // Get User details, if specified
        username = edtUsername.getText().toString();
        phoneNumber = edtPhoneNumber.getText().toString();
        lookupId = phoneNumber;

        showProgressbar();

        /**
         * Get or Create a User for given lookupId on HyperTrack Server here to
         * login your user & configure HyperTrack SDK with this generated
         * HyperTrack UserId.
         * OR
         * Implement your API call for User Login and get back a HyperTrack
         * UserId from your API Server to be configured in the HyperTrack SDK.
         */
        HyperTrack.getOrCreateUser(username, phoneNumber, lookupId,
                new HyperTrackCallback() {
                    @Override
                    public void onSuccess(@NonNull SuccessResponse successResponse) {
                        // Hide Login Button loader
                        User user = (User) successResponse.getResponseObject();
                        // Handle createUser success here, if required
                        // HyperTrack SDK auto-configures UserId on createUser API call,
                        // so no need to call HyperTrack.setUserId() API

//                        user.getId();
                        Toast.makeText(TrackActionActivity.this, user.getId(), Toast.LENGTH_SHORT).show();
                        userid = user.getId();
                        // On UserLogin success
                        onUserLoginSuccess();
                    }

                    @Override
                    public void onError(@NonNull ErrorResponse errorResponse) {
                        // Hide Login Button loader
                        Toast.makeText(TrackActionActivity.this, "Error " + errorResponse.getErrorMessage(),
                                Toast.LENGTH_SHORT).show();

                        hideProgressbar();
                    }
                });
    }

    /**
     * Call this method when user has successfully logged in
     */
    private void onUserLoginSuccess() {

        Intent mainActivityIntent = new Intent(TrackActionActivity.this, NavigateActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainActivityIntent.putExtra("userid", userid);
        mainActivityIntent.putExtra("selectedLoc", currentLatLng);
        mainActivityIntent.putExtra("address", tvAddress.getText().toString());
        startActivity(mainActivityIntent);
        finish();


//        final String username = edtUsername.getText().toString();
//        LatLng expectedLatLng = new LatLng(LAT_DESTINATION, LON_DESTINATION);
//
//        arrExpectPlace.clear();
//        arrAction.clear();
//        Place expectedPlace = new Place().setLocation(expectedLatLng.latitude, expectedLatLng.longitude)
////                    .setAddress(currentLatLng)
//                .setName(username);
//        arrExpectPlace.add(expectedPlace);
//
//        Place expectedPlace2 = new Place().setLocation(currentLatLng.latitude, currentLatLng.longitude)
//                    .setAddress(tvAddress.getText().toString())
//                    .setName(username);
//        arrExpectPlace.add(expectedPlace2);
//
//        Place expectPlace = arrExpectPlace.get(0);
//
//        assignAction(expectPlace, 0);

//        startTracking(userid, arrAction);

    }

    /**
     * Call this method to check Location Settings before proceeding for User
     * Login
     */
    private void checkForLocationSettings() {
        // Check for Location permission
        if (!HyperTrack.checkLocationPermission(this)) {
            HyperTrack.requestPermissions(this);
            return;
        }

        // Check for Location settings
        if (!HyperTrack.checkLocationServices(this)) {
            HyperTrack.requestLocationServices(this, null);
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
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
            Toast.makeText(this, "GPS not enabled", Toast.LENGTH_SHORT).show();
        }

        if(mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    private boolean isGpsEnabled(){
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isGpsProviderEnabled;
    }

    @Override
    public void onCameraMoveStarted(int i) {
    }

    @Override
    public void onCameraIdle() {
        // Cleaning all the markers.
        if (mMap != null) {
            mMap.clear();
        }

        currentLatLng = mMap.getCameraPosition().target;
        mZoom = mMap.getCameraPosition().zoom;
        getAddress();
//        gpsUtills.isDeviceConfiguredProperly();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LogUtils.infoLog(TAG, "Connected to GoogleApiClient");

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(location != null){
            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            showCurrentLocation();
            getAddress();
        } //else {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(LOCATION_UPDATES_IN_SECONDS * 1000); // Update location every second

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        } else
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(TrackActionActivity.this, "Error" + " " + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(TrackActionActivity.this, "Error" + " onConnectionSuspended", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap = googleMap;

        if(isGpsEnabled) {
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    showCurrentLocation();
                    mMap.setOnCameraMoveStartedListener(TrackActionActivity.this);
                    mMap.setOnCameraIdleListener(TrackActionActivity.this);
                }
            }, 1000);
        } else if(ispermissionGranted) {
            showSettingsAlert();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LogUtils.infoLog(TAG, location.toString());

        if(currentLatLng == null) {
            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            showCurrentLocation();
            getAddress();
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {
        switch(id) {
            case LOADER_FETCH_ADDRESS:
                return new FetchAddressLoader(this, currentLatLng);
//                return new FetchData(this, bundle, WEBSERVICE_TYPE.POST, WEBSERVICE_CALL.REV_GEO);

            default:
                return null;
        }
//        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch(loader.getId()) {
            case LOADER_FETCH_ADDRESS:
                if (data instanceof AddressDO) {
                    AddressDO objAddressDO = (AddressDO) data;
                    if (objAddressDO.code == AddressConstants.SUCCESS_RESULT)
                        setAddress(objAddressDO.message);
                    else if (objAddressDO.code == AddressConstants.FAILURE_RESULT)
                        setFailureAddress(objAddressDO.message);
                }
                break;

            default:
                return;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == HyperTrack.REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkForLocationSettings();
            } else {
                // Handle Location Permission denied error
                Toast.makeText(this, "Location Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 1) {

            int location = 0;
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                        grantResult == PackageManager.PERMISSION_GRANTED) {
                    location++;
                } else if (permission.equals(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                        grantResult == PackageManager.PERMISSION_GRANTED) {
                    location++;
                }
            }

            if(location == 2) {
                ispermissionGranted = true;
                buildGoogleApiClient();

            } else {
                Toast.makeText(TrackActionActivity.this, "Allow app to access location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HyperTrack.REQUEST_CODE_LOCATION_SERVICES) {
            if (resultCode == Activity.RESULT_OK) {
                checkForLocationSettings();
            } else {
                // Handle Enable Location Services request denied error
                Toast.makeText(this, R.string.enable_location_settings, Toast.LENGTH_SHORT).show();
            }
        }
    }

    void getAddress() {
        if(currentLatLng != null) {
            Bundle bundle = new Bundle();
            LinkedHashMap<String, String> param = new PostParamBuilder().revGeoParam(currentLatLng.latitude, currentLatLng.longitude);
            bundle.putSerializable("currentLatLng", param);
            if(getSupportLoaderManager().getLoader(LOADER_FETCH_ADDRESS) == null)
                getSupportLoaderManager().initLoader(LOADER_FETCH_ADDRESS, bundle, this).forceLoad();
            else
                getSupportLoaderManager().restartLoader(LOADER_FETCH_ADDRESS, bundle, this).forceLoad();
        }
    }

    private void showCurrentLocation(){
        if(currentLatLng != null) {
            if(mMap!=null) {
                mMap.clear();
                mZoom = 18.0f;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,mZoom));
            }
        } else {
            Toast.makeText(this, "Unable_to_fetch_current_location", Toast.LENGTH_SHORT).show();
        }
    }

    private void setAddress(String message){
        if(message.contains("\n"))
            message = message.replace("\n", " ");
        tvAddress.setText("Address: " + message);
    }

    public void setFailureAddress(String message){
        tvAddress.setText("");
    }

    void initializeUiControls() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnLogin            = (Button) findViewById(R.id.btnLogin);
        edtPhoneNumber      = (EditText) findViewById(R.id.edtPhoneNumber);
        edtUsername         = (EditText) findViewById(R.id.edtUsername);
        tvAddress           = (TextView) findViewById(R.id.tvAddress);
        ivLocation          = (ImageView) findViewById(R.id.ivLocation);
    }
}
