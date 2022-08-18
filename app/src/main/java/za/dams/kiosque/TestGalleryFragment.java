package za.dams.kiosque;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import za.dams.kiosque.util.SimpleImageLoader;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestGalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestGalleryFragment extends Fragment {

    // Array of integers points to images stored in /res/drawable-ldpi/
    int[] images = new int[]{
            R.drawable.gallery1,
            R.drawable.gallery2,
            R.drawable.gallery3
    };

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.gallery_view, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Log.w(TAG,"My Fragment Id is "+mTransaction.getCrmFileCode() );
        ArrayList<HashMap<String, String>> mList = new ArrayList<HashMap<String, String>>();
        for( int i=0 ; i<3 ; i++ ) {
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("image", Integer.toString(images[i]));
            mList.add(hm);
        }
        String[] adaptFrom = {"image"};
        int[] adaptTo = {R.id.galleryitem};
        GridView mgv = (GridView) getView().findViewById(R.id.galleryview);

        ArrayList<ImageModel> arrImages = new ArrayList<ImageModel>() ;
        for( int i=0 ; i<19 ; i++ ) {
            int picIdx = i%3 + 1 ;
            arrImages.add( new ImageModel("https://10-39-10-205.int.mirabel-sil.com/tmp/dl.php?pic="+picIdx));
        }




        MediaAdapter gridAdapter = new MediaAdapter(getActivity().getApplicationContext());
        gridAdapter.setData(new ArrayList<ImageModel>() ) ;
        mgv.setAdapter(gridAdapter);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.w("DAMS","Coucou !") ;
                gridAdapter.setData(arrImages ) ;
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, 2000);

        /*
   	MediaAdapter gridAdapter = new MediaAdapter(mContext);
    	gridAdapter.setData(arrCfr) ;
    	GridView gridView = (GridView)mInflater.inflate(R.layout.explorer_gallery, null) ;
    	gridView.setAdapter(gridAdapter) ;
        */
    }


    private class ImageModel {
        String sUrl ;
        public ImageModel(String tUrl) {
            sUrl = tUrl ;
        }
    }

    private class MediaAdapter extends BaseAdapter {

        Context mAdapterContext ;

        LayoutInflater mInflater ;
        ArrayList<ImageModel> mArrObj ;

        SimpleImageLoader mSimpleImageLoader ;

        public MediaAdapter( Context c ) {
            super() ;
            mAdapterContext = c.getApplicationContext() ;
            mInflater = (LayoutInflater) c.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            mSimpleImageLoader = new SimpleImageLoader(mAdapterContext) ;
        }

        public void setData( ArrayList<ImageModel> arrObj ) {
            mArrObj = arrObj ;
            notifyDataSetChanged() ;
        }

        @Override
        public int getCount() {
            return mArrObj.size() ;
        }

        @Override
        public Object getItem(int position) {
            return mArrObj.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ImageModel imodel = mArrObj.get(position) ;

            if( convertView==null ) {
                convertView = mInflater.inflate(R.layout.gallery_item, null) ;
            }

            ImageView imgView = (ImageView)convertView.findViewById(R.id.galleryitem) ;

            mSimpleImageLoader.download(imodel.sUrl, imgView) ;


            return convertView;
        }

    }

}