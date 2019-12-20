package com.example.iumandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.iumandroid.core.Product;
import com.example.iumandroid.services.ApiService;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class AddProductActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        Retrofit retrofit = initRetrofit();
        apiService = retrofit.create(ApiService.class);

        final EditText manufacturer = findViewById(R.id.aManu);
        final EditText model = findViewById(R.id.aModel);
        final EditText price = findViewById(R.id.aPrice);
        final EditText quantity = findViewById(R.id.aQuantity);

        findViewById(R.id.addbtn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product newProduct = new Product();
                newProduct.setManufacturer(String.valueOf(manufacturer.getText()));
                newProduct.setName(String.valueOf(model.getText()));
                newProduct.setAmount(Integer.valueOf(quantity.getText().toString()));
                newProduct.setPrice(Float.valueOf(price.getText().toString()));
                newProduct.setUpdated(new Date());
                newProduct.setCreated(new Date());
                Call<Product> call = apiService.createProduct(newProduct);
                call.enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        Toast.makeText(getApplicationContext(),"Created", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"Something went wrong", Toast.LENGTH_SHORT).show();
                        //TODO: MAKE IT ADD TO DATABASE WHEN OFFLINE
                    }
                });
            }
        });

    }
}
