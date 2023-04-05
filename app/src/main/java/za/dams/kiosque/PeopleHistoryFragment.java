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
import android.widget.ArrayAdapter;
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
public class PeopleHistoryFragment extends Fragment {

    ViewGroup mListView ;

    boolean onForeground = false ;
    protected ProgressDialog mProgressDialog;


    public PeopleHistoryFragment() {
        // Required empty public constructor
    }
    public static PeopleHistoryFragment newInstance() {
        PeopleHistoryFragment fragment = new PeopleHistoryFragment();
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
            ArrayList<Mode> modes = new ArrayList<>();
            modes.add( new Mode( PeopleScanFragment.ScanModes.ON_SINGLE,"3/04 12:10:05","People : Cyril Destrumel\nRole : OPERATEUR / EMBALLEUR\nClient: DPNA"));
            modes.add( new Mode( PeopleScanFragment.ScanModes.ON_SINGLE,"3/04 12:09:51","People : Audrey Courbet\nRole : PREPARATEUR DE COMMANDES\nClient: EVERGREEN"));
            modes.add( new Mode( PeopleScanFragment.ScanModes.ON_SINGLE,"3/04 12:09:29","People : Sébastien Basson\nRole : CONTRÔLE\nClient: WERNER MERTZ"));
            modes.add( new Mode( PeopleScanFragment.ScanModes.ON_SINGLE,"3/04 12:09:22","People : David Lebon\nRole : CONTROLEUR/FLASHEUR\nClient: SPHERE EMB"));

            adapter = new ScanListAdapter(getActivity(),modes);
        }
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(adapter);

        ((PeopleActivity)getActivity()).setTitle("History / Recent scans");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_people_modes, container, false);


        Log.w("DAMS","onCreateView") ;

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mListView = (ViewGroup) view.findViewById(R.id.listview) ;
        //((ListView)mListView).setAdapter();

    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();

    }





    private void onLinkClicked(Mode clickedLink) {

    }


    private static class Mode {
        public PeopleScanFragment.ScanModes mode ;
        public String modeTitle;
        public String modeCaption;

        public Mode(PeopleScanFragment.ScanModes mode, String modeTitle, String modeCaption) {
            this.mode = mode ;
            this.modeTitle = modeTitle;
            this.modeCaption = modeCaption;
        }
    }



    private class ScanListAdapter extends ArrayAdapter<Mode> implements AdapterView.OnItemClickListener {

        private static final String TAG = "ScanListAdapter";

        private LayoutInflater mInflater;
        private static final int LAYOUT = R.layout.fragment_history_item;


        public ScanListAdapter(Context context, ArrayList<Mode> modes) {
            super(context,0,modes);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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


            View view;
            if (convertView == null) {
                view = mInflater.inflate(LAYOUT, parent, false);
            } else {
                view = convertView;
            }

            Mode mode = getItem(position) ;
            view.setTag(mode);



            setText(view, R.id.item_title, mode.modeTitle);
            setText(view, R.id.item_caption, mode.modeCaption);

            View colorView = view.findViewById(R.id.color);
            int color = getResources().getColor( android.R.color.holo_green_light ) ;
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
            Mode mm = getItem(position);
            PeopleHistoryFragment.this.onLinkClicked(mm);
        }



    }



}