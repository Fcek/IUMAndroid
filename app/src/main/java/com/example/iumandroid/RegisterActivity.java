package com.example.iumandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.iumandroid.core.Account;
import com.example.iumandroid.services.ApiService;
import com.example.iumandroid.utilities.Hash;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RegisterActivity extends AppCompatActivity{

    ApiService apiService;
    private final String url = "http://192.168.0.94:9090/";
    private final String url1 = "http://10.0.2.2:9090/";
    private final String url2 = "https://jsonplaceholder.typicode.com/todos/";
    private Retrofit initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url1)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final EditText email = findViewById(R.id.emailRegister);
        final EditText pwd = findViewById(R.id.pwdRegister);
        final EditText cpwd = findViewById(R.id.confpwdRegister);
        final CheckBox check = findViewById(R.id.acceptTerms);

        Retrofit retrofit = initRetrofit();
        apiService = retrofit.create(ApiService.class);

        findViewById(R.id.createAcc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString();
                String pwdText = pwd.getText().toString();
                String cpwdText = cpwd.getText().toString();
                if(pwdText.equals(cpwdText) && check.isChecked()){
                    Account account = new Account();
                    account.setEmail(emailText);
                    account.setPassword(Hash.md5(pwdText));

                    Call<Account> call = apiService.createAccount(account);
                    call.enqueue(new Callback<Account>() {
                        @Override
                        public void onResponse(Call<Account> call, Response<Account> response) {
                            Toast.makeText(getApplicationContext(),"Created", Toast.LENGTH_SHORT).show();
                            startActivity ( new Intent(RegisterActivity.this, MainActivity.class) );
                        }

                        @Override
                        public void onFailure(Call<Account> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                            System.out.println(t.getMessage());
                        }
                    });
                }

            }
        });
    }
}
