package com.malist.app.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.malist.app.R;
import com.malist.app.data.model.AnimeModel;
import com.malist.app.data.model.WatchStatus;
import com.malist.app.data.prefs.PreferencesManager;
import com.malist.app.databinding.FragmentLibraryBinding;
import com.malist.app.ui.detail.AnimeDetailBottomSheet;

public class LibraryFragment extends Fragment {

    private FragmentLibraryBinding binding;
    private LibraryViewModel viewModel;
    private LibraryAdapter libraryAdapter;
    private PreferencesManager prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLibraryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = PreferencesManager.getInstance(requireContext());
        viewModel = new ViewModelProvider(this).get(LibraryViewModel.class);

        setupRecyclerView();
        setupTabs();
        observeViewModel();
        
        viewModel.observeLibrary(getViewLifecycleOwner());
    }

    private void setupRecyclerView() {
        libraryAdapter = new LibraryAdapter(
                this::handleRemoveClick,
                anime -> AnimeDetailBottomSheet.newInstance(anime)
                        .show(getChildFragmentManager(), "anime_detail")
        );

        binding.rvLibrary.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvLibrary.setAdapter(libraryAdapter);
        binding.rvLibrary.setHasFixedSize(false);
    }

    private void handleRemoveClick(AnimeModel anime) {
        if (prefs.isConfirmDelete()) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.confirm_delete_title)
                    .setMessage(getString(R.string.confirm_delete_message, anime.getTitle()))
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.delete, (dialog, which) -> {
                        viewModel.removeAnime(anime.getMalId());
                        Toast.makeText(requireContext(), R.string.removed_from_library, Toast.LENGTH_SHORT).show();
                    })
                    .show();
        } else {
            viewModel.removeAnime(anime.getMalId());
            Toast.makeText(requireContext(), R.string.removed_from_library, Toast.LENGTH_SHORT).show();
        }
    }

    private void setupTabs() {
        int defaultTab = Math.max(0, Math.min(prefs.getDefaultLibraryTab(), 2));
        TabLayout.Tab tab = binding.tabLayout.getTabAt(defaultTab);
        if (tab != null) {
            tab.select();
        }
        viewModel.setFilter(tabPositionToStatus(defaultTab));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab != null ? tab.getPosition() : 0;
                viewModel.setFilter(tabPositionToStatus(pos));
                prefs.setDefaultLibraryTab(pos);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        binding.toggleMediaType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                String mediaType = checkedId == R.id.btnManga ? "manga" : "anime";
                viewModel.setMediaType(mediaType);
            }
        });
    }

    private WatchStatus tabPositionToStatus(int pos) {
        switch (pos) {
            case 1:
                return WatchStatus.PLANNED;
            case 2:
                return WatchStatus.COMPLETED;
            case 0:
            default:
                return WatchStatus.WATCHING;
        }
    }

    private void observeViewModel() {
        viewModel.getFilteredList().observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                libraryAdapter.submitList(list);
                binding.emptyLayout.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                binding.rvLibrary.setVisibility(!list.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
