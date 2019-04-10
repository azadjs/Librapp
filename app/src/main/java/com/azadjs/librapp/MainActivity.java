package com.azadjs.librapp;

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

import android.os.Bundle;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    EditText searchBar;
    TextView appBarText;
    ImageButton cancelSearchButton;
    LinearLayout searchBarLayout;

    BottomAppBar bottomAppBar;
    FloatingActionButton floatingActionButton;



    private static List<AppModel> appModelResult = new ArrayList<>();
    AppAdapter appAdapter;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Fragment fragment;
    FirebaseAuth mAuth;
    public static DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchBar = findViewById(R.id.search_bar);
        appBarText = findViewById(R.id.app_bar_text);
        appAdapter = new AppAdapter(AddAppDialog.getAppModelResult());
        cancelSearchButton = findViewById(R.id.cancel_search);
        searchBarLayout = findViewById(R.id.search_layout);
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(bottomAppBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
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
            }
        });

        /*searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });*/
    }

    /*private void filter(String text) {
        ArrayList<AppModel> filteredApps = new ArrayList<>();
        for (AppModel s : appModelResult) {
            if(s.getAppText().toLowerCase().contains(text.toLowerCase())){
                filteredApps.add(s);
                reloadFragment(fragment);
            }
        }
        appAdapter.filterList(filteredApps);
    }*/

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
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void reloadFragment(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().detach(fragment).commitNowAllowingStateLoss();
        getSupportFragmentManager().beginTransaction().attach(fragment).commitAllowingStateLoss();
    }

}
