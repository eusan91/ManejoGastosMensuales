package com.santamaria.manejogastosmensuales.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.santamaria.manejogastosmensuales.Fragments.HistoryFragment;
import com.santamaria.manejogastosmensuales.Fragments.LastMonthFragment;
import com.santamaria.manejogastosmensuales.Fragments.MainFragment;

/**
 * Created by Santamaria on 08/04/2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter{

    private int numberOfTabs;

    public PagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);

        this.numberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0:
                return new LastMonthFragment();
            case 1:
                return new MainFragment();
            case 2:
                return new HistoryFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
