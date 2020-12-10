package za.dams.kiosque;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.Nullable;

import za.dams.kiosque.util.LinksManager;

public class LinkAddFragment extends DialogFragment {
    private static final String ARG_LINKIDX = "link_idx";
    private int mLinkIdx ;
    private LinksManager.LinkModel mModel ;

    private TextView mTxtUrlBase ;
    private TextView mTxtUrlParams ;
    private CheckBox mChkIsProd ;

    public static LinkAddFragment newInstance(int linkIdx) {
        LinkAddFragment fragment = new LinkAddFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LINKIDX, linkIdx);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLinkIdx = getArguments().getInt(ARG_LINKIDX);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if( savedInstanceState != null ) {
            // TODO : restore key-in values ?
        } else {
            loadModel() ;
        }
    }

    private void loadModel() {
        if( mLinkIdx >= 0 ) {
            mModel = LinksManager.getLinkByIdx(getActivity(), mLinkIdx);
        }
        if( mModel == null ){
            mModel = new LinksManager.LinkModel() ;
        }
        modelToFields() ;
    }
    private void modelToFields() {
        mTxtUrlBase.setText(mModel.urlBase);
        mTxtUrlParams.setText(mModel.urlParams);
        mChkIsProd.setChecked(mModel.isProd);
    }
    private void saveModel() {

    }
    private void fieldsToModel() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // https://medium.com/@rmirabelle/how-to-set-dialogfragment-width-and-height-733c5b174178
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_linkadd, null, false);
        mTxtUrlBase = (TextView)v.findViewById(R.id.txt_urlBase) ;
        mTxtUrlParams = (TextView)v.findViewById(R.id.txt_urlParams) ;
        mChkIsProd = (CheckBox)v.findViewById(R.id.chk_isProd) ;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit link");
        builder.setView(v);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Save", null);
        return builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        Window window = getDialog().getWindow();
        if(window == null) return;
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT ;
        window.setAttributes(params);
    }


}
