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

    private static final int RES_LOADING = R.drawable.ic_explorer_fileicon ;
    private Context mContext ;

    private View mFab ;

    public TestGalleryFragment() {
        // Required empty public constructor
    }

    public static TestGalleryFragment newInstance() {
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

        GridView mgv = (GridView) getView().findViewById(R.id.galleryview);
        mgv.post(new Runnable() {
            @Override
            public void run() {
                int width = mgv.getMeasuredWidth() ;
                int cols = mgv.getNumColumns() ;
                int targetWidth = width/cols ;
                //Log.w("DAMS","Target width is "+ (width/cols)) ;

                MediaAdapter gridAdapter = new MediaAdapter(getActivity().getApplicationContext(),targetWidth);
                mgv.setAdapter(gridAdapter);
            }
        });



    }


    @Override
    public void onClick(View v) {
        if( v==mFab ) {
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
        }
    }

    private class MediaAdapter extends BaseAdapter {

        Context mAdapterContext ;
        int mTargetWidth ;

        LayoutInflater mInflater ;
        TracyPodTransactionManager mTracyPodTransactionManager ;

        SimpleImageLoader mSimpleImageLoader ;

        public MediaAdapter( Context c, int targetWidth ) {
            super() ;
            mAdapterContext = c.getApplicationContext() ;
            mInflater = (LayoutInflater) c.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            mSimpleImageLoader = new SimpleImageLoader(mAdapterContext) ;
            mTracyPodTransactionManager = TracyPodTransactionManager.getInstance(c) ;

            mTargetWidth = targetWidth ;
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
            return getItem(position).photoFilename.hashCode() ;
        }

        @Override
        public boolean hasStableIds() {
            return true ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (position >= getCount()) {
                return null;
            }

            TracyPodTransactionManager.PhotoModel photoRow = getItem(position) ;

            if( convertView!=null && (convertView.getTag() == photoRow.photoFilename) ) {
                return convertView ;
            }

            convertView = mInflater.inflate(R.layout.gallery_item, null) ;
            ImageView imgView = (ImageView)convertView.findViewById(R.id.galleryitem) ;
            if( photoRow.exampleUrl != null ) {
                mSimpleImageLoader.download(photoRow.exampleUrl, imgView);
            } else if( photoRow.photoFilename != null ) {
                ayncloadThumb(photoRow.photoFilename,imgView,mTargetWidth) ;
                convertView.setTag( photoRow.photoFilename ) ;
            }

            return convertView;
        }

    }


    private void ayncloadThumb( String filename, ImageView imgView, int targetWidth ) {
        File file = new File(getContext().getCacheDir(),filename) ;

        BitmapLoaderTask task = new BitmapLoaderTask(imgView,targetWidth);
        imgView.setImageDrawable(mContext.getResources().getDrawable(RES_LOADING));
        imgView.setMinimumHeight(64);
        imgView.setScaleType(ImageView.ScaleType.CENTER) ;
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,file.getPath());
    }
    class BitmapLoaderTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        private int targetWidth ;

        public BitmapLoaderTask(ImageView imageView, int tw) {
            imageViewReference = new WeakReference<ImageView>(imageView);
            targetWidth = tw ;
        }

        @Override
        protected Bitmap doInBackground(String... fileName) {
            Bitmap resultBitmap = null ;

            if( fileName.length == 0 ) {
                return null ;
            }
            resultBitmap =  BitmapFactory.decodeFile( fileName[0] ) ;

            if( targetWidth != 0 ) {
                int targetHeight = targetWidth * resultBitmap.getHeight() / resultBitmap.getWidth() ;
                resultBitmap = Bitmap.createScaledBitmap(resultBitmap, targetWidth, targetHeight, true) ;
            }
            if( resultBitmap != null ) {
                return resultBitmap ;
            }
            return null ;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
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