package com.karnwal.foodcommunity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.format.DateFormat;
import android.util.Log;
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
import java.util.Calendar;
import java.util.List;
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
    protected ImageButton refreshReceiver;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
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
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            zipcode = intent.getStringExtra("mZipcode");
        }
    };
    private final String PREFS_NAME = "PREFERENCES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        binding.navView.setCheckedItem(R.id.nav_home);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean dialogShown = settings.getBoolean("dialogShown", false);
        if (!dialogShown) {
            this.runOnUiThread(() -> {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Welcome to Food Community");
            builder1.setCancelable(false);
            builder1.setPositiveButton(
                    "Next",
                    (dialog, id) -> {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                        builder2.setMessage("Right now you are currently in the donor section\n\nHere you can put up donations for extra food that you have\n\n" +
                                "To add a donation click on the + button" +
                                "\n\nYou will also see other people's food donations here");
                        builder2.setCancelable(false);
                        builder2.setPositiveButton(
                                "Next",
                                (dialog12, id12) -> {
                                    AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new RecipientFragment()).commit();
                                    builder3.setMessage("Now you are in the recipient section\n\nHere you can request for food by clicking the + button and" +
                                            " entering the information");
                                    builder3.setCancelable(false);
                                    builder3.setPositiveButton(
                                            "Next",
                                            (dialog123, id123) -> {
                                                AlertDialog.Builder builder4 = new AlertDialog.Builder(this);
                                                builder4.setMessage("To access all of your donations, requests and settings click on the three lines on the top left\n\n" +
                                                        "To refresh the data click on the icon on the top right");
                                                builder4.setCancelable(false);
                                                builder4.setPositiveButton(
                                                        "Finish",
                                                        (dialog1234, id1234) -> dialog1234.cancel());
                                                builder4.setNegativeButton(
                                                        "Back",
                                                        (dialog1, id1) -> {
                                                            builder3.create().show();
                                                        });
                                                AlertDialog alert11 = builder4.create();
                                                alert11.show();
                                            });
                                    builder3.setNegativeButton(
                                            "Back",
                                            (dialog1, id1) -> {
                                                builder2.create().show();
                                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new DonorFragment()).commit();
                                            });
                                    AlertDialog alert11 = builder3.create();
                                    alert11.show();
                                });
                        builder2.setNegativeButton(
                                "Back",
                                (dialog1, id1) -> builder1.create().show());
                        AlertDialog alert11 = builder2.create();
                        alert11.show();
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
            });
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("dialogShown", true);
            editor.commit();
        }
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
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new MyRequestsFragment()).commit();
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
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("Zipcode-Send"));
        try {
            geocoder = new Geocoder(this);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {
        }
        refreshReceiver = binding.refreshM;
    }

    protected Bundle getExtras() {
        Bundle bundle = new Bundle();
        bundle.putString("databaseReference", "" + mDataBase.child(Constants.ZIPCODES).child(zipcode));
        bundle.putString("mZipcode", zipcode);
        return bundle;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
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
        catch (Exception exception) {}
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