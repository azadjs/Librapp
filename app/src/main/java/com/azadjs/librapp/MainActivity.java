package com.azadjs.librapp;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    static TextView appBarText;
    EditText searchBar;
    ImageButton cancelSearchButton;
    LinearLayout searchBarLayout;
    BottomAppBar bottomAppBar;
    FloatingActionButton floatingActionButton;

    public static InterstitialAd mInterstitialAd;
    private static List<AppModel> appModelResult = new ArrayList<>();

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Fragment fragment;
    FirebaseAuth mAuth;
    public static DatabaseReference databaseReference;

    public static TextView getAppBarText() {
        return appBarText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appBarText = findViewById(R.id.app_bar_text);
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        searchBar = findViewById(R.id.search_bar);
        cancelSearchButton = findViewById(R.id.cancel_search);
        searchBarLayout = findViewById(R.id.search_layout);
        setSupportActionBar(bottomAppBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAppDialog addAppDialog = new AddAppDialog();
                addAppDialog.show(getSupportFragmentManager(), "AddBottomSheet");
            }
        });

        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheet = new BottomSheetDialog();
                bottomSheet.show(getSupportFragmentManager(), "BottomSheet");
            }
        });


        fragmentManager = getSupportFragmentManager();
        fragment = new RecyclerFragment();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.recycler_fragment, fragment);
        fragmentTransaction.commit();

        mAuth = FirebaseAuth.getInstance();
        if (databaseReference == null) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());
            databaseReference.keepSynced(true);

            Query query = databaseReference;
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    AppModel appModel = dataSnapshot.getValue(AppModel.class);
                    appModelResult.add(0, appModel);
                    AddAppDialog.setAppModelResult(appModelResult);
                    reloadFragment(fragment);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }

        cancelSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(searchBarLayout.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                searchBarLayout.setVisibility(View.GONE);
                searchBar.setText(null);
                appBarText.setVisibility(View.VISIBLE);
                AddAppDialog.getAppModelResult().clear();
                Query query = databaseReference;
                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        AppModel appModel = dataSnapshot.getValue(AppModel.class);
                        appModelResult.add(0, appModel);
                        AddAppDialog.setAppModelResult(appModelResult);
                        reloadFragment(fragment);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                AppAdapter appAdapter = new AppAdapter(AddAppDialog.getAppModelResult());
                RecyclerFragment.getInstance().setAdapter();
                appAdapter.getFilter().filter(s.toString());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.other_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_search:
                searchBarLayout.setVisibility(View.VISIBLE);
                appBarText.setVisibility(View.GONE);
                searchBar.requestFocus();
                InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                methodManager.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
                return true;

            case R.id.toolbar_last_added:
                Collections.sort(appModelResult, new Comparator<AppModel>() {
                    @Override
                    public int compare(AppModel o1, AppModel o2) {
                        int compareByLastAdded = o2.getAppAddDate().compareTo(o1.getAppAddDate());
                        SharedPreferences preferences = getSharedPreferences("Librapp", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("ByLastAdded",compareByLastAdded);
                        editor.commit();
                        return compareByLastAdded;
                    }
                });
                reloadFragment(fragment);
                return true;

            case R.id.toolbar_alphabet:
                Collections.sort(appModelResult, new Comparator<AppModel>() {
                    @Override
                    public int compare(AppModel o1, AppModel o2) {
                    int compareByAppText = o1.getAppText().toLowerCase().compareTo(o2.getAppText().toLowerCase());
                    SharedPreferences preferences = getSharedPreferences("Librapp", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("ByAppText",compareByAppText);
                    editor.commit();
                    return compareByAppText;
                    }
                });
                reloadFragment(fragment);
                return true;

            case R.id.toolbar_category:
                Collections.sort(appModelResult, new Comparator<AppModel>() {
                    @Override
                    public int compare(AppModel o1, AppModel o2) {
                        int compareByAppCategory = o1.getAppCategory().compareTo(o2.getAppCategory());
                        SharedPreferences preferences = getSharedPreferences("Librapp", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("ByAppCategory",compareByAppCategory);
                        editor.commit();
                        return compareByAppCategory;
                    }
                });
                reloadFragment(fragment);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void reloadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().detach(fragment).commitNowAllowingStateLoss();
        getSupportFragmentManager().beginTransaction().attach(fragment).commitAllowingStateLoss();
    }


}
