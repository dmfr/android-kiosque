package za.dams.kiosque.util;

public class LinksManager {
    public class LinkModel {
        public String name ;
        public String urlBase ;
        public String urlParams ;
        public boolean isProd ;
        public String getUrl() {
            return urlBase+'?'+urlParams ;
        }
    }


}
