package com.arpaul.samplehypertrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hypertrack.lib.HyperTrack;

/**
 * Created by Aritra Ranjan on 5/11/2017.
 */

public class TrackLogoutActivity extends AppCompatActivity {

    private Button btnLogout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Stop HyperTrack SDK
                HyperTrack.stopTracking();
                Intent mainActivityIntent = new Intent(TrackLogoutActivity.this, TrackActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainActivityIntent);
                finish();
            }
        });
    }
}
