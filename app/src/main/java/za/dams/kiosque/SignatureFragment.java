package za.dams.kiosque;

import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;

import android.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.ByteArrayOutputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignatureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignatureFragment extends DialogFragment implements SignaturePad.OnSignedListener, View.OnClickListener {
    public interface SignatureListener {
        void onSignatureResult(String imgJpegBase64);
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    SignatureListener mListener ;

    SignaturePad mSignaturePad ;
    Button mClearButton ;
    Button mSaveButton ;

    public void setListener( SignatureFragment.SignatureListener sl ) {
        mListener = sl ;
    }

    public SignatureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignatureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignatureFragment newInstance(String param1, String param2) {
        SignatureFragment fragment = new SignatureFragment();
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
        return inflater.inflate(R.layout.fragment_signature, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSignaturePad = (SignaturePad) view.findViewById(R.id.signature_pad);
        mSaveButton = (Button)view.findViewById(R.id.saveButton) ;
        mClearButton = (Button)view.findViewById(R.id.clearButton) ;
    }

    @Override
    public void onPause() {
        super.onPause();
        mSignaturePad.setOnSignedListener(null);
        mSaveButton.setOnClickListener(null);
        mClearButton.setOnClickListener(null);
    }
    @Override
    public void onResume() {
        // Set the width of the dialog proportional to 90% of the screen width
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        int minSize = (int)(Math.min(size.x,size.y) * 0.95) ;

        window.setLayout(minSize, (int)(minSize * 1.2) );
        window.setGravity(Gravity.CENTER);

        super.onResume();
        //barcodeView.resume();
        mSignaturePad.setOnSignedListener(this);
        mSaveButton.setOnClickListener(this);
        mClearButton.setOnClickListener(this);
    }


    @Override
    public void onStartSigning() {
        //Event triggered when the pad is touched
    }

    @Override
    public void onSigned() {
        //Event triggered when the pad is signed
    }

    @Override
    public void onClear() {
        //Event triggered when the pad is cleared
    }




    @Override
    public void onClick(View view) {
        if( view==mSaveButton ) {
            Log.w("DAMS","Signed") ;
            Bitmap signature = mSignaturePad.getSignatureBitmap() ;

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            signature.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();

            String imgJpegBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            if( mListener != null ) {
                mListener.onSignatureResult(imgJpegBase64);
            }
            this.dismiss();
        }
        if( view==mClearButton ) {
            mSignaturePad.clear();
            //this.dismiss();
        }
    }
}