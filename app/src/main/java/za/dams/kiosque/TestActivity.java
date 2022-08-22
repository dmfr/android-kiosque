package za.dams.kiosque;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import androidx.fragment.app.FragmentActivity ;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.UUID;

import za.dams.kiosque.util.TracyPodTransactionManager;

public class TestActivity extends FragmentActivity {

    ViewPager mViewPager ;

    private class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 3;
        private String tabTitles[] = new String[] { "Scan", "Photos", "Signature" };
        private Context context;

        public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            if( position==0 ){
                return TestScanFragment.newInstance("pouet","pouet");
            }
            if( position==1 ){
                return TestGalleryFragment.newInstance("pouet","pouet");
            }
            if( position==2 ){
                return TestFormFragment.newInstance("pouet","pouet");
            }
            return DummyFragment.newInstance("pouet","pouet");
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Log.w("DAMS","TestActivity create") ;
        TracyPodTransactionManager tracyPod = TracyPodTransactionManager.getInstance(this) ;
        UUID tracyPodUUID = tracyPod.getTransactionUUID();
        if( tracyPodUUID != null ) {
            Log.w("DAMS", "UUID is " + tracyPodUUID.toString());
        } else {
            Log.w("DAMS", "No transaction UUID");
        }

        setContentView(R.layout.activity_test);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(),
                TestActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);


        Log.w("DAMS",""+getSupportFragmentManager().getFragments().size()) ; ;


        //mViewPager.getAdapter().get
        /*
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + mViewPager.getCurrentItem());
        Log.w("DAMS",""+page.getClass().toString()) ;
        if( page instanceof TestScanFragment ) {

        }
         */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tracypod, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch( item.getItemId() ) {
            case R.id.action_test:
                actionTest();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private Fragment getCurrentFragment() {
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + mViewPager.getCurrentItem());
        return page ;
    }
    private void actionTest() {
        Fragment page = getCurrentFragment() ;
        if( page != null && page instanceof TestScanFragment ) {
            Log.w("DAMS","TestScanFragment !!!!") ;
            ((TestScanFragment) page).addDummy();
        }

    }










}