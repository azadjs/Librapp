package com.azadjs.librapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InstantAddActivity extends AppCompatActivity {

    private TextInputLayout appUrlLayout;
    static TextInputEditText appUrl;
    private TextInputEditText appDesc;
    private Button saveButton;
    private Spinner spinnerCategory;

    AppModel appModel = new AppModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instant_add);
        appUrlLayout = findViewById(R.id.app_url_layout_instant);
        appUrl = findViewById(R.id.app_url_instant);
        appDesc = findViewById(R.id.app_desc_instant);
        saveButton = findViewById(R.id.save_button_instant);
        spinnerCategory = findViewById(R.id.spinner_category_instant);
        List<String> categories = new ArrayList<String>();
        categories.add(0,"Choose category");
        categories.add("Education");
        categories.add("Entertainment");
        categories.add("Games");
        categories.add("Health");
        categories.add("Personalization");
        categories.add("Social");
        categories.add("Tools");
        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Choose category")){
                    appModel.setAppCategory("ERROR");
                }else{
                    String selectedCategory = parent.getItemAtPosition(position).toString();
                    appModel.setAppCategory(selectedCategory);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        instantApp();
        new ParseInstant().execute();
        appModel.setAppUrl(appUrl.getText().toString());


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appModel.setAppDesc(appDesc.getText().toString());
                appModel.setImage(ParseInstant.geteImage());
                appModel.setAppText(ParseInstant.geteName());
                appModel.setAppAddDate(new Date());

                if(appUrl.getText().toString().trim().length() != 0 && checkConnectivity()
                        && appModel.getAppText() != null && appModel.getImage() != null &&
                        !appModel.getAppCategory().equals("ERROR")) {
                    DatabaseReference postRef = MainActivity.databaseReference;
                    postRef.orderByChild("appText").equalTo(appModel.getAppText())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        Toast.makeText(getApplicationContext(),"This app exists in your library",Toast.LENGTH_SHORT).show();
                                    } else {
                                        final String key = MainActivity.databaseReference.push().getKey();
                                        appModel.setAppId(key);
                                        ////////////////////////////////////////////////////////////////////////////////
                                        //Add field in list and database
                                        ////////////////////////////////////////////////////////////////////////////////
                                        AppModel myAppModel = new AppModel(key, appModel.getImage(),appModel.getAppText(),
                                                appModel.getAppCategory(),appModel.getAppDesc(),appModel.getAppUrl(), appModel.getAppAddDate());
                                        MainActivity.databaseReference.child(key).setValue(myAppModel);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }else{
                    if(appUrl.getText().toString().trim().length() == 0)
                        appUrlLayout.setError(getText(R.string.url_error));
                    if(!checkConnectivity()){
                        Toast.makeText(getApplicationContext(),"Connection Error",Toast.LENGTH_SHORT).show();
                    }
                    if(appModel.getAppCategory().equals("ERROR")){
                        TextView textView = (TextView) spinnerCategory.getSelectedView();
                        textView.setText("");
                        textView.setTextColor(Color.RED);
                        textView.setText(R.string.category_error);
                    }
                }
                //finish();
            }
        });

    }

    public boolean checkConnectivity(){
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()){
            return false;
        }else{
            return true;
        }
    }

    public void instantApp(){
        Intent intent = getIntent();
        String action = intent.getAction();
        System.out.println(action);
        String type = intent.getType();
        System.out.println(type);
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String getIntentAppName = intent.getStringExtra(Intent.EXTRA_TEXT);
                appUrl.setText(getIntentAppName);
            }
        }
    }
}
