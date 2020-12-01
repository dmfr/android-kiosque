/*
Logcat filter : ^(?:(?!eglCodecCommon).)*$
https://stackoverflow.com/questions/24187728/sticky-immersive-mode-disabled-after-soft-keyboard-shown
 */

package za.dams.kiosque;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;


import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Toolbar;


public class MainActivity extends Activity
implements FirstFragment.OnButtonClickedListener, FragmentManager.OnBackStackChangedListener {

    private final Handler mUpdateUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateUI();
        }
    };

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        getFragmentManager().addOnBackStackChangedListener(this);

        if (savedInstanceState == null) {
            FirstFragment firstFrag = new FirstFragment() ;

            FragmentTransaction ft = getFragmentManager().beginTransaction() ;
            ft.add(R.id.fragment_container, (Fragment)firstFrag);
            ft.commit();
            Log.w("DAMS","Adding fragment") ;
        }

        // Restore Fullscreen on keyboard hide
        setupKeyboardVisibilityListener() ;


        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Fragment currentBackStackFragment = getFragmentManager().findFragmentByTag("visible_fragment");
            boolean isWebSession = (currentBackStackFragment != null && currentBackStackFragment instanceof SecondFragment) ;
            if( isWebSession ) {
                SecondFragment sf = (SecondFragment)currentBackStackFragment ;
                sf.callJavascript() ;
            }
        }
        if (id == R.id.action_barcode) {
            scanBarcode();
        }
        if (id == R.id.action_fragment) {
            scanFragment();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        Log.w("DAMS","atacched fragment");
        if( fragment instanceof FirstFragment) {
            ((FirstFragment) fragment).setOnButtonClickedListener(this);
        }
    }

    @Override
    public void onButtonClicked(View clickedButton) {
        Log.w("DAMS","fragment button cliquer") ;
        SecondFragment firstFrag = new SecondFragment() ;

        FragmentTransaction ft = getFragmentManager().beginTransaction() ;
        ft.replace(R.id.fragment_container, (Fragment)firstFrag,"visible_fragment");
        ft.addToBackStack(null);
        //ft.addOnBackStackChangedListener(this) ;
        ft.commit();
        Log.w("DAMS","Adding fragment") ;
    }




    private void startWebSession(){
        // hide system bar
    }

    @Override
    public void onBackStackChanged() {
        updateUI();

    }


    private void delayedUpdateUI(int delayMillis) {
        mUpdateUiHandler.removeMessages(0);
        if( delayMillis >= 0 ) {
            mUpdateUiHandler.sendEmptyMessageDelayed(0, delayMillis);
        }
    }
    private void updateUI() {
        Fragment currentBackStackFragment = getFragmentManager().findFragmentByTag("visible_fragment");
        boolean isWebSession = (currentBackStackFragment != null && currentBackStackFragment instanceof SecondFragment) ;


        if( isWebSession ) {
            hideSystemUI() ;
        } else {
            showSystemUI() ;
        }
    }
    private void showSystemUI() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(0);

        ActionBar actionBar = getActionBar();
        //actionBar.show();
    }
    private void hideSystemUI() {
        // Prevent actionbar from disabling fullscreen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                // Low profile ?
                |View.SYSTEM_UI_FLAG_LOW_PROFILE);

        ActionBar actionBar = getActionBar();
        //actionBar.hide();
    }

    private void setupKeyboardVisibilityListener() {
        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityChanged(isShown);
            }
        });
    }
    public void onKeyboardVisibilityChanged(boolean visible) {
        Log.w("dams","keybord visible = "+visible) ;
        if( !visible ) {
            delayedUpdateUI(300);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // When the window loses focus (e.g. the action overflow is shown),
        // cancel any pending hide action. When the window gains focus,
        // hide the system UI.
        // DM : re-hide everything when actionbar menu hides
        if (hasFocus) {
            delayedUpdateUI(300);
        } else {
            delayedUpdateUI(-1);
        }
    }






    public void scanBarcode() {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.setPackage("com.google.zxing.client.android");
        startActivityForResult(intent, 0);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //here is where you get your result
                String barcode = intent.getStringExtra("SCAN_RESULT");
                Log.w("DAMS","scanned = "+barcode) ;
            }
        }
    }


    public void scanFragment() {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        ScanFragment newFragment = ScanFragment.newInstance("str1","str2");
        newFragment.show(ft, "dialog");
    }
}
