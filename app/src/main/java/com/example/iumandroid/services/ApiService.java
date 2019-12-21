package com.example.iumandroid.services;

import com.example.iumandroid.core.Account;
import com.example.iumandroid.core.Product;
import com.example.iumandroid.core.Synchronize;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;


public interface ApiService {

    @GET("product")
    Call<Product> getProduct(@Query("q") int id);

    @GET("products")
    Call<List<Product>> getAllProducts();

    @GET("account")
    Call<Account> getAccount(@Query("q") String email);

    @GET("account/id")
    Call<Account> getAccountId(@Query("q") int id);

    @POST("create/account")
    Call<Account> createAccount(@Body Account account);

    @POST("create/product")
    Call<Product> createProduct(@Body Product product);

    @PUT("update/product")
    Call<Product> updateProduct(@Body Product product);

    @PUT("update")
    Call<Synchronize> updateAll(@Body Synchronize synchronize);

    @DELETE("delete/product")
    Call<Product> deleteProduct(@Query("q") int id);

    @POST("google")
    Call<String> verify(@Body String token);
}
