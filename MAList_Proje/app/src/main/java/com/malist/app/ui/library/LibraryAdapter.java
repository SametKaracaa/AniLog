package com.malist.app.ui.library;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.malist.app.R;
import com.malist.app.data.model.AnimeModel;
import com.malist.app.data.model.WatchStatus;
import com.malist.app.databinding.ItemLibraryAnimeBinding;
import com.malist.app.util.FormatUtils;

public class LibraryAdapter extends ListAdapter<AnimeModel, LibraryAdapter.LibraryViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(AnimeModel anime);
    }

    public interface OnRemoveClickListener {
        void onRemoveClick(AnimeModel anime);
    }

    private final OnRemoveClickListener onRemoveClick;
    private final OnItemClickListener onItemClick;

    public LibraryAdapter(OnRemoveClickListener onRemoveClick, OnItemClickListener onItemClick) {
        super(new LibraryDiffCallback());
        this.onRemoveClick = onRemoveClick;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLibraryAnimeBinding binding = ItemLibraryAnimeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new LibraryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewHolder holder, int position) {
        holder.bind(getItem(position));
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(
                holder.itemView.getContext(), R.anim.slide_up
        ));
    }

    class LibraryViewHolder extends RecyclerView.ViewHolder {
        private final ItemLibraryAnimeBinding binding;

        public LibraryViewHolder(@NonNull ItemLibraryAnimeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AnimeModel anime) {
            FormatUtils.loadImage(binding.ivLibraryPoster, anime.getImageUrl());
            binding.tvLibraryTitle.setText(anime.getTitle());
            WatchStatus status = WatchStatus.fromString(anime.getWatchStatus());

            if (status == WatchStatus.COMPLETED && anime.getUserRating() > 0f) {
                binding.tvLibraryScore.setText("★ " + anime.getUserRating());
                binding.tvLibraryScore.setTextColor(itemView.getContext().getColor(R.color.accent_primary));
            } else {
                binding.tvLibraryScore.setText(FormatUtils.formatScore(anime.getScore()));
                binding.tvLibraryScore.setTextColor(itemView.getContext().getColor(R.color.score_text));
            }

            binding.tvLibraryEpisodes.setText(anime.getIsManga() ? FormatUtils.formatChapters(anime.getChapters()) : FormatUtils.formatEpisodes(anime.getEpisodes()));

            binding.tvLibraryStatus.setText(status != null ? status.getDisplayName() : anime.getWatchStatus());
            int statusColor = R.color.text_secondary;
            if (status == WatchStatus.WATCHING) {
                statusColor = R.color.status_watching;
            } else if (status == WatchStatus.PLANNED) {
                statusColor = R.color.status_planned;
            } else if (status == WatchStatus.COMPLETED) {
                statusColor = R.color.status_completed;
            }
            binding.tvLibraryStatus.setTextColor(itemView.getContext().getColor(statusColor));

            binding.btnRemove.setOnClickListener(v -> {
                if (onRemoveClick != null) onRemoveClick.onRemoveClick(anime);
            });
            binding.getRoot().setOnClickListener(v -> {
                if (onItemClick != null) onItemClick.onItemClick(anime);
            });
        }
    }

    static class LibraryDiffCallback extends DiffUtil.ItemCallback<AnimeModel> {
        @Override
        public boolean areItemsTheSame(@NonNull AnimeModel oldItem, @NonNull AnimeModel newItem) {
            return oldItem.getMalId() == newItem.getMalId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull AnimeModel oldItem, @NonNull AnimeModel newItem) {
            return oldItem.equals(newItem);
        }
    }
}
