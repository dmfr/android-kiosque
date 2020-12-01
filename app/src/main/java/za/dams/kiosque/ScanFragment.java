package za.dams.kiosque;

import android.app.DialogFragment;
import android.graphics.Point;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanFragment extends DialogFragment {
    public interface ScanListener {
        void onScanResult(String scanResult);
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    DecoratedBarcodeView barcodeView;

    private ScanListener mListener ;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
                /*
                if(result.getText() == null || result.getText().equals(lastText)) {
                    // Prevent duplicate scans
                    return;
                }*/

            //lastText = result.getText();
            //barcodeView.setStatusText(result.getText());

            //beepManager.playBeepSoundAndVibrate();

            //Added preview of scanned barcode
            //ImageView imageView = findViewById(R.id.barcodePreview);
            //imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
            if( mListener != null ) {
                mListener.onScanResult(result.getText());
            }
            ScanFragment.this.dismiss();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    public void setListener( ScanListener sl ) {
        mListener = sl ;
    }

    public ScanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScannerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScanFragment newInstance(String param1, String param2) {
        ScanFragment fragment = new ScanFragment();
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
        getDialog().setTitle("Scan barcode");

        View rootView = inflater.inflate(R.layout.fragment_scan, container, false);

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
        // Set the width of the dialog proportional to 90% of the screen width
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        int minSize = (int)(Math.min(size.x,size.y) * 0.90) ;

        window.setLayout(minSize, (int)(minSize * 1.2) );
        window.setGravity(Gravity.CENTER);

        super.onResume();
        barcodeView.resume();
    }
}