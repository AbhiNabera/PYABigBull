package nabera.ranjan.abhinabera.pyabigbull.Dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.elconfidencial.bubbleshowcase.BubbleShowCase;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseSequence;
import nabera.ranjan.abhinabera.pyabigbull.R;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity{

    BottomNavigationView bottomNavigationView;
    BottomNavigationItemView navData, navUser, navPortfolio, navleaderBoard;

    public ViewPager viewPager;

    DataFragment dataFragment;
    UserDataFragment userDataFragment;
    StandingsFragment leaderBoardFragment;
    PortofolioFragment portofolioFragment;
    MenuItem prevMenuItem;

    static AppCompatActivity appCompatActivity;

    private static final String SHOWCASE_ID = "showcase";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        appCompatActivity = MainActivity.this;

        SharedPreferences prefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        boolean haveWeShownPreferences = prefs.getBoolean("HaveShownPrefs", false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);

        navData = (BottomNavigationItemView) findViewById(R.id.navigation_data);
        navPortfolio = (BottomNavigationItemView) findViewById(R.id.navigation_portfolio);
        navUser = (BottomNavigationItemView) findViewById(R.id.navigation_userdata);
        navleaderBoard = (BottomNavigationItemView) findViewById(R.id.navigation_leaderboard);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_data:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.navigation_portfolio:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.navigation_userdata:
                                viewPager.setCurrentItem(2);
                                break;
                            case R.id.navigation_leaderboard:
                                viewPager.setCurrentItem(3);
                                break;
                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);

        BubbleShowCaseBuilder first = new BubbleShowCaseBuilder(this);
        first.title("LIVE")
                .description("Invest or buy here.")
                .arrowPosition(BubbleShowCase.ArrowPosition.BOTTOM)
                .backgroundColor(Color.GREEN)
                .imageResourceId(R.drawable.purchase)
                .closeActionImageResourceId(R.drawable.ic_buble_close)
                .textColor(Color.BLACK)
                .titleTextSize(17)
                .descriptionTextSize(15)
                .targetView(navData);


        BubbleShowCaseBuilder second = new BubbleShowCaseBuilder(this);
        second.title("YOUR PORTFOLIO")
                .description("Sell here.")
                .arrowPosition(BubbleShowCase.ArrowPosition.BOTTOM)
                .backgroundColor(Color.GREEN)
                .textColor(Color.BLACK)
                .imageResourceId(R.drawable.sell)
                .closeActionImageResourceId(R.drawable.ic_buble_close)
                .titleTextSize(17)
                .descriptionTextSize(15)
                .targetView(navPortfolio);

        BubbleShowCaseBuilder third = new BubbleShowCaseBuilder(this);
        third.title("RULES")
                .description("Please read the playing conditions available here before starting.")
                .arrowPosition(BubbleShowCase.ArrowPosition.BOTTOM)
                .backgroundColor(Color.GREEN)
                .textColor(Color.BLACK)
                .imageResourceId(R.drawable.conditions)
                .closeActionImageResourceId(R.drawable.ic_buble_close)
                .titleTextSize(17)
                .descriptionTextSize(15)
                .targetView(navUser);

        BubbleShowCaseBuilder fourth = new BubbleShowCaseBuilder(this);
        fourth.description("Pull down to refresh and update the data.")
                .backgroundColor(Color.GREEN)
                .textColor(Color.BLACK)
                .imageResourceId(R.drawable.refresh)
                .closeActionImageResourceId(R.drawable.ic_buble_close)
                .descriptionTextSize(15);


        if (!haveWeShownPreferences) {
            BubbleShowCaseSequence sequence = new BubbleShowCaseSequence();
            sequence.addShowCase(first).addShowCase(second).addShowCase(third).addShowCase(fourth).show();
            SharedPreferences.Editor ed = prefs.edit();
            ed.putBoolean("HaveShownPrefs", true);
            ed.commit();
        } else {
            // we have already shown the preferences activity before
        }



    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        dataFragment = new DataFragment();
        portofolioFragment = new PortofolioFragment();
        userDataFragment=new UserDataFragment();
        leaderBoardFragment = new StandingsFragment();
        adapter.addFragment(dataFragment);
        adapter.addFragment(portofolioFragment);
        adapter.addFragment(userDataFragment);
        adapter.addFragment(leaderBoardFragment);
        viewPager.setOffscreenPageLimit(adapter.getCount() - 1);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

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

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }


}