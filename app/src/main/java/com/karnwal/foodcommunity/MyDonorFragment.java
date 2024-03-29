package com.karnwal.foodcommunity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karnwal.foodcommunity.databinding.FragmentDonorBinding;
import com.karnwal.foodcommunity.databinding.FragmentMyDonorBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MyDonorFragment extends Fragment implements DonorDialogFragment.OnInputSelected {

    private FragmentMyDonorBinding binding;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ArrayList<FoodDrive> donorArrayList = new ArrayList<>();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference originReference = FirebaseDatabase.getInstance().getReference().child(Constants.ZIPCODES);
    private DatabaseReference reference;
    private DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child(Constants.USERS).child(firebaseUser.getUid());
    private DonorAdapter adapter;
    private Executor executor = Executors.newFixedThreadPool(3);
    private AlertDialog alertDialog;
    private String zipcode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyDonorBinding.inflate(inflater, container, false);
        Bundle extras = ((MainActivity) getActivity()).getExtras();
        if (extras != null) {
            reference = FirebaseDatabase.getInstance().getReferenceFromUrl(extras.getString("databaseReference"));
            zipcode = extras.getString("mZipcode");
            getUserDonors();
        }
        binding.foodDriveRecycler.setHasFixedSize(true);
        binding.foodDriveRecycler.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new DonorAdapter(this.getActivity(), donorArrayList, this::editOnClick);
        binding.foodDriveRecycler.setAdapter(adapter);
        binding.addButtonRedirect.setOnClickListener(v -> {
            DonorDialogFragment dialog = new DonorDialogFragment();
            dialog.setTargetFragment(MyDonorFragment.this, 1);
            dialog.show(getFragmentManager(), "Dialog");
        });
        return binding.getRoot();
    }

    private void getUserDonors() {
        originReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.child(Constants.OWNERUID).getValue(String.class).equals(firebaseUser.getUid())) {
                            if ((dataSnapshot1.getValue(FoodDrive.class).getIsFoodDrive())) {
                                donorArrayList.add(dataSnapshot1.getValue(FoodDrive.class));
                            }
                        }
                    }
                }
                if (donorArrayList.size() == 0)
                    binding.textView3.setText("No Contributions");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void editOnClick(FoodDrive foodDrive, int currentPosition) {
        View view = LayoutInflater.from(this.getContext()).inflate(R.layout.edit_donor, null);
        AlertDialog.Builder builderObj = new AlertDialog.Builder(view.getContext());
        EditText name = view.findViewById(R.id.editname);
        EditText address = view.findViewById(R.id.editaddress);
        EditText foodList = view.findViewById(R.id.editfoodList);
        EditText additionalInformation = view.findViewById(R.id.editadditionalInformation);
        ImageView closeAlert = view.findViewById(R.id.editcloseAlert);
        Button addButton = view.findViewById(R.id.editButton);
        Button editDate = view.findViewById(R.id.editchooseDate);
        name.setText(foodDrive.getName());
        address.setText(foodDrive.getAddress());
        foodList.setText(foodDrive.getFoodList());
        additionalInformation.setText(foodDrive.getAdditionalInformation());
        editDate.setText("Chosen Date: " + foodDrive.getCalendar());
        builderObj.setView(view);
        closeAlert.setOnClickListener(v -> alertDialog.cancel());
        final String[] mCalendar = new String[3];
        editDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy hh:mm aa");
            try {calendar.setTime(simpleDateFormat.parse(foodDrive.getCalendar()));}
            catch (ParseException ignored) {}
            int YEAR = calendar.get(Calendar.YEAR);
            int MONTH = calendar.get(Calendar.MONTH);
            int DATE = calendar.get(Calendar.DATE);
            int HOUR = calendar.get(Calendar.HOUR_OF_DAY);
            int MINUTE = calendar.get(Calendar.MINUTE);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this.getActivity(), (view1, year, month, date) -> {
                Calendar currentCalendar = Calendar.getInstance();
                currentCalendar.set(Calendar.YEAR, year);
                currentCalendar.set(Calendar.MONTH, month);
                currentCalendar.set(Calendar.DATE, date);
                mCalendar[0] = (DateFormat.format("E, dd MMM yyyy", currentCalendar)).toString();
                if (mCalendar[1] == null) mCalendar[1] = (DateFormat.format("hh:mm", calendar)).toString();
                if (mCalendar[2] == null) {
                    if (HOUR >= 12) mCalendar[2] = "PM";
                    else mCalendar[2] = "AM";
                }
                editDate.setText("Chosen Date: " + mCalendar[0] + " " + mCalendar[1] + " " + mCalendar[2]);
            }, YEAR, MONTH, DATE);
            datePickerDialog.setCancelable(false);
            datePickerDialog.show();
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                TimePickerDialog timePickerDialog = new TimePickerDialog(this.getActivity(), (view1, hour, minute) -> {
                    Calendar currentCalendar = Calendar.getInstance();
                    currentCalendar.set(Calendar.HOUR, hour);
                    currentCalendar.set(Calendar.MINUTE, minute);
                    if (hour >= 12) mCalendar[2] = "PM";
                    else mCalendar[2] = "AM";
                    mCalendar[1] = (DateFormat.format("hh:mm", currentCalendar)).toString();
                    if (mCalendar[0] == null)
                        mCalendar[0] = (DateFormat.format("E, dd MMM yyyy", calendar)).toString();
                    editDate.setText("Chosen Date: " + mCalendar[0] + " " + mCalendar[1] + " " + mCalendar[2]);
                }, HOUR, MINUTE, DateFormat.is24HourFormat(this.getActivity()));
                timePickerDialog.setCancelable(false);
                timePickerDialog.show();
            }, 5);
        });
        if (mCalendar[0] == null || mCalendar[1] == null || mCalendar[3] == null) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy hh:mm aa");
            try {calendar.setTime(simpleDateFormat.parse(foodDrive.getCalendar()));}
            catch (ParseException ignored) {}
            mCalendar[0] = (DateFormat.format("E, dd MMM yyyy", calendar)).toString();
            mCalendar[1] = (DateFormat.format("hh:mm", calendar)).toString();
            if (calendar.get(Calendar.HOUR_OF_DAY) >= 12) mCalendar[2] = "PM";
            else mCalendar[2] = "AM";
        }
        addButton.setOnClickListener(v -> {
            String strName = "", strAddress = "", strFoodList = "", strAdditionInformation = "";
            if (name.getText() != null) {
                strName = name.getText().toString();
            }
            if (strName.equals("")) {
                Toast.makeText(this.getContext(), "Please enter Food Drive Name", Toast.LENGTH_LONG).show();
                return;
            }
            if (address.getText() != null) {
                strAddress = address.getText().toString();
            }
            if (strAddress.equals("")) {
                Toast.makeText(this.getContext(), "Please enter Address", Toast.LENGTH_LONG).show();
                return;
            }
            if (foodList.getText() != null) {
                strFoodList = foodList.getText().toString();
            }
            if (strFoodList.equals("")) {
                Toast.makeText(this.getContext(), "Please enter Foods", Toast.LENGTH_LONG).show();
                return;
            }
            if (additionalInformation.getText() != null) {
                strAdditionInformation = additionalInformation.getText().toString();
            }
            if (strAdditionInformation.equals("")) {
                Toast.makeText(this.getContext(), "Please enter Additional Information", Toast.LENGTH_LONG).show();
                return;
            }
            editFoodDrive(strName, strAddress, strFoodList, strAdditionInformation, mCalendar[0] + " " + mCalendar[1] + " " + mCalendar[2], currentPosition);
            alertDialog.cancel();
        });
        alertDialog = builderObj.create();
        alertDialog.show();
    }

    private void editFoodDrive(String strName, String strAddress, String strFoodList, String strAdditionInformation, String calendar, int currentPosition) {
        FoodDrive foodDrive = new FoodDrive();
        foodDrive.setName(strName);
        foodDrive.setAddress(strAddress);
        foodDrive.setFoodList(strFoodList);
        foodDrive.setAdditionalInformation(strAdditionInformation);
        foodDrive.setCalendar(calendar);
        adapter.editData(foodDrive, currentPosition);
//        passData();
    }

    @Override
    public void sendInput(String name, String address, String foodList, String calendar, String additionalInformation) {
        addFoodDrive(name, address, foodList, calendar, additionalInformation);
    }

    private void addFoodDrive(String name, String address, String foodList, String calendar, String additionalInformation) {
        binding.textView3.setText("Your Contributions");
        FoodDrive foodDrive = new FoodDrive();
        foodDrive.setName(name);
        foodDrive.setAddress(address);
        foodDrive.setFoodList(foodList);
        foodDrive.setAdditionalInformation(additionalInformation);
        foodDrive.setCalendar(calendar);
        String uuid = String.valueOf(UUID.randomUUID());
        foodDrive.setUUID(uuid);
        foodDrive.setOwnerUID(firebaseUser.getUid());
        foodDrive.setIsFoodDrive(true);
        foodDrive.setZipcode(zipcode);
        reference.child(uuid).setValue(foodDrive);
        reference.child(uuid).child(Constants.UUID).setValue(uuid);
        reference.child(uuid).child(Constants.OWNERUID).setValue(firebaseUser.getUid());
        donorArrayList.add(foodDrive);
        adapter = new DonorAdapter(this.getContext(), donorArrayList, this::editOnClick);
        binding.foodDriveRecycler.setAdapter(adapter);
    }
}