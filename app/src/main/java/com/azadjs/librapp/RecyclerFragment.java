package com.azadjs.librapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class RecyclerFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public RecyclerFragment() {
    }

    @Override
    public void setTargetFragment(@Nullable Fragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);
    }
    private static RecyclerFragment instance;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AppAdapter appAdapter;
    private ImageView emptyView;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recycler, container, false);
        instance = this;
        mSwipeRefreshLayout = v.findViewById(R.id.swipe_to_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = v.findViewById(R.id.app_list);
        emptyView = v.findViewById(R.id.empty_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        ((LinearLayoutManager) layoutManager).setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        appAdapter = new AppAdapter(AddAppDialog.getAppModelResult());
        recyclerView.setAdapter(appAdapter);
        appAdapter.notifyDataSetChanged();
        changeView();
        TextView appBarText = MainActivity.getAppBarText();
        appBarText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutManager.smoothScrollToPosition(recyclerView, null, 0);
            }
        });
        return v;
    }

    public static RecyclerFragment getInstance(){
        return instance;
    }

    public void changeView(){
        if(appAdapter.getItemCount() == 0){
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        changeView();
        mSwipeRefreshLayout.setRefreshing(false);
    }


}
