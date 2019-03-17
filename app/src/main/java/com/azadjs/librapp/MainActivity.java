package com.azadjs.librapp;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomAppBar bottomAppBar;
    FloatingActionButton floatingActionButton;
    ImageView emptyView;

    private RecyclerView recyclerView;
    List<AppModel> appModelResult = AddAppDialog.getAppModelResult();
    AddAppDialog addAppDialog = new AddAppDialog();
    private AppAdapter appAdapter;
    private RecyclerView.LayoutManager layoutManager;

    AppModel appModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emptyView = findViewById(R.id.empty_view);
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(bottomAppBar);
        floatingActionButton =findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAppDialog addAppDialog = new AddAppDialog();
                addAppDialog.show(getSupportFragmentManager(),"AddBottomSheet");
            }
        });

        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheet = new BottomSheetDialog();
                bottomSheet.show(getSupportFragmentManager(), "BottomSheet");
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////
        //RECYCLER VIEW ADAPTER BINDING
        ////////////////////////////////////////////////////////////////////////////////////////////

        recyclerView = findViewById(R.id.app_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        appAdapter = new AppAdapter(AddAppDialog.getAppModelResult());

        addAppDialog.setAppAdapter(appAdapter);

        recyclerView.setAdapter(appAdapter);
        appAdapter.notifyDataSetChanged();
        if(appAdapter.getItemCount() == 0){
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(getApplicationContext(),"Clicked app"+appModel.getAppText(),Toast.LENGTH_SHORT).show();
                        /*Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AddAppDialog.appUrl.getText().toString()));
                        startActivity(browseIntent);*/
                        Log.d("POSITION", "onItemClick position: " + position);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Intent browse = new Intent(Intent.ACTION_VIEW,Uri.parse(AddAppDialog.appUrl.getText().toString()));
                        startActivity(browse);
                    }
                })
        );
    }
}
