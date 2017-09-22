package cl.oktech.wood.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import cl.oktech.wood.fragments.ClosedOrdersFragment;
import cl.oktech.wood.fragments.ToDoOrdersFragment;

/**
 * Created by cl on 20-07-17.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int mNumberOfTabs;


    public PagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        mNumberOfTabs = numberOfTabs;
    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new ToDoOrdersFragment();
            case 1:
                return new ClosedOrdersFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumberOfTabs;
    }
}
