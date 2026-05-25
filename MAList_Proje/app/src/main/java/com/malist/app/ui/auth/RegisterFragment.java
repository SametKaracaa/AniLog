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
import com.malist.app.databinding.FragmentRegisterBinding;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        binding.btnRegister.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            String confirm = binding.etPasswordConfirm.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.error_fill_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                Toast.makeText(requireContext(), getString(R.string.error_passwords_mismatch), Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(requireContext(), getString(R.string.error_password_length), Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.register(email, password);
        });

        binding.tvLoginHint.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );
    }

    private void observeViewModel() {
        viewModel.getAuthState().observe(getViewLifecycleOwner(), state -> {
            if (state == AuthViewModel.STATE_LOADING) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnRegister.setEnabled(false);
            } else if (state == AuthViewModel.STATE_SUCCESS) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), getString(R.string.success_register), Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).navigate(R.id.action_registerFragment_to_discoverFragment);
            } else if (state == AuthViewModel.STATE_ERROR) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnRegister.setEnabled(true);
                String msg = viewModel.getErrorMessage().getValue();
                Toast.makeText(requireContext(), msg != null ? msg : "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
