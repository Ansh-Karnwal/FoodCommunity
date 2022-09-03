package com.karnwal.foodcommunity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karnwal.foodcommunity.databinding.FragmentDonorBinding;

public class DonorFragment extends Fragment {

    private FragmentDonorBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDonorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}