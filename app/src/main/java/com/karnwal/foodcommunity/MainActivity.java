package com.karnwal.foodcommunity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fondesa.kpermissions.extension.PermissionsBuilderKt;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karnwal.foodcommunity.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Location location;
    private ActivityMainBinding binding;
    private List<Address> addresses;
    private Geocoder geocoder;
    private DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
                            if (snapshot.exists()) {}
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
            mDataBase.child(Constants.USERS).child(user.getUid()).child(Constants.ZIPCODE).setValue(address.getPostalCode());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}