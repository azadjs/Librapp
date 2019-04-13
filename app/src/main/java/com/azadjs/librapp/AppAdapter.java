package com.azadjs.librapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {

    private List<AppModel> appModelList;
    FirebaseAuth mAuth;

    public AppAdapter(List<AppModel> appModelList) {
        this.appModelList = appModelList;
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder{

        public ImageView appImage,appDelete;
        public TextView appText,appCategory,appDesc;


        public AppViewHolder(@NonNull View itemView) {
            super(itemView);

            appImage = itemView.findViewById(R.id.app_image_list);
            appText = itemView.findViewById(R.id.app_text_list);
            appCategory = itemView.findViewById(R.id.app_category_list);
            appDesc = itemView.findViewById(R.id.app_desc_list);
            appDelete = itemView.findViewById(R.id.app_delete);
        }
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list,parent,false);
        AppViewHolder appViewHolder = new AppViewHolder(v);
        return  appViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final AppViewHolder holder, final int position) {
        final AppModel appModel = appModelList.get(position);
        holder.appText.setText(appModel.getAppText());
        holder.appCategory.setText(appModel.getAppCategory());
        holder.appDesc.setText(appModel.getAppDesc());
        Picasso.get().load(appModel.getImage()).into(holder.appImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context mContext = v.getContext();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(appModel.getAppUrl()));
                mContext.startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        holder.appDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(appModelList.size() > 1) {
                    appModelList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, appModelList.size());
                    MainActivity.databaseReference.child(appModel.getAppId()).removeValue();
                }else if(appModelList.size() == 1){
                    appModelList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, appModelList.size());
                    MainActivity.databaseReference.child(appModel.getAppId()).removeValue();
                    RecyclerFragment.getInstance().onRefresh();
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return appModelList.size();
    }

    /*public void filterList(List<AppModel> filteredList){
       this.appModelList = filteredList;
       notifyDataSetChanged();
    }*/

}
