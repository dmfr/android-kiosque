package za.dams.kiosque;

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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

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
        fillList();
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
        if( mInflater == null ) {
            Log.w("DAMS","Layout is null") ;
        } else {
            Log.w("DAMS","Layout OK");
        }

        View view ;
        View colorView ;
        int color ;
        ImageButton imgbtn ;

        view = mInflater.inflate(R.layout.fragment_linkslist_item, null);
        setText(view, R.id.item_title, "103986434 / 449770");
        setText(view, R.id.item_caption, "AIRBUS LOGISTIK GMBH");
        colorView = view.findViewById(R.id.color);
        color = getResources().getColor( true ? android.R.color.holo_green_light : R.color.grey ) ;
        colorView.setBackgroundColor( color );
        imgbtn = (ImageButton)view.findViewById(R.id.imgbutton) ;
        imgbtn.setVisibility(View.GONE);
        mListView.addView(view);


        view = mInflater.inflate(R.layout.fragment_linkslist_item, null);
        setText(view, R.id.item_title, "103986425 / 449766");
        setText(view, R.id.item_caption, "SAFRAN LANDING SYSTEMS");
        colorView = view.findViewById(R.id.color);
        color = getResources().getColor( true ? android.R.color.holo_green_light : R.color.grey ) ;
        colorView.setBackgroundColor( color );
        imgbtn = (ImageButton)view.findViewById(R.id.imgbutton) ;
        imgbtn.setVisibility(View.GONE);
        mListView.addView(view);


        view = mInflater.inflate(R.layout.fragment_linkslist_item, null);
        setText(view, R.id.item_title, "103986369 / 449705");
        setText(view, R.id.item_caption, "SAUDI ARABIAN AIRLINES");
        colorView = view.findViewById(R.id.color);
        color = getResources().getColor( true ? android.R.color.holo_green_light : R.color.grey ) ;
        colorView.setBackgroundColor( color );
        imgbtn = (ImageButton)view.findViewById(R.id.imgbutton) ;
        imgbtn.setVisibility(View.GONE);
        mListView.addView(view);

    }
    private void setText(View view, int id, String text) {
        TextView textView = (TextView) view.findViewById(id);
        textView.setText(text);
    }
}