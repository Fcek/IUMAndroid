package com.example.iumandroid.wrapper;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.iumandroid.core.Product;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Wrapper {

    public static ContentValues product2Cv(Product product){
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

    public static List<Product> cv2Product(Cursor cursor){
        List<Product> products = new ArrayList<>();
        while (cursor.moveToNext()) {
            Product product = new Product();
            product.setId(cursor.getInt(
                    cursor.getColumnIndexOrThrow("id")
            ));
            product.setServerId(cursor.getInt(
                    cursor.getColumnIndexOrThrow("serverid")
            ));
            product.setName(cursor.getString(
                    cursor.getColumnIndexOrThrow("name")
            ));
            product.setManufacturer(cursor.getString(
                    cursor.getColumnIndexOrThrow("manufacturer")
            ));
            product.setPrice(cursor.getFloat(
                    cursor.getColumnIndexOrThrow("price")
            ));
            product.setAmount(cursor.getInt(
                    cursor.getColumnIndexOrThrow("amount")
            ));
            product.setCreated(new Date(cursor.getInt(
                    cursor.getColumnIndexOrThrow("created")
            )));
            product.setUpdated(new Date(cursor.getInt(
                    cursor.getColumnIndexOrThrow("updated")
            )));
            products.add(product);
        }
        cursor.close();
        return products;
    }

}
