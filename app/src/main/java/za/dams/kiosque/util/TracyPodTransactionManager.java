package za.dams.kiosque.util;

public class TracyPodTransactionManager {
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

}
