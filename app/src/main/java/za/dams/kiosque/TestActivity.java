package za.dams.kiosque;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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

import za.dams.kiosque.util.TracyHttpRest;
import za.dams.kiosque.util.TracyPodTransactionManager;

public class TestActivity extends FragmentActivity {

    ViewPager mViewPager ;
    private Thread asyncSanityCheker ;

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

        if( savedInstanceState == null ) {
            Log.w("DAMS", "AsyncCheck !!!");
            // Async check
            asyncSanityCheker = new Thread() {
                public void run() {
                    boolean checkResult = asyncSanityCheck() ;
                    if( isInterrupted() ) {
                        return ;
                    }
                    if( checkResult ) {
                        return;
                    }

                    Handler mainHandler = new Handler(getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            quitDenied();
                        }
                    };
                    mainHandler.post(myRunnable);
                };
            };
            asyncSanityCheker.start();
        }

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
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
    protected void onDestroy() {
        if( asyncSanityCheker != null ) {
            asyncSanityCheker.interrupt();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tracypod, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isScanFragment = (getCurrentFragment() instanceof TestScanFragment) ;
        menu.findItem(R.id.action_scaninput).setVisible( isScanFragment );
        menu.findItem(R.id.action_scandummy).setVisible( isScanFragment );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch( item.getItemId() ) {
            case R.id.action_scaninput:
                actionScanInput();
                break;
            case R.id.action_scandummy:
                actionScanDummy();
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
    private void actionScanInput() {
        Fragment page = getCurrentFragment() ;
        if( page != null && page instanceof TestScanFragment ) {
            ((TestScanFragment) page).openInputDialog();
        }
    }
    private void actionScanDummy() {
        Fragment page = getCurrentFragment() ;
        if( page != null && page instanceof TestScanFragment ) {
            ((TestScanFragment) page).addDummy();
        }

    }







    private boolean asyncSanityCheck() {
        try {
            Thread.sleep(500);
        } catch( Exception e ) {

        }
        return TracyHttpRest.sendPing(this) ;
    }
    private void quitDenied() {
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
        builder.setTitle("Authentication error")
                .setMessage("This device is not authorized on currently defined service.\nPlease contact support with ANDROID_ID="+android_id)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        TestActivity.this.finish() ;
                        return ;
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


}