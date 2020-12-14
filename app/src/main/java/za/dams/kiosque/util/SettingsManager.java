package za.dams.kiosque.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

public class SettingsManager {
    public static final String SHARED_PREFS_NAME = "Kiosque";
    public static final String SHARED_PREFS_KEY_ZOOM = "zoom_factor" ;

    public static int getZoomFactor(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME,0);
        int zoomFactor = prefs.getInt(SHARED_PREFS_KEY_ZOOM,0) ;
        return zoomFactor ;
    }
    public static void storeZoomFactor(Context context, int zoomFactor) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME,0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(SHARED_PREFS_KEY_ZOOM,zoomFactor) ;
        editor.apply();
    }

}
