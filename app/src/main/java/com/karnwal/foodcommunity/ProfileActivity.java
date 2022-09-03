package com.karnwal.foodcommunity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karnwal.foodcommunity.databinding.ActivityProfileBinding;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.logOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        });
        mDataBase.child(Constants.USERS).child(user.getUid()).child(Constants.ZIPCODE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.zipcodeText.setHint("Change Zip Code (" + snapshot.getValue(String.class) + ")");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        binding.changeZipCode.setOnClickListener(v -> {
            mDataBase.child(Constants.USERS).child(user.getUid()).child(Constants.ZIPCODE).setValue("" + binding.zipcodeText.getText());
            binding.zipcodeText.setHint("Change Zip Code " + "(" + binding.zipcodeText.getText() + ")");
        });

    }
}