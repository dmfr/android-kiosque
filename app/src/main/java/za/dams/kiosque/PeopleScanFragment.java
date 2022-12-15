package za.dams.kiosque;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import za.dams.kiosque.util.SimpleImageLoader;
import za.dams.kiosque.util.TracyHttpRest;
import za.dams.kiosque.util.TracyPodTransactionManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PeopleScanFragment extends Fragment {

    DecoratedBarcodeView barcodeView;
    ViewGroup mListView ;

    boolean onForeground = false ;
    protected ProgressDialog mProgressDialog;

    private BarcodeCallback callback = new BarcodeCallback() {
        ArrayList<String> lastQueryString = new ArrayList<String>() ;

        @Override
        public void barcodeResult(BarcodeResult result) {
            String queryString = result.getText() ;
            if( lastQueryString.contains(queryString) ) {
                return ;
            }
            lastQueryString.add(queryString);
            //PeopleScanFragment.this.doQueryScan(queryString);
            fakeScan();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    public PeopleScanFragment() {
        // Required empty public constructor
    }
    public static PeopleScanFragment newInstance() {
        PeopleScanFragment fragment = new PeopleScanFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listview = (ListView)mListView ;
        if( listview == null ) {
            return ;
        }
        ScanListAdapter adapter = (ScanListAdapter)(listview.getAdapter()) ;
        if (adapter == null) {
            adapter = new ScanListAdapter(getActivity());
        }
        listview.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_people_scan, container, false);

        barcodeView = rootView.findViewById(R.id.barcode_view);
        barcodeView.setStatusText(null);
        /*
        capture = new CaptureManager(getActivity(), barcodeView);
        capture.initializeFromIntent(getActivity().getIntent(), savedInstanceState);
        capture.decode();
         */
        barcodeView.decodeContinuous(callback);

        Log.w("DAMS","onCreateView") ;

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
        onForeground = isVisibleToUser ;
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
        if( true ) {
            barcodeView.resume();
        }
    }

    private void doRefresh() {
        ScanListAdapter sla = ((ScanListAdapter)((ListView)mListView).getAdapter()) ;
        if( sla == null ) {
            return ;
        }
        sla.notifyDataSetChanged();

        boolean buttonsVisible = mScanEntries.size() == ScanTypes.values().length ;
        getView().findViewById(R.id.buttons).setVisibility(buttonsVisible ? View.VISIBLE : View.INVISIBLE);
    }


    public void openInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("(Debug) Input scan value");
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_scan_input, (ViewGroup) getView(), false);
        // Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                setInputValue( input.getText().toString() ) ;
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void setInputValue(String inputValue) {
        Log.w("DAMS","Dummy scan value : "+inputValue) ;
        doQueryScan(inputValue) ;
    }


    private void doQueryScan( String queryScan ) {

    }


    private void onLinkClicked(ScanEntry clickedLink) {
        Log.w("DAMS","pouet pouet");
    }


    private enum ScanTypes {
        TYPE_PEOPLE, TYPE_ROLE, TYPE_CLIENT
    }
    public static class ScanEntry {
        ScanTypes scanType ;
        boolean status ;
        String entryCode ;
        String entryTxt ;
    }

    private HashMap<ScanTypes,ScanEntry> mScanEntries = new HashMap<>() ;

    public void fakeScan() {
        for( ScanTypes st : ScanTypes.values() ) {
            if( mScanEntries.get(st) == null  ) {
                ScanEntry se = new ScanEntry();
                se.scanType = st ;
                se.status = true ;
                switch( st ) {
                    case TYPE_PEOPLE:
                        se.entryTxt = "Damien Mirand" ;
                        break ;
                    case TYPE_ROLE:
                        se.entryTxt = "Cariste" ;
                        break ;
                    case TYPE_CLIENT:
                        se.entryTxt = "Procter & Gamble" ;
                        break ;
                    default :
                        return ;
                }
                mScanEntries.put(st,se) ;
                break ;
            }
        }
        doRefresh() ;
    }

    private class ScanListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

        private static final String TAG = "ScanListAdapter";

        private LayoutInflater mInflater;
        private static final int LAYOUT = R.layout.fragment_linkslist_item;

        private final TracyPodTransactionManager mTracyPodTransactionManager ;

        public ScanListAdapter(Context context) {
            super();
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mTracyPodTransactionManager = TracyPodTransactionManager.getInstance(context) ;
        }

        @Override
        public int getCount() {
            return ScanTypes.values().length ;
        }

        @Override
        public ScanEntry getItem(int position) {
            ScanTypes type = ScanTypes.values()[position] ;
            if( mScanEntries.get(type) == null ) {
                ScanEntry se = new ScanEntry() ;
                se.scanType = type ;
                se.status = false ;
                return se ;
            }
            return mScanEntries.get(type) ;
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

            ScanEntry se = getItem(position) ;

            View view;
            if (convertView == null) {
                view = mInflater.inflate(LAYOUT, parent, false);
            } else {
                view = convertView;
            }

            view.setTag(se);

            String title = "" ;
            switch( se.scanType ) {
                case TYPE_ROLE:
                    title = "Role / Métier" ;
                    break ;
                case TYPE_PEOPLE:
                    title = "Collaborateur" ;
                    break ;
                case TYPE_CLIENT:
                    title = "Client / Mission" ;
                    break ;
            }

            if( se.status ) {
                setText(view, R.id.item_title, se.entryTxt);
                setText(view, R.id.item_caption, title);
            } else {
                setText(view, R.id.item_title, title);
                setText(view, R.id.item_caption, null);
            }

            View colorView = view.findViewById(R.id.color);
            int color = getResources().getColor( se.status ? android.R.color.holo_green_light : R.color.grey ) ;
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
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ScanEntry se = getItem(position);

            PeopleScanFragment.this.onLinkClicked(se);
        }



    }



}