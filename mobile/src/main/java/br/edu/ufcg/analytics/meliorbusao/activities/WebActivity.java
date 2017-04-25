package br.edu.ufcg.analytics.meliorbusao.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.ClientCertRequest;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import br.edu.ufcg.analytics.meliorbusao.R;

public class WebActivity extends AppCompatActivity {

    private WebView mWebView;
    private final String TAG = "WEB_ACTIVITY" ;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This must be called in this order
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_web);
        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

        mWebView = (WebView) findViewById(R.id.web_view);

        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportZoom(false);

        mWebView.loadUrl("https://eubrabigsea.dei.uc.pt/");
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                setTitle("Carregando...");
                setProgress(progress * 100);

                if (progress == 100)
                    setTitle(R.string.app_name);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {

            /*
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                Log.d(TAG, "shouldOverrideUrlLoading() URL : " + url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.d(TAG, "Invalid username or password");


            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.d(TAG, "Invalid username or password");

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("checkin_data")) {
                    flag = true;
                    Log.d(TAG, "Checkin data");

                }
                Log.d(TAG, "Page Finished");
            }


            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Log.d(TAG, "SPY");

                return super.shouldInterceptRequest(view, request);

            } */


        });
    }

}
