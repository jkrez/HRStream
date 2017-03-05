package com.streamteam.pushpoint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import layout.AgeFragment;
import layout.HeartRateFragment;
import layout.UsernameFragment;
import layout.Welcome2;

import static com.streamteam.pushpoint.Helpers.isWhitespace;

public class FirstLoginActivity extends FragmentActivity {

    public boolean ReadyToWorkout = false;
    public ViewPager MyPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login);

        this.MyPager = (ViewPager) findViewById(R.id.viewPager);
        this.MyPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        if (!isWhitespace(AppSettings.Settings.getString(Constants.AgeKey, "")) &&
            !isWhitespace(AppSettings.Settings.getString(Constants.HrKey, "")) &&
            !isWhitespace(AppSettings.Settings.getString(Constants.NameKey, ""))) {

            Intent intent = new Intent(this, WorkoutActivity.class);
            startActivity(intent);
            Activity a = this;
            if (a != null) {
                a.finish();
            }
        }
    }

    public void NextFragment() {
        this.MyPager.setCurrentItem(this.MyPager.getCurrentItem() + 1);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public int Position = 0;
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            this.Position = pos;
            switch(pos) {

                case 0: return Welcome2.newInstance("FirstFragment, Instance 1");
                case 1: return UsernameFragment.newInstance("SecondFragment, Instance 1");
                case 2: return AgeFragment.newInstance("ThirdFragment, Instance 1");
                case 3: return HeartRateFragment.newInstance("ThirdFragment, Instance 2");
                default: return Welcome2.newInstance("ThirdFragment, Default");
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}