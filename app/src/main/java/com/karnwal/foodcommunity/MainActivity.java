package com.karnwal.foodcommunity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.fondesa.kpermissions.extension.PermissionsBuilderKt;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karnwal.foodcommunity.databinding.ActivityMainBinding;
import com.karnwal.foodcommunity.databinding.NavHeaderBinding;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private Location location;
    private ActivityMainBinding binding;
    private List<Address> addresses;
    private Geocoder geocoder;
    private DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    protected static String zipcode;
    private boolean isRefreshClicked;
    private ArrayList<FoodDrive> dataList;
    private Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        binding.navView.setCheckedItem(R.id.nav_home);
        Bundle extras = new Bundle();
        executor.execute(() -> {
            for (;;) {
                if (zipcode != null) {
                    extras.putString("databaseReference", "" + mDataBase.child(zipcode));
                    break;
                }
            }
        });

        binding.navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_settings:
                    Intent settingsIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                    settingsIntent.putExtras(extras);
                    startActivity(settingsIntent);
                    break;
                case R.id.nav_my_donors:
                    Intent myDonorIntent = new Intent(getApplicationContext(), MyDonorActivity.class);
                    myDonorIntent.putExtras(extras);
                    startActivity(myDonorIntent);
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
        binding.bottomNavigationView.setItemBackground(null);
        binding.bottomNavigationView.getMenu().findItem(R.id.placeholder).setEnabled(false);
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        binding.settings.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        });
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        try {
            geocoder = new Geocoder(this);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {
        }
        binding.refreshM.setOnClickListener(v -> {
            isRefreshClicked = true;
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = (BottomNavigationView.OnNavigationItemSelectedListener) item -> {
        Fragment selectedFragment = null;
        switch (item.getItemId()) {
            case R.id.Donor:
                selectedFragment = new DonorFragment();
                break;
            case R.id.Recipient:
                selectedFragment = new RecipientFragment();
                break;
        }
        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, selectedFragment).commit();
        }
        catch (Exception ignored) {}
        return true;
    };

    protected Boolean getRefreshStatus() {
        if (isRefreshClicked) {
            isRefreshClicked = false;
            return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
                        Address address = addresses.get(0);
                        zipcode = address.getPostalCode();
                        mDataBase.child(Constants.USERS).child(user.getUid()).child(Constants.ZIPCODE).setValue(address.getPostalCode());
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    catch (Exception exception) {
                    }
                }
                else {
                    mDataBase.child(Constants.USERS).child(user.getUid()).child(Constants.ZIPCODE).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                zipcode = snapshot.getValue(String.class);
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Enter Zip Code Manually", Toast.LENGTH_LONG).show();
                                openDialog();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openDialog() {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.zipcode_dialog);
        EditText editText = dialog.findViewById(R.id.zipcodeText);
        Button submitButton = dialog.findViewById(R.id.submit);
        submitButton.setOnClickListener(v ->{
            mDataBase.child(Constants.USERS).child(user.getUid()).child(Constants.ZIPCODE).setValue("" + editText.getText());
            dialog.dismiss();
        });
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
            Address address = addresses.get(0);
            zipcode = address.getPostalCode();
            mDataBase.child(Constants.USERS).child(user.getUid()).child(Constants.ZIPCODE).setValue(address.getPostalCode());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }


//    @Override
//    public void onDataPass(ArrayList<FoodDrive> data) {
//        dataList = new ArrayList<>(data);
//    }
}