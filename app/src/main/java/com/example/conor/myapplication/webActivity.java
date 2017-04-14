package com.example.conor.myapplication;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;

public class webActivity extends AppCompatActivity {

    WebView webView;
    int depthCount;
    Date time;
    long start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = (WebView) findViewById(R.id.web);
        depthCount = 0;

        String url = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString("link");

        }


        webView.loadUrl(url);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        time= new Date();
        start = System.currentTimeMillis();

        webView.setWebViewClient(new WebViewClient() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Toast.makeText(getBaseContext(), request.getUrl().toString(), Toast.LENGTH_SHORT).show();
                recordResult();
                webView.loadUrl(request.getUrl().toString());
                start = System.currentTimeMillis();
                return true;
            }

        });
    }

    public void recordResult(){
        long end = System.currentTimeMillis();
        long timeOnPage = (end - start)/1000;

        if(timeOnPage>3){
            depthCount++;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://" + MainActivity.ipAddress + ":" + MainActivity.port + "/KingstonGeneralHospital", "root", "root");
                Statement stmt = con.createStatement();
                String s = "Insert INTO ClickedLinks VALUES ('"  + MainActivity.searchID + "', '" + webView.getUrl() + "', '" +  timeOnPage + "', '" + depthCount +"')";
                stmt.executeUpdate(s);
                con.close();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    @Override
    public void onBackPressed() {
        recordResult();
        finish();
        return;
    }








}
