package com.malist.app.util;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.malist.app.R;

/**
 * Utility class replacing Kotlin extension functions.
 */
public final class FormatUtils {

    private FormatUtils() {}

    public static void loadImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .placeholder(R.drawable.shimmer_placeholder)
                .error(R.drawable.shimmer_placeholder)
                .centerCrop()
                .into(imageView);
    }

    public static String formatScore(double score) {
        return score > 0 ? String.format("%.1f", score) : "N/A";
    }

    public static String formatEpisodes(int episodes) {
        return episodes > 0 ? episodes + " Ep" : "? Ep";
    }

    public static String formatChapters(int chapters) {
        return chapters > 0 ? chapters + " Ch" : "? Ch";
    }
}
