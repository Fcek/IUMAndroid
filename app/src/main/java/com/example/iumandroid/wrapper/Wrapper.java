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
//        cv.put("serverid", product.getId());
//        cv.put("id", product.getId());
        cv.put("name", product.getName());
        cv.put("manufacturer", product.getManufacturer());
        cv.put("price", product.getPrice());
        cv.put("amount", product.getAmount());
        if(product.getCreated() != null){
            cv.put("created", product.getCreated().getTime()/1000);
        }
        cv.put("updated", product.getUpdated().getTime()/1000);
        return cv;
    }

    public static List<Product> cv2Product(Cursor cursor){
        List<Product> products = new ArrayList<>();
        while (cursor.moveToNext()) {
            Product product = new Product();
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
            product.setCreated(new Date((long)cursor.getInt(
                    cursor.getColumnIndexOrThrow("created")
            )*1000L));
            product.setUpdated(new Date((long)cursor.getInt(
                    cursor.getColumnIndexOrThrow("updated")
            )*1000L));
            System.out.println(product.toString());
            products.add(product);
        }
        cursor.close();
        return products;
    }
    public static List<Product> cv2ProductOffline(Cursor cursor){
        List<Product> products = new ArrayList<>();
        while (cursor.moveToNext()) {
            Product product = new Product();
            product.setServerId(cursor.getInt(
                    cursor.getColumnIndexOrThrow("serverid")
            ));
            product.setId(cursor.getInt(
                    cursor.getColumnIndexOrThrow("id")
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
            product.setCreated(new Date((long)cursor.getInt(
                    cursor.getColumnIndexOrThrow("created")
            )*1000L));
            product.setUpdated(new Date((long)cursor.getInt(
                    cursor.getColumnIndexOrThrow("updated")
            )*1000L));
            product.setCount(cursor.getInt(
                    cursor.getColumnIndexOrThrow("count")
            ));
            System.out.println(product.toString());
            products.add(product);
        }
        cursor.close();
        return products;
    }

}
