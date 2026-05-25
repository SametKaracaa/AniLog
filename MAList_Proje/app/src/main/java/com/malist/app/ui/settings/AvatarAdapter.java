package com.malist.app.ui.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.malist.app.R;
import com.malist.app.util.FormatUtils;

import java.util.List;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder> {

    public interface OnAvatarSelectedListener {
        void onAvatarSelected(String avatarUrl);
    }

    private final List<String> avatars;
    private String selectedAvatar;
    private final OnAvatarSelectedListener onAvatarSelected;

    public AvatarAdapter(List<String> avatars, String selectedAvatar, OnAvatarSelectedListener onAvatarSelected) {
        this.avatars = avatars;
        this.selectedAvatar = selectedAvatar;
        this.onAvatarSelected = onAvatarSelected;
    }

    @NonNull
    @Override
    public AvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_avatar, parent, false);
        return new AvatarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvatarViewHolder holder, int position) {
        String avatarUrl = avatars.get(position);
        FormatUtils.loadImage(holder.ivAvatar, avatarUrl);
        holder.vBorder.setVisibility(avatarUrl.equals(selectedAvatar) ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return avatars.size();
    }

    class AvatarViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivAvatar;
        final View vBorder;

        public AvatarViewHolder(@NonNull View view) {
            super(view);
            ivAvatar = view.findViewById(R.id.ivAvatarImage);
            vBorder = view.findViewById(R.id.vSelectionBorder);

            view.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    int prevSelected = avatars.indexOf(selectedAvatar);
                    selectedAvatar = avatars.get(position);
                    notifyItemChanged(prevSelected);
                    notifyItemChanged(position);
                    if (onAvatarSelected != null) {
                        onAvatarSelected.onAvatarSelected(selectedAvatar);
                    }
                }
            });
        }
    }
}
