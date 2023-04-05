package za.dams.kiosque;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.core.app.ActivityCompat;

public class PeopleActivity extends Activity implements FragmentManager.OnBackStackChangedListener {

    private static final int REQUEST_VIDEO_PERMISSIONS = 1;
    private static final String[] VIDEO_PERMISSIONS = {
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if( !hasPermissionsGranted(VIDEO_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_VIDEO_PERMISSIONS);
            return ;
        }

        setContentView(R.layout.activity_people);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().addOnBackStackChangedListener(this);

        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, PeopleModesFragment.newInstance(),"visible_fragment")
                    .commit();
        }
        updateUI() ;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }



    private boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this,permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_VIDEO_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_people, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isScanFragment = getCurrentFragment() instanceof PeopleScanFragment ;
        menu.findItem(R.id.action_scandummy).setVisible( isScanFragment );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch( item.getItemId() ) {
            case R.id.action_scandummy:
                actionScanDummy();
                break;
            case R.id.action_history:
                launchHistory();
                break;
            case android.R.id.home :
                onBackPressed();
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void actionScanDummy() {
        Fragment f = getFragmentManager().findFragmentByTag("visible_fragment");
        if( f instanceof PeopleScanFragment ) {
            ((PeopleScanFragment) f).fakeScan();
        }
    }




    public void launchScanMode(PeopleScanFragment.ScanModes scanMode) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, PeopleScanFragment.newInstance(scanMode),"visible_fragment")
                .addToBackStack(null)
                .commit();
    }
    public void launchScanModeTeam() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, PeopleTeamFragment.newInstance(),"visible_fragment")
                .addToBackStack(null)
                .commit();
    }
    public void launchHistory(){
        getFragmentManager().beginTransaction()
                .replace(R.id.container, PeopleHistoryFragment.newInstance(),"visible_fragment")
                .addToBackStack(null)
                .commit();
    }



    private Fragment getCurrentFragment() {
        Fragment currentBackStackFragment = getFragmentManager().findFragmentByTag("visible_fragment");
        return currentBackStackFragment ;
    }

    private void updateUI() {
        Fragment f = getCurrentFragment() ;
        boolean hasBackBtn = (f !=null ) && !(f instanceof PeopleModesFragment);
        getActionBar().setDisplayHomeAsUpEnabled(hasBackBtn);

        invalidateOptionsMenu();
    }

    @Override
    public void onBackStackChanged() {
        updateUI() ;
    }
}

