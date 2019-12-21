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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.iumandroid.core.Product;
import com.example.iumandroid.core.Synchronize;
import com.example.iumandroid.db.MyDbHandler;
import com.example.iumandroid.services.ApiService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import com.example.iumandroid.wrapper.Wrapper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class MainActivity extends AppCompatActivity {

    // nasz interfejs
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String role = (String) getIntent().getExtras().get("role");

        Retrofit retrofit = initRetrofit();
        apiService = retrofit.create(ApiService.class);

        final MyDbHandler dbHelper = new MyDbHandler(this);

        final Spinner products = findViewById(R.id.products);
        final EditText manufacturer = findViewById(R.id.pManu);
        final EditText model = findViewById(R.id.pModel);
        final EditText price = findViewById(R.id.pPrice);
        final EditText quantity = findViewById(R.id.pQuantity);
        final EditText number = findViewById(R.id.number);
        Button remove = findViewById(R.id.removebtn);
        Button update2Server = findViewById(R.id.updateserver);
        final List<String> productsString = new ArrayList<>();
        final List<Product> allProducts = new ArrayList<>();
        final List<Integer> deleted = new ArrayList<>();

        final SQLiteDatabase dbr = dbHelper.getReadableDatabase();
        final SQLiteDatabase dbw = dbHelper.getWritableDatabase();

       final Call<List<Product>> productListCall = apiService.getAllProducts();
       productListCall.enqueue(new Callback<List<Product>>() {
           @Override
           public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
               allProducts.addAll(response.body());

               dbw.execSQL("DROP TABLE products");
               dbw.execSQL("CREATE TABLE products (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                       "manufacturer TEXT, name TEXT, price REAL, amount INTEGER, " +
                       "created INTEGER, updated INTEGER, serverid INTEGER)");
               ContentValues cvProduct;
               for (Product p : allProducts) {
                   cvProduct = Wrapper.product2Cv(p);
                   dbw.insert("products", null, cvProduct);
                   productsString.add(p.toString());
               }
               ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,productsString);
               arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
               products.setAdapter(arrayAdapter);
               Cursor cursor = dbr.query("products", null, null, null, null, null, "id DESC");
               final List itemIds = new ArrayList<>();
               while (cursor.moveToNext()) {
                   String role = cursor.getString(
                           cursor.getColumnIndexOrThrow("name")
                   );
                   System.out.println(role);
               }
               cursor.close();
           }

           @Override
           public void onFailure(Call<List<Product>> call, Throwable t) {
               Toast.makeText(getApplicationContext(),"Can't connect to the server", Toast.LENGTH_SHORT).show();
               Cursor cursor = dbr.query("products", null, null, null, null, null, "id DESC");
               List<Product> allProductsOffline = Wrapper.cv2Product(cursor);
               allProducts.addAll(allProductsOffline);
               for (Product p : allProducts) {
                   productsString.add(p.toString());
               }
               ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,productsString);
               arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
               products.setAdapter(arrayAdapter);
           }
       });

        if(role.equals("admin")){
            remove.setEnabled(true);
        } else {
            remove.setEnabled(false);
        }

        products.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Product selected = allProducts.get(position);
                manufacturer.setText(selected.getManufacturer());
                model.setText(selected.getName());
                price.setText(String.valueOf(selected.getPrice()));
                quantity.setText(String.valueOf(selected.getAmount()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.increase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Product selected = allProducts.get(products.getSelectedItemPosition());
                System.out.println(Integer.valueOf(number.getText().toString()));
                selected.setAmount(selected.getAmount() + (Integer.valueOf(number.getText().toString())));
                selected.setUpdated(new Date());
                Call<Product> call = apiService.updateProduct(selected);
                call.enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        Toast.makeText(getApplicationContext(),"Increased", Toast.LENGTH_SHORT).show();
                        quantity.setText(String.valueOf(selected.getAmount()));
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"No connection", Toast.LENGTH_SHORT).show();
                        dbw.update("products", Wrapper.product2Cv(selected), "id = " + selected.getId(), null);
                        quantity.setText(String.valueOf(selected.getAmount()));
                    }
                });
            }
        });

        findViewById(R.id.decrease).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Product selected = allProducts.get(products.getSelectedItemPosition());
                if(selected.getAmount() - (Integer.valueOf(number.getText().toString())) >= 0){
                    selected.setAmount(selected.getAmount() - (Integer.valueOf(number.getText().toString())));
                    selected.setUpdated(new Date());
                    Call<Product> call = apiService.updateProduct(selected);
                    call.enqueue(new Callback<Product>() {
                        @Override
                        public void onResponse(Call<Product> call, Response<Product> response) {
                            Toast.makeText(getApplicationContext(),"Decreased", Toast.LENGTH_SHORT).show();
                            quantity.setText(String.valueOf(selected.getAmount()));
                        }

                        @Override
                        public void onFailure(Call<Product> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),"Something went wrong", Toast.LENGTH_SHORT).show();
                            dbw.update("products", Wrapper.product2Cv(selected), "id = " + selected.getId(), null);
                            quantity.setText(String.valueOf(selected.getAmount()));
                        }
                    });
                }
            }
        });

        findViewById(R.id.updatebtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Product selected = allProducts.get(products.getSelectedItemPosition());
                selected.setManufacturer(String.valueOf(manufacturer.getText()));
                selected.setName(String.valueOf(model.getText()));
                selected.setAmount(Integer.valueOf(quantity.getText().toString()));
                selected.setPrice(Float.valueOf(price.getText().toString()));
                selected.setUpdated(new Date());
                Call<Product> call = apiService.updateProduct(selected);
                call.enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        Toast.makeText(getApplicationContext(),"Updated", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"Something went wrong", Toast.LENGTH_SHORT).show();
                        dbw.update("products", Wrapper.product2Cv(selected), "id = " + selected.getId(), null);
                    }
                });
            }
        });

        findViewById(R.id.addbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity ( new Intent(MainActivity.this, AddProductActivity.class) );
            }
        });

        findViewById(R.id.removebtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Call<Product> call = apiService.deleteProduct((int) allProducts.get(products.getSelectedItemPosition()).getId());
                call.enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        Toast.makeText(getApplicationContext(),"Removed", Toast.LENGTH_SHORT).show();
                        recreate();
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"Removed offline", Toast.LENGTH_SHORT).show();
                        deleted.add((int) allProducts.get(products.getSelectedItemPosition()).getId());
                        dbw.delete("products", "id = " + (int) allProducts.get(products.getSelectedItemPosition()).getId(), null);
                        recreate();;
                    }
                });
            }
        });

        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.server_client_id))
                        .requestEmail()
                        .build();
                final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
                if(mGoogleSignInClient != null){
                    mGoogleSignInClient.signOut();
                }
                ContentValues values = new ContentValues();
                values.put("role", "client");
                values.put("logged", 0);
                dbw.update("currentuser", values, "id=1", null);
                startActivity ( new Intent(MainActivity.this, LoginActivity.class) );
                finish();
            }
        });

        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        findViewById(R.id.updateserver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    Cursor cursor = dbr.query("products", null, null, null, null, null, "id DESC");
                    List<Product> allProductsOffline = Wrapper.cv2Product(cursor);
                    Synchronize synchronize = new Synchronize();
                    synchronize.setDeleted(deleted);
                    synchronize.setProductList(allProductsOffline);
                    Call<Synchronize> call = apiService.updateAll(synchronize);
                    call.enqueue(new Callback<Synchronize>() {
                        @Override
                        public void onResponse(Call<Synchronize> call, Response<Synchronize> response) {
                            if(response.code() == 200){
                                Toast.makeText(getApplicationContext(),"Synchronized successfully", Toast.LENGTH_SHORT).show();
                            }
                            recreate();
                        }

                        @Override
                        public void onFailure(Call<Synchronize> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),"Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(),"Not connected to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
