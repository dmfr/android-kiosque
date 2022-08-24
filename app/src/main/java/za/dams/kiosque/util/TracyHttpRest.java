package za.dams.kiosque.util;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import za.dams.kiosque.R;

public class TracyHttpRest {
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
    public static boolean sendPing(Context c) {
        boolean accepted = false ;
        try {
            HttpURLConnection httpConnection = getHttpTracyConnection(c,"/pod-ping");
            int respCode = httpConnection.getResponseCode() ;
            Log.w("DAMS","Response code is "+respCode) ;
            if( (respCode >= 200) && (respCode < 300) ) {
                accepted = true ;
            }

        } catch(Exception e) {

        }
        return accepted ;
    }
}
