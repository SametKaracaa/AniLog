package com.malist.app.ui.swipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.malist.app.R;
import com.malist.app.data.model.AnimeModel;
import com.malist.app.util.FormatUtils;

import java.util.List;

public class CardStackAdapter extends ListAdapter<AnimeModel, CardStackAdapter.ViewHolder> {

    public CardStackAdapter() {
        super(new CardDiffCallback());
    }

    public List<AnimeModel> getList() {
        return getCurrentList();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_swipe_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnimeModel anime = getItem(position);
        String imageUrl = (anime.getLargeImageUrl() != null && !anime.getLargeImageUrl().isEmpty())
                ? anime.getLargeImageUrl()
                : anime.getImageUrl();
        FormatUtils.loadImage(holder.image, imageUrl);
        holder.title.setText(anime.getTitle());
        holder.score.setText(FormatUtils.formatScore(anime.getScore()));
        holder.type.setText(anime.getType());
        holder.genres.setText(anime.getGenres());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView title;
        final TextView score;
        final TextView type;
        final TextView genres;

        public ViewHolder(@NonNull View view) {
            super(view);
            image = view.findViewById(R.id.ivSwipeImage);
            title = view.findViewById(R.id.tvSwipeTitle);
            score = view.findViewById(R.id.tvSwipeScore);
            type = view.findViewById(R.id.tvSwipeType);
            genres = view.findViewById(R.id.tvSwipeGenres);
        }
    }

    static class CardDiffCallback extends DiffUtil.ItemCallback<AnimeModel> {
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
