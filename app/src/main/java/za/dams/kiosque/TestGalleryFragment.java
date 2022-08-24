package za.dams.kiosque;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import za.dams.kiosque.util.SimpleImageLoader;
import za.dams.kiosque.util.TracyPodTransactionManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestGalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestGalleryFragment extends Fragment implements View.OnClickListener {

    // Array of integers points to images stored in /res/drawable-ldpi/
    int[] images = new int[]{
            R.drawable.gallery1,
            R.drawable.gallery2,
            R.drawable.gallery3
    };

    private static final int RES_LOADING = R.drawable.ic_explorer_fileicon ;
    private Context mContext ;
    private Bitmap mBitmapLoading ;

    private View mFab ;

    public TestGalleryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DummyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TestGalleryFragment newInstance(String param1, String param2) {
        TestGalleryFragment fragment = new TestGalleryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.gallery_view, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        mFab = fab ;

        return view ;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity().getApplicationContext() ;
        mBitmapLoading = ((BitmapDrawable)mContext.getResources().getDrawable(RES_LOADING)).getBitmap() ;

        GridView mgv = (GridView) getView().findViewById(R.id.galleryview);
        MediaAdapter gridAdapter = new MediaAdapter(getActivity().getApplicationContext());
        mgv.setAdapter(gridAdapter);
    }


    @Override
    public void onClick(View v) {
        if( v==mFab ) {
            Log.w("DAMS","Floating button clicked") ;
            FragmentManager fm = getFragmentManager() ;
            TestCameraFragment f = TestCameraFragment.newInstance() ;
            f.setTargetFragment(this,1);
            f.show( fm, "dialog") ;
        }
    }
    public void onCameraFinish() {
        GridView gv = (GridView)getView().findViewById(R.id.galleryview) ;
        if( gv != null ) {
            ((MediaAdapter)gv.getAdapter()).notifyDataSetChanged();
            Log.w("DAMS","Adapter count "+((MediaAdapter)gv.getAdapter()).getCount()) ;
        }
    }

    private class MediaAdapter extends BaseAdapter {

        Context mAdapterContext ;

        LayoutInflater mInflater ;
        TracyPodTransactionManager mTracyPodTransactionManager ;

        SimpleImageLoader mSimpleImageLoader ;

        public MediaAdapter( Context c ) {
            super() ;
            mAdapterContext = c.getApplicationContext() ;
            mInflater = (LayoutInflater) c.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            mSimpleImageLoader = new SimpleImageLoader(mAdapterContext) ;
            mTracyPodTransactionManager = TracyPodTransactionManager.getInstance(c) ;
        }


        @Override
        public int getCount() {
            ArrayList<TracyPodTransactionManager.PhotoModel> mData = mTracyPodTransactionManager.getArrPhotos();
            if( mData==null ) {
                return 0 ;
            }
            return mData.size() ;
        }

        @Override
        public TracyPodTransactionManager.PhotoModel getItem(int position) {
            ArrayList<TracyPodTransactionManager.PhotoModel> mData = mTracyPodTransactionManager.getArrPhotos();
            if (position >= getCount()) {
                return null;
            }

            return mData.get(position) ;
        }

        @Override
        public long getItemId(int position) {
            if (position >= getCount()) {
                return 0;
            }
            return position ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (position >= getCount()) {
                return null;
            }

            TracyPodTransactionManager.PhotoModel photoRow = getItem(position) ;

            if( convertView==null ) {
                convertView = mInflater.inflate(R.layout.gallery_item, null) ;
            }

            ImageView imgView = (ImageView)convertView.findViewById(R.id.galleryitem) ;
            if( photoRow.exampleUrl != null ) {
                mSimpleImageLoader.download(photoRow.exampleUrl, imgView);
            } else if( photoRow.photoFilename != null ) {
                ayncloadThumb(photoRow.photoFilename,imgView) ;
            }
            return convertView;
        }

    }


    /*
    Dummy async load inspired from SimpleImageLoader
     */
    private void ayncloadThumb( String filename, ImageView imgView ) {
        File file = new File(getContext().getCacheDir(),filename) ;

        BitmapLoaderTask task = new BitmapLoaderTask(imgView);
        LoadedDrawable loadedDrawable = new LoadedDrawable(task);
        imgView.setImageDrawable(loadedDrawable);
        imgView.setMinimumHeight(156);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,file.getPath());
    }
    private class LoadedDrawable extends BitmapDrawable {
        private final WeakReference<BitmapLoaderTask> bitmapLoaderTaskReference;

        public LoadedDrawable(BitmapLoaderTask bitmaploaderTask) {
            super(mContext.getResources(),mBitmapLoading);
            bitmapLoaderTaskReference =
                    new WeakReference<BitmapLoaderTask>(bitmaploaderTask);
        }

        public BitmapLoaderTask getBitmapLoaderTask() {
            return bitmapLoaderTaskReference.get();
        }
    }
    private static BitmapLoaderTask getBitmapLoaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof LoadedDrawable) {
                LoadedDrawable loadedDrawable = (LoadedDrawable)drawable;
                return loadedDrawable.getBitmapLoaderTask();
            }
        }
        return null;
    }
    class BitmapLoaderTask extends AsyncTask<String, Void, Bitmap> {
        private URL urlRequested ;
        private URL urlDownload ;
        private final WeakReference<ImageView> imageViewReference;

        public BitmapLoaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        /**
         * Actual download method.
         */
        @Override
        protected Bitmap doInBackground(String... fileName) {
            Bitmap resultBitmap = null ;

            if( fileName.length == 0 ) {
                return null ;
            }
            resultBitmap =  BitmapFactory.decodeFile( fileName[0] ) ;

            // Téléchargement
            //resultBitmap = downloadBitmap(urlDownload);
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

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                BitmapLoaderTask bitmapLoaderTask = getBitmapLoaderTask(imageView);
                // Change bitmap only if this process is still associated with it
                // Or if we don't use any bitmap to task association (NO_DOWNLOADED_DRAWABLE mode)
                if( this == bitmapLoaderTask ) {
                    if( bitmap!=null ) {
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        imageView.setImageBitmap(bitmap);
                    } else {
                        imageView.setScaleType(ImageView.ScaleType.CENTER) ;
                        imageView.setImageBitmap(null);
                    }
                }

            }

        }
    }


}