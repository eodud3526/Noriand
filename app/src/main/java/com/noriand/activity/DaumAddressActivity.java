package com.noriand.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.noriand.R;
import com.noriand.constant.ServerConstant;
import com.noriand.view.dialog.CommonDialog;

public class DaumAddressActivity extends BaseActivity {
    // --------------------------------------------------
    // Common

    private CustomWebViewClient mCustomWebViewClient = null;
    private CustomWebChromeClient mCustomWebChromeClient = null;
    // --------------------------------------------------
    // View
    private RelativeLayout mrlPrev = null;
    private WebView mwv = null;

    // --------------------------------------------------
    // Data

    // --------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daum_address);

        init();
        setLayout();
        setListener();

        setWebView();
        mwv.loadUrl(ServerConstant.BASE_URL + ServerConstant.SEARCH_ADDRSS_URL);
    }

    private void init() {
    }

    private void setLayout() {
        mrlPrev = (RelativeLayout)findViewById(R.id.rl_daum_address_back);
        mwv = (WebView)findViewById(R.id.wv_daum_address);
    }

    private void setListener() {
        mrlPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onBack() {
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    private void onSuccess() {
        Intent intent = getIntent();
        intent.putExtra("isOrder", true);
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    private void setWebView() {
        mCustomWebViewClient = new CustomWebViewClient();
        mCustomWebChromeClient = new CustomWebChromeClient();

        mwv.setWebViewClient(mCustomWebViewClient);
        mwv.setWebChromeClient(mCustomWebChromeClient);
        mwv.setVerticalScrollBarEnabled(false);
        mwv.requestFocus(View.FOCUS_DOWN);

        WebSettings webSettings = mwv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setSupportMultipleWindows(false);
        webSettings.setAppCacheEnabled(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // deprecated되기 이전 OS의 폰들을 위하여
        webSettings.setRenderPriority(RenderPriority.HIGH);

        mwv.addJavascriptInterface(new AndroidBridge(), "noriand");
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            endProgress();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            startProgress();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            // super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView wv, String url) {
            return super.shouldOverrideUrlLoading(wv, url);
        }
    }

    private class CustomWebChromeClient extends WebChromeClient {
        @JavascriptInterface
        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 final JsResult result) {
            showDialogOneButton(message, new CommonDialog.DialogConfirmListener() {
                @Override
                public void onConfirm() {
//					result.confirm();
                    mActivity.finish();
                }

                @Override
                public void onCancel() {
                    result.cancel();
                }
            });
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message,
                                   final JsResult result) {
            showDialogTwoButton(message, new CommonDialog.DialogConfirmListener() {
                @Override
                public void onConfirm() {
                    result.confirm();

                    onSuccess();
                }

                @Override
                public void onCancel() {
                    result.cancel();
                }
            });
            return true;
        }

        @JavascriptInterface
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView wv = new WebView(mActivity);
            wv.getSettings().setJavaScriptEnabled(true);
            wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            wv.getSettings().setSupportMultipleWindows(true);
            wv.setWebChromeClient(this);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(wv);
            resultMsg.sendToTarget();
            return super.onCreateWindow(view, isDialog, isUserGesture,
                    resultMsg);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message,
                                  String defaultValue, JsPromptResult result) {
            showDialogOneButton(message, null);
            return true;
        }
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String zoneCode, final String address, String x, String y) {
            mwv.post(new Runnable() {
                @Override
                public void run() {
                Intent intent = getIntent();
                intent.putExtra("zoneCode", zoneCode);
                intent.putExtra("address", address);
                intent.putExtra("x", x);
                intent.putExtra("y", y);
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                }
            });
        }
    }
}