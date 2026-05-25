package com.malist.app.ui.detail;

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
import com.malist.app.data.api.JikanModels.JikanCharacterData;
import com.malist.app.data.api.JikanModels.JikanVoiceActor;
import com.malist.app.util.FormatUtils;

public class CharacterAdapter extends ListAdapter<JikanCharacterData, CharacterAdapter.ViewHolder> {

    public CharacterAdapter() {
        super(new CharacterDiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_character_seiyuu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivCharacterImage;
        private final ImageView ivSeiyuuImage;
        private final TextView tvCharacterName;
        private final TextView tvCharacterRole;
        private final TextView tvSeiyuuName;
        private final TextView tvSeiyuuLanguage;

        public ViewHolder(@NonNull View view) {
            super(view);
            ivCharacterImage = view.findViewById(R.id.ivCharacterImage);
            ivSeiyuuImage = view.findViewById(R.id.ivSeiyuuImage);
            tvCharacterName = view.findViewById(R.id.tvCharacterName);
            tvCharacterRole = view.findViewById(R.id.tvCharacterRole);
            tvSeiyuuName = view.findViewById(R.id.tvSeiyuuName);
            tvSeiyuuLanguage = view.findViewById(R.id.tvSeiyuuLanguage);
        }

        public void bind(JikanCharacterData data) {
            if (data.character != null && data.character.images != null && data.character.images.jpg != null) {
                FormatUtils.loadImage(ivCharacterImage, data.character.images.jpg.imageUrl);
            }
            if (data.character != null) {
                tvCharacterName.setText(data.character.name);
            }
            tvCharacterRole.setText(data.role != null ? data.role : "Unknown");

            JikanVoiceActor selectedActor = null;
            if (data.voiceActors != null && !data.voiceActors.isEmpty()) {
                for (JikanVoiceActor actor : data.voiceActors) {
                    if ("Japanese".equals(actor.language)) {
                        selectedActor = actor;
                        break;
                    }
                }
                if (selectedActor == null) {
                    selectedActor = data.voiceActors.get(0);
                }
            }

            if (selectedActor != null && selectedActor.person != null) {
                ivSeiyuuImage.setVisibility(View.VISIBLE);
                if (selectedActor.person.images != null && selectedActor.person.images.jpg != null) {
                    FormatUtils.loadImage(ivSeiyuuImage, selectedActor.person.images.jpg.imageUrl);
                }
                tvSeiyuuName.setText(selectedActor.person.name);
                tvSeiyuuLanguage.setText(selectedActor.language);
            } else {
                ivSeiyuuImage.setVisibility(View.GONE);
                tvSeiyuuName.setText("No Voice Actor");
                tvSeiyuuLanguage.setText("");
            }
        }
    }

    static class CharacterDiffCallback extends DiffUtil.ItemCallback<JikanCharacterData> {
        @Override
        public boolean areItemsTheSame(@NonNull JikanCharacterData oldItem, @NonNull JikanCharacterData newItem) {
            if (oldItem.character == null || newItem.character == null) return false;
            return oldItem.character.malId == newItem.character.malId;
        }

        @Override
        public boolean areContentsTheSame(@NonNull JikanCharacterData oldItem, @NonNull JikanCharacterData newItem) {
            return oldItem.equals(newItem);
        }
    }
}
