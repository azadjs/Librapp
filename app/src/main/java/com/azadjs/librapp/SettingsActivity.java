package com.azadjs.librapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.r0adkll.slidr.Slidr;

public class SettingsActivity extends AppCompatActivity {
    TextView appVersion, privacyPolicy, termsAndConditions;
    Button rateUs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_settings);
        appVersion = findViewById(R.id.app_version);
        privacyPolicy = findViewById(R.id.privacy_policy);
        termsAndConditions = findViewById(R.id.terms_and_conditions);
        rateUs = findViewById(R.id.rate_us);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Settings");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
    });

        appVersion.setText(BuildConfig.VERSION_NAME);
        Slidr.attach(this);
        privacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        termsAndConditions.setMovementMethod(LinkMovementMethod.getInstance());
        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName()));
                startActivity(intent);
            }
        });
    }
}
