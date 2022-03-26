package com.example.sensorApplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText loginField, passField;
    Button login, register;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginField = (TextInputEditText) findViewById(R.id.loginFieldnew);
        passField = (TextInputEditText) findViewById(R.id.pwdFieldnew);
        login = (Button) findViewById(R.id.loginButton);
        register = (Button) findViewById(R.id.signupButton);
        DB = new DBHelper(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = loginField.getText().toString();
                String pass = passField.getText().toString();

                if(user.equals("") || pass.equals("")){
                    Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                }
                else{
                    Boolean checkuserpass = DB.checkUsernamePassword(user,pass);
                    if(checkuserpass==true){
                        Toast.makeText(LoginActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), InitialPage.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Credentials not found", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
}