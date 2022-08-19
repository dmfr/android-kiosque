package za.dams.kiosque.util;

import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class TracyPodTransactionManager {
    public static class ScanRowModel {
        public String displayTitle ;
        public String displayCaption ;
    }

    @SuppressWarnings("unused")
    private static final String TAG = "PARACRM/TracyPodTransactionManager";
    private static final String SHARED_PREFS_NAME = "TracyPodTransactionManager" ;
    private static final String SHARED_PREFS_KEY  = "jsonSavedInstance" ;


    private static TracyPodTransactionManager _instance = null;

    private Context mContext ;
    private UUID mTransactionUUID = null ;
    private ArrayList<ScanRowModel> mArrScanRows = null ;

    private TracyPodTransactionManager( Context c ) {
        mContext = c ;
        mTransactionUUID = null ;
        mArrScanRows = new ArrayList<ScanRowModel>() ;
    }
    private TracyPodTransactionManager( Context c , JSONObject jsonObject ) {
        mContext = c ;
        /*
        try {
            tForwardedEventId = jsonObject.getLong("tForwardedEventId");

            tTransactions = new ArrayList<CrmFileTransaction>();
            JSONArray jsonArrCft = jsonObject.getJSONArray("tTransactions") ;
            if( jsonArrCft != null ) {
                for( int idx=0 ; idx<jsonArrCft.length() ; idx++ ) {
                    JSONObject jsonCft = jsonArrCft.getJSONObject(idx) ;
                    if( jsonCft != null ) {
                        CrmFileTransaction cft = new CrmFileTransaction( c , jsonCft ) ;
                        tTransactions.add(cft) ;
                    }
                }
            }
        } catch( JSONException e ) {
            e.printStackTrace() ;
        }
         */
    }

    public static synchronized TracyPodTransactionManager getInstance( Context c ) {
        /*
        if (_instance == null) {
            SharedPreferences prefs = c.getSharedPreferences(SHARED_PREFS_NAME,Context.MODE_PRIVATE);
            try {
                String jsonString = prefs.getString(SHARED_PREFS_KEY, "") ;
                JSONObject jsonObject = new JSONObject(jsonString) ;
                _instance = new CrmFileTransactionManager(c,jsonObject) ;
            }
            catch( Exception e ) {
                _instance = new CrmFileTransactionManager(c);
            }
        }
         */
        if (_instance == null) {
            _instance = new TracyPodTransactionManager(c);
        }
        return _instance;
    }
    public static synchronized void saveInstance( Context c ) {
        if( _instance != null ) {
            /*
            JSONObject jsonObject = _instance.toJSONObject() ;
            if( jsonObject != null ) {
                SharedPreferences prefs = c.getSharedPreferences(SHARED_PREFS_NAME,Context.MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = prefs.edit() ;
                prefsEditor.putString( SHARED_PREFS_KEY , jsonObject.toString() ) ;
                prefsEditor.commit() ;
            }
             */
        }
    }
    public static synchronized void purgeInstance( Context c ) {
        _instance = null ;
        /*
        SharedPreferences prefs = c.getSharedPreferences(SHARED_PREFS_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit() ;
        prefsEditor.remove(SHARED_PREFS_KEY) ;
        prefsEditor.commit() ;
         */
    }


    public UUID getTransactionUUID() {
        return mTransactionUUID;
    }
    public ArrayList<ScanRowModel> getArrScanRows() {
        return mArrScanRows ;
    }
    public void addDummy() {
        int i = mArrScanRows.size();

        ScanRowModel newRow = new ScanRowModel() ;
        newRow.displayTitle = "103986425 / 449766 "+"("+i+")" ;
        newRow.displayCaption = "AIRBUS LOGISTIK GMBH" ;
        mArrScanRows.add( newRow ) ;

        onSave();
    }
    private boolean isEmpty() {
        if( mArrScanRows.size() > 0 ) {
            return false ;
        }
        return true ;
    }
    public void onSave() {
        if( isEmpty() ) {
            mTransactionUUID = null ;
            return ;
        }
        if( mTransactionUUID == null ) {
            mTransactionUUID = UUID.randomUUID() ;
        }
    }
}
