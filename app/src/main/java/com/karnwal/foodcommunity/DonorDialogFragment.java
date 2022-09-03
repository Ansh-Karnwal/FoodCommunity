package com.karnwal.foodcommunity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.karnwal.foodcommunity.databinding.AddDonorBinding;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DonorDialogFragment extends DialogFragment {
    public interface OnInputSelected {
        void sendInput(String name, String address, String foodList, Calendar calendar, String additionalInformation);
    }

    public OnInputSelected mOnInputSelected;
    private AddDonorBinding binding;
    private EditText mName, mAddress, mFoodList, mAdditionalInformation;
    private TimePicker mTimePicker;
    private DatePicker mDatePicker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AddDonorBinding.inflate(inflater, container, false);
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setAttributes(layoutParams);
        }
        mName = binding.name;
        mAddress = binding.address;
        mFoodList = binding.foodList;
        mTimePicker = binding.timePicker;
        mDatePicker = binding.datePicker;
        mAdditionalInformation = binding.additionalInformation;
        binding.closeAlert.setOnClickListener(v -> {
            getDialog().dismiss();
        });
        binding.addButton.setOnClickListener(v -> {
            String name = mName.getText().toString();
            String address = mAddress.getText().toString();
            String foodList = mFoodList.getText().toString();
            Calendar calendar = new GregorianCalendar(mDatePicker.getYear(),
                    mDatePicker.getMonth(),
                    mDatePicker.getDayOfMonth(),
                    mTimePicker.getCurrentHour(),
                    mTimePicker.getCurrentMinute());
            String additionalInformation = mAdditionalInformation.getText().toString();
            if(!name.equals("")) {
                mOnInputSelected.sendInput(name, address, foodList, calendar, additionalInformation);
            }
            getDialog().dismiss();
        });
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mOnInputSelected = (OnInputSelected) getTargetFragment();
        }
        catch (ClassCastException classCastException) {
            Log.e("Error", "onAttach: ClassCastException : " + classCastException.getMessage());
        }
    }
}
