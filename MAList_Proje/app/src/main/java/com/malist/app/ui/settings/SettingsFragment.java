package com.malist.app.ui.settings;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.malist.app.MainActivity;
import com.malist.app.R;
import com.malist.app.databinding.FragmentSettingsBinding;
import com.malist.app.util.FormatUtils;

import java.util.Arrays;
import java.util.List;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;

    private static final List<String> PREBUILT_AVATARS = Arrays.asList(
            "https://cdn.myanimelist.net/images/characters/8/406163.jpg",
            "https://cdn.myanimelist.net/images/characters/9/310307.jpg",
            "https://cdn.myanimelist.net/images/characters/2/241413.jpg",
            "https://cdn.myanimelist.net/images/characters/10/249647.jpg",
            "https://cdn.myanimelist.net/images/characters/2/327920.jpg",
            "https://cdn.myanimelist.net/images/characters/6/122643.jpg",
            "https://cdn.myanimelist.net/images/characters/3/100534.jpg",
            "https://cdn.myanimelist.net/images/characters/6/63870.jpg",
            "https://cdn.myanimelist.net/images/characters/5/150011.jpg",
            "https://cdn.myanimelist.net/images/characters/15/615956.jpg",
            "https://cdn.myanimelist.net/images/characters/7/525105.jpg",
            "https://cdn.myanimelist.net/images/characters/9/311327.jpg",
            "https://cdn.myanimelist.net/images/characters/4/489561.jpg",
            "https://cdn.myanimelist.net/images/characters/9/345616.jpg",
            "https://cdn.myanimelist.net/images/characters/2/504723.jpg",
            "https://cdn.myanimelist.net/images/characters/7/316615.jpg",
            "https://cdn.myanimelist.net/images/characters/13/241133.jpg",
            "https://cdn.myanimelist.net/images/characters/2/34293.jpg",
            "https://cdn.myanimelist.net/images/characters/6/326131.jpg",
            "https://cdn.myanimelist.net/images/characters/8/491455.jpg",
            "https://cdn.myanimelist.net/images/characters/2/322173.jpg",
            "https://cdn.myanimelist.net/images/characters/7/83946.jpg"
    );

    private ActivityResultLauncher<String> notificationPermissionLauncher;
    private String selectedAvatarForDialog = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        viewModel.setNotificationsEnabled(true);
                        binding.switchNotifications.setChecked(true);
                        Toast.makeText(requireContext(), R.string.notifications_enabled, Toast.LENGTH_SHORT).show();
                    } else {
                        binding.switchNotifications.setChecked(false);
                        Toast.makeText(requireContext(), R.string.notifications_permission_denied, Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        observeViewModel();
        setupListeners();
    }

    private void observeViewModel() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        binding.tvProfileEmail.setText(user != null && user.getEmail() != null ? user.getEmail() : "No email");

        viewModel.getUserProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                String username = profile.getUsername() != null && !profile.getUsername().isEmpty()
                        ? profile.getUsername() : "Otaku_" + (user != null && user.getUid().length() >= 5 ? user.getUid().substring(0, 5) : "123");
                binding.tvProfileName.setText(username);

                if (profile.getAvatarUrl() != null && !profile.getAvatarUrl().isEmpty()) {
                    FormatUtils.loadImage(binding.ivProfileAvatar, profile.getAvatarUrl());
                } else {
                    FormatUtils.loadImage(binding.ivProfileAvatar, PREBUILT_AVATARS.get(0));
                }
            }
        });

        viewModel.getNotificationsEnabled().observe(getViewLifecycleOwner(), enabled -> {
            if (enabled != null) {
                binding.switchNotifications.setChecked(enabled);
                binding.rowReminderTime.setEnabled(enabled);
                binding.rowReminderTime.setAlpha(enabled ? 1f : 0.5f);
                binding.btnTestNotification.setEnabled(enabled);
                binding.btnTestNotification.setAlpha(enabled ? 1f : 0.5f);
            }
        });

        viewModel.getConfirmDelete().observe(getViewLifecycleOwner(), enabled -> {
            if (enabled != null) {
                binding.switchConfirmDelete.setChecked(enabled);
            }
        });

        androidx.lifecycle.Observer<Integer> timeObserver = val -> {
            int h = viewModel.getReminderHour().getValue() != null ? viewModel.getReminderHour().getValue() : 20;
            int m = viewModel.getReminderMinute().getValue() != null ? viewModel.getReminderMinute().getValue() : 0;
            binding.tvReminderTime.setText(String.format("%02d:%02d", h, m));
        };

        viewModel.getReminderHour().observe(getViewLifecycleOwner(), timeObserver);
        viewModel.getReminderMinute().observe(getViewLifecycleOwner(), timeObserver);
    }

    private void setupListeners() {
        binding.switchNotifications.setOnCheckedChangeListener((btn, isChecked) -> {
            if (!btn.isPressed()) return;
            if (isChecked) {
                requestNotificationPermissionIfNeeded();
            } else {
                viewModel.setNotificationsEnabled(false);
                Toast.makeText(requireContext(), R.string.notifications_disabled, Toast.LENGTH_SHORT).show();
            }
        });

        binding.switchConfirmDelete.setOnCheckedChangeListener((btn, isChecked) -> {
            if (!btn.isPressed()) return;
            viewModel.setConfirmDelete(isChecked);
            int msg = isChecked ? R.string.confirm_delete_on : R.string.confirm_delete_off;
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        });

        binding.rowReminderTime.setOnClickListener(v -> {
            if (!binding.rowReminderTime.isEnabled()) return;
            showTimePicker();
        });

        binding.btnTestNotification.setOnClickListener(v -> {
            viewModel.triggerTestNotification();
            Toast.makeText(requireContext(), R.string.test_notification_sent, Toast.LENGTH_SHORT).show();
        });

        binding.btnAbout.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.about_title)
                .setMessage(R.string.about_text)
                .setPositiveButton(R.string.ok, null)
                .show());

        binding.btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        binding.btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void showEditProfileDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        TextInputEditText etUsername = dialogView.findViewById(R.id.etUsername);
        RecyclerView rvAvatars = dialogView.findViewById(R.id.rvAvatars);

        com.malist.app.data.model.UserProfile currentProfile = viewModel.getUserProfile().getValue();
        if (currentProfile != null) {
            etUsername.setText(currentProfile.getUsername());
        }

        selectedAvatarForDialog = currentProfile != null && currentProfile.getAvatarUrl() != null && !currentProfile.getAvatarUrl().isEmpty()
                ? currentProfile.getAvatarUrl() : PREBUILT_AVATARS.get(0);

        AvatarAdapter avatarAdapter = new AvatarAdapter(PREBUILT_AVATARS, selectedAvatarForDialog, url -> selectedAvatarForDialog = url);

        rvAvatars.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        rvAvatars.setAdapter(avatarAdapter);

        new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newUsername = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
                    viewModel.updateProfile(newUsername, selectedAvatarForDialog);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showTimePicker() {
        int hour = viewModel.getReminderHour().getValue() != null ? viewModel.getReminderHour().getValue() : 20;
        int minute = viewModel.getReminderMinute().getValue() != null ? viewModel.getReminderMinute().getValue() : 0;
        new TimePickerDialog(
                requireContext(),
                (view, h, m) -> {
                    viewModel.setReminderTime(h, m);
                    Toast.makeText(
                            requireContext(),
                            getString(R.string.reminder_time_set, String.format("%02d:%02d", h, m)),
                            Toast.LENGTH_SHORT
                    ).show();
                },
                hour, minute, true
        ).show();
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean granted = ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED;
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                return;
            }
        }
        viewModel.setNotificationsEnabled(true);
        Toast.makeText(requireContext(), R.string.notifications_enabled, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
