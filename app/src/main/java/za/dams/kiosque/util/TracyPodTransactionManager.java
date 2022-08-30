package za.dams.kiosque.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TracyPodTransactionManager {
    public static class ScanRowModel {
        public int hatFilerecordId ;
        public int[] arrCdeFilerecordIds ;
        public int countParcels ;
        public String atrConsigneeTxt ;

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
    private HashMap<String,String> mMapFields = null ;
    private Bitmap mSignatureBitmap = null ;

    private TracyPodTransactionManager( Context c ) {
        mContext = c ;
        mTransactionUUID = null ;
        mArrScanRows = new ArrayList<ScanRowModel>() ;
        mArrPhotos = new ArrayList<PhotoModel>() ;
        mMapFields = new HashMap<String,String>() ;
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

    public void addScanResponse( TracyHttpRest.TracyHttpScanResponse resp ) {
        try {
            int hatFilerecordId = resp.getInt("hat_filerecord_id") ;

            JSONArray jsonCdes = resp.getJSONArray("cdes") ;
            int[] cdeFilerecordIds = new int[jsonCdes.length()] ;
            String cdeIdDn = null ;
            String atrConsigneeTxt = resp.getString("atr_consignee_txt") ; ;
            for( int i=0 ; i<jsonCdes.length() ; i++ ) {
                JSONObject cdeRow = jsonCdes.getJSONObject(i);
                cdeFilerecordIds[i] = cdeRow.getInt("cde_filerecord_id") ;
                if( cdeIdDn == null ) {
                    cdeIdDn = cdeRow.getString("id_dn") ;
                }
            }

            String displayTitle = resp.getString("id_hat") ;
            displayTitle += " / ";
            if( cdeFilerecordIds.length > 1 ) {
                displayTitle += cdeIdDn+"+"+" ("+cdeFilerecordIds.length+")" ;
            } else {
                displayTitle += cdeIdDn ;
            }

            String displayCaption = atrConsigneeTxt ;


            ScanRowModel newRow = new ScanRowModel();
            newRow.hatFilerecordId = hatFilerecordId ;
            newRow.arrCdeFilerecordIds = cdeFilerecordIds ;
            newRow.atrConsigneeTxt = atrConsigneeTxt ;
            newRow.displayTitle = displayTitle;
            newRow.displayCaption = displayCaption;


            for( ScanRowModel srm : mArrScanRows ) {
                if( srm.hatFilerecordId == hatFilerecordId ) {
                    return ;
                }
            }

            mArrScanRows.add(0, newRow);
            onSave();

        } catch(Exception e) {
            Log.e("DAMS",e.toString()) ;
        }
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

    public void setField( String fieldName, String fieldValue ) {
        mMapFields.put(fieldName,fieldValue) ;
    }
    public String getField( String fieldName ) {
        return mMapFields.get(fieldName) ;
    }
    public void setSignatureBitmap( Bitmap signatureBmp ) {
        mSignatureBitmap = signatureBmp ;
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


    public JSONObject getFinalTransaction() {
        try {
            if (isEmpty() || (mTransactionUUID == null)) {
                return null;
            }
            JSONObject jsonObj = new JSONObject();

            JSONArray jsonArrRows = new JSONArray();
            for (ScanRowModel scanRow : mArrScanRows) {
                jsonArrRows.put(scanRow.hatFilerecordId);
            }
            jsonObj.put("arrHatFilerecordIds", jsonArrRows);

            JSONArray jsonArrPhotos = new JSONArray();
            for (PhotoModel photoRow : mArrPhotos) {

                File file = new File(mContext.getCacheDir(),photoRow.photoFilename) ;
                byte[] buffer = new byte[(int) file.length() + 100];
                @SuppressWarnings("resource")
                int length = new FileInputStream(file).read(buffer);
                String base64 = Base64.encodeToString(buffer, 0, length,
                        Base64.DEFAULT);
                jsonArrPhotos.put(base64) ;
            }
            jsonObj.put("arrBase64Photos", jsonArrPhotos);

            JSONObject jsonFields = new JSONObject();
            for(Map.Entry<String, String> entry : mMapFields.entrySet()) {
                jsonFields.put(entry.getKey(),entry.getValue());
            }
            jsonObj.put("objFields", jsonFields);

            if( mSignatureBitmap != null ) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                mSignatureBitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String imgJpegBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                jsonObj.put("signatureBase64", imgJpegBase64);
            }

            return jsonObj;
        } catch(Exception e) {

        }
        return null ;
    }
}
