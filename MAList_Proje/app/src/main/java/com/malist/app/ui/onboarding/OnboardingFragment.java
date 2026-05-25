package com.malist.app.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.malist.app.R;
import com.malist.app.data.prefs.PreferencesManager;
import com.malist.app.databinding.FragmentOnboardingBinding;

import java.util.ArrayList;
import java.util.List;

public class OnboardingFragment extends Fragment {

    private FragmentOnboardingBinding binding;
    private ImageView[] dots;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<OnboardingItem> onboardingItems = new ArrayList<>();
        onboardingItems.add(new OnboardingItem(R.drawable.img_onboarding_discover, R.string.onboarding_title_1, R.string.onboarding_desc_1));
        onboardingItems.add(new OnboardingItem(R.drawable.img_onboarding_library, R.string.onboarding_title_2, R.string.onboarding_desc_2));
        onboardingItems.add(new OnboardingItem(R.drawable.img_onboarding_track, R.string.onboarding_title_3, R.string.onboarding_desc_3));

        dots = new ImageView[]{binding.dot1, binding.dot2, binding.dot3};

        OnboardingAdapter adapter = new OnboardingAdapter(onboardingItems);
        binding.viewPagerOnboarding.setAdapter(adapter);

        binding.viewPagerOnboarding.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                for (int i = 0; i < dots.length; i++) {
                    if (i == position) {
                        dots[i].setImageResource(R.drawable.dot_selected);
                    } else {
                        dots[i].setImageResource(R.drawable.dot_unselected);
                    }
                }

                if (position == onboardingItems.size() - 1) {
                    binding.btnNext.setText(getString(R.string.onboarding_start));
                    binding.btnSkip.setVisibility(View.INVISIBLE);
                } else {
                    binding.btnNext.setText(getString(R.string.onboarding_next));
                    binding.btnSkip.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            if (binding.viewPagerOnboarding.getCurrentItem() + 1 < onboardingItems.size()) {
                binding.viewPagerOnboarding.setCurrentItem(binding.viewPagerOnboarding.getCurrentItem() + 1);
            } else {
                finishOnboarding();
            }
        });

        binding.btnSkip.setOnClickListener(v -> finishOnboarding());
    }

    private void finishOnboarding() {
        PreferencesManager prefs = PreferencesManager.getInstance(requireContext());
        prefs.setFirstLaunch(false);

        NavHostFragment.findNavController(this).navigate(R.id.action_onboardingFragment_to_loginFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
