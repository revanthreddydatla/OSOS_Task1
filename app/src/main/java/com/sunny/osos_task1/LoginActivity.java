package com.sunny.osos_task1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Console;

public class LoginActivity extends AppCompatActivity {

    EditText userNameEditText;
    EditText userPasswordEditText;
    Button loginButton;
    TextView loginFailedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userNameEditText = findViewById(R.id.user_name_input);
        userPasswordEditText = findViewById(R.id.user_password_input);
        loginButton = findViewById(R.id.login_button);
        loginFailedTextView = findViewById(R.id.loginfail_textview);

        SharedPreferences sharedPreferences = getSharedPreferences(BuildConfig.APPLICATION_ID+"_loginStatus",MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        Log.d("rockyLogin", "isLoggedIn: "+isLoggedIn);
        Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);

        if(isLoggedIn){
            startActivity(mainActivityIntent);
        }else{

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String userName = userNameEditText.getText().toString();
                    String userPassword = userPasswordEditText.getText().toString();
                    if(userName.equals("123") && userPassword.equals("123")){
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putBoolean("isLoggedIn", true);
                        myEdit.commit();
                        startActivity(mainActivityIntent);
                    }else{
                        loginFailedTextView.setText("login failed");
                    }
                }
            });

        }

    }
}