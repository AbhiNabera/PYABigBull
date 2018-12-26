package com.example.abhinabera.pyabigbull.DataActivities;

import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.Dashboard.ViewPagerAdapter;
import com.example.abhinabera.pyabigbull.NiftyOverviewFragment;
import com.example.abhinabera.pyabigbull.NiftyStocksFragment;
import com.example.abhinabera.pyabigbull.R;

import java.util.ArrayList;
import java.util.List;

public class NiftyActivity extends AppCompatActivity {

    private TabLayout niftyTabs;
    private ViewPager niftyViewPager;
    Typeface custom_font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_nifty);
        getSupportActionBar().hide();

        custom_font = ResourcesCompat.getFont(this, R.font.hammersmithone);

        niftyViewPager = (ViewPager) findViewById(R.id.niftyViewpager);
        setupViewPager(niftyViewPager);

        niftyTabs = (TabLayout) findViewById(R.id.niftyTabs);
        niftyTabs.setupWithViewPager(niftyViewPager);

        changeTabsFont();
    }

    private void changeTabsFont() {
        ViewGroup vg = (ViewGroup) niftyTabs.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(custom_font);
                }
            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NiftyOverviewFragment(), "OVERVIEW");
        adapter.addFragment(new NiftyStocksFragment(), "STOCKS");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.enter1, R.anim.exit1);
    }
}
