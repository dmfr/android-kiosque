package za.dams.kiosque;

import androidx.fragment.app.DialogFragment;
import android.graphics.Point;
import android.os.Bundle;

import android.app.Fragment;

import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestCameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestCameraFragment extends DialogFragment {
    public interface CameraListener {
        void onCameraResult(boolean photoTaken);
    }

    private CameraListener mListener ;


    public TestCameraFragment() {
        // Required empty public constructor
    }
    public static TestCameraFragment newInstance() {
        TestCameraFragment fragment = new TestCameraFragment();
        return fragment;
    }

    public void setListener( CameraListener cl ) {
        mListener = cl ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Photo preview");

        View rootView = inflater.inflate(R.layout.fragment_test_camera, container, false);



        return rootView;
    }
    @Override
    public void onResume() {
        // Set the width of the dialog proportional to 90% of the screen width
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        int minSize = (int)(Math.min(size.x,size.y) * 1) ;


        window.setLayout(minSize, (int)(minSize * 4 / 3) );
        window.setGravity(Gravity.CENTER);


        super.onResume();
        //barcodeView.resume();
    }
}