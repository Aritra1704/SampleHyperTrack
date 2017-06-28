package com.arpaul.track;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hypertrack.lib.HyperTrack;
import com.hypertrack.lib.callbacks.HyperTrackCallback;
import com.hypertrack.lib.models.ErrorResponse;
import com.hypertrack.lib.models.SuccessResponse;

import java.util.ArrayList;

/**
 * Created by Aritra Ranjan on 5/11/2017.
 */

public class TrackLogoutActivity extends AppCompatActivity {

    private Button btnLogout, btnTrack;
    private ProgressDialog progressDialog;
    private TextView tvActionId;
    private String userid = "", actionid = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        if(getIntent().hasExtra("userid"))
            userid = getIntent().getStringExtra("userid");
        if(getIntent().hasExtra("actionid"))
            actionid = getIntent().getStringExtra("actionid");

        tvActionId = (TextView) findViewById(R.id.tvActionId);
        tvActionId.setText(actionid);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Stop HyperTrack SDK
                HyperTrack.stopTracking();
                Intent mainActivityIntent = new Intent(TrackLogoutActivity.this, TrackActionActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainActivityIntent);
                finish();
            }
        });
        btnTrack = (Button) findViewById(R.id.btnTrack);
        btnTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (progressDialog != null) {
                    progressDialog.show();
                }

                ArrayList<String> actions = new ArrayList<>();
                actions.add(actionid);

                // Call trackAction API method with action ID for tracking.
                // Start YourMapActivity containing HyperTrackMapFragment view with the customization on success
                // response of trackAction method
                HyperTrack.trackAction(actions, new HyperTrackCallback() {
                    @Override
                    public void onSuccess(@NonNull SuccessResponse response) {

                        if (progressDialog != null) {
                            progressDialog.cancel();
                        }

                        //Start Activity containing HyperTrackMapFragment
                        Intent intent = new Intent(TrackLogoutActivity.this, YourMapActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(@NonNull ErrorResponse errorResponse) {
                        if (progressDialog != null) {
                            progressDialog.cancel();
                        }
                        Toast.makeText(TrackLogoutActivity.this, "Error Occurred while trackActions: " + errorResponse.getErrorMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        initializeProgressDialog();
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching ");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }
}
