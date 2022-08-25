package za.dams.kiosque;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.os.AsyncTask;
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
import java.util.List;

import za.dams.kiosque.util.SimpleImageLoader;
import za.dams.kiosque.util.TracyHttpRest;
import za.dams.kiosque.util.TracyPodTransactionManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestScanFragment extends Fragment {

    DecoratedBarcodeView barcodeView;
    ViewGroup mListView ;

    boolean onForeground = false ;
    protected ProgressDialog mProgressDialog;

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
    public static TestScanFragment newInstance() {
        TestScanFragment fragment = new TestScanFragment();
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
        onForeground = false ;
        super.onPause();
        transactionDoSave() ;
        barcodeView.pauseAndWait();
    }

    @Override
    public void onResume() {
        super.onResume();
        onForeground = true ;
        barcodeView.resume();
    }

    private void doRefresh() {
        ScanListAdapter sla = ((ScanListAdapter)((ListView)mListView).getAdapter()) ;
        if( sla == null ) {
            return ;
        }
        sla.notifyDataSetChanged();
    }

    public void addDummy() {
        ScanListAdapter sla = ((ScanListAdapter)((ListView)mListView).getAdapter()) ;
        if( sla == null ) {
            Log.w("DAMS","???") ;
            return ;
        }
        sla.addDummy() ;
    }
    public void openInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("(Debug) Input scan value");
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_scan_input, (ViewGroup) getView(), false);
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
        ScanQueryTask task = new ScanQueryTask(this.getContext());
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,queryScan);
    }


    private void onLinkClicked(TracyPodTransactionManager.ScanRowModel clickedLink) {
        Log.w("DAMS","pouet pouet");
    }

    private void transactionDoSave() {
        TracyPodTransactionManager tracyPod = TracyPodTransactionManager.getInstance(getActivity()) ;
        tracyPod.onSave() ;
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
            ArrayList<TracyPodTransactionManager.ScanRowModel> mData = mTracyPodTransactionManager.getArrScanRows();
            if( mData==null ) {
                return 0 ;
            }
            return mData.size() ;
        }

        @Override
        public TracyPodTransactionManager.ScanRowModel getItem(int position) {
            ArrayList<TracyPodTransactionManager.ScanRowModel> mData = mTracyPodTransactionManager.getArrScanRows();
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

            TracyPodTransactionManager.ScanRowModel scanRow = getItem(position) ;

            View view;
            if (convertView == null) {
                view = mInflater.inflate(LAYOUT, parent, false);
            } else {
                view = convertView;
            }

            view.setTag(scanRow);

            setText(view, R.id.item_title, scanRow.displayTitle);
            setText(view, R.id.item_caption, scanRow.displayCaption);

            View colorView = view.findViewById(R.id.color);
            int color = getResources().getColor( true ? android.R.color.holo_green_light : R.color.grey ) ;
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
            TracyPodTransactionManager.ScanRowModel clickedLink = getItem(position);

            TestScanFragment.this.onLinkClicked(clickedLink);
        }


        public void addDummy() {
            mTracyPodTransactionManager.addDummy();
            notifyDataSetChanged();
        }
    }

    class ScanQueryTask extends AsyncTask<String, Void, TracyHttpRest.TracyHttpScanResponse> {
        private Context mContext ;
        private String queryString ;

        public ScanQueryTask(Context c) {
            mContext = c.getApplicationContext() ;
        }

        @Override
        protected void onPreExecute(){
            if( onForeground ) {
                mProgressDialog = ProgressDialog.show(
                        TestScanFragment.this.getContext(),
                        "Scan query",
                        "Please wait...",
                        true);
            }
        }

        @Override
        protected TracyHttpRest.TracyHttpScanResponse doInBackground(String... scanQuery) {
            try {
                Thread.sleep(200) ;
            } catch( Exception e ) {

            }
            if( scanQuery.length == 0 ) {
                return null ;
            }
            queryString = scanQuery[0] ;

            return TracyHttpRest.scanQuery(mContext, queryString) ;
        }

        @Override
        protected void onPostExecute(TracyHttpRest.TracyHttpScanResponse scanResponse) {
            if( scanResponse != null ) {
                TracyPodTransactionManager.getInstance(mContext).addDummy(queryString);
                doRefresh() ;
            }
            if( mProgressDialog != null ) {
                mProgressDialog.dismiss() ;
            }
            if( scanResponse == null ) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TestScanFragment.this.getContext());
                builder.setTitle("Scan rejected")
                        .setMessage("Query <"+queryString+"> not accepted")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

}