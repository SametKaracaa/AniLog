package com.malist.app.ui.swipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.malist.app.R;
import com.malist.app.data.model.AnimeModel;
import com.malist.app.data.model.WatchStatus;
import com.malist.app.data.prefs.PreferencesManager;
import com.malist.app.data.repository.FirebaseRepository;
import com.malist.app.databinding.FragmentSwipeBinding;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SwipeFragment extends Fragment implements CardStackListener {

    private FragmentSwipeBinding binding;
    private SwipeViewModel viewModel;
    private final FirebaseRepository firebaseRepo = new FirebaseRepository();

    private Set<Integer> currentLibraryIds = new HashSet<>();

    private CardStackLayoutManager layoutManager;
    private CardStackAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSwipeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SwipeViewModel.class);

        setupCardStack();
        setupButtons();
        observeViewModel();

        checkTutorial();

        binding.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
    }

    private void setupCardStack() {
        layoutManager = new CardStackLayoutManager(requireContext(), this);
        layoutManager.setStackFrom(StackFrom.Bottom);
        layoutManager.setVisibleCount(3);
        layoutManager.setTranslationInterval(8.0f);
        layoutManager.setScaleInterval(0.95f);
        layoutManager.setSwipeThreshold(0.3f);
        layoutManager.setMaxDegree(20.0f);
        layoutManager.setDirections(Direction.HORIZONTAL);
        layoutManager.setCanScrollHorizontal(true);
        layoutManager.setCanScrollVertical(false);
        layoutManager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
        layoutManager.setOverlayInterpolator(new LinearInterpolator());

        adapter = new CardStackAdapter();
        binding.cardStackView.setLayoutManager(layoutManager);
        binding.cardStackView.setAdapter(adapter);

        RecyclerView.ItemAnimator animator = binding.cardStackView.getItemAnimator();
        if (animator instanceof androidx.recyclerview.widget.DefaultItemAnimator) {
            ((androidx.recyclerview.widget.DefaultItemAnimator) animator).setSupportsChangeAnimations(false);
        }
    }

    private void setupButtons() {
        binding.btnSkip.setOnClickListener(v -> {
            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Left)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(new LinearInterpolator())
                    .build();
            layoutManager.setSwipeAnimationSetting(setting);
            binding.cardStackView.swipe();
        });

        binding.btnRewind.setOnClickListener(v -> binding.cardStackView.rewind());

        binding.btnAdd.setOnClickListener(v -> {
            SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Right)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(new LinearInterpolator())
                    .build();
            layoutManager.setSwipeAnimationSetting(setting);
            binding.cardStackView.swipe();
        });
    }

    private void observeViewModel() {
        firebaseRepo.getLibrary().observe(getViewLifecycleOwner(), libraryList -> {
            if (libraryList != null) {
                currentLibraryIds.clear();
                for (AnimeModel model : libraryList) {
                    currentLibraryIds.add(model.getMalId());
                }
                updateAdapter(viewModel.getAnimeList().getValue());
            }
        });

        viewModel.getAnimeList().observe(getViewLifecycleOwner(), this::updateAdapter);

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (Boolean.TRUE.equals(loading) && adapter.getItemCount() == 0) {
                binding.pbLoading.setVisibility(View.VISIBLE);
            } else {
                binding.pbLoading.setVisibility(View.GONE);
            }
        });
    }

    private void updateAdapter(List<AnimeModel> list) {
        if (list == null || list.isEmpty()) return;
        List<AnimeModel> filteredList = new ArrayList<>();
        for (AnimeModel model : list) {
            if (!currentLibraryIds.contains(model.getMalId())) {
                filteredList.add(model);
            }
        }
        adapter.submitList(filteredList);
        binding.pbLoading.setVisibility(View.GONE);

        if (filteredList.size() - layoutManager.getTopPosition() < 5) {
            viewModel.loadMore();
        }
    }

    private void checkTutorial() {
        PreferencesManager prefs = PreferencesManager.getInstance(requireContext());
        if (prefs.isShowRadarTutorial()) {
            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_radar_tutorial, null);
            CheckBox cbDoNotShowAgain = dialogView.findViewById(R.id.cbDoNotShowAgain);

            new MaterialAlertDialogBuilder(requireContext())
                    .setView(dialogView)
                    .setCancelable(false)
                    .setPositiveButton("Got it!", (dialog, which) -> {
                        if (cbDoNotShowAgain.isChecked()) {
                            prefs.setShowRadarTutorial(false);
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onCardSwiped(Direction direction) {
        int position = layoutManager.getTopPosition() - 1;
        List<AnimeModel> animeList = adapter.getList();

        if (position >= 0 && position < animeList.size()) {
            AnimeModel anime = animeList.get(position);
            if (direction == Direction.Right) {
                firebaseRepo.saveAnime(anime, WatchStatus.PLANNED, new FirebaseRepository.OnCompleteCallback() {
                    @Override
                    public void onSuccess() {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), anime.getTitle() + " added to Planned!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Failed to add", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        if (layoutManager.getTopPosition() == adapter.getItemCount() - 5) {
            viewModel.loadMore();
        }
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {}

    @Override
    public void onCardRewound() {}

    @Override
    public void onCardCanceled() {}

    @Override
    public void onCardAppeared(View view, int position) {}

    @Override
    public void onCardDisappeared(View view, int position) {}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
