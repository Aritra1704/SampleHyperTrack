package com.arpaul.track;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.arpaul.track.adapter.ActionAdapter;
import com.hypertrack.lib.HyperTrack;
import com.hypertrack.lib.callbacks.HyperTrackCallback;
import com.hypertrack.lib.models.Action;
import com.hypertrack.lib.models.ErrorResponse;
import com.hypertrack.lib.models.SuccessResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aritra Ranjan on 5/12/2017.
 */

public class ParentActivity extends AppCompatActivity {

    private EditText edtActionId;
    private RecyclerView rvActions;
    private Button btnTrack, btnAddAction;

    private ActionAdapter adapter;
    private ArrayList<String> actionIDs = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        edtActionId     = (EditText) findViewById(R.id.edtActionId);
        btnTrack        = (Button) findViewById(R.id.btnTrack);
        btnAddAction    = (Button) findViewById(R.id.btnAddAction);
        rvActions       = (RecyclerView) findViewById(R.id.rvActions);
        adapter = new ActionAdapter(this, actionIDs);
        rvActions.setAdapter(adapter);

        actionIDs.add("9030303407");
        actionIDs.add("9830791735");
        adapter.refresh(actionIDs);
        btnAddAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionIDs.add(edtActionId.getText().toString());
                adapter.refresh(actionIDs);
                edtActionId.setText("");
            }
        });

        btnTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackAction();
            }
        });
    }


    void trackAction() {

        HyperTrack.trackAction(actionIDs, new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse response) {

                // Handle trackAction success here
                List<Action> actionList = (List<Action>) response.getResponseObject();
                // Start Activity containing HyperTrackMapFragment
                // ActionId can also be passed along as intent extras
                Intent intent = new Intent(ParentActivity.this, TrackingActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                // Handle trackAction error here
                Toast.makeText(ParentActivity.this, "Error.." + errorResponse.getErrorMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
