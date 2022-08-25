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
    public static class PhotoModel {
        public String exampleUrl ;

        public String photoFilename ;
    }

    @SuppressWarnings("unused")
    private static final String TAG = "PARACRM/TracyPodTransactionManager";
    private static final String SHARED_PREFS_NAME = "TracyPodTransactionManager" ;
    private static final String SHARED_PREFS_KEY  = "jsonSavedInstance" ;


    private static TracyPodTransactionManager _instance = null;

    private Context mContext ;
    private UUID mTransactionUUID = null ;
    private ArrayList<ScanRowModel> mArrScanRows = null ;
    private ArrayList<PhotoModel> mArrPhotos = null ;

    private TracyPodTransactionManager( Context c ) {
        mContext = c ;
        mTransactionUUID = null ;
        mArrScanRows = new ArrayList<ScanRowModel>() ;
        mArrPhotos = new ArrayList<PhotoModel>() ;
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
    public ArrayList<PhotoModel> getArrPhotos() {
        return mArrPhotos ;
    }

    public void addDummy() {
        addDummy(null) ;
    }
    public void addDummy(String scan) {
        if( scan == null ) {
            int i = mArrScanRows.size() + 1 ;
            scan = ""+i ;
        }

        ScanRowModel newRow = new ScanRowModel() ;
        newRow.displayTitle = "103986425 / 449766 "+"("+scan+")" ;
        newRow.displayCaption = "AIRBUS LOGISTIK GMBH" ;
        mArrScanRows.add( 0, newRow ) ;

        onSave();
    }


    public void addPhoto(PhotoModel tmpPhoto) {
        PhotoModel newPhoto = new PhotoModel();
        newPhoto.photoFilename = tmpPhoto.photoFilename ;
        mArrPhotos.add(0,newPhoto) ;

        onSave();
    }
    public void pushExamplePhotos() {
        mArrPhotos.clear();
        for( int i=0 ; i<19 ; i++ ) {
            int picIdx = i%3 + 1 ;
            String url= "https://10-39-10-205.int.mirabel-sil.com/tmp/dl.php?pic="+picIdx ;
            PhotoModel newPhoto = new PhotoModel();
            newPhoto.exampleUrl = url ;
            mArrPhotos.add(newPhoto) ;
        }
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
