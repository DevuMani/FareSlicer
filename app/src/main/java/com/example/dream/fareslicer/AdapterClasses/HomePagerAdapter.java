package com.example.dream.fareslicer.AdapterClasses;

import android.content.Context;
import android.os.Bundle;

import com.example.dream.fareslicer.FragmentClasses.GroupFragment;
import com.example.dream.fareslicer.FragmentClasses.TransactionFragment;

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

        switch (position) {
            case 0:

                TransactionFragment transactionFragment=new TransactionFragment();
                return transactionFragment;

            case 1:

                 GroupFragment groupFragment=new GroupFragment();
                 return groupFragment;


            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
