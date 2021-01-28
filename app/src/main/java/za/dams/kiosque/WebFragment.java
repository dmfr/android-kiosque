package za.dams.kiosque;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import za.dams.kiosque.util.SettingsManager;


public class WebFragment extends Fragment {
    public interface WebListener {
        void onSignatureOpen();
    }
    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void signatureOpen() {
            Log.w("DAMS","WebAppInterface signatureOpen") ;
            WebFragment.this.signatureOpen() ;
        }
    }


    private Activity mActivity ;
    private WebView mWebView;
    private ImageView mloadingView;

    private WebListener mListener ;

    private static final String ARG_URL = "url";
    private String mUrl;

    public WebFragment() {
        // Required empty public constructor
    }
    public static WebFragment newInstance(String url) {
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    public void setListener( WebListener wl ) {
        mListener = wl ;
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState) ;

        if (getArguments() != null) {
            mUrl = getArguments().getString(ARG_URL);
        }

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
        applyZoomFactor() ;
        //mWebView.loadUrl("https://tracypda.dev.mirabel-sil.com/index-dev.html");
        mWebView.loadUrl(mUrl) ;
    }

    public void applyZoomFactor() {
        int zoomFactor = SettingsManager.getZoomFactor(mActivity);
        if( mWebView != null ) {
            mWebView.setInitialScale(zoomFactor) ;
        }
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

    @Override
    public void onResume() {
        super.onResume();
        mWebView.addJavascriptInterface(new WebAppInterface(mActivity), "Android");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void pushScanResult(String scanResult) {
        Log.w("DAMS","pushScanResult = "+scanResult) ;
        final String javaEvent = "scan" ;
        mWebView.loadUrl("javascript:postFromJava(\""+javaEvent+"\",\""+scanResult+"\")");
    }
    public void pushSignature(String imgJpegBase64) {
        Log.w("DAMS","pushSignature = "+imgJpegBase64) ;
        final String javaEvent = "sign_result" ;
        mWebView.loadUrl("javascript:postFromJava(\""+javaEvent+"\",\""+imgJpegBase64+"\")");
    }


    private void signatureOpen() {
        Log.w("DAMS","WebGragment signatureOpen") ;
        if( mListener != null ) {
            mListener.onSignatureOpen() ;
        }
    }
}