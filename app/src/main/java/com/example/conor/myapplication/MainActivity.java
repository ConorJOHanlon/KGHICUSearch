package com.example.conor.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;

import android.os.AsyncTask;

import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import android.view.*;
import android.widget.EditText;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    Button searchButton;
    EditText searchQuery;
    DownloadTask a;
    Intent searchIntent;
    public ProgressDialog progressDialog;
    public Thread mThread;

    ArrayList<items> results = new ArrayList<items>();
    ArrayList<String> resultsToString = new ArrayList<String>();
    public static String ipAddress = "10.0.2.2";
    public static String port = "3310";
    public static String searchID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchButton = (Button) findViewById(R.id.btn_search);
        searchQuery = (EditText) findViewById(R.id.input_search);

        progressDialog = new ProgressDialog(MainActivity.this,
                ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Searching...");

        //Returns results from api and stores results in table
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                progressDialog.show();
                a = new DownloadTask();
                mThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            a.execute("https://www.googleapis.com/customsearch/v1?cx=014978321843205871943:-ypdvsizhq4&key=AIzaSyBhMTg1K8QgTJkqufr8nvkz4ppouKnZIYI&q=" + searchQuery.getText()).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        try {
                            Class.forName("com.mysql.jdbc.Driver");
                            Connection con = DriverManager.getConnection("jdbc:mysql://" + ipAddress + ":" + port + "/KingstonGeneralHospital", "root", "root");
                            Statement stmt = con.createStatement();
                            String s = "Insert INTO SearchQueries VALUES (NULL, '" + loginActivity.emailAddress + "', '" + searchQuery.getText() + "');";
                            stmt.executeUpdate(s);
                            con.close();


                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                };
                mThread.start();

            }
        });


    }

    public class DownloadTask extends AsyncTask<String, Void, JsonObject> {
        public Boolean finished = false;

        @Override
        protected JsonObject doInBackground(String... params) {
            progressDialog.show();
            try {
                downloadContent(params[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                return downloadContent(params[0]);
            } catch (IOException e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(JsonObject result) {
            Gson g = new Gson();
            resultsToString = null;
            resultsToString = new ArrayList<>();


            if (result != null) {


                for (int i = 0; i < 9; i++) {
                    results.add(i, g.fromJson(result.getAsJsonArray("items").get(i), items.class));
                    resultsToString.add(i, results.get(i).toString());
                }

            }



            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://" + ipAddress + ":" + port + "/KingstonGeneralHospital", "root", "root");
                Statement stmt = con.createStatement();
                Log.d("S", "Success");


               String s = "SELECT SearchID FROM SearchQueries WHERE Query='" + searchQuery.getText() + "' ORDER BY 'SearchID' DESC";
                ResultSet rs = stmt.executeQuery(s);


                String searchId = new String();
                while (rs.next()) {
                    searchId = rs.getString("SearchID");
                }
                MainActivity.searchID = searchId;
                int i = 1;
                for(String theResult : resultsToString){
                    try {

                        s = "Insert INTO SearchResults  VALUES ('" + searchId + "', '" + theResult + "', " + i + ", '0') ";
                        stmt.executeUpdate(s);
                        i++;
                    }
                    catch (Exception e) {
                        e.printStackTrace();

                    }
                    continue;


                }
                con.close();
            } catch (Exception e) {
                e.printStackTrace();

            }
            progressDialog.dismiss();
            Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
            searchIntent.putStringArrayListExtra("searchResults", resultsToString);
            startActivity(searchIntent);

        }


        private JsonObject downloadContent(String myurl) throws IOException {
            InputStream is = null;
            JsonObject jsonObject;

            try {
                URL url = new URL(myurl);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                int response = conn.getResponseCode();
                if (response >= 400) {
                    return null;

                }
                is = conn.getInputStream();

                JsonParser jsonParser = new JsonParser();
                jsonObject = (JsonObject) jsonParser.parse(
                        new InputStreamReader(is, "UTF-8"));
            } finally {
                if (is != null) {
                    is.close();
                }
            }
            return jsonObject;
        }
    }

}




