package za.dams.kiosque;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import za.dams.kiosque.util.LinksManager;

//https://stackoverflow.com/questions/11770773/listfragment-layout-from-xml

public class LinkListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<List<LinksManager.LinkModel>>
        ,View.OnClickListener{
    private LinkListActionListener mListener ;

    public interface LinkListActionListener {
        public void onLinkAdd() ;
    }
    public void setLinkListActionListener(LinkListFragment.LinkListActionListener listener) {
        mListener = listener ;
    }

    private static final String TAG = "LinksListFragment";
    private static final int LOADER_ID = 1 ;

    private Activity mContext;
    //private SettingsCallbacks mCallback ;

    private View mListContainer ;
    private View mProgressContainer ;
    private View mFab ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_linkslist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mListContainer = view.findViewById(R.id.listContainer) ;
        mProgressContainer = view.findViewById(R.id.progressContainer) ;

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        mFab = fab ;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*
        final String[] items = getResources().getStringArray(R.array.list_example);
        final ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(),
                R.layout.fragment_linkslist_item, R.id.item_title, items);

        setEmptyText("No profiles defined");
        setListAdapter(aa);
        setListShown(true) ;
         */

        //setEmptyText("No profiles defined");
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<List<LinksManager.LinkModel>> onCreateLoader(int i, Bundle bundle) {
        setListShown(false) ;
        return new LinksLoader( getActivity() );
    }

    public void setListShown(boolean torf) {
        Log.w("DAMS","Set list shown = "+torf);
        mListContainer.setVisibility( torf ? View.VISIBLE : View.GONE );
        mProgressContainer.setVisibility( !torf ? View.VISIBLE : View.GONE );
    }

    @Override
    public void onLoadFinished(Loader<List<LinksManager.LinkModel>> loader, List<LinksManager.LinkModel> data) {
        LinksAdapter adapter = (LinksAdapter) getListAdapter();

        // TODO (maybe ?) mark currently defined profile

        if (adapter == null) {
            adapter = new LinksAdapter(getActivity(), data, null);
        } else {
            adapter.changeData(data);
        }
        setListAdapter(adapter);
        setListShown(true) ;
        getListView().setOnItemClickListener(adapter);

    }
    @Override
    public void onLoaderReset(Loader<List<LinksManager.LinkModel>> loader) {
        setListAdapter(null);
    }

    @Override
    public void onClick(View view) {
        if( view==mFab && mListener != null ) {
            mListener.onLinkAdd();
        }
    }

    private class LinksAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

        private static final String TAG = "Settings/AccountSubscribeAdapter";

        private LayoutInflater mInflater;
        private static final int LAYOUT = R.layout.fragment_linkslist_item;

        private List<LinksManager.LinkModel> mData;
        private String mSelectedEntryKey ;

        public LinksAdapter(Context context, List<LinksManager.LinkModel> data, String presetEntryKey) {
            super();
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            initData(data,presetEntryKey) ;
        }

        private void initData( List<LinksManager.LinkModel> data, String presetEntryKey ) {
            mData = data ;
            if( presetEntryKey == null ) {
                mSelectedEntryKey = null ;
                return ;
            }

            boolean found = false ;
            for( LinksManager.LinkModel be : data ) {
                if( presetEntryKey != null && be.name.equals(presetEntryKey) ) {
                    found = true ;
                }
            }
            if( found ) {
                mSelectedEntryKey = presetEntryKey ;
            }
        }

        public void changeData( List<LinksManager.LinkModel> data ) {
            initData(data,mSelectedEntryKey) ;
        }

        @Override
        public int getCount() {
            if( mData==null ) {
                return 0 ;
            }
            return mData.size() ;
        }

        @Override
        public LinksManager.LinkModel getItem(int position) {
            if (position >= getCount()) {
                return null;
            }

            return mData.get(position) ;
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

            LinksManager.LinkModel linkModel = getItem(position) ;

            View view;
            if (convertView == null) {
                view = mInflater.inflate(LAYOUT, parent, false);
            } else {
                view = convertView;
            }

            view.setTag(linkModel);

            setText(view, R.id.item_title, linkModel.name);
            setText(view, R.id.item_caption, linkModel.getUrl());
            return view;
        }
        private void setText(View view, int id, String text) {
            TextView textView = (TextView) view.findViewById(id);
            textView.setText(text);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)  {
            LinksManager.LinkModel clickedLink = getItem(position) ;

            LinkListFragment.this.onLinkClicked(clickedLink) ;
        }


        public String getSelectedEntryKey() {
            return mSelectedEntryKey ;
        }
    }

    private static class LinksLoader extends AsyncTaskLoader<List<LinksManager.LinkModel>> {

        List<LinksManager.LinkModel> mData;
        Context mContext ;


        public LinksLoader(Context context) {
            super(context);
            mContext = context ;
        }

        /**
         * This is where the bulk of our work is done.  This function is
         * called in a background thread and should generate a new set of
         * data to be published by the loader.
         */
        @Override public List<LinksManager.LinkModel> loadInBackground() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Log.w(TAG,"Response from server : "+response) ;
            //ArrayList<LinksManager.LinkModel> entries = new ArrayList<LinksManager.LinkModel>();
             //return entries;
            return LinksManager.getLinks(mContext) ;
        }

        /**
         * Called when there is new data to deliver to the client.  The
         * super class will take care of delivering it; the implementation
         * here just adds a little more logic.
         */
        @Override public void deliverResult(List<LinksManager.LinkModel> data) {
            if (isReset()) {
                // An async query came in while the loader is stopped.  We
                // don't need the result.
                if (data != null) {
                    onReleaseResources(data);
                }
            }
            List<LinksManager.LinkModel> oldApps = data;
            mData = data;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(data);
            }

            // At this point we can release the resources associated with
            // 'oldApps' if needed; now that the new result is delivered we
            // know that it is no longer in use.
            if (oldApps != null) {
                onReleaseResources(oldApps);
            }
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override protected void onStartLoading() {
            if (mData != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(mData);
            }

            // Has something interesting in the configuration changed since we
            // last built the app list?
            boolean configChange = false ;

            if (takeContentChanged() || mData == null || configChange) {
                // If the data has changed since the last time it was loaded
                // or is not currently available, start a load.
                forceLoad();
            }
        }

        /**
         * Handles a request to stop the Loader.
         */
        @Override protected void onStopLoading() {
            // Attempt to cancel the current load task if possible.
            cancelLoad();
        }

        /**
         * Handles a request to cancel a load.
         */
        @Override public void onCanceled(List<LinksManager.LinkModel> data) {
            super.onCanceled(data);

            // At this point we can release the resources associated with 'apps'
            // if needed.
            onReleaseResources(data);
        }

        /**
         * Handles a request to completely reset the Loader.
         */
        @Override protected void onReset() {
            super.onReset();

            // Ensure the loader is stopped
            onStopLoading();

            // At this point we can release the resources associated with 'apps'
            // if needed.
            if (mData != null) {
                onReleaseResources(mData);
                mData = null;
            }
        }

        /**
         * Helper function to take care of releasing resources associated
         * with an actively loaded data set.
         */
        protected void onReleaseResources(List<LinksManager.LinkModel> apps) {
            // For a simple List<> there is nothing to do.  For something
            // like a Cursor, we would close it here.
        }
    }



    private void forceReload() {
        final LoaderManager lm = getLoaderManager();
        if( lm.getLoader(LOADER_ID)!=null ) {
            this.setListShown(false) ;
            lm.getLoader(LOADER_ID).forceLoad() ;
        }
    }
    private void closeFragment() {
        getFragmentManager().popBackStack() ;
    }

    private void onLinkClicked(LinksManager.LinkModel clickedLink) {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch( requestCode ) {
            case LinkAddFragment.REQUEST_CODE :
                if( requestCode == LinkAddFragment.RESULT_SAVED ) {
                    forceReload();
                }
        }
    }
}
