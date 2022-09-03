package com.karnwal.foodcommunity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DonorDialogFragment extends DialogFragment {
    public interface OnInputSelected {
        void sendInput(String name, String address, String foodList, Calendar calendar, String additionalInformation);
    }

    public OnInputSelected mOnInputSelected;

    private EditText mName, mAddress, mFoodList, mCalendar, mAdditionalInformation;
    private Button mActionOk;
    private ImageView mActionCancel;

//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//    }
}
