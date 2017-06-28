package com.arpaul.track;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.arpaul.utilitieslib.UnCaughtException;

/**
 * Created by aritrapal on 26/06/17.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(BaseActivity.this,"aritra.pal@zippr.com",getString(R.string.app_name)));

        initializeProgressDialog();
    }

    public void showSettingsAlert() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, "GPS not enabled.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showProgressbar() {
        if (progressDialog != null) {
            progressDialog.show();
        }
    }

    public void hideProgressbar() {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching ");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }
}
