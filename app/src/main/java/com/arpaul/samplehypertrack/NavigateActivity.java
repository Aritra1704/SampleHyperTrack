package com.arpaul.samplehypertrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Aritra Ranjan on 5/12/2017.
 */

public class NavigateActivity extends AppCompatActivity {

    private Button btnTrackChild, btnTrack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        btnTrackChild = (Button) findViewById(R.id.btnTrackChild);
        btnTrack = (Button) findViewById(R.id.btnTrack);

        btnTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NavigateActivity.this, ParentActivity.class));
            }
        });

        btnTrackChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NavigateActivity.this, TrackActivity.class));
            }
        });
    }
}
