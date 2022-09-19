package com.karnwal.foodcommunity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.karnwal.foodcommunity.databinding.FragmentRecipientBinding;

public class RecipientFragment extends Fragment {

    private FragmentRecipientBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipientBinding.inflate(inflater, container, false);
        binding.bottomNavigationView.setItemBackground(null);
        binding.bottomNavigationView.getMenu().findItem(R.id.placeholder).setEnabled(false);
        binding.bottomNavigationView.setSelectedItemId(R.id.Recipient);
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        return binding.getRoot();
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
            getFragmentManager().beginTransaction().replace(R.id.fragment, selectedFragment).commit();
        }
        catch (Exception ignored) {}
        return true;
    };
}