package com.azadjs.librapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;


public class AddAppDialog extends BottomSheetDialog implements AdapterView.OnItemSelectedListener {

    static TextInputEditText appUrl;
    private TextInputEditText appDesc;
    private MaterialButton saveButton;
    private Spinner spinnerCategory;

    AppModel appModel = new AppModel();
    private static List<AppModel> appModelResult = new ArrayList<>();

    public static List<AppModel> getAppModelResult() {
        return appModelResult;
    }


    public static void setAppModelResult(List<AppModel> appModelResult) {
        AddAppDialog.appModelResult = appModelResult;
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Internet Check
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean checkConnectivity(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()){
            return false;
        }else{
            return true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bottomsheet_add,container,false);
        final View viewPos = v.findViewById(R.id.myCoordinatorLayout);
        appUrl = v.findViewById(R.id.app_url);
        appDesc = v.findViewById(R.id.app_desc);
        saveButton = v.findViewById(R.id.save_button);
        spinnerCategory = v.findViewById(R.id.spinner_category);
        List<String> categories = new ArrayList<String>();
        categories.add(0,"Choose category");
        categories.add("Education");
        categories.add("Entertainment");
        categories.add("Games");
        categories.add("Health");
        categories.add("Personalization");
        categories.add("Social");
        categories.add("Tools");
        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        spinnerCategory.setOnItemSelectedListener(this);

        appUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(URLUtil.isValidUrl(appUrl.getText().toString()))
                new Parse().execute();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appModel.setAppDesc(appDesc.getText().toString());
                appModel.setImage(Parse.eImage);
                appModel.setAppText(Parse.eName);
                if(appUrl.getText().toString().trim().length() != 0 && checkConnectivity()
                        && appModel.getAppText() != null && appModel.getImage() != null && !appModel.getAppCategory().equals("ERROR")) {
                    appModelResult.add(0,new AppModel(appModel.getImage(),appModel.getAppText(),appModel.getAppCategory(),appModel.getAppDesc()));
                    setAppModelResult(appModelResult);

                    getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.recycler_fragment)).commitNow();
                    getFragmentManager().popBackStackImmediate();
                    getFragmentManager().beginTransaction().add(R.id.recycler_fragment,new RecyclerFragment()).commit();
                    dismiss();
                }else{
                    if(appUrl.getText().toString().trim().length() == 0)
                    appUrl.setError("URL is required :)");
                    if(!checkConnectivity()){
                        Snackbar snackbar = Snackbar.make(viewPos, "Connection error", Snackbar.LENGTH_LONG)
                                .setAction("SETTING", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                                    }
                                }).setActionTextColor(Color.WHITE);
                        snackbar.show();
                    }
                    if(appModel.getAppCategory().equals("ERROR")){
                       TextView textView = (TextView) spinnerCategory.getSelectedView();
                       textView.setText("");
                       textView.setTextColor(Color.RED);
                       textView.setText(R.string.category_error);
                    }

                }
            }
        });

        return v;
    }

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
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
