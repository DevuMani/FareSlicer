package com.example.dream.fareslicer.AdapterClasses;

import android.content.Context;
import android.os.Bundle;

import com.example.dream.fareslicer.HomeFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * Created by user on 01-06-2018.
 */

public class HomePagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    Context context;

    public HomePagerAdapter(FragmentManager fm, int NumOfTabs, Context context) {
        super(fm);

        this.mNumOfTabs=NumOfTabs;
        this.context=context;
    }

    @Override
    public Fragment getItem(int position) {


        Bundle bundle=new Bundle();
        switch (position) {
            case 0:
                bundle.putInt("tab",0);
                HomeFragment leaveFragment1=new HomeFragment();
                leaveFragment1.setArguments(bundle);

                return leaveFragment1;

            case 1:

                bundle.putInt("tab",1);
                HomeFragment leaveFragment2=new HomeFragment();
                leaveFragment2.setArguments(bundle);

                return leaveFragment2;


            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
