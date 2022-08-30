package za.dams.kiosque;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity ;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.util.UUID;

import za.dams.kiosque.util.TracyHttpRest;
import za.dams.kiosque.util.TracyPodTransactionManager;

public class TestActivity extends FragmentActivity implements TestFormFragment.SubmitListener {
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    ViewPager mViewPager ;
    private Thread asyncSanityCheker ;

    ProgressDialog mProgressDialog;


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
                return TestScanFragment.newInstance();
            }
            if( position==1 ){
                return TestGalleryFragment.newInstance();
            }
            if( position==2 ){
                return TestFormFragment.newInstance();
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
                    boolean connectionFailed = false ;
                    boolean checkResult = false ;
                    try {
                        checkResult = asyncSanityCheck() ;
                    } catch(Exception e) {
                        connectionFailed = true ;
                    }
                    if( isInterrupted() ) {
                        return ;
                    }
                    if( checkResult ) {
                        return;
                    }

                    final boolean fConnectionFailed = connectionFailed ;
                    Handler mainHandler = new Handler(getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if( fConnectionFailed ) {
                                quitDenied(false);
                                return ;
                            }
                            quitDenied(true);
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

        // HACK !!!! : test photos
        if( savedInstanceState == null ) {
            if( false ) {
                if (tracyPod.getArrPhotos().size() == 0) {
                    tracyPod.getArrPhotos().clear();
                    tracyPod.pushExamplePhotos();
                }
            }
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
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TestActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(TestActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
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


    @Override
    public void onAttachFragment(Fragment fragment) {
        // https://medium.com/better-programming/proper-fragment-communication-in-android-489fcac520b0
        //Log.w("DAMS","atacched fragment");
        if( fragment instanceof TestFormFragment ) {
            ((TestFormFragment) fragment).setListener(this);
        }

    }





    private boolean asyncSanityCheck() throws Exception {
        try {
            Thread.sleep(500);
        } catch( Exception e ) {

        }
        return TracyHttpRest.sendPing(this) ;
    }
    private void quitDenied( boolean isDenied ) {
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        String title = isDenied ? "Authentication error" : "Connection error" ;
        String caption = isDenied ? "This device is not authorized on currently defined service.\nPlease contact support with ANDROID_ID="+android_id : "Server unreachable" ;

        AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
        builder.setTitle(title)
                .setMessage(caption)
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

    @Override
    public void onSubmit() {
        doTransactionSubmit() ;
    }

    public void doTransactionSubmit() {
        mProgressDialog = ProgressDialog.show(
                this,
                "Sending transaction",
                "Please wait...",
                true);
        Thread transactionEnd = new Thread() {
            public void run() {
                boolean connectionFailed = false ;
                boolean checkResult = false ;

                //JSONObject jsonObject = TracyPodTransactionManager.getInstance(TestActivity.this).getFinalTransaction() ;
                //Log.w("DAMS",jsonObject.toString()) ;
                TracyHttpRest.sendFinalTransaction(TestActivity.this,TracyPodTransactionManager.getInstance(TestActivity.this)) ;

                Handler mainHandler = new Handler(getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        TestActivity.this.onTransactionEnd();
                    }
                };
                mainHandler.post(myRunnable);
            };
        };
        transactionEnd.start();

    }
    private void onTransactionEnd() {
        if( mProgressDialog != null ) {
            mProgressDialog.dismiss();
            mProgressDialog = null ;
        }
        finish();
    }

}