package com.example.conor.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
public class loginActivity extends AppCompatActivity {

    public EditText email;
    public EditText password;
    public Button loginButton;
    public TextView signupLink;
    public static String emailAddress;
    public ProgressDialog progressDialog;
    public Thread mThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        email = (EditText) findViewById(R.id.input_email);
        password = (EditText) findViewById(R.id.input_password);

        loginButton = (Button) findViewById(R.id.btn_login);
        signupLink = (TextView) findViewById(R.id.link_signup);

        progressDialog = new ProgressDialog(loginActivity.this,
                ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Logging in...");



        loginButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
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

                            if (!rs.next()) {
                                rs.close();
                                con.close();
                                progressDialog.dismiss();
                                email.setError("username does not exist!");
                            } else {
                                rs.close();
                                s = "SELECT Password FROM Users WHERE Email='" + email.getText() + "'";
                                ResultSet rs2 = stmt.executeQuery(s);
                                rs2.next();
                                String sPassword = rs2.getString(1);
                                rs2.close();
                                String ePassword = Base64.encodeToString(password.getText().toString().getBytes(), Base64.DEFAULT);

                                if (sPassword.equals(ePassword)) {
                                    progressDialog.dismiss();
                                    emailAddress = email.getText().toString();
                                    Intent searchIntent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(searchIntent);

                                } else {
                                    progressDialog.dismiss();
                                    password.setError("incorrect Password!");
                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                };
                mThread.start();
            }

        });

        Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);

        signupLink.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent searchIntent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(searchIntent);
            }

        });
    }

}
