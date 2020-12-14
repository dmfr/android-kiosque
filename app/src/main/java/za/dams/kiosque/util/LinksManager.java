package za.dams.kiosque.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LinksManager {
    public static final String SHARED_PREFS_NAME = "Kiosque";
    public static final String SHARED_PREFS_KEY = "json_links" ;

    public static class LinkModel {
        public int idx = -1 ;
        public String name ;
        public String urlBase ;
        public String urlParams ;
        public boolean isProd ;
        public String getUrl() {
            return urlParams.length()> 0 ? urlBase+'?'+urlParams : urlBase ;
        }
    }

    public static List<LinkModel> getLinks(Context context) {
        /*
        LinkModel lm1 = new LinkModel() ;
        lm1.name = "Tracy PDA Prod" ;
        lm1.urlBase = "https://services.schenkerfrance.fr/_paracrm/mobile/" ;
        lm1.urlParams = "user=dm:tracy@dbs&pass=1806" ;
        lm1.isProd = true ;
         */

        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME,0);
        String jsonLinks = prefs.getString(SHARED_PREFS_KEY,"[]") ;

        ArrayList<LinkModel> list = new ArrayList<LinkModel>() ;
        try {
            JSONArray jsonArray = new JSONArray(jsonLinks) ;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objectInArray = jsonArray.getJSONObject(i);

                LinkModel lm = new LinkModel() ;
                lm.idx = i ;
                lm.name = objectInArray.getString("name");
                lm.urlBase = objectInArray.getString("urlBase");
                lm.urlParams = objectInArray.getString("urlParams");
                lm.isProd = objectInArray.getBoolean("isProd");
                list.add( lm ) ;
            }
        } catch (JSONException e) {}
        return list ;
    }
    public static void storeLinks(Context context, List<LinkModel> linkModels) {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < linkModels.size(); i++) {
                LinkModel lm = linkModels.get(i);
                JSONObject objectInArray = new JSONObject();
                objectInArray.put("name", lm.name);
                objectInArray.put("urlBase", lm.urlBase);
                objectInArray.put("urlParams", lm.urlParams);
                objectInArray.put("isProd", lm.isProd);
                jsonArray.put(objectInArray) ;
            }
        } catch (JSONException e)  {}

        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SHARED_PREFS_KEY,jsonArray.toString()) ;
        editor.apply();
    }
    public static LinkModel getLinkByIdx(Context context, int linkIdx) {
        List<LinkModel> list = getLinks(context) ;
        if( list.size() >= linkIdx + 1 ) {
            return list.get(linkIdx) ;
        }
        return null ;
    }
    public static void storeLinkAtIdx( Context context, LinkModel linkModel, int linkIdx ) {
        List<LinkModel> list = getLinks(context) ;
        if( linkIdx >= 0 && list.size() >= linkIdx + 1 ) {
            list.set(linkIdx,linkModel) ;
        } else {
            list.add(linkModel) ;
        }
        storeLinks(context,list) ;
    }
    public static void deleteLinkAtIdx( Context context, int linkIdx ) {
        List<LinkModel> list = getLinks(context) ;
        if( linkIdx >= 0 && list.size() >= linkIdx + 1 ) {
            list.remove(linkIdx) ;
        }
        storeLinks(context,list) ;
    }
}
