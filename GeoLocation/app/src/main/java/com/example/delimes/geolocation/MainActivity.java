package com.example.delimes.geolocation;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ////////////////////////////////////////////////////////////////////
        ViewPager pager=(ViewPager)findViewById(R.id.pager);
        pager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager()));
        ///////////////////////////////////////////////////////////////////


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }




    public class MyFragmentAdapter extends FragmentPagerAdapter {
        public MyFragmentAdapter(FragmentManager mgr) {
            super(mgr);
        }
        @Override
        public int getCount() {
            return(2);
        }
        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            if(position == 0) {
                return (PageFragment.newInstance(position));
            }else{
                return (PageFragment2.newInstance(position));
            }
        }
    }


}

