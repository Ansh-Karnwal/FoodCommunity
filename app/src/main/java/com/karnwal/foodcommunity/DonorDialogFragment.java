package com.karnwal.foodcommunity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.karnwal.foodcommunity.databinding.AddDonorBinding;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;

public class DonorDialogFragment extends DialogFragment {
    public interface OnInputSelected {
        void sendInput(String name, String address, String foodList, String calendar, String additionalInformation);
    }

    public OnInputSelected mOnInputSelected;
    private AddDonorBinding binding;
    private EditText mName, mAddress, mFoodList, mAdditionalInformation;

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
        mAdditionalInformation = binding.additionalInformation;
        binding.closeAlert.setOnClickListener(v -> {
            getDialog().dismiss();
        });
        final String[] mCalendar = new String[3];
        binding.chooseDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int YEAR = calendar.get(Calendar.YEAR);
            int MONTH = calendar.get(Calendar.MONTH);
            int DATE = calendar.get(Calendar.DATE);
            int HOUR = calendar.get(Calendar.HOUR_OF_DAY);
            int MINUTE = calendar.get(Calendar.MINUTE);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this.getActivity(), (view, year, month, date) -> {
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
                binding.chooseDate.setText("Chosen Date: " + mCalendar[0] + " " + mCalendar[1] + " " + mCalendar[2]);
            }, YEAR, MONTH, DATE);
            datePickerDialog.setCancelable(false);
            datePickerDialog.show();
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                TimePickerDialog timePickerDialog = new TimePickerDialog(this.getActivity(), (view, hour, minute) -> {
                    Calendar currentCalendar = Calendar.getInstance();
                    currentCalendar.set(Calendar.HOUR, hour);
                    currentCalendar.set(Calendar.MINUTE, minute);
                    if (hour >= 12) mCalendar[2] = "PM";
                    else mCalendar[2] = "AM";
                    mCalendar[1] = (DateFormat.format("hh:mm", currentCalendar)).toString();
                    if (mCalendar[0] == null)
                        mCalendar[0] = (DateFormat.format("E, dd MMM yyyy", calendar)).toString();
                    binding.chooseDate.setText("Chosen Date: " + mCalendar[0] + " " + mCalendar[1] + " " + mCalendar[2]);
                }, HOUR, MINUTE, false);
                timePickerDialog.setCancelable(false);
                timePickerDialog.show();
            }, 5);
        });
        binding.addButton.setOnClickListener(v -> {
            String name = mName.getText().toString();
            if (name.equals("")) {
                Toast.makeText(this.getActivity(), "Please Enter Name", Toast.LENGTH_SHORT).show();
                return;
            }
            String address = mAddress.getText().toString();
            if (address.equals("")) {
                Toast.makeText(this.getActivity(), "Please Enter Address", Toast.LENGTH_SHORT).show();
                return;
            }
            String foodList = mFoodList.getText().toString();
            if (foodList.equals("")) {
                Toast.makeText(this.getActivity(), "Please Enter Food/s", Toast.LENGTH_SHORT).show();
                return;
            }
            String additionalInformation = mAdditionalInformation.getText().toString();
            String calendar = mCalendar[0] + " " + mCalendar[1] + " " + mCalendar[2];
            if (calendar.equals("null null null")) {
                Toast.makeText(this.getActivity(), "Please Choose Date And Time", Toast.LENGTH_SHORT).show();
                return;
            }
            mOnInputSelected.sendInput(name, address, foodList, calendar, additionalInformation);
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
