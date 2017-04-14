package com.example.conor.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;

import android.os.AsyncTask;

import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import android.util.Patterns;
import android.widget.Button;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;


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
import java.util.Queue;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;


public class SignupActivity extends AppCompatActivity {

    public EditText email;
    public EditText password;
    public EditText age;
    public EditText sex;
    public EditText discipline;
    public EditText experience;
    public Button signupButton;
    public TextView loginLink;
    public ProgressDialog progressDialog;
    public Thread mThread;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        email = (EditText) findViewById(R.id.input_email);
        password = (EditText) findViewById(R.id.input_password);
        age = (EditText) findViewById(R.id.input_age);
        sex = (EditText) findViewById(R.id.input_sex);
        discipline = (EditText) findViewById(R.id.input_discipline);
        experience = (EditText) findViewById(R.id.input_experience);

        signupButton = (Button) findViewById(R.id.btn_signup);
        loginLink = (TextView) findViewById(R.id.link_login);

        progressDialog = new ProgressDialog(SignupActivity.this,
                ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Signing up...");

        signupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (validate()) {
                    progressDialog.show();
                    mThread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                Class.forName("com.mysql.jdbc.Driver");
                                Connection con = DriverManager.getConnection("jdbc:mysql://" + MainActivity.ipAddress + ":" + MainActivity.port + "/KingstonGeneralHospital", "root", "root");
                                Statement stmt = con.createStatement();

                                String s = "SELECT Email FROM Users WHERE Email='" + email.getText() + "'";
                                ResultSet rs = stmt.executeQuery(s);
                                String sPassword = Base64.encodeToString(password.getText().toString().getBytes(), Base64.DEFAULT);
                                if (!rs.next()) {
                                    s = "Insert INTO Users VALUES ('" + email.getText() + "', '" + sPassword + "', '" + age.getText() + "', '" + sex.getText() + "', '"
                                            + discipline.getText() + "', '" + experience.getText() + "');";
                                    stmt.executeUpdate(s);
                                    con.close();
                                    loginActivity.emailAddress = email.getText().toString();
                                    Intent searchIntent = new Intent(getApplicationContext(), MainActivity.class);
                                    progressDialog.dismiss();
                                    startActivity(searchIntent);
                                } else {
                                    progressDialog.dismiss();
                                    email.setError("user already exists!");
                                    con.close();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    mThread.start();
                }

            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent searchIntent = new Intent(getApplicationContext(), loginActivity.class);
                startActivity(searchIntent);
            }

        });
    }





    public boolean validate() {
        boolean valid = true;

        String sEmail = email.getText().toString();
        String sPassword = password.getText().toString();
        String sAge = age.getText().toString();
        String sSex = sex.getText().toString();
        String sDiscipline = discipline.getText().toString();
        String sExperience = experience.getText().toString();

        if (sEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            email.setError("Enter a valid email address");
            valid = false;
        }
        else if (!sEmail.matches("^[a-zA-Z0-9_.+-]+@(?:(?:[a-zA-Z0-9-]+\\.)?[a-zA-Z]+\\.)?(queensu)\\.ca$")){
            email.setError("Must be '@queensu.ca'");
            valid = false;

        }

        else {
            email.setError(null);
        }

        if (sPassword.isEmpty() || sPassword.length() < 4 || sPassword.length() > 10) {
            password.setError("Must be between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;

    }






}