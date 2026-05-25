package com.malist.app.ui.discover;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.malist.app.R;
import com.malist.app.databinding.FragmentDiscoverBinding;
import com.malist.app.ui.detail.AnimeDetailBottomSheet;

public class DiscoverFragment extends Fragment {

    private FragmentDiscoverBinding binding;
    private DiscoverViewModel viewModel;
    private AnimeAdapter animeAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDiscoverBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DiscoverViewModel.class);

        setupSearch();
        setupRecyclerView();
        setupSwipeRefresh();
        observeViewModel();

        binding.btnRetry.setOnClickListener(v -> viewModel.loadTopAnime());

        binding.fabSwipeMode.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.action_discoverFragment_to_swipeFragment)
        );
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setSearchQuery(s != null ? s.toString() : "");
            }
        });

        binding.btnFilter.setOnClickListener(v -> {
            FilterBottomSheet bottomSheet = new FilterBottomSheet(
                    viewModel.getCurrentGenres(),
                    viewModel.getCurrentOrderBy(),
                    viewModel.getCurrentSortOrder(),
                    (genreIds, orderBy, sortOrder) -> {
                        viewModel.setGenres(genreIds);
                        viewModel.setSortParams(orderBy, sortOrder);
                    }
            );
            bottomSheet.show(getChildFragmentManager(), "filter_bottom_sheet");
        });

        binding.toggleMediaType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                String mediaType = checkedId == R.id.btnManga ? "manga" : "anime";
                viewModel.setMediaType(mediaType);
                binding.fabSwipeMode.setVisibility("anime".equals(mediaType) ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void setupRecyclerView() {
        animeAdapter = new AnimeAdapter(anime -> {
            AnimeDetailBottomSheet.newInstance(anime)
                    .show(getChildFragmentManager(), "anime_detail");
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        binding.rvAnimeGrid.setLayoutManager(gridLayoutManager);
        binding.rvAnimeGrid.setAdapter(animeAdapter);
        binding.rvAnimeGrid.setHasFixedSize(true);

        binding.rvAnimeGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int totalItemCount = gridLayoutManager.getItemCount();
                    int lastVisible = gridLayoutManager.findLastVisibleItemPosition();
                    if (lastVisible >= totalItemCount - 6 && viewModel.canLoadMore()) {
                        viewModel.loadMore();
                    }
                }
            }
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(
                android.R.color.holo_purple,
                android.R.color.holo_blue_light
        );
        binding.swipeRefresh.setProgressBackgroundColorSchemeResource(
                R.color.bg_secondary
        );
        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.loadTopAnime());
    }

    private void observeViewModel() {
        viewModel.getAnimeList().observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                animeAdapter.submitList(list);
                binding.swipeRefresh.setVisibility(!list.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            binding.swipeRefresh.setRefreshing(false);
            if (Boolean.TRUE.equals(loading) && animeAdapter.getItemCount() == 0) {
                binding.shimmerLayout.setVisibility(View.VISIBLE);
                binding.shimmerLayout.startShimmer();
                binding.swipeRefresh.setVisibility(View.GONE);
                binding.errorLayout.setVisibility(View.GONE);
            } else {
                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.setVisibility(View.GONE);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && animeAdapter.getItemCount() == 0) {
                binding.errorLayout.setVisibility(View.VISIBLE);
                binding.tvError.setText(error);
                binding.shimmerLayout.setVisibility(View.GONE);
                binding.swipeRefresh.setVisibility(View.GONE);
            } else {
                binding.errorLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
