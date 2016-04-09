package lu.ing.gameofcode.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lu.ing.gameofcode.R;
import lu.ing.gameofcode.fragments.BusFragment;
import lu.ing.gameofcode.fragments.VelohFragment;

public class PlanningActivity extends AppCompatActivity {

    @Bind(R.id.sunny)
    RelativeLayout sunnyLayout;

    @Bind(R.id.rainy)
    RelativeLayout rainyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning);
        ButterKnife.bind(this);

        boolean raining = true;
        if (RandomUtils.nextInt(0, 100) > 50) {
            sunnyLayout.setVisibility(View.GONE);
            rainyLayout.setVisibility(View.VISIBLE);
        } else {
            sunnyLayout.setVisibility(View.VISIBLE);
            rainyLayout.setVisibility(View.GONE);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BusFragment(), "Bus");
        adapter.addFragment(new VelohFragment(), "Veloh");
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

}