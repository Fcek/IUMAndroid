package com.example.iumandroid.wrapper;

import android.content.ContentValues;

import com.example.iumandroid.core.Product;

public class Wrapper {

    public static ContentValues cvProduct(Product product){
        ContentValues cv = new ContentValues();
        cv.put("serverid", product.getId());
        cv.put("name", product.getName());
        cv.put("manufacturer", product.getManufacturer());
        cv.put("price", product.getPrice());
        cv.put("amount", product.getAmount());
        if(product.getCreated() != null){
            cv.put("created", product.getCreated().getTime());
        }
        cv.put("updated", product.getUpdated().getTime());
        return cv;
    }

}
