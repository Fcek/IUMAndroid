package com.example.iumandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button loginClick = (Button) findViewById(R.id.loginbtn);
        loginClick.setOnClickListener(this);
        Button registerClick = (Button) findViewById(R.id.registerbtn);
        registerClick.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.loginbtn: {
                System.out.println("abc1");
                break;
            }

            case R.id.registerbtn: {
                this.startActivity ( new Intent(this, RegisterActivity.class) );
                break;
            }
        }
    }
}
