package com.malist.app.ui.discover;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.malist.app.R;
import com.malist.app.data.model.AnimeModel;
import com.malist.app.databinding.ItemAnimeCardBinding;
import com.malist.app.util.FormatUtils;

public class AnimeAdapter extends ListAdapter<AnimeModel, AnimeAdapter.AnimeViewHolder> {

    public interface OnAnimeClickListener {
        void onAnimeClick(AnimeModel anime);
    }

    private final OnAnimeClickListener clickListener;

    public AnimeAdapter(OnAnimeClickListener clickListener) {
        super(new AnimeDiffCallback());
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public AnimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAnimeCardBinding binding = ItemAnimeCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new AnimeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeViewHolder holder, int position) {
        holder.bind(getItem(position));
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(
                holder.itemView.getContext(), R.anim.slide_up
        ));
    }

    class AnimeViewHolder extends RecyclerView.ViewHolder {
        private final ItemAnimeCardBinding binding;

        public AnimeViewHolder(@NonNull ItemAnimeCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AnimeModel anime) {
            FormatUtils.loadImage(binding.ivAnimePoster, anime.getImageUrl());
            binding.tvAnimeTitle.setText(anime.getTitle());
            binding.tvScore.setText(FormatUtils.formatScore(anime.getScore()));
            binding.tvEpisodes.setText(anime.getIsManga() ? FormatUtils.formatChapters(anime.getChapters()) : FormatUtils.formatEpisodes(anime.getEpisodes()));
            binding.tvType.setText(anime.getType());

            binding.getRoot().setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onAnimeClick(anime);
                }
            });
        }
    }

    static class AnimeDiffCallback extends DiffUtil.ItemCallback<AnimeModel> {
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
