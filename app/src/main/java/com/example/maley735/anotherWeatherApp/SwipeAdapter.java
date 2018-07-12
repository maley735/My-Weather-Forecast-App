package com.example.maley735.anotherWeatherApp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SwipeAdapter extends FragmentPagerAdapter {

    public SwipeAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public CharSequence getPageTitle(int position){
        //names of the Swipe Pages
        CharSequence res;
        if(position == 0){
            res = "Today";
        } else if(position == 1){
            res= "Tomorrow";
        } else{
            res = "" + (position) + " Days Later";
        }

        return res;
    }

    @Override
    public Fragment getItem(int position) {
        //every fragment has their page number in their bundle to be exrtacted in FragmentPage.java
        Fragment fragment = new FragmentPage();
        Bundle bundle = new Bundle();
        bundle.putInt("count", position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        //returns Swipe Page Number
        return MainActivity.NUMBER_OF_DATES;
    }
}
