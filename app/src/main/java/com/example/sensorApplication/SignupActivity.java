package com.example.sensorApplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    EditText uname, pwd1, pwd2;
    Button butn1;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        uname = (EditText) findViewById(R.id.usernameRegFieldnew);
        pwd1 = (EditText) findViewById(R.id.pass1RegFieldnew);
        pwd2 = (EditText) findViewById(R.id.pass2RegFieldnew);
        butn1 = (Button) findViewById(R.id.button);
        DB = new DBHelper(this);

        butn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = uname.getText().toString();
                String pass = pwd1.getText().toString();
                String repass = pwd2.getText().toString();

                if(user.equals("") || pass.equals("") || repass.equals("")){
                    Toast.makeText(SignupActivity.this, "Please enter all fields correctly", Toast.LENGTH_SHORT).show();
                }

                else{
                    if(pass.equals(repass)){
                        Boolean checkuser = DB.checkUsername(user);
                        if(checkuser==false){
                            Boolean insert = DB.insertData(user,pass);
                            if(insert==true){
                                Toast.makeText(SignupActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(SignupActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(SignupActivity.this, "User already exists. Please use Sign In", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(SignupActivity.this, "Passwords not matching", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


    }
}