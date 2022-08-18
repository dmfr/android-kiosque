package za.dams.kiosque.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import za.dams.kiosque.R;

public class SimpleImageLoader {
    /*
     * Inspired by :
     * http://android-developers.blogspot.com/2010/07/multithreading-for-performance.html
     */
    private static final String LOG_TAG = "SimpleImageLoader";
    private static final int RES_LOADING = R.drawable.ic_explorer_fileicon ;
    private static final int RES_ERROR = R.drawable.crm_missing ;

    private final Context mContext ;
    private final Bitmap mBitmapLoading ;
    private final Bitmap mBitmapError ;

    private static final boolean noCache = false ;


    private Callback mCallback ;
    public interface Callback {
        public void onImageLoaded( URL crmUrlRequested, ImageView imageView ) ;
        public void onImageLoadFailed( URL crmUrlRequested, ImageView imageView ) ;
    }
    public void setCallback( Callback callback ) {
        mCallback = callback ;
    }



    public SimpleImageLoader( Context c ) {
        mContext = c ;
        mBitmapLoading = ((BitmapDrawable)c.getResources().getDrawable(RES_LOADING)).getBitmap() ;
        mBitmapError = ((BitmapDrawable)c.getResources().getDrawable(RES_ERROR)).getBitmap() ;
    }


