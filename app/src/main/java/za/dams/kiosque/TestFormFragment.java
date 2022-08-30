package za.dams.kiosque;

import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.gcacace.signaturepad.views.SignaturePad;

import za.dams.kiosque.util.TracyPodTransactionManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestFormFragment extends Fragment implements View.OnClickListener{
    private SubmitListener mListener ;
    public interface SubmitListener {
        public void onSubmit();
    }
    public void setListener(SubmitListener listener) {
        mListener = listener ;
    }

    private LayoutInflater mInflater ;


    SignaturePad mSignaturePad ;
    TextView mReceiverName ;
    TextView mReceiverLocation ;
    Button mBtn ;

    public TestFormFragment() {
        // Required empty public constructor
    }
    public static TestFormFragment newInstance() {
        TestFormFragment fragment = new TestFormFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFromTransaction();
    }@Override
    public void onPause() {
        saveToTransaction();
        super.onPause();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mInflater = (LayoutInflater) getActivity().getLayoutInflater();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test_form, container, false);
        mBtn = (Button)view.findViewById(R.id.btn_submit) ;
        mBtn.setOnClickListener(this);

        mReceiverName = (TextView)view.findViewById(R.id.field_receiverName) ;
        mReceiverLocation = (TextView)view.findViewById(R.id.field_receiverLocation) ;
        mSignaturePad = (SignaturePad)view.findViewById(R.id.signature_pad) ;

        return view ;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {



    }

    private void setText(View view, int id, String text) {
        TextView textView = (TextView) view.findViewById(id);
        textView.setText(text);
    }

    @Override
    public void onClick(View v) {
        if( v==mBtn ) {
            saveToTransaction() ;
            if( mListener != null ) {
                mListener.onSubmit();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(mSignaturePad != null) {
            if (!isVisibleToUser) {
                mSignaturePad.clear();
                saveToTransaction();
            }
        }
    }

    private void loadFromTransaction() {
        TracyPodTransactionManager t = TracyPodTransactionManager.getInstance(getActivity()) ;
        mReceiverName.setText(t.getField("receiver_name"));
        mReceiverLocation.setText(t.getField("receiver_location"));
    }
    private void saveToTransaction() {
        Log.w("DAMS","Saving") ;
        TracyPodTransactionManager t = TracyPodTransactionManager.getInstance(getActivity()) ;

        t.setField("receiver_name",mReceiverName.getText().toString()) ;
        t.setField("receiver_location",mReceiverLocation.getText().toString()) ;

        if( mSignaturePad.isEmpty() ) {
            t.setSignatureBitmap(null);
        } else {
            Bitmap signatureBmp = mSignaturePad.getSignatureBitmap();
            t.setSignatureBitmap(signatureBmp);
        }
    }

}