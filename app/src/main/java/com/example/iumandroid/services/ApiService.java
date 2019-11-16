package com.example.iumandroid.services;

import com.example.iumandroid.core.Account;
import com.example.iumandroid.core.Product;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiService {
//    @GET("abc")
//    Call<Account> findAccount();

    @GET("product") // deklarujemy endpoint oraz metodę
    Call<Product> getProduct(@Query("q") int id);

    @POST("create/account") // deklarujemy endpoint, metodę oraz dane do wysłania
    Call<Account> createAccount(@Body Account account);
}
