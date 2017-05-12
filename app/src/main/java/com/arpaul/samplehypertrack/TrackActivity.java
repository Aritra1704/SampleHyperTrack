package com.arpaul.samplehypertrack;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hypertrack.lib.HyperTrack;
import com.hypertrack.lib.callbacks.HyperTrackCallback;
import com.hypertrack.lib.models.ErrorResponse;
import com.hypertrack.lib.models.SuccessResponse;
import com.hypertrack.lib.models.User;

/**
 * Created by Aritra Ranjan on 5/11/2017.
 */

public class TrackActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText edtPhoneNumber, edtUsername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        btnLogin            = (Button) findViewById(R.id.btnLogin);
        edtPhoneNumber      = (EditText) findViewById(R.id.edtPhoneNumber);
        edtUsername         = (EditText) findViewById(R.id.edtUsername);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginButtonClick(v);
            }
        });
    }

    /**
     * Call this method when User Login button has been clicked.
     * Note that this method is linked with the layout file (content_login.xml)
     * using this button's layout's onClick attribute. So no need to invoke this
     * method or handle login button's click listener explicitly.
     *
     * @param view
     */
    public void onLoginButtonClick(View view) {
        // Check if Location Settings are enabled, if yes then attempt
        // DriverLogin
        checkForLocationSettings();
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

        // Location Permissions and Settings have been enabled
        // Proceed with your app logic here i.e User Login in this case
        attemptUserLogin();
    }

    private void attemptUserLogin() {
        if (TextUtils.isEmpty(edtPhoneNumber.getText().toString())) {
            Toast.makeText(this, "Phone number absent.",Toast.LENGTH_SHORT).show();
            return;
        }

        // Get User details, if specified
        final String username = edtUsername.getText().toString();
        final String phoneNumber = edtPhoneNumber.getText().toString();
        final String lookupId = phoneNumber;

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

                        // On UserLogin success
                        onUserLoginSuccess();
                    }

                    @Override
                    public void onError(@NonNull ErrorResponse errorResponse) {
                        // Hide Login Button loader
                        Toast.makeText(TrackActivity.this, "Error " + errorResponse.getErrorMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Call this method when user has successfully logged in
     */
    private void onUserLoginSuccess() {
        HyperTrack.startTracking(new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse successResponse) {
                // Hide Login Button loader

                Toast.makeText(TrackActivity.this, "Success", Toast.LENGTH_SHORT).show();

                // Start User Session by starting MainActivity
                Intent mainActivityIntent = new Intent(TrackActivity.this, TrackLogoutActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainActivityIntent);
                finish();
            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                // Hide Login Button loader

                Toast.makeText(TrackActivity.this, "Error" + " " + errorResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    /**
     * Handle on Grant Location Permissions request accepted/denied result
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);

        if (requestCode == HyperTrack.REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {
                // Check if Location Settings are enabled to proceed
                checkForLocationSettings();

            } else {
                // Handle Location Permission denied error
                Toast.makeText(this, "Location Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Handle on Enable Location Services request accepted/denied result
     * @param requestCode
     * @param resultCode
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HyperTrack.REQUEST_CODE_LOCATION_SERVICES) {
            if (resultCode == Activity.RESULT_OK) {
                // Check if Location Settings are enabled to proceed
                checkForLocationSettings();

            } else {
                // Handle Enable Location Services request denied error
                Toast.makeText(this, R.string.enable_location_settings,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
