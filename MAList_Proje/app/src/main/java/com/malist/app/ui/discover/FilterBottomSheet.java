package com.malist.app.ui.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.malist.app.R;
import com.malist.app.data.model.AnimeGenre;
import com.malist.app.data.model.AnimeOrderBy;
import com.malist.app.data.model.AnimeSortOrder;
import com.malist.app.databinding.BottomSheetFilterBinding;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilterBottomSheet extends BottomSheetDialogFragment {

    public interface OnFilterSelectedListener {
        void onFilterSelected(Set<String> genres, String orderBy, String sortOrder);
    }

    private BottomSheetFilterBinding binding;
    private final Set<String> currentSelectedGenres;
    private final String currentOrderBy;
    private final String currentSortOrder;
    private final OnFilterSelectedListener onFilterSelected;

    public FilterBottomSheet(Set<String> currentSelectedGenres, String currentOrderBy, String currentSortOrder, OnFilterSelectedListener onFilterSelected) {
        this.currentSelectedGenres = currentSelectedGenres;
        this.currentOrderBy = currentOrderBy;
        this.currentSortOrder = currentSortOrder;
        this.onFilterSelected = onFilterSelected;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        for (AnimeGenre genre : AnimeGenre.values()) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.item_chip, binding.chipGroupFilter, false);
            chip.setText(genre.getDisplayName());
            chip.setTag(genre.getId());
            chip.setChecked(currentSelectedGenres.contains(genre.getId()));
            binding.chipGroupFilter.addView(chip);
        }

        if (AnimeOrderBy.POPULARITY.getValue().equals(currentOrderBy)) {
            binding.chipGroupSort.check(R.id.chipSortPopularity);
        } else if (AnimeOrderBy.DATE.getValue().equals(currentOrderBy)) {
            binding.chipGroupSort.check(R.id.chipSortDate);
        } else {
            binding.chipGroupSort.check(R.id.chipSortScore);
        }

        if (AnimeSortOrder.ASC.getValue().equals(currentSortOrder)) {
            binding.chipGroupOrder.check(R.id.chipOrderAsc);
        } else {
            binding.chipGroupOrder.check(R.id.chipOrderDesc);
        }

        binding.btnClear.setOnClickListener(v -> {
            binding.chipGroupFilter.clearCheck();
            binding.chipGroupSort.check(R.id.chipSortScore);
            binding.chipGroupOrder.check(R.id.chipOrderDesc);
            if (onFilterSelected != null) {
                onFilterSelected.onFilterSelected(new HashSet<>(), AnimeOrderBy.SCORE.getValue(), AnimeSortOrder.DESC.getValue());
            }
            dismiss();
        });

        binding.btnApply.setOnClickListener(v -> {
            Set<String> selectedGenres = new HashSet<>();
            List<Integer> checkedIds = binding.chipGroupFilter.getCheckedChipIds();
            for (int id : checkedIds) {
                Chip chip = binding.chipGroupFilter.findViewById(id);
                if (chip != null && chip.getTag() instanceof String) {
                    selectedGenres.add((String) chip.getTag());
                }
            }

            String selectedOrderBy = AnimeOrderBy.SCORE.getValue();
            int checkedSortId = binding.chipGroupSort.getCheckedChipId();
            if (checkedSortId == R.id.chipSortPopularity) {
                selectedOrderBy = AnimeOrderBy.POPULARITY.getValue();
            } else if (checkedSortId == R.id.chipSortDate) {
                selectedOrderBy = AnimeOrderBy.DATE.getValue();
            }

            String selectedSortOrder = AnimeSortOrder.DESC.getValue();
            if (binding.chipGroupOrder.getCheckedChipId() == R.id.chipOrderAsc) {
                selectedSortOrder = AnimeSortOrder.ASC.getValue();
            }

            if (onFilterSelected != null) {
                onFilterSelected.onFilterSelected(selectedGenres, selectedOrderBy, selectedSortOrder);
            }
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
