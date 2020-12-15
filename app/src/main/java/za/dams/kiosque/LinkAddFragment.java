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
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.Nullable;

import za.dams.kiosque.util.LinksManager;

public class LinkAddFragment extends DialogFragment
        implements View.OnClickListener {
    public static final int REQUEST_CODE = 1 ;
    public static final int RESULT_SAVED = 1 ;
    private static final String ARG_LINKIDX = "link_idx";

    private int mLinkIdx ;
    private LinksManager.LinkModel mModel ;

    private TextView mTxtName ;
    private TextView mTxtUrlBase ;
    private TextView mTxtUrlParams ;
    private CheckBox mChkIsProd ;
    private Button mBtnPositive ;

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
        loadModel() ;
        super.onActivityCreated(savedInstanceState);
        if( savedInstanceState == null ) {
            modelToFields();
        }
    }

    private boolean loadModel() {
        if( mLinkIdx >= 0 ) {
            mModel = LinksManager.getLinkByIdx(getActivity(), mLinkIdx);
        }
        if( mModel == null ){
            mModel = new LinksManager.LinkModel() ;
        }
        return true ;
    }
    private boolean modelToFields() {
        mTxtName.setText(mModel.name);
        mTxtUrlBase.setText(mModel.urlBase);
        mTxtUrlParams.setText(mModel.urlParams);
        mChkIsProd.setChecked(mModel.isProd);
        return true ;
    }

    private boolean handleSave() {
        if( !fieldsToModel() ) {
            return false ;
        }
        LinksManager.storeLinkAtIdx(getActivity(), mModel, mLinkIdx);
        return true ;
    }
    private boolean fieldsToModel() {
        boolean hasErrors = false ;
        if(  !URLUtil.isValidUrl(mTxtUrlBase.getText().toString().trim()) ) {
            mTxtUrlBase.setError("Must be valid URL");
            hasErrors = true ;
        }
        if( mTxtName.getText().toString().trim().length() < 4 ) {
            mTxtName.setError("Name empty/incorrect");
            hasErrors = true ;
        }
        if( hasErrors ) {
            return false ;
        }
        mModel.name = mTxtName.getText().toString().trim() ;
        mModel.urlBase = mTxtUrlBase.getText().toString().trim() ;
        mModel.urlParams = mTxtUrlParams.getText().toString().trim() ;
        mModel.isProd = mChkIsProd.isChecked() ;
        return true ;
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
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Save", null);
        return builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        final AlertDialog d = (AlertDialog)getDialog();
        mBtnPositive = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
        mBtnPositive.setOnClickListener(this);

        Window window = getDialog().getWindow();
        if(window == null) return;
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT ;
        window.setAttributes(params);
    }


    @Override
    public void onClick(View view) {
        // https://stackoverflow.com/questions/2620444/how-to-prevent-a-dialog-from-closing-when-a-button-is-clicked/9523257
        if( view == mBtnPositive ) {
            if( handleSave() == true ) {
                if( getTargetFragment() != null ) {
                    getTargetFragment().onActivityResult(getTargetRequestCode(),RESULT_SAVED,null);
                }
                getDialog().dismiss();
            }
        }
    }
}
