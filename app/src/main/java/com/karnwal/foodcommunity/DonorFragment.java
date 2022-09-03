package com.karnwal.foodcommunity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.karnwal.foodcommunity.databinding.EditDonorBinding;
import com.karnwal.foodcommunity.databinding.FragmentDonorBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DonorFragment extends Fragment implements DonorDialogFragment.OnInputSelected {

    private AlertDialog alertDialog;
    private FragmentDonorBinding binding;
    private DonorAdapter adapter;
    private ArrayList<FoodDrive> donorArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDonorBinding.inflate(inflater, container, false);
        donorArrayList = new ArrayList<>();
        binding.foodDriveRecycler.setHasFixedSize(true);
        binding.foodDriveRecycler.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new DonorAdapter(this.getContext(), donorArrayList, this::editOnClick);
        binding.foodDriveRecycler.setAdapter(adapter);
        binding.addButton.setOnClickListener(v -> {
            DonorDialogFragment dialog = new DonorDialogFragment();
            dialog.setTargetFragment(DonorFragment.this, 1);
            dialog.show(getFragmentManager(), "Dialog");
        });
        return binding.getRoot();
    }

    @Override
    public void sendInput(String name, String address, String foodList, Calendar calendar, String additionalInformation) {
        addFoodDrive(name, address, foodList, calendar, additionalInformation);
    }

    private void addFoodDrive(String name, String address, String foodList, Calendar calendar, String additionalInformation) {
        FoodDrive foodDrive = new FoodDrive();
        foodDrive.setName(name);
        foodDrive.setAddress(address);
        foodDrive.setFoodList(foodList);
        foodDrive.setAdditionalInformation(additionalInformation);
        foodDrive.setCalendar(calendar);
        donorArrayList.add(foodDrive);
        adapter = new DonorAdapter(this.getContext(), donorArrayList, this::editOnClick);
        binding.foodDriveRecycler.setAdapter(adapter);
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
        name.setText(foodDrive.getName());
        address.setText(foodDrive.getAddress());
        foodList.setText(foodDrive.getFoodList());
        additionalInformation.setText(foodDrive.getAdditionalInformation());
        builderObj.setView(view);
        builderObj.setCancelable(false);
        closeAlert.setOnClickListener(v -> alertDialog.cancel());
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
            editSubscription(strName, strAddress, strFoodList, strAdditionInformation, foodDrive.getCalendar(), currentPosition);
            alertDialog.cancel();
        });
        alertDialog = builderObj.create();
        alertDialog.show();
    }

    private void editSubscription(String strName, String strAddress, String strFoodList, String strAdditionInformation, Calendar calendar, int currentPosition) {
        FoodDrive foodDrive = new FoodDrive();
        foodDrive.setName(strName);
        foodDrive.setAddress(strAddress);
        foodDrive.setFoodList(strFoodList);
        foodDrive.setAdditionalInformation(strAdditionInformation);
        foodDrive.setCalendar(calendar);
        adapter.editData(foodDrive, currentPosition);
    }
}