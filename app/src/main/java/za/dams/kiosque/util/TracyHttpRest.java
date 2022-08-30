package za.dams.kiosque.util;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import za.dams.kiosque.R;



public class TracyHttpRest {
    public static class TracyHttpScanResponse extends JSONObject {
        public TracyHttpScanResponse(String str) throws JSONException {
            super(str) ;
        }
        String scanQueryString ;
        boolean isValid ;
    }

    private static HttpURLConnection getHttpTracyConnection( Context c, String path ) throws Exception {
        String android_id = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);

        String sUrl = c.getResources().getString(R.string.tracy_rest_baseurl) ;
        if( path != null ) {
            sUrl += "/"+path ;
        }

        // Create URL
        URL endpoint = new URL(sUrl);
        HttpURLConnection httpConnection = (HttpURLConnection) endpoint.openConnection();
        httpConnection.setRequestMethod("GET");
        httpConnection.setRequestProperty("Authorization", "AndroidId"+" "+android_id);

        return httpConnection ;
    }
    public static InputStream getHttpInputStream(HttpURLConnection httpConnection) throws Exception {
        int respCode = httpConnection.getResponseCode() ;
        InputStream is ;
        if (respCode >= 200 && respCode < 400) {
            // Create an InputStream in order to extract the response object
            is = httpConnection.getInputStream();
        }
        else {
            is = httpConnection.getErrorStream();
        }
        return is ;
    }
    public static String getHttpReturnString(HttpURLConnection httpConnection) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(getHttpInputStream(httpConnection)));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();
        return sb.toString() ;
    }
    public static boolean sendPing(Context c) throws Exception {
        boolean accepted = false ;
        try {
            HttpURLConnection httpConnection = getHttpTracyConnection(c,"/pod-ping");
            httpConnection.setConnectTimeout(5*1000);
            httpConnection.connect();
            int respCode = httpConnection.getResponseCode() ;
            Log.w("DAMS","Response code is "+respCode) ;
            if( respCode==204 ) {
                accepted = true ;
            } else {
                String resp = getHttpReturnString(httpConnection);
                Log.w("DAMS",resp) ;
            }

        } catch(Exception e) {
            throw e ;
        }
        return accepted ;
    }
    public static TracyHttpScanResponse scanQuery(Context c, String queryString) throws Exception {
        TracyHttpScanResponse jsonObject = null ;
        boolean accepted = false ;


        try {
            HttpURLConnection httpConnection = getHttpTracyConnection(c,"/pod-scan/"+ URLEncoder.encode(queryString,"utf-8"));
            httpConnection.connect();
            int respCode = httpConnection.getResponseCode() ;
            Log.w("DAMS","Response code is "+respCode) ;

            String resp = getHttpReturnString(httpConnection);
            jsonObject = new TracyHttpScanResponse(resp) ;
            if( respCode == 200 ) {
                accepted=true ;
            }
        } catch(Exception e) {
            Log.e("DAMS",e.toString()) ;
        }

        if( !accepted ) {
            String errorStr = "" ;
            try {
                errorStr = jsonObject.getString("error") ;
            } catch(Exception e) {
                errorStr = "Unknown error" ;
            }
            throw new Exception(errorStr) ;
        }


        jsonObject.scanQueryString = queryString ;
        jsonObject.isValid = true ;
        return jsonObject ;
    }
    public static void sendFinalTransaction( Context c , TracyPodTransactionManager tracyPodTransactionManager ) {
        try {
            HttpURLConnection httpConnection = getHttpTracyConnection(c, "/pod-submit");
            httpConnection.setRequestMethod("POST");
            httpConnection.setUseCaches(false);
            httpConnection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream());
            wr.writeBytes(tracyPodTransactionManager.getFinalTransaction().toString());
            wr.close();

            httpConnection.connect();
            int respCode = httpConnection.getResponseCode() ;
            Log.w("DAMS","Response code is "+respCode) ;
        } catch(Exception e) {

        }
    }
}
