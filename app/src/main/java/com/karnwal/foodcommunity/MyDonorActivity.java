package com.karnwal.foodcommunity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karnwal.foodcommunity.databinding.ActivityMyDonorBinding;
import com.squareup.picasso.Picasso;

public class MyDonorActivity extends AppCompatActivity {

    private ActivityMyDonorBinding binding;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyDonorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        binding.navView.setCheckedItem(R.id.nav_my_donors);
        binding.navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_settings:
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    break;
                case R.id.nav_home:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    break;
                case R.id.nav_my_donors:
                    startActivity(new Intent(getApplicationContext(), MyDonorActivity.class));
                    break;
                case R.id.nav_my_requests:
                    // TODO: 9/5/2022 Add Requests
                    break;
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        View headerLayout = binding.navView.getHeaderView(0);
        ImageView profilePicture = headerLayout.findViewById(R.id.profilePicture);
        TextView userName = headerLayout.findViewById(R.id.userName);
        TextView email = headerLayout.findViewById(R.id.email);
        try {
            Picasso.get().load(user.getPhotoUrl()).into(profilePicture);
        }
        catch (Exception exception) {}
        userName.setText(user.getDisplayName());
        email.setText(user.getEmail());
    }
}