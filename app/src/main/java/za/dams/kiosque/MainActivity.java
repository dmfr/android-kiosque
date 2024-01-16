/*
Logcat filter : ^(?:(?!eglCodecCommon).)*$
https://stackoverflow.com/questions/24187728/sticky-immersive-mode-disabled-after-soft-keyboard-shown
 */

package za.dams.kiosque;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import za.dams.kiosque.util.LinksManager;
import za.dams.kiosque.util.TracyPodTransactionManager;


public class MainActivity extends Activity
implements FragmentManager.OnBackStackChangedListener
        , ScanFragment.ScanListener
        , SignatureFragment.SignatureListener
        , LinkListFragment.LinkListActionListener
        , SettingZoomFragment.OnZoomFactorChangerListener
        , WebFragment.WebListener
{

    private final Handler mUpdateUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateUI();
        }
    };
    boolean mKeyboardVisible ;

    private MenuItem mCheckToolbar ;

    protected ProgressDialog mProgressDialog;

    @Override
    public void onBackPressed() {
        Fragment currentBackStackFragment = getFragmentManager().findFragmentByTag("visible_fragment");
        boolean isWebSession = (currentBackStackFragment != null && currentBackStackFragment instanceof WebFragment) ;
        if( isWebSession ) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage("Quit web session ?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                           MainActivity.super.onBackPressed();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
            return ;
        }
        super.onBackPressed();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        getFragmentManager().addOnBackStackChangedListener(this);

        if (savedInstanceState == null) {
            LinkListFragment firstFrag = new LinkListFragment() ;
            firstFrag.setLinkListActionListener(this);

            FragmentTransaction ft = getFragmentManager().beginTransaction() ;
            ft.add(R.id.fragment_container, (Fragment)firstFrag, "visible_fragment");
            ft.commit();
            //Log.w("DAMS","Adding fragment") ;
        }

        // Restore Fullscreen on keyboard hide
        setupKeyboardVisibilityListener() ;


        updateUI();


        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getResources().getString(R.string.activity_intent_filter_action));
        registerReceiver(myBroadcastReceiver, filter);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mCheckToolbar = menu.findItem(R.id.check_toolbar) ;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch( id ) {
            case R.id.check_toolbar :
                item.setChecked( !item.isChecked() ) ;
                break ;
        }


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_setting_zoom) {
            openSettingZoom() ;
        }
        if (id == R.id.action_zxing) {
            scanFragment();
        }
        if (id == R.id.action_signature) {
            signatureFragment();
        }
        if (id == R.id.action_test) {
            //testFragment();
            testActivity();
        }
        if (id == R.id.action_people) {
            //testFragment();
            peopleActivity();
        }
        if (id == R.id.action_print) {
            //testFragment();
            doBluetoothPrint();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        // https://medium.com/better-programming/proper-fragment-communication-in-android-489fcac520b0
        //Log.w("DAMS","atacched fragment");
        if( fragment instanceof ScanFragment ) {
            ((ScanFragment) fragment).setListener(this);
        }
        if( fragment instanceof SignatureFragment ) {
            ((SignatureFragment) fragment).setListener(this);
        }
        if( fragment instanceof LinkListFragment ) {
            ((LinkListFragment) fragment).setLinkListActionListener(this);
        }
        if( fragment instanceof LinkAddFragment ) {
            //((LinkAddFragment) fragment).setLinkListActionListener(this);
        }
        if( fragment instanceof SettingZoomFragment ) {
            ((SettingZoomFragment)fragment).setListener(this);
        }
        if( fragment instanceof WebFragment ) {
            ((WebFragment)fragment).setListener(this) ;
        }
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
        boolean isWebSession = (currentBackStackFragment != null && currentBackStackFragment instanceof WebFragment) ;


        if( isWebSession ) {
            if( mKeyboardVisible ) {
                showSystemUI(false) ;
            } else {
                hideSystemUI();
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            showSystemUI() ;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
    }
    private void showSystemUI() {
        showSystemUI(true);
    }
    private void showSystemUI(boolean showActionBar) {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(0);

        if( showActionBar ) {
            ActionBar actionBar = getActionBar();
            actionBar.show();
        }
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

        //CheckBox checkboxToolbar = (CheckBox)(findViewById(R.id.check_toolbar));
        boolean keepToolbarVisible = mCheckToolbar != null && mCheckToolbar.isChecked() ;
        if( !keepToolbarVisible ) {
            ActionBar actionBar = getActionBar();
            actionBar.hide();
        }
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
                    //Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityChanged(isShown);
            }
        });
    }
    public void onKeyboardVisibilityChanged(boolean visible) {
        if( true ) {
            mKeyboardVisible = visible ;
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







    public void openSettingZoom() {
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
        SettingZoomFragment newFragment = SettingZoomFragment.newInstance();
        newFragment.show(ft, "dialog");
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

    public void signatureFragment() {
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
        SignatureFragment newFragment = SignatureFragment.newInstance("str1","str2");
        newFragment.show(ft, "dialog");
    }

    public void testFragment() {

        TestFragment newFragment = TestFragment.newInstance("str1","str2");
        FragmentTransaction ft = getFragmentManager().beginTransaction() ;
        ft.replace(R.id.fragment_container, (Fragment)newFragment, "visible_fragment");
        ft.addToBackStack(null);
        ft.commit();
    }

    public void testActivity() {
        boolean directLaunch = true ;
        TracyPodTransactionManager tracyPod = TracyPodTransactionManager.getInstance(this) ;
        if( tracyPod.getTransactionUUID() != null ) {
            directLaunch = false ;
        }


        Intent intentLaunch = new Intent(this, TestActivity.class);

        if( directLaunch ) {
            startActivity(intentLaunch);
            return ;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Pending transaction. Resume or discard ?")
                .setCancelable(true)
                .setPositiveButton("Resume", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(intentLaunch);
                    }
                })
                .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TracyPodTransactionManager.purgeInstance(MainActivity.this);
                        startActivity(intentLaunch);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void peopleActivity() {
        Intent intentLaunch = new Intent(this, PeopleActivity.class);
        startActivity(intentLaunch);
    }



    @Override
    public void onScanResult(String scanResult) {
        //Log.w("DAMS","Scan result = "+scanResult);
        Fragment currentBackStackFragment = getFragmentManager().findFragmentByTag("visible_fragment");
        boolean isWebSession = (currentBackStackFragment != null && currentBackStackFragment instanceof WebFragment) ;
        if( isWebSession ) {
            WebFragment sf = (WebFragment)currentBackStackFragment ;
            sf.pushScanResult(scanResult);
        } else {
            Toast.makeText(this, "Scan : "+scanResult, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSignatureResult(String imgJpegBase64) {
        //Log.w("DAMS","Scan result = "+scanResult);
        Fragment currentBackStackFragment = getFragmentManager().findFragmentByTag("visible_fragment");
        boolean isWebSession = (currentBackStackFragment != null && currentBackStackFragment instanceof WebFragment) ;
        if( isWebSession ) {
            WebFragment sf = (WebFragment)currentBackStackFragment ;
            sf.pushSignature(imgJpegBase64);
        } else {
            Toast.makeText(this, "Signature : "+imgJpegBase64, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLinkClick(int linkIdx) {
        WebFragment firstFrag = WebFragment.newInstance( LinksManager.getLinkByIdx(this,linkIdx).getUrl() ) ;

        FragmentTransaction ft = getFragmentManager().beginTransaction() ;
        ft.replace(R.id.fragment_container, (Fragment)firstFrag,"visible_fragment");
        ft.addToBackStack(null);
        //ft.addOnBackStackChangedListener(this) ;
        ft.commit();
    }

    @Override
    public void onLinkAdd() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        Fragment currentBackStackFragment = getFragmentManager().findFragmentByTag("visible_fragment");
        LinkAddFragment newFragment = LinkAddFragment.newInstance(-1);
        if( currentBackStackFragment instanceof LinkListFragment ) {
            newFragment.setTargetFragment(currentBackStackFragment,LinkAddFragment.REQUEST_CODE);
        }
        newFragment.show(ft, "dialog");
    }

    @Override
    public void onLinkEdit(int linkIdx) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        Fragment currentBackStackFragment = getFragmentManager().findFragmentByTag("visible_fragment");
        LinkAddFragment newFragment = LinkAddFragment.newInstance(linkIdx);
        if( currentBackStackFragment instanceof LinkListFragment ) {
            newFragment.setTargetFragment(currentBackStackFragment,LinkAddFragment.REQUEST_CODE);
        }
        newFragment.show(ft, "dialog");
    }

    @Override
    public void onLinkDelete(int linkIdx) {
        LinksManager.deleteLinkAtIdx(this,linkIdx);

        // Create and show the dialog.
        Fragment currentBackStackFragment = getFragmentManager().findFragmentByTag("visible_fragment");
        LinkAddFragment newFragment = LinkAddFragment.newInstance(linkIdx);
        if( currentBackStackFragment instanceof LinkListFragment ) {
            // HACK to force reload (through ActivityResult)?
            currentBackStackFragment.onActivityResult(LinkAddFragment.REQUEST_CODE,LinkAddFragment.RESULT_SAVED,null);
        }
    }

    @Override
    public void onZoomFactorChanged(int zoomFactor) {
        // Create and show the dialog.
        Fragment currentBackStackFragment = getFragmentManager().findFragmentByTag("visible_fragment");
        if( currentBackStackFragment instanceof WebFragment) {
            //((SecondFragment)currentBackStackFragment).applyZoomFactor() ;
        }

    }




    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();

            if (action.equals(getResources().getString(R.string.activity_intent_filter_action))) {
                /*
                Log.w("DAMS",intent.getExtras().toString());
                for (String key : intent.getExtras().keySet())
                {
                    Log.d("Bundle Debug", key + " = \"" + intent.getExtras().get(key) + "\"");
                }
                 */

                String datawedgeKey = getResources().getString(R.string.datawedge_intent_key_data) ;
                String defaultKey = "data" ; //honeywell

                String decodedData = null ;
                if( intent.hasExtra(datawedgeKey) ) {
                    decodedData = intent.getStringExtra(datawedgeKey) ;
                } else {
                    decodedData = intent.getStringExtra(defaultKey) ;
                }
                //Log.w("DAMS",decodedData) ;
                try {
                    onScanResult(decodedData) ;
                } catch (Exception e) {}
            }
        }
    };

    @Override
    public void onSignatureOpen() {
        signatureFragment();
    }



    public void doBluetoothPrint() {
        String str = null ;
        new BluetoothPrintingTask(this).execute(str);
    }
    class BluetoothPrintingTask extends AsyncTask<String, Void, Void> {

        Context mContext ;
        BluetoothPrinting mBluetoothPrint ;

        public BluetoothPrintingTask(Context context) {
            mContext = context ;

        }


        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(
                    mContext,
                    "Sending transaction",
                    "Please wait...",
                    true);
        }

        /**
         * Actual download method.
         */
        @Override
        protected Void doInBackground(String... params) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            String zpl = "^XA^CF0,60^FO250,50^GB100,100,100^FS^FO275,75^FR^GB100,100,100^FS^FO93,93^GB40,40,40^FS^XZ" ;

            BluetoothPrinting btp = new BluetoothPrinting(mContext) ;
            btp.ListPairedDevices();
            btp.printString(zpl) ;

            return null ;
        }

        /**
         * Once the image is downloaded, associates it to the imageView
         */
        @Override
        protected void onPostExecute(Void v) {

            mProgressDialog.dismiss();
        }

    }

    private static class BluetoothPrinting {
        Context mContext ;
        BluetoothAdapter mBluetoothAdapter ;

        private UUID applicationUUID = UUID
                .fromString("00001101-0000-1000-8000-00805F9B34FB");

        BluetoothPrinting(Context context) {
            mContext = context ;
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        private BluetoothDevice ListPairedDevices() {
            Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                    .getBondedDevices();
            if (mPairedDevices.size() > 0) {
                for (BluetoothDevice mDevice : mPairedDevices) {
                    Log.v("DAMS", "PairedDevices: " + mDevice.getName() + "  "
                            + mDevice.getAddress());
                }
            }
            if( mPairedDevices.size()==1 ) {
                return mPairedDevices.iterator().next() ;
            }
            return null ;
        }

        private boolean printString( String zpl ) {
            BluetoothDevice btd = ListPairedDevices() ;
            if( btd == null ) {
                return false;
            }

            try {
            BluetoothSocket bts = btd
                    .createRfcommSocketToServiceRecord(applicationUUID);
                //mBluetoothAdapter.cancelDiscovery() ;

                bts.connect();
                OutputStream os = bts.getOutputStream();
                byte[] b = zpl.getBytes("UTF-8") ;
                Log.w("DAMS","b is len="+b.length) ;
                os.write(b);
                Thread.sleep(500);
                bts.close();

                Log.w("DAMS","Printing done !");


                return true ;
            } catch(Exception e) {
                e.printStackTrace();
            }

            return false ;
        }

    }

}
