package za.dams.kiosque;

import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;

import android.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LayoutInflater mInflater ;

    ViewGroup mListView ;

    public TestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TestFragment newInstance(String param1, String param2) {
        TestFragment fragment = new TestFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mListView = (ViewGroup) view.findViewById(R.id.listview) ;


    }

    private void fillList() {
        if( mInflater == null ) {
            Log.w("DAMS","Layout is null") ;
        } else {
            Log.w("DAMS","Layout OK");
        }


        for( int a=0 ; a<10 ; a++) {
            View view = mInflater.inflate(R.layout.fragment_linkslist_item, null);


            //view.setTag(linkModel);

            setText(view, R.id.item_title, "Tagda pouet");
            setText(view, R.id.item_caption, "ceci est un lien");

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


            mListView.addView(view);
        }
    }
    private void setText(View view, int id, String text) {
        TextView textView = (TextView) view.findViewById(id);
        textView.setText(text);
    }
}