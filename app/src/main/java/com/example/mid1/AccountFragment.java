package com.example.mid1;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;

/**
 * Account fragment displaying read-only user information (hard-coded fields).
 * Also provides a logout button that clears the login state and returns to the splash screen.
 */
public class AccountFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton btnLogout = view.findViewById(R.id.btn_account_logout);

        // Logout: clear login state and navigate back to splash
        btnLogout.setOnClickListener(v -> logout());
    }

    /** Clears the user.isLogin flag and restarts the app from the splash screen. */
    private void logout() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
        prefs.edit().putBoolean("user.isLogin", false).apply();

        startActivity(new Intent(requireContext(), splash.class));
        requireActivity().finish();
    }
}