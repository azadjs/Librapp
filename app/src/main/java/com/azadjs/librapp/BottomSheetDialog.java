package com.azadjs.librapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bottomsheet, container, false);
        final NavigationView navigationView = (NavigationView) v.findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.toolbar_settings:
                        startActivity(new Intent(BottomSheetDialog.this.getActivity(),SettingsActivity.class));
                        getActivity().getSupportFragmentManager().beginTransaction().remove(BottomSheetDialog.this).commit();
                        return true;
                    case R.id.toolbar_signout:
                        startActivity(new Intent(BottomSheetDialog.this.getActivity(),LoginActivity.class));
                        mAuth.getInstance().signOut();
                        getActivity().finish();
                        return true;
                    default:
                        return true;
                }
            }
        });
        return v;
    }

}
