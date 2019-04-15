package com.azadjs.librapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> implements Filterable
{

    private List<AppModel> appModelList;
    private List<AppModel> appAdapterListFull;
    FirebaseAuth mAuth;

    public AppAdapter(List<AppModel> appModelList) {
        this.appModelList = appModelList;
        appAdapterListFull = appModelList;
    }

    @Override
    public Filter getFilter() {
        return appFilter;
    }

    private Filter appFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<AppModel> filteredlist = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filteredlist.addAll(appAdapterListFull);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(AppModel item: appAdapterListFull){
                    if(item.getAppText().toLowerCase().contains(filterPattern)) {
                        filteredlist.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredlist;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            appAdapterListFull.clear();
            appAdapterListFull.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

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
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Context mContext = v.getContext();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBody = appModel.getAppUrl();
                String shareSub  = "Why did you still don't know about "+appModel.getAppText()+" ?";
                intent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                mContext.startActivity(Intent.createChooser(intent,"Share via"));
                return true;
            }
        });

        holder.appDelete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Context mContext = v.getContext();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                alertDialogBuilder.setTitle("Delete all apps");
                alertDialogBuilder.setMessage("Are you sure you want to delete all apps?");
                alertDialogBuilder.setIcon(R.drawable.ic_delete);
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        appModelList.clear();
                        notifyDataSetChanged();
                        MainActivity.databaseReference.removeValue();
                        RecyclerFragment.getInstance().onRefresh();
                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });
    }



    @Override
    public int getItemCount() {
        return appModelList.size();
    }

}