    /**
     * Download the specified image from the Internet and binds it to the provided ImageView. The
     * binding is immediate if the image is found in the cache and will be done asynchronously
     * otherwise. A null bitmap will be associated to the ImageView if an error occurs.
     *
     * @param url The URL of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     */
    public void download( String sUrl, ImageView imageView) {
        URL url = null ;
        try {
            url = new URL(sUrl);
        } catch( MalformedURLException e ) {

        }
        if( url == null ) {
            imageView.setImageBitmap(mBitmapError);
            return ;
        }
        download(url,imageView) ;
    }
    public void download( URL url, ImageView imageView) {
        resetPurgeTimer();
        Bitmap bitmap = getBitmapFromCache(url);

        if (bitmap == null) {
            if (cancelPotentialDownload(url, imageView)) {
                BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
                DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
                imageView.setImageDrawable(downloadedDrawable);
                imageView.setMinimumHeight(156);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url);
            }
        } else {
            cancelPotentialDownload(url, imageView);
            imageView.setImageBitmap(bitmap);
        }
    }

    /*
     * Same as download but the image is always downloaded and the cache is not used.
     * Kept private at the moment as its interest is not clear.
       private void forceDownload(String url, ImageView view) {
          forceDownload(url, view, null);
       }
     */


    /**
     * Returns true if the current download has been canceled or if there was no download in
     * progress on this image view.
     * Returns false if the download in progress deals with the same url. The download is not
     * stopped in that case.
     */
    private static boolean cancelPotentialDownload(URL url, ImageView imageView) {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

        if (bitmapDownloaderTask != null) {
            URL bitmapCrmUrl = bitmapDownloaderTask.urlRequested;
            if ((bitmapCrmUrl == null) || (!bitmapCrmUrl.equals(url))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    /**
     * @param imageView Any imageView
     * @return Retrieve the currently active download task (if any) associated with this imageView.
     * null if there is no such task.
     */
    private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }


    Bitmap downloadBitmap(URL url) {
        byte[] data = null ;

        HttpURLConnection httpConn = null ;
        InputStream inputStream = null ;
        BufferedInputStream bis = null ;
        ByteArrayOutputStream byteBuffer = null ;
        try {
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("GET");

            inputStream = httpConn.getInputStream();
            bis = new BufferedInputStream(inputStream);
            byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = bis.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

            data = byteBuffer.toByteArray();


        } catch( IOException e ) {

        } finally {
            try {
                if (byteBuffer != null) {
                    byteBuffer.close();
                    byteBuffer = null;
                }
                if (bis != null) {
                    bis.close();
                    bis = null;
                }
                if (inputStream != null) {
                    inputStream.close();
                    inputStream = null;
                }
                if (httpConn != null) {
                    httpConn.disconnect();
                    httpConn = null;
                }
            } catch (IOException e) {

            }
        }

        if( data == null ) {
            return null ;
        }

        // return BitmapFactory.decodeStream(inputStream);
        // Bug on slow connections, fixed in future release.
        Bitmap decodedBitmap =  BitmapFactory.decodeByteArray(data, 0, data.length);
        if( decodedBitmap==null ) {
            Log.w(LOG_TAG, "Error while decoding bitmap from " + url.toString());
            return null ;
        }
        return decodedBitmap ;
    }

    /*
     * An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
     */
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

    /**
     * The actual AsyncTask that will asynchronously download the image.
     */
    class BitmapDownloaderTask extends AsyncTask<URL, Void, Bitmap> {
        private URL urlRequested ;
        private URL urlDownload ;
        private final WeakReference<ImageView> imageViewReference;

        public BitmapDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        /**
         * Actual download method.
         */
        @Override
        protected Bitmap doInBackground(URL... params) {
            Bitmap resultBitmap = null ;

            urlRequested = params[0];
            try {
                urlDownload = new URL( urlRequested.toString() ) ;
            } catch (MalformedURLException e) {
                return null ;
            }

            // Téléchargement
            resultBitmap = downloadBitmap(urlDownload);
            if( resultBitmap != null ) {
                return resultBitmap ;
            }
            return null ;
        }

        /**
         * Once the image is downloaded, associates it to the imageView
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if( bitmap!=null ) {
                addBitmapToCache(urlDownload, bitmap);
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
                // Change bitmap only if this process is still associated with it
                // Or if we don't use any bitmap to task association (NO_DOWNLOADED_DRAWABLE mode)
                if( this == bitmapDownloaderTask ) {
                    if( bitmap!=null ) {
                        imageView.setScaleType(ScaleType.FIT_XY);
                        imageView.setImageBitmap(bitmap);
                    } else {
                        imageView.setScaleType(ScaleType.CENTER) ;
                        imageView.setImageBitmap(mBitmapError);
                    }
                }

                if( mCallback!=null && bitmap==null ) {
                    mCallback.onImageLoadFailed( urlRequested, imageView ) ;
                } else if( mCallback!=null && urlDownload.equals(urlRequested) ) {
                    mCallback.onImageLoaded( urlRequested, imageView ) ;
                }
            }

        }
    }


    /**
     * A fake Drawable that will be attached to the imageView while the download is in progress.
     *
     * <p>Contains a reference to the actual download task, so that a download task can be stopped
     * if a new binding is required, and makes sure that only the last started download process can
     * bind its result, independently of the download finish order.</p>
     */
    private class DownloadedDrawable extends BitmapDrawable {
        private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

        public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
            super(mContext.getResources(),mBitmapLoading);
            bitmapDownloaderTaskReference =
                    new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
        }

        public BitmapDownloaderTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }
    }


    /*
     * Cache-related fields and methods.
     *
     * We use a hard and a soft cache. A soft reference cache is too aggressively cleared by the
     * Garbage Collector.
     */

    private static final int HARD_CACHE_CAPACITY = 10;
    private static final int DELAY_BEFORE_PURGE = 10 * 1000; // in milliseconds

    // Hard cache, with a fixed maximum capacity and a life duration
    private final HashMap<URL, Bitmap> sHardBitmapCache =
            new LinkedHashMap<URL, Bitmap>(HARD_CACHE_CAPACITY / 2, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(LinkedHashMap.Entry<URL, Bitmap> eldest) {
                    if (size() > HARD_CACHE_CAPACITY) {
                        // Entries push-out of hard reference cache are transferred to soft reference cache
                        sSoftBitmapCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
                        return true;
                    } else
                        return false;
                }
            };

    // Soft cache for bitmaps kicked out of hard cache
    private final static ConcurrentHashMap<URL, SoftReference<Bitmap>> sSoftBitmapCache =
            new ConcurrentHashMap<URL, SoftReference<Bitmap>>(HARD_CACHE_CAPACITY / 2);

    private final Handler purgeHandler = new Handler();

    private final Runnable purger = new Runnable() {
        public void run() {
            clearCache();
        }
    };

    /**
     * Adds this bitmap to the cache.
     * @param bitmap The newly downloaded bitmap.
     */
    private void addBitmapToCache(URL url, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (sHardBitmapCache) {
                sHardBitmapCache.put(url, bitmap);
            }
        }
    }

    /**
     * @param url The URL of the image that will be retrieved from the cache.
     * @return The cached bitmap or null if it was not found.
     */
    private Bitmap getBitmapFromCache(URL url) {
        if( noCache ) {
            return null ;
        }
        // First try the hard reference cache
        synchronized (sHardBitmapCache) {
            final Bitmap bitmap = sHardBitmapCache.get(url);
            if (bitmap != null) {
                // Bitmap found in hard cache
                // Move element to first position, so that it is removed last
                sHardBitmapCache.remove(url);
                sHardBitmapCache.put(url, bitmap);
                return bitmap;
            }
        }

        // Then try the soft reference cache
        SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(url);
        if (bitmapReference != null) {
            final Bitmap bitmap = bitmapReference.get();
            if (bitmap != null) {
                // Bitmap found in soft cache
                return bitmap;
            } else {
                // Soft reference has been Garbage Collected
                sSoftBitmapCache.remove(url);
            }
        }

        return null;
    }

    /**
     * Clears the image cache used internally to improve performance. Note that for memory
     * efficiency reasons, the cache will automatically be cleared after a certain inactivity delay.
     */
    public void clearCache() {
        sHardBitmapCache.clear();
        sSoftBitmapCache.clear();
    }

    /**
     * Allow a new delay before the automatic cache clear is done.
     */
    private void resetPurgeTimer() {
        purgeHandler.removeCallbacks(purger);
        purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
    }
}