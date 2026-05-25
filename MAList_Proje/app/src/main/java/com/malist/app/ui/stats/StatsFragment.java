package com.malist.app.ui.stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.malist.app.R;
import com.malist.app.databinding.FragmentStatsBinding;

import java.util.List;
import java.util.Locale;

public class StatsFragment extends Fragment {

    private FragmentStatsBinding binding;
    private StatsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(StatsViewModel.class);

        viewModel.getStats().observe(getViewLifecycleOwner(), stats -> {
            if (stats == null) return;

            binding.tvTotalAnime.setText(String.format(Locale.getDefault(), "%,d", stats.getTotalAnime()));
            binding.tvTotalEpisodes.setText(String.format(Locale.getDefault(), "%,d", stats.getTotalEpisodes()));

            if (stats.getMeanScore() > 0) {
                binding.tvMeanScore.setText(String.format(Locale.getDefault(), "%.1f", stats.getMeanScore()));
            } else {
                binding.tvMeanScore.setText("N/A");
            }

            binding.tvTimeWasted.setText(getString(R.string.stats_days_hours, stats.getDaysWasted(), stats.getHoursWasted()));

            binding.llGenresContainer.removeAllViews();
            List<UserStats.GenreCount> topGenres = stats.getTopGenres();

            if (topGenres == null || topGenres.isEmpty()) {
                TextView tvEmpty = new TextView(requireContext());
                tvEmpty.setText("No genre data available yet.");
                tvEmpty.setTextColor(requireContext().getColor(R.color.text_secondary));
                binding.llGenresContainer.addView(tvEmpty);
            } else {
                int maxCount = 0;
                for (UserStats.GenreCount gc : topGenres) {
                    if (gc.count > maxCount) maxCount = gc.count;
                }

                for (UserStats.GenreCount gc : topGenres) {
                    View itemView = LayoutInflater.from(requireContext())
                            .inflate(R.layout.item_stat_genre, binding.llGenresContainer, false);

                    TextView tvName = itemView.findViewById(R.id.tvGenreName);
                    TextView tvCount = itemView.findViewById(R.id.tvGenreCount);
                    ProgressBar pb = itemView.findViewById(R.id.pbGenre);

                    tvName.setText(gc.genre);
                    tvCount.setText(String.valueOf(gc.count));
                    pb.setMax(maxCount);
                    pb.setProgress(gc.count);

                    binding.llGenresContainer.addView(itemView);
                }
            }
        });
        
        viewModel.observeLibrary(getViewLifecycleOwner());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
