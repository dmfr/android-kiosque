package za.dams.kiosque.util;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class LinksManager {
    public static class LinkModel {
        public String name ;
        public String urlBase ;
        public String urlParams ;
        public boolean isProd ;
        public String getUrl() {
            return urlBase+'?'+urlParams ;
        }
    }

    public static List<LinkModel> getLinks(Context context) {
        LinkModel lm1 = new LinkModel() ;
        lm1.name = "Tracy PDA Prod" ;
        lm1.urlBase = "https://services.schenkerfrance.fr/_paracrm/mobile/" ;
        lm1.urlParams = "user=dm:tracy@dbs&pass=1806" ;
        lm1.isProd = true ;


        ArrayList<LinkModel> list = new ArrayList<LinkModel>() ;
        list.add( lm1 ) ;
        return list ;
    }
    public static void storeLinks(Context context, List<LinkModel> linkModels) {
        
    }
    public static LinkModel getLinkByIdx(Context context, int linkIdx) {
        List<LinkModel> list = getLinks(context) ;
        if( list.size() >= linkIdx + 1 ) {
            return list.get(linkIdx) ;
        }
        return null ;
    }
    public static void storeLinkAtIdx( Context context, LinkModel linkModel, int linkIdx ) {

    }
}
