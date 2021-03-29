package ru.finnapp.ui.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * Класс SectionsPagerAdapter предназначен для управления
 * вкладками (фрагментами).
 *
 */
public class SectionsPagerAdapter extends FragmentStateAdapter {
    private static final int ITEM_SIZE = 2;

    /**
     * Конструктор.
     *
     * @param fragmentActivity Активити для управления фрагментами.
     */
    public SectionsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return PlaceholderFragmentMain.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return ITEM_SIZE;
    }
}