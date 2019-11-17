package com.example.iumandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.iumandroid.core.Product;
import com.example.iumandroid.services.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

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
    boolean created = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String role = (String) getIntent().getExtras().get("role");

        Retrofit retrofit = initRetrofit();
        apiService = retrofit.create(ApiService.class);

        final Spinner products = findViewById(R.id.products);
        final EditText manufacturer = findViewById(R.id.pManu);
        final EditText model = findViewById(R.id.pModel);
        final EditText price = findViewById(R.id.pPrice);
        final EditText quantity = findViewById(R.id.pQuantity);
        final EditText number = findViewById(R.id.number);
        Button remove = findViewById(R.id.removebtn);
        final List<Product> allProducts = new ArrayList<>();
        final List<String> productsString = new ArrayList<>();
        created = true;


        final Call<List<Product>> productListCall = apiService.getAllProducts();
        productListCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                allProducts.addAll(response.body());
                for (Product p : allProducts) {
                    productsString.add(p.toString());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,productsString);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                products.setAdapter(arrayAdapter);
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Can't connect to the server", Toast.LENGTH_SHORT).show();
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
                Call<Product> call = apiService.updateProduct(selected);
                call.enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        Toast.makeText(getApplicationContext(),"Increased", Toast.LENGTH_SHORT).show();
                        quantity.setText(String.valueOf(selected.getAmount()));
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"Something went wrong", Toast.LENGTH_SHORT).show();
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
                        }
                    });
                }
            }
        });

        findViewById(R.id.updatebtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product selected = allProducts.get(products.getSelectedItemPosition());
                selected.setManufacturer(String.valueOf(manufacturer.getText()));
                selected.setName(String.valueOf(model.getText()));
                selected.setAmount(Integer.valueOf(quantity.getText().toString()));
                selected.setPrice(Float.valueOf(price.getText().toString()));
                Call<Product> call = apiService.updateProduct(selected);
                call.enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        Toast.makeText(getApplicationContext(),"Updated", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"Something went wrong", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(),"Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
//    @Override
//    protected void onResume(){
//        super.onResume();
//        if(created){
//            recreate();
//        }
//
//    }

}
