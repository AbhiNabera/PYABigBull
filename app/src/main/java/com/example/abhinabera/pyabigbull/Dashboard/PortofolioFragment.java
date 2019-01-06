package com.example.abhinabera.pyabigbull.Dashboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.abhinabera.pyabigbull.Dashboard.DataFragment;
import com.example.abhinabera.pyabigbull.Dashboard.LeaderBoardFragment;
import com.example.abhinabera.pyabigbull.Dashboard.MainActivity;
import com.example.abhinabera.pyabigbull.Dashboard.UserDataFragment;
import com.example.abhinabera.pyabigbull.R;
import com.example.abhinabera.pyabigbull.UserActivities.Userstocks.BoughtFragment;
import com.example.abhinabera.pyabigbull.UserActivities.Userstocks.SoldFragment;
import com.example.abhinabera.pyabigbull.UserActivities.Userstocks.UserStocks;

import java.util.ArrayList;
import java.util.List;

public class PortofolioFragment extends Fragment {

    BoughtFragment boughtFragment;
    SoldFragment soldFragment;

    ViewPager investmentViewPager;
    TabLayout investmentTabs;

    public static PortofolioFragment newInstance() {
        return new PortofolioFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_portofolio, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        investmentViewPager = (ViewPager) view.findViewById(R.id.investmentViewpager);
        setupViewPager(investmentViewPager);

        investmentTabs = (TabLayout) view.findViewById(R.id.investmentTabs);
        investmentTabs.setupWithViewPager(investmentViewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new BoughtFragment(), "BOUGHT");
        adapter.addFragment(new SoldFragment(), "SOLD");
        viewPager.setOffscreenPageLimit(adapter.getCount() - 1);
        viewPager.setAdapter(adapter);
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {
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


}
