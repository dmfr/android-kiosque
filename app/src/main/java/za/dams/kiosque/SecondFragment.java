package za.dams.kiosque;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.annotation.Nullable;


public class SecondFragment extends Fragment {
    private Activity mActivity ;
    private WebView mWebView;
    private ImageView mloadingView;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState) ;

        setRetainInstance(true);

        mWebView = new WebView(mActivity) ;
        WebSettings webSettings = mWebView.getSettings();

        // http://stackoverflow.com/a/14062315/4534
        webSettings.setSaveFormData(false);

        webSettings.setJavaScriptEnabled(true);
        // Make links clickable
        mWebView.setWebViewClient(new WebViewClient());
        //mloadingView.setVisibility(View.GONE);

        // TODO: Ensure Webview reset to a clean slate
        // Work out other possible "fingerprinting" to be avoided
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.clearFormData();


        // http://developer.android.com/reference/android/webkit/CookieManager.html
        CookieManager cm = CookieManager.getInstance();
        cm.removeAllCookies(null);
        // The slate must be clean

        mWebView.setVisibility(View.VISIBLE);
        // Log.d(TAG, result.toString());
        //mWebView.setFocusableInTouchMode(false);
        //mWebView.setFocusable(false);
        mWebView.setInitialScale(250) ;
        mWebView.loadUrl("https://tracypda.dev.mirabel-sil.com/");

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return mWebView ;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = getActivity() ;
    }






}