package za.dams.kiosque;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestGalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestGalleryFragment extends Fragment {

    // Array of integers points to images stored in /res/drawable-ldpi/
    int[] images = new int[]{
            R.drawable.gallery1,
            R.drawable.gallery2,
            R.drawable.gallery3
    };

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TestGalleryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DummyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TestGalleryFragment newInstance(String param1, String param2) {
        TestGalleryFragment fragment = new TestGalleryFragment();
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
        return inflater.inflate(R.layout.gallery_view, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Log.w(TAG,"My Fragment Id is "+mTransaction.getCrmFileCode() );
        ArrayList<HashMap<String, String>> mList = new ArrayList<HashMap<String, String>>();
        for( int i=0 ; i<3 ; i++ ) {
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("image", Integer.toString(images[i]));
            mList.add(hm);
        }
        String[] adaptFrom = {"image"};
        int[] adaptTo = {R.id.galleryitem};
        GridView mgv = (GridView) getView().findViewById(R.id.galleryview);
        //mgv.setAdapter(new SimpleAdapter(getActivity().getApplicationContext(), mList, R.layout.gallery_item, adaptFrom, adaptTo));
        mgv.setAdapter(new SimpleAdapter(getActivity().getApplicationContext(), mList, R.layout.gallery_item, adaptFrom, adaptTo));
    }
}