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
import android.widget.CheckBox;
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
public class PeopleTeamFragment extends Fragment {

    ViewGroup mListView ;

    boolean onForeground = false ;
    protected ProgressDialog mProgressDialog;


    public PeopleTeamFragment() {
        // Required empty public constructor
    }
    public static PeopleTeamFragment newInstance() {
        PeopleTeamFragment fragment = new PeopleTeamFragment();
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
            ArrayList<TeamPeople> peoples = new ArrayList<>();
            peoples.add( new TeamPeople( "Damien Mirand",true));
            peoples.add( new TeamPeople( "Hervé Danet",true));
            peoples.add( new TeamPeople( "Laure Tremblay",true));
            peoples.add( new TeamPeople( "Alain Lahaye",false));
            adapter = new ScanListAdapter(getActivity(),peoples);
        }
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(adapter);

        ((PeopleActivity)getActivity()).setTitle("Equipe RECEPTION");
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





    private void onLinkClicked(TeamPeople clickedLink) {

    }


    private static class TeamPeople {
        public String txtName;
        public boolean checked ;

        public TeamPeople(String txtName, boolean checked) {
            this.txtName = txtName ;
            this.checked = checked;
        }
    }



    private class ScanListAdapter extends ArrayAdapter<TeamPeople> implements AdapterView.OnItemClickListener {

        private static final String TAG = "ScanListAdapter";

        private LayoutInflater mInflater;
        private static final int LAYOUT = R.layout.fragment_team_item;


        public ScanListAdapter(Context context, ArrayList<TeamPeople> peoples) {
            super(context,0,peoples);
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

            TeamPeople people = getItem(position) ;
            view.setTag(people);



            setText(view, R.id.txtName, people.txtName);
            ((CheckBox) view.findViewById(R.id.checkBox)).setChecked(people.checked);
            //setText(view, R.id.item_caption, mode.modeCaption);




            return view;
        }
        private void setText(View view, int id, String text) {
            TextView textView = (TextView) view.findViewById(id);
            textView.setText(text);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TeamPeople pp = getItem(position);
            PeopleTeamFragment.this.onLinkClicked(pp);
        }



    }



}