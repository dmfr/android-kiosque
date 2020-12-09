package za.dams.kiosque;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class LinkAddFragment extends DialogFragment {

    public static LinkAddFragment newInstance() {
        LinkAddFragment fragment = new LinkAddFragment();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.w("DAMS","onCreateDialog") ;
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_linkadd, null, false);
        if( v == null ) {
            Log.w("DAMS","is null") ;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Create Year");
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
