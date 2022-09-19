package com.karnwal.foodcommunity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karnwal.foodcommunity.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    private DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        binding.logOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this.getActivity(), LoginActivity.class);
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
        return binding.getRoot();
    }
}