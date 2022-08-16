package za.dams.kiosque;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.ArrayList;
import java.util.List;

import za.dams.kiosque.util.TracyPodTransactionManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestScanFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LayoutInflater mInflater ;

    DecoratedBarcodeView barcodeView;
    ViewGroup mListView ;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    public TestScanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TestScanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TestScanFragment newInstance(String param1, String param2) {
        TestScanFragment fragment = new TestScanFragment();
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mInflater = (LayoutInflater) getActivity().getLayoutInflater();
        fillList() ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test_scan, container, false);

        barcodeView = rootView.findViewById(R.id.barcode_view);
        barcodeView.setStatusText(null);
        /*
        capture = new CaptureManager(getActivity(), barcodeView);
        capture.initializeFromIntent(getActivity().getIntent(), savedInstanceState);
        capture.decode();
         */
        barcodeView.decodeContinuous(callback);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mListView = (ViewGroup) view.findViewById(R.id.listview) ;
        //((ListView)mListView).setAdapter();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(barcodeView != null) {
            if (isVisibleToUser) {
                barcodeView.resume();
            } else {
                barcodeView.pauseAndWait();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeView.pauseAndWait();
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    private void fillList() {
        ArrayList<TracyPodTransactionManager.LinkModel> data = new ArrayList<TracyPodTransactionManager.LinkModel>();

        TracyPodTransactionManager.LinkModel lm ;
        for( int i=0 ; i<4 ; i++) {
            lm = new TracyPodTransactionManager.LinkModel() ;
            lm.name = "103986425 / 449766 "+"("+i+")";
            lm.urlBase = "AIRBUS LOGISTIK GMBH";
            lm.isProd = (i%2==0);
            data.add(lm);
        }

        ListView listview = (ListView)mListView ;
        if( listview == null ) {
            return ;
        }
        ScanListAdapter adapter = (ScanListAdapter)(listview.getAdapter()) ;
        if (adapter == null) {
            adapter = new ScanListAdapter(getActivity(), data, null);
        } else {
            adapter.changeData(data);
        }
        listview.setAdapter(adapter);
        //setListShown(true) ;
        //getListView().setOnItemClickListener(adapter);

    }
    private void setText(View view, int id, String text) {
        TextView textView = (TextView) view.findViewById(id);
        textView.setText(text);
    }

    private void onLinkClicked(TracyPodTransactionManager.LinkModel clickedLink) {
        Log.w("DAMS","pouet pouet");
    }



    private class ScanListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

        private static final String TAG = "ScanListAdapter";

        private LayoutInflater mInflater;
        private static final int LAYOUT = R.layout.fragment_linkslist_item;

        private List<TracyPodTransactionManager.LinkModel> mData;
        private String mSelectedEntryKey ;

        public ScanListAdapter(Context context, List<TracyPodTransactionManager.LinkModel> data, String presetEntryKey) {
            super();
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            initData(data,presetEntryKey) ;
        }

        private void initData( List<TracyPodTransactionManager.LinkModel> data, String presetEntryKey ) {
            mData = data ;
            if( presetEntryKey == null ) {
                mSelectedEntryKey = null ;
                return ;
            }

            boolean found = false ;
            for( TracyPodTransactionManager.LinkModel be : data ) {
                if( presetEntryKey != null && be.name.equals(presetEntryKey) ) {
                    found = true ;
                }
            }
            if( found ) {
                mSelectedEntryKey = presetEntryKey ;
            }
        }

        public void changeData( List<TracyPodTransactionManager.LinkModel> data ) {
            initData(data,mSelectedEntryKey) ;
        }

        @Override
        public int getCount() {
            if( mData==null ) {
                return 0 ;
            }
            return mData.size() ;
        }

        @Override
        public TracyPodTransactionManager.LinkModel getItem(int position) {
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

            TracyPodTransactionManager.LinkModel linkModel = getItem(position) ;

            View view;
            if (convertView == null) {
                view = mInflater.inflate(LAYOUT, parent, false);
            } else {
                view = convertView;
            }

            view.setTag(linkModel);

            setText(view, R.id.item_title, linkModel.name);
            setText(view, R.id.item_caption, linkModel.urlBase);

            View colorView = view.findViewById(R.id.color);
            int color = getResources().getColor( linkModel.isProd ? android.R.color.holo_green_light : R.color.grey ) ;
            colorView.setBackgroundColor( color );

            ImageButton imgbtn = (ImageButton)view.findViewById(R.id.imgbutton) ;

            imgbtn.setVisibility(View.GONE);
            /*
            imgbtn.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_more));
            imgbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ViewGroup)view).showContextMenu();
                }
            });
             */


            return view;
        }
        private void setText(View view, int id, String text) {
            TextView textView = (TextView) view.findViewById(id);
            textView.setText(text);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)  {
            TracyPodTransactionManager.LinkModel clickedLink = getItem(position) ;

            TestScanFragment.this.onLinkClicked(clickedLink) ;
        }


        public String getSelectedEntryKey() {
            return mSelectedEntryKey ;
        }
    }

}