package com.malist.app.ui.onboarding;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

public class OnboardingItem {
    @DrawableRes
    public final int imageRes;
    @StringRes
    public final int titleRes;
    @StringRes
    public final int descRes;

    public OnboardingItem(@DrawableRes int imageRes, @StringRes int titleRes, @StringRes int descRes) {
        this.imageRes = imageRes;
        this.titleRes = titleRes;
        this.descRes = descRes;
    }
}
