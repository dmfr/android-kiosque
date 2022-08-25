package za.dams.kiosque.util;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import za.dams.kiosque.R;



public class TracyHttpRest {
    public static class TracyHttpScanResponse {
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
    public static boolean sendPing(Context c) throws Exception {
        boolean accepted = false ;
        try {
            HttpURLConnection httpConnection = getHttpTracyConnection(c,"/pod-ping");
            httpConnection.setConnectTimeout(5*1000);
            int respCode = httpConnection.getResponseCode() ;
            Log.w("DAMS","Response code is "+respCode) ;
            if( respCode==204 ) {
                accepted = true ;
            }

        } catch(Exception e) {
            throw e ;
        }
        return accepted ;
    }
    public static TracyHttpScanResponse scanQuery(Context c, String queryString) {
        String respMsg = null ;
        try {
            HttpURLConnection httpConnection = getHttpTracyConnection(c,"/pod-scan");
            int respCode = httpConnection.getResponseCode() ;
            respMsg = httpConnection.getResponseMessage() ;
            Log.w("DAMS","Response code is "+respCode) ;
            if( respCode != 200 ) {
                return null ;
            }
        } catch(Exception e) {

        }

        Log.w("DAMS","Message is : "+respMsg);

        TracyHttpScanResponse resp = new TracyHttpRest.TracyHttpScanResponse() ;
        resp.isValid = true ;
        return resp ;
    }
}
