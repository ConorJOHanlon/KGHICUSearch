package com.example.conor.myapplication;

import android.os.Bundle;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.content.Intent;


import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ListView resultView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ArrayList<String> searchResults = getIntent().getStringArrayListExtra("searchResults");
        Log.d("HI", searchResults.toString());
        final ArrayList<items> searchTitle = new ArrayList<>();

        for(String results : searchResults){
            searchTitle.add(new items(results.toString()));;
        }
        resultView = (ListView) findViewById(R.id.searchResults);

        resultView.setAdapter(new searchAdapter(this, searchTitle));
        resultView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView resultsView, View view, int position,
                                    long id) {
               String link = searchTitle.get(position).link;
                Intent startActivity = new Intent(getApplicationContext(),webActivity.class);
                startActivity.putExtra("link",link);
                startActivityForResult(startActivity, 15);
                Toast.makeText(getBaseContext(), link, Toast.LENGTH_SHORT).show();


            }
        });

    }



}



class searchAdapter extends BaseAdapter {



    Context context;
    ArrayList<items> data;
    private static LayoutInflater inflater = null;


    public searchAdapter(Context context, ArrayList<items> data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public items getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        final RatingBar ratingBar;
        if (vi == null)
            vi = inflater.inflate(R.layout.list_item, null);
        TextView title = (TextView) vi.findViewById(R.id.title);
        TextView link = (TextView) vi.findViewById(R.id.link);
        ratingBar = (RatingBar) vi.findViewById(R.id.ratingBar);


        final String a =  data.get(position).title;
        final String b =  data.get(position).link;
        title.setText(a);
        link.setText(b);

       ratingBar.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View view, MotionEvent event) {

               if (event.getAction() == MotionEvent.ACTION_UP) {
                   float touchPositionX = event.getX();
                   float width = ratingBar.getWidth();
                   float starsf = (touchPositionX / width) * 5.0f;
                   int stars = (int)starsf + 1;
                   ratingBar.setRating(stars);

               }

               if (event.getAction() == MotionEvent.ACTION_DOWN) {
                   view.setPressed(true);
               }

               if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                   view.setPressed(false);
               }

                       try {
                           Class.forName("com.mysql.jdbc.Driver");
                           Connection con = DriverManager.getConnection("jdbc:mysql://" + MainActivity.ipAddress + ":" + MainActivity.port + "/KingstonGeneralHospital", "root", "root");
                           Statement stmt = con.createStatement();
                           Log.d("S", "Success");


                           String s = "UPDATE SearchResults SET Rating = " + ratingBar.getRating() + " WHERE SearchID = '" + MainActivity.searchID  + "' AND SearchResult = '" + a + ":::" + b + "'";
                           stmt.executeUpdate(s);



                       } catch (Exception e) {
                           e.printStackTrace();

                       }
                return  true;
                   }
               });

        return vi;
    }



}


