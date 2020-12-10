package za.dams.kiosque;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
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

public class LinkAddFragment extends DialogFragment implements DialogInterface.OnClickListener {
    public static final int REQUEST_CODE = 1 ;
    public static final int RESULT_SAVED = 1 ;
    private static final String ARG_LINKIDX = "link_idx";

    private int mLinkIdx ;
    private LinksManager.LinkModel mModel ;

    private TextView mTxtName ;
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
        mTxtName.setText(mModel.name);
        mTxtUrlBase.setText(mModel.urlBase);
        mTxtUrlParams.setText(mModel.urlParams);
        mChkIsProd.setChecked(mModel.isProd);
    }
    private void saveModel() {
        fieldsToModel();
        LinksManager.storeLinkAtIdx(getActivity(), mModel, mLinkIdx);
    }
    private void fieldsToModel() {
        mModel.name = mTxtName.getText().toString().trim() ;
        mModel.urlBase = mTxtUrlBase.getText().toString().trim() ;
        mModel.urlParams = mTxtUrlParams.getText().toString().trim() ;
        mModel.isProd = mChkIsProd.isChecked() ;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // https://medium.com/@rmirabelle/how-to-set-dialogfragment-width-and-height-733c5b174178
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_linkadd, null, false);
        mTxtName = (TextView)v.findViewById(R.id.txt_name) ;
        mTxtUrlBase = (TextView)v.findViewById(R.id.txt_urlBase) ;
        mTxtUrlParams = (TextView)v.findViewById(R.id.txt_urlParams) ;
        mChkIsProd = (CheckBox)v.findViewById(R.id.chk_isProd) ;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit link");
        builder.setView(v);
        builder.setNegativeButton("Cancel", this);
        builder.setPositiveButton("Save", this);
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


    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch( i ) {
            case DialogInterface.BUTTON_POSITIVE :
                if( getTargetFragment() != null ) {
                    getTargetFragment().onActivityResult(getTargetRequestCode(),RESULT_SAVED,null);
                }
                saveModel() ;
                Log.w("DAMS","BUTTON_POSITIVE");
                break ;

            case DialogInterface.BUTTON_NEGATIVE :
                Log.w("DAMS","BUTTON_NEGATIVE");
                break ;
        }
    }
}
