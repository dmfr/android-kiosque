package za.dams.kiosque;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import za.dams.kiosque.util.SettingsManager;

public class SettingZoomFragment extends DialogFragment implements DialogInterface.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private OnZoomFactorChangerListener mListener ;
    public interface OnZoomFactorChangerListener {
        void onZoomFactorChanged(int zoomFactor);
    }
    public void setListener( OnZoomFactorChangerListener l ) {
        mListener = l ;
    }

    private static final String ARG_ZOOMFACTOR = "zoom_factor";
    private int mZoomFactor;
    private SeekBar mSeekBar ;
    private TextView mSeekTxt ;

    private static final int progressStep = 50 ;

    public SettingZoomFragment() {
        // Required empty public constructor
    }

    public static SettingZoomFragment newInstance(int zoomFactor) {
        SettingZoomFragment fragment = new SettingZoomFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ZOOMFACTOR, zoomFactor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mZoomFactor = getArguments().getInt(ARG_ZOOMFACTOR);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_setting_zoom, null, false);
        mSeekBar = (SeekBar)v.findViewById(R.id.seek_bar) ;
        mSeekTxt = (TextView)v.findViewById(R.id.seek_txt) ;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Zoom factor");
        builder.setView(v);
        builder.setNegativeButton("Cancel", this);
        builder.setPositiveButton("Save", this);
        return builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        mSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if( savedInstanceState != null ) {
            // TODO : restore key-in values ?
        } else {
            loadValue() ;
        }
    }

    private void loadValue() {
        mZoomFactor = SettingsManager.getZoomFactor(getActivity()) ;
        applyValue() ;
    }
    private void applyValue() {
        mSeekBar.setProgress(mZoomFactor);
        mSeekTxt.setText(""+mZoomFactor);
    }
    private void saveValue() {
        SettingsManager.storeZoomFactor(getActivity(),mZoomFactor) ;
        if( mListener != null ) {
            mListener.onZoomFactorChanged(mZoomFactor);
        }
    }


    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch( i ) {
            case DialogInterface.BUTTON_POSITIVE :
                saveValue();
                break ;
            default :
                break ;
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        progress = ((int)Math.round(progress/progressStep ))*progressStep;
        mZoomFactor = progress ;

        applyValue();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}