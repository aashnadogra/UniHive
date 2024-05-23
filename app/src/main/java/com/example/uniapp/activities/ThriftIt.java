package com.example.uniapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.uniapp.R;
import com.example.uniapp.databinding.ActivityThriftItBinding;
import com.example.uniapp.fragments.AccountFragment;
import com.example.uniapp.fragments.ChatsFragment;
import com.example.uniapp.fragments.HomeFragment;
import com.example.uniapp.fragments.MyAdsFragment;
import com.google.android.material.navigation.NavigationBarView;

public class ThriftIt extends AppCompatActivity {
    private ActivityThriftItBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityThriftItBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showHomeFragment();

        binding.bottomNv.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_home) {
                    showHomeFragment();
                    return true;
                } else if (itemId == R.id.menu_chats) {
                    showChatsFragment();
                    return true;

                } else if (itemId == R.id.menu_my_ads) {
                    showMyAdsFragment();
                    return true;

                } else if (itemId == R.id.menu_account) {
                    showAccountFragment();
                    return true;

                } else {
                    return false;
                }
            }
        });

        binding.sellFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ThriftIt.this, AdCreateActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showHomeFragment() {
        binding.toolbarTitleTv.setText("Home");
        HomeFragment fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFl.getId(), fragment, "HomeFragment");
        fragmentTransaction.commit();

    }
    private void showChatsFragment() {
        binding.toolbarTitleTv.setText("Chats");
        ChatsFragment fragment = new ChatsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFl.getId(), fragment, "ChatsFragment");
        fragmentTransaction.commit();
    }
    private void showMyAdsFragment() {
        binding.toolbarTitleTv.setText("My Ads");
        MyAdsFragment fragment = new MyAdsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFl.getId(), fragment, "MyAdsFragment");
        fragmentTransaction.commit();
    }
    private void showAccountFragment() {
        binding.toolbarTitleTv.setText("Account");
        AccountFragment fragment = new AccountFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.fragmentsFl.getId(), fragment, "AccountFragment");
        fragmentTransaction.commit();
    }
}