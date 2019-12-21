package com.example.iumandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.iumandroid.core.Account;
import com.example.iumandroid.core.Product;
import com.example.iumandroid.db.MyDbHandler;
import com.example.iumandroid.services.ApiService;
import com.example.iumandroid.utilities.Hash;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class LoginActivity extends AppCompatActivity {


    private final String url = "http://192.168.0.94:9090/";
    private final String url1 = "http://10.0.2.2:9090/";
    private final String url2 = "https://jsonplaceholder.typicode.com/todos/";
    // nasz interfejs
    ApiService apiService;

    private Retrofit initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url1)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Retrofit retrofit = initRetrofit();
        apiService = retrofit.create(ApiService.class);
        final EditText email = findViewById(R.id.login);
        final EditText pwd = findViewById(R.id.pwd);

        MyDbHandler dbHelper = new MyDbHandler(this);

        if(!isNetworkAvailable()){
            SQLiteDatabase db1 = dbHelper.getReadableDatabase();
            String[] selectionArgs = {"1"};
            Cursor cursor = db1.query("currentuser", null, "id = ?", selectionArgs, null, null, "id DESC");
            final List itemIds = new ArrayList<>();
            while (cursor.moveToNext()) {
                long logged = cursor.getLong(
                        cursor.getColumnIndexOrThrow("logged"));
                String role = cursor.getString(
                        cursor.getColumnIndexOrThrow("role")
                );
                itemIds.add(logged);
                itemIds.add(role);
            }
            cursor.close();
            db1.close();
            Toast.makeText(getApplicationContext(),"abc: "+itemIds, Toast.LENGTH_SHORT).show();
            if((long)itemIds.get(0) == 1){
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("role", String.valueOf(itemIds.get(1)));
                startActivity(intent);
                db1.close();
                finish();
            } else {
                Toast.makeText(getApplicationContext(),"Not connected to internet", Toast.LENGTH_SHORT).show();
            }
        }

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("role", "client");
        values.put("logged", 0);
        db.update("currentuser", values, "id=1", null);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.loginbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailText = email.getText().toString();
                final String pwdText = pwd.getText().toString();
                Call<Account> call = apiService.getAccount(emailText);
                call.enqueue(new Callback<Account>() {
                    @Override
                    public void onResponse(Call<Account> call, Response<Account> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getPassword().equals(Hash.md5(pwdText))) {
                                Toast.makeText(getApplicationContext(), "Logged Successfully", Toast.LENGTH_SHORT).show();
                                //startActivity ( new Intent(LoginActivity.this, MainActivity.class) );
                                ContentValues values = new ContentValues();
                                values.put("role", response.body().getRole());
                                values.put("logged", 1);
                                db.update("currentuser", values, "id=1", null);
                                //Toast.makeText(getApplicationContext(), "abc: " + itemIds.get(1), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("role", response.body().getRole());
                                startActivity(intent);
                                db.close();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Wrong Password, try again...", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong Email, try again...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Account> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Can't connect to the server", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        findViewById(R.id.registerbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    @Override
    protected void onStart() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("role", "client");
            startActivity(intent);
        }
        super.onStart();
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            String idToken = account.getIdToken();

            MyDbHandler dbHelper = new MyDbHandler(this);
            final SQLiteDatabase db = dbHelper.getWritableDatabase();

            Retrofit retrofit = initRetrofit();
            apiService = retrofit.create(ApiService.class);

            Call<String> call = apiService.verify(idToken);
            System.out.println(idToken);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    Toast.makeText(getApplicationContext(), "Verified", Toast.LENGTH_SHORT).show();

                    ContentValues values = new ContentValues();
                    values.put("role", "client");
                    values.put("logged", 1);
                    db.update("currentuser", values, "id=1", null);
                    db.close();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("role", "client");
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Did not verify", Toast.LENGTH_SHORT).show();
                }
            });


        } catch (ApiException e) {
            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
        }
    }

}

