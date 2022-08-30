package za.dams.kiosque;

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

    ViewGroup mListView ;
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
        return view ;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mListView = (ViewGroup) view.findViewById(R.id.listview) ;


    }

    private void setText(View view, int id, String text) {
        TextView textView = (TextView) view.findViewById(id);
        textView.setText(text);
    }

    @Override
    public void onClick(View v) {
        if( v==mBtn ) {
            if( mListener != null ) {
                mListener.onSubmit();
            }
        }
    }

}