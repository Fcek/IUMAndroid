package com.example.iumandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button clickButton = (Button) findViewById(R.id.createAcc);
        clickButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.startActivity ( new Intent(this, LoginActivity.class) );
    }
}
