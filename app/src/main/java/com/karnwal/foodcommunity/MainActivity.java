package com.karnwal.foodcommunity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karnwal.foodcommunity.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Location location;
    private ActivityMainBinding binding;
    private List<Address> addresses;
    private Geocoder geocoder;
    private DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    protected static String zipcode;
    protected ImageButton refreshReceiver;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.no_wifi_dialog);
            Button retryButton = dialog.findViewById(R.id.retryButton);
            if (!MainActivity.haveNetworkConnection(context)) {
                dialog.show();
            }
            retryButton.setOnClickListener(v -> {
                if (MainActivity.haveNetworkConnection(context)) {
                    dialog.dismiss();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        binding.navView.setCheckedItem(R.id.nav_home);
//        if (!haveNetworkConnection()) {
//            Dialog dialog = new Dialog(MainActivity.this);
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            dialog.setCancelable(false);
//            dialog.setContentView(R.layout.no_wifi_dialog);
//            dialog.show();
//        }
        binding.navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    binding.refreshM.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new DonorFragment()).commit();
                    break;
                case R.id.nav_settings:
                    binding.refreshM.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new ProfileFragment()).commit();
                    break;
                case R.id.nav_my_donors:
                    binding.refreshM.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new MyDonorFragment()).commit();
                    break;
                case R.id.nav_my_requests:
                    binding.refreshM.setVisibility(View.GONE);
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
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        try {
            geocoder = new Geocoder(this);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {
        }
        refreshReceiver = binding.refreshM;
    }

    protected Bundle getExtras() {
        Bundle bundle = new Bundle();
        bundle.putString("databaseReference", "" + mDataBase.child(zipcode));
        return bundle;
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
        final String[] tempCode = new String[1];
        submitButton.setOnClickListener(v ->{
            mDataBase.child(Constants.USERS).child(user.getUid()).child(Constants.ZIPCODE).setValue("" + editText.getText());
            tempCode[0] = "" + editText.getText();
            zipcode = tempCode[0];
            if (("" + editText.getText()).equals("")) {
                Toast.makeText(MainActivity.this, "Please Enter a Zip Code", Toast.LENGTH_SHORT).show();
                return;
            }
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
        catch (Exception e) {
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

    protected static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    protected static String getZipcode() {
        return zipcode;
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(broadcastReceiver);
        super.onStop();
    }
}