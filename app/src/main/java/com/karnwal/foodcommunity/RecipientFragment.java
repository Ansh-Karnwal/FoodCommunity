package com.karnwal.foodcommunity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karnwal.foodcommunity.databinding.FragmentRecipientBinding;

public class RecipientFragment extends Fragment {

    private FragmentRecipientBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipientBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}