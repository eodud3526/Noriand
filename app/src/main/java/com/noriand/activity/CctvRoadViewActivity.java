package com.noriand.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.noriand.R;
import com.noriand.util.StringUtil;
import com.noriand.vo.TraceItemVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class CctvRoadViewActivity extends BaseActivity{
    private WebView mWebView;
    private WebSettings mWebSettings;
    private String x = "";
    private String y = "";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cctv);
        setStatusBar(Color.WHITE);

        mWebView = (WebView)findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClient());
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        setData();

        String url = "kakaomap://roadView?p=" +y+","+x;
        //System.out.println(url);

        Intent intent = null;
        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }

    private void setData() {
        Intent intent = getIntent();
        String strItem = intent.getStringExtra("strItem");
        if(!StringUtil.isEmpty(strItem)) {
            TraceItemVO item = new TraceItemVO();
            try {
                JSONObject jsonObject = new JSONObject(strItem);
                item.parseJSONObject(jsonObject);
            } catch(JSONException e) {
            }
            if(item != null) {
                x = item.x;
                y = item.y;
            }
        }
    }
}
