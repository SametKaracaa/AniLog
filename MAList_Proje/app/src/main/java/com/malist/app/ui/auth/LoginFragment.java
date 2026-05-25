package com.malist.app.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.malist.app.R;
import com.malist.app.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        if (viewModel.isUserAuthenticated()) {
            navigateToMain();
            return;
        }

        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                viewModel.login(email, password);
            } else {
                Toast.makeText(requireContext(), getString(R.string.error_fill_fields), Toast.LENGTH_SHORT).show();
            }
        });

        binding.tvRegisterHint.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.action_loginFragment_to_registerFragment)
        );
    }

    private void observeViewModel() {
        viewModel.getAuthState().observe(getViewLifecycleOwner(), state -> {
            if (state == AuthViewModel.STATE_LOADING) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnLogin.setEnabled(false);
            } else if (state == AuthViewModel.STATE_SUCCESS) {
                binding.progressBar.setVisibility(View.GONE);
                navigateToMain();
            } else if (state == AuthViewModel.STATE_ERROR) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnLogin.setEnabled(true);
                String msg = viewModel.getErrorMessage().getValue();
                Toast.makeText(requireContext(), msg != null ? msg : "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMain() {
        NavHostFragment.findNavController(this).navigate(R.id.action_loginFragment_to_discoverFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
