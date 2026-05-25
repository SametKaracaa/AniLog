package com.malist.app.ui.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.malist.app.databinding.FragmentAnimeFullDetailBinding;

public class AnimeFullDetailFragment extends Fragment {

    private FragmentAnimeFullDetailBinding binding;
    private AnimeFullDetailViewModel viewModel;
    private CharacterAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAnimeFullDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AnimeFullDetailViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());

        setupRecyclerView();
        observeViewModel();

        if (getArguments() != null) {
            int malId = getArguments().getInt("mal_id", -1);
            if (malId != -1) {
                viewModel.loadCharacters(malId);
            }
        }
    }

    private void setupRecyclerView() {
        adapter = new CharacterAdapter();
        binding.rvCharacters.setAdapter(adapter);
        binding.rvCharacters.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void observeViewModel() {
        viewModel.getCharacters().observe(getViewLifecycleOwner(), characters -> {
            if (characters != null && !characters.isEmpty()) {
                adapter.submitList(characters);
                binding.rvCharacters.setVisibility(View.VISIBLE);
                binding.tvError.setVisibility(View.GONE);
            } else {
                binding.rvCharacters.setVisibility(View.GONE);
                binding.tvError.setVisibility(View.VISIBLE);
                binding.tvError.setText("No characters found.");
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.pbCharacters.setVisibility(Boolean.TRUE.equals(isLoading) ? View.VISIBLE : View.GONE);
            if (Boolean.TRUE.equals(isLoading)) {
                binding.rvCharacters.setVisibility(View.GONE);
                binding.tvError.setVisibility(View.GONE);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                binding.tvError.setVisibility(View.VISIBLE);
                binding.tvError.setText(error);
                binding.rvCharacters.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
