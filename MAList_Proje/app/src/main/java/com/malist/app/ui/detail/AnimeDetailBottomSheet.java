package com.malist.app.ui.detail;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.navigation.fragment.NavHostFragment;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.malist.app.R;
import com.malist.app.data.model.AnimeModel;
import com.malist.app.data.model.WatchStatus;
import com.malist.app.data.prefs.PreferencesManager;
import com.malist.app.data.repository.FirebaseRepository;
import com.malist.app.databinding.BottomSheetAnimeDetailBinding;
import com.malist.app.util.FormatUtils;

import java.util.ArrayList;
import java.util.List;

public class AnimeDetailBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_MAL_ID = "mal_id";
    private static final String ARG_TITLE = "title";
    private static final String ARG_TITLE_EN = "title_en";
    private static final String ARG_IMAGE_URL = "image_url";
    private static final String ARG_LARGE_IMAGE_URL = "large_image_url";
    private static final String ARG_SCORE = "score";
    private static final String ARG_EPISODES = "episodes";
    private static final String ARG_CHAPTERS = "chapters";
    private static final String ARG_SYNOPSIS = "synopsis";
    private static final String ARG_STATUS = "status";
    private static final String ARG_TYPE = "type";
    private static final String ARG_GENRES = "genres";
    private static final String ARG_WATCH_STATUS = "watch_status";
    private static final String ARG_IS_MANGA = "is_manga";
    private static final String ARG_USER_RATING = "user_rating";
    private static final String ARG_USER_REVIEW = "user_review";
    private static final String ARG_WATCHED_EPISODES = "watched_episodes";

    private BottomSheetAnimeDetailBinding binding;
    private final FirebaseRepository firebaseRepo = new FirebaseRepository();

    private AnimeModel anime;
    private WatchStatus existingStatus;

    public static AnimeDetailBottomSheet newInstance(AnimeModel anime) {
        AnimeDetailBottomSheet fragment = new AnimeDetailBottomSheet();
        Bundle args = new Bundle();
        args.putInt(ARG_MAL_ID, anime.getMalId());
        args.putString(ARG_TITLE, anime.getTitle());
        args.putString(ARG_TITLE_EN, anime.getTitleEnglish());
        args.putString(ARG_IMAGE_URL, anime.getImageUrl());
        args.putString(ARG_LARGE_IMAGE_URL, anime.getLargeImageUrl());
        args.putDouble(ARG_SCORE, anime.getScore());
        args.putInt(ARG_EPISODES, anime.getEpisodes());
        args.putInt(ARG_CHAPTERS, anime.getChapters());
        args.putString(ARG_SYNOPSIS, anime.getSynopsis());
        args.putString(ARG_STATUS, anime.getStatus());
        args.putString(ARG_TYPE, anime.getType());
        args.putString(ARG_GENRES, anime.getGenres());
        args.putString(ARG_WATCH_STATUS, anime.getWatchStatus());
        args.putBoolean(ARG_IS_MANGA, anime.getIsManga());
        args.putFloat(ARG_USER_RATING, anime.getUserRating());
        args.putString(ARG_USER_REVIEW, anime.getUserReview());
        args.putInt(ARG_WATCHED_EPISODES, anime.getWatchedEpisodes());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = getArguments();
            anime = new AnimeModel(
                    args.getInt(ARG_MAL_ID),
                    args.getString(ARG_TITLE, ""),
                    args.getString(ARG_TITLE_EN),
                    args.getString(ARG_IMAGE_URL, ""),
                    args.getString(ARG_LARGE_IMAGE_URL, ""),
                    args.getDouble(ARG_SCORE),
                    args.getInt(ARG_EPISODES),
                    args.getInt(ARG_CHAPTERS),
                    args.getString(ARG_SYNOPSIS, ""),
                    args.getString(ARG_STATUS, ""),
                    args.getString(ARG_TYPE, ""),
                    args.getString(ARG_GENRES, ""),
                    args.getString(ARG_WATCH_STATUS, ""),
                    args.getBoolean(ARG_IS_MANGA, false),
                    args.getFloat(ARG_USER_RATING, 0f),
                    args.getString(ARG_USER_REVIEW, ""),
                    args.getInt(ARG_WATCHED_EPISODES, 0)
            );
            existingStatus = WatchStatus.fromString(anime.getWatchStatus() != null ? anime.getWatchStatus() : "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetAnimeDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (anime != null) {
            String imageUrl = (anime.getLargeImageUrl() != null && !anime.getLargeImageUrl().isEmpty())
                    ? anime.getLargeImageUrl() : anime.getImageUrl();

            Glide.with(requireContext())
                    .asBitmap()
                    .load(imageUrl)
                    .placeholder(R.drawable.shimmer_placeholder)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            binding.ivDetailPoster.setImageBitmap(resource);
                            Palette.from(resource).generate(palette -> {
                                if (palette != null) {
                                    int fallbackColor = ContextCompat.getColor(requireContext(), R.color.accent_primary);
                                    int vibrant = palette.getVibrantColor(palette.getMutedColor(fallbackColor));
                                    binding.btnAddToLibrary.setBackgroundTintList(ColorStateList.valueOf(vibrant));
                                    binding.tvDetailScore.setTextColor(vibrant);
                                    binding.tvDetailGenres.setTextColor(vibrant);
                                    binding.btnRemoveFromLibrary.setTextColor(vibrant);
                                    binding.tvCurrentStatus.setTextColor(vibrant);
                                }
                            });
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {}
                    });

            binding.tvDetailTitle.setText(anime.getTitle());
            binding.tvDetailTitleEn.setText(anime.getTitleEnglish() != null ? anime.getTitleEnglish() : "");
            binding.tvDetailTitleEn.setVisibility(
                    (anime.getTitleEnglish() == null || anime.getTitleEnglish().isEmpty()) ? View.GONE : View.VISIBLE
            );
            binding.tvDetailScore.setText(FormatUtils.formatScore(anime.getScore()));
            binding.tvDetailEpisodes.setText(anime.getIsManga() ? FormatUtils.formatChapters(anime.getChapters()) : FormatUtils.formatEpisodes(anime.getEpisodes()));
            binding.tvDetailType.setText(anime.getType());
            binding.tvDetailGenres.setText(anime.getGenres());
            binding.tvDetailSynopsis.setText((anime.getSynopsis() == null || anime.getSynopsis().isEmpty())
                    ? getString(R.string.no_synopsis) : anime.getSynopsis());

            if (existingStatus != null) {
                binding.tvCurrentStatus.setVisibility(View.VISIBLE);
                binding.tvCurrentStatus.setText(getString(R.string.current_status_format, existingStatus.getDisplayName()));
                binding.btnAddToLibrary.setText(getString(R.string.btn_update_status));
                binding.btnRemoveFromLibrary.setVisibility(View.VISIBLE);
            } else {
                binding.tvCurrentStatus.setVisibility(View.GONE);
                binding.btnAddToLibrary.setText(getString(R.string.btn_add_library));
                binding.btnRemoveFromLibrary.setVisibility(View.GONE);
            }

            if (existingStatus == WatchStatus.COMPLETED) {
                binding.btnWriteReview.setVisibility(View.VISIBLE);
                if (anime.getUserRating() > 0f || (anime.getUserReview() != null && !anime.getUserReview().isEmpty())) {
                    binding.llReviewContainer.setVisibility(View.VISIBLE);
                    binding.tvUserRating.setText(anime.getUserRating() > 0f ? anime.getUserRating() + " / 10" : "No rating");
                    binding.tvUserReview.setText(anime.getUserReview());
                    binding.tvUserReview.setVisibility((anime.getUserReview() != null && !anime.getUserReview().isEmpty()) ? View.VISIBLE : View.GONE);
                    binding.btnWriteReview.setText("Edit Review");
                } else {
                    binding.llReviewContainer.setVisibility(View.GONE);
                }
            } else {
                binding.btnWriteReview.setVisibility(View.GONE);
                binding.llReviewContainer.setVisibility(View.GONE);
            }

            binding.btnWriteReview.setOnClickListener(v -> showReviewDialog(anime));

            binding.btnShare.setOnClickListener(v -> {
                String scoreText = anime.getUserRating() > 0f ? String.valueOf(anime.getUserRating()) : FormatUtils.formatScore(anime.getScore());
                String shareText = "I discovered " + anime.getTitle() + " on AniLog!\nScore: " + scoreText + "/10\nCheck it out!";
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                startActivity(Intent.createChooser(shareIntent, "Share Anime via"));
            });

            binding.btnAddToLibrary.setOnClickListener(v -> showStatusPicker(anime));
            binding.btnRemoveFromLibrary.setOnClickListener(v -> confirmRemove(anime));
            binding.btnViewFullDetails.setOnClickListener(v -> {
                dismiss();
                Bundle bundle = new Bundle();
                bundle.putInt("mal_id", anime.getMalId());
                try {
                    NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.animeFullDetailFragment, bundle);
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Navigation error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showStatusPicker(AnimeModel anime) {
        String[] statusNames = new String[WatchStatus.values().length];
        for (int i = 0; i < WatchStatus.values().length; i++) {
            statusNames[i] = WatchStatus.values()[i].getDisplayName();
        }
        int checkedItem = existingStatus != null ? existingStatus.ordinal() : -1;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.select_status))
                .setSingleChoiceItems(statusNames, checkedItem, (dialog, which) -> {
                    WatchStatus selectedStatus = WatchStatus.values()[which];
                    dialog.dismiss();
                    if (selectedStatus == WatchStatus.WATCHING) {
                        if (!anime.getIsManga()) {
                            showEpisodeProgressDialog(anime);
                        } else {
                            saveToLibrary(anime, selectedStatus);
                        }
                    } else if (selectedStatus == WatchStatus.COMPLETED) {
                        showCompletionDialog(anime);
                    } else {
                        saveToLibrary(anime, selectedStatus);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showCompletionDialog(AnimeModel anime) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_write_review, null);
        TextView tvSelectedScore = dialogView.findViewById(R.id.tvSelectedScore);
        TextInputEditText etUserReview = dialogView.findViewById(R.id.etUserReview);

        final int[] selectedScore = {(int) anime.getUserRating()};
        etUserReview.setText(anime.getUserReview());

        List<MaterialButton> scoreButtons = new ArrayList<>();
        scoreButtons.add(dialogView.findViewById(R.id.btnScore1));
        scoreButtons.add(dialogView.findViewById(R.id.btnScore2));
        scoreButtons.add(dialogView.findViewById(R.id.btnScore3));
        scoreButtons.add(dialogView.findViewById(R.id.btnScore4));
        scoreButtons.add(dialogView.findViewById(R.id.btnScore5));
        scoreButtons.add(dialogView.findViewById(R.id.btnScore6));
        scoreButtons.add(dialogView.findViewById(R.id.btnScore7));
        scoreButtons.add(dialogView.findViewById(R.id.btnScore8));
        scoreButtons.add(dialogView.findViewById(R.id.btnScore9));
        scoreButtons.add(dialogView.findViewById(R.id.btnScore10));

        Runnable updateScoreUI = () -> {
            int score = selectedScore[0];
            String[] labels = {"", "Appalling", "Horrible", "Very Bad", "Bad", "Average", "Fine", "Good", "Very Good", "Great", "Masterpiece"};
            tvSelectedScore.setText(score > 0 ? score + " / 10 — " + labels[score] : "Not rated");
            for (int i = 0; i < scoreButtons.size(); i++) {
                MaterialButton btn = scoreButtons.get(i);
                if (i + 1 == score) {
                    btn.setBackgroundColor(requireContext().getColor(R.color.accent_primary));
                    btn.setTextColor(requireContext().getColor(R.color.bg_primary));
                    btn.setStrokeWidth(0);
                } else {
                    btn.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                    btn.setTextColor(requireContext().getColor(R.color.text_primary));
                    btn.setStrokeColor(ColorStateList.valueOf(requireContext().getColor(R.color.divider)));
                    btn.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.stroke_width_1dp));
                }
            }
        };

        for (int i = 0; i < scoreButtons.size(); i++) {
            final int index = i;
            scoreButtons.get(i).setOnClickListener(v -> {
                selectedScore[0] = index + 1;
                updateScoreUI.run();
            });
        }
        updateScoreUI.run();

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Completed! 🎉")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    float newRating = selectedScore[0];
                    String newReview = etUserReview.getText().toString().trim();
                    AnimeModel updatedAnime = new AnimeModel(
                            anime.getMalId(), anime.getTitle(), anime.getTitleEnglish(), anime.getImageUrl(), anime.getLargeImageUrl(),
                            anime.getScore(), anime.getEpisodes(), anime.getChapters(), anime.getSynopsis(), anime.getStatus(),
                            anime.getType(), anime.getGenres(), anime.getWatchStatus(), anime.getIsManga(), newRating, newReview, anime.getWatchedEpisodes()
                    );
                    this.anime = updatedAnime;
                    saveToLibrary(updatedAnime, WatchStatus.COMPLETED);

                    binding.llReviewContainer.setVisibility(View.VISIBLE);
                    binding.tvUserRating.setText(newRating > 0f ? (int) newRating + " / 10" : "No rating");
                    binding.tvUserReview.setText(newReview);
                    binding.tvUserReview.setVisibility(!newReview.isEmpty() ? View.VISIBLE : View.GONE);
                    binding.btnWriteReview.setText("Edit Review");
                })
                .setNeutralButton("Skip", (dialog, which) -> saveToLibrary(anime, WatchStatus.COMPLETED))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showEpisodeProgressDialog(AnimeModel anime) {
        int maxEps = anime.getEpisodes();

        EditText input = new EditText(requireContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setHint("e.g. 5");
        input.setText(anime.getWatchedEpisodes() > 0 ? String.valueOf(anime.getWatchedEpisodes()) : "");
        input.setPadding(60, 40, 60, 40);
        input.setTextColor(requireContext().getColor(R.color.text_primary));
        input.setHintTextColor(requireContext().getColor(R.color.text_secondary));

        String totalEps = maxEps > 0 ? " / " + maxEps : "";

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Episode Progress")
                .setMessage("How many episodes have you watched?" + totalEps)
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    int watched;
                    try {
                        watched = Integer.parseInt(input.getText().toString());
                    } catch (NumberFormatException e) {
                        watched = 0;
                    }

                    if (maxEps > 0 && watched > maxEps) {
                        watched = maxEps;
                    }

                    if (maxEps > 0 && watched >= maxEps) {
                        AnimeModel updatedAnime = new AnimeModel(
                                anime.getMalId(), anime.getTitle(), anime.getTitleEnglish(), anime.getImageUrl(), anime.getLargeImageUrl(),
                                anime.getScore(), maxEps, anime.getChapters(), anime.getSynopsis(), anime.getStatus(),
                                anime.getType(), anime.getGenres(), anime.getWatchStatus(), anime.getIsManga(), anime.getUserRating(), anime.getUserReview(), maxEps
                        );
                        this.anime = updatedAnime;
                        showCompletionDialog(updatedAnime);
                    } else {
                        AnimeModel updatedAnime = new AnimeModel(
                                anime.getMalId(), anime.getTitle(), anime.getTitleEnglish(), anime.getImageUrl(), anime.getLargeImageUrl(),
                                anime.getScore(), anime.getEpisodes(), anime.getChapters(), anime.getSynopsis(), anime.getStatus(),
                                anime.getType(), anime.getGenres(), anime.getWatchStatus(), anime.getIsManga(), anime.getUserRating(), anime.getUserReview(), watched
                        );
                        this.anime = updatedAnime;
                        saveToLibrary(updatedAnime, WatchStatus.WATCHING);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showReviewDialog(AnimeModel anime) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_write_review, null);
        TextView tvSelectedScore = dialogView.findViewById(R.id.tvSelectedScore);
        TextInputEditText etUserReview = dialogView.findViewById(R.id.etUserReview);

        final int[] selectedScore = {(int) anime.getUserRating()};
        etUserReview.setText(anime.getUserReview());

        List<MaterialButton> scoreButtons = new ArrayList<>();
        scoreButtons.add(dialogView.findViewById(R.id.btnScore1));
        scoreButtons.add(dialogView.findViewById(R.id.btnScore2));
        scoreButtons.add(dialogView.findViewById(R.id.btnScore3));
        scoreButtons.add(dialogView.findViewById(R.id.btnScore4));
        scoreButtons.add(dialogView.findViewById(R.id.btnScore5));
        scoreButtons.add(dialogView.findViewById(R.id.btnScore6));
        scoreButtons.add(dialogView.findViewById(R.id.btnScore7));
        scoreButtons.add(dialogView.findViewById(R.id.btnScore8));
        scoreButtons.add(dialogView.findViewById(R.id.btnScore9));
        scoreButtons.add(dialogView.findViewById(R.id.btnScore10));

        Runnable updateScoreUI = () -> {
            int score = selectedScore[0];
            String[] labels = {"", "Appalling", "Horrible", "Very Bad", "Bad", "Average", "Fine", "Good", "Very Good", "Great", "Masterpiece"};
            tvSelectedScore.setText(score > 0 ? score + " / 10 — " + labels[score] : "Not rated");
            for (int i = 0; i < scoreButtons.size(); i++) {
                MaterialButton btn = scoreButtons.get(i);
                if (i + 1 == score) {
                    btn.setBackgroundColor(requireContext().getColor(R.color.accent_primary));
                    btn.setTextColor(requireContext().getColor(R.color.bg_primary));
                    btn.setStrokeWidth(0);
                } else {
                    btn.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                    btn.setTextColor(requireContext().getColor(R.color.text_primary));
                    btn.setStrokeColor(ColorStateList.valueOf(requireContext().getColor(R.color.divider)));
                    btn.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.stroke_width_1dp));
                }
            }
        };

        for (int i = 0; i < scoreButtons.size(); i++) {
            final int index = i;
            scoreButtons.get(i).setOnClickListener(v -> {
                selectedScore[0] = index + 1;
                updateScoreUI.run();
            });
        }
        updateScoreUI.run();

        new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    float newRating = selectedScore[0];
                    String newReview = etUserReview.getText().toString().trim();

                    AnimeModel updatedAnime = new AnimeModel(
                            anime.getMalId(), anime.getTitle(), anime.getTitleEnglish(), anime.getImageUrl(), anime.getLargeImageUrl(),
                            anime.getScore(), anime.getEpisodes(), anime.getChapters(), anime.getSynopsis(), anime.getStatus(),
                            anime.getType(), anime.getGenres(), anime.getWatchStatus(), anime.getIsManga(), newRating, newReview, anime.getWatchedEpisodes()
                    );
                    this.anime = updatedAnime;

                    saveToLibrary(updatedAnime, WatchStatus.COMPLETED);

                    binding.llReviewContainer.setVisibility(View.VISIBLE);
                    binding.tvUserRating.setText(newRating > 0f ? (int) newRating + " / 10" : "No rating");
                    binding.tvUserReview.setText(newReview);
                    binding.tvUserReview.setVisibility(!newReview.isEmpty() ? View.VISIBLE : View.GONE);
                    binding.btnWriteReview.setText("Edit Review");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmRemove(AnimeModel anime) {
        PreferencesManager prefs = PreferencesManager.getInstance(requireContext());
        if (prefs.isConfirmDelete()) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.confirm_delete_title)
                    .setMessage(getString(R.string.confirm_delete_message, anime.getTitle()))
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.delete, (dialog, which) -> removeFromLibrary(anime))
                    .show();
        } else {
            removeFromLibrary(anime);
        }
    }

    private void removeFromLibrary(AnimeModel anime) {
        firebaseRepo.removeAnime(anime.getMalId(), new FirebaseRepository.OnCompleteCallback() {
            @Override
            public void onSuccess() {
                if (getContext() != null) {
                    Toast.makeText(getContext(), R.string.removed_from_library, Toast.LENGTH_SHORT).show();
                }
                dismiss();
            }

            @Override
            public void onError(Exception e) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveToLibrary(AnimeModel anime, WatchStatus status) {
        firebaseRepo.saveAnime(anime, status, new FirebaseRepository.OnCompleteCallback() {
            @Override
            public void onSuccess() {
                if (getContext() != null) {
                    int msgRes = existingStatus != null ? R.string.updated_in_library : R.string.added_to_library;
                    Toast.makeText(getContext(), msgRes, Toast.LENGTH_SHORT).show();
                }
                dismiss();
            }

            @Override
            public void onError(Exception e) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
