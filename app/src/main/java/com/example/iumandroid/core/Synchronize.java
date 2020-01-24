package com.example.iumandroid.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Synchronize {

    @JsonProperty("products")
    private List<Product> productList;

    @JsonProperty("deleted")
    private List<Integer> deleted;

    @JsonProperty("ssid")
    private int ssid;

    public int getSsid() {
        return ssid;
    }

    public void setSsid(int ssid) {
        this.ssid = ssid;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public List<Integer> getDeleted() {
        return deleted;
    }

    public void setDeleted(List<Integer> deleted) {
        this.deleted = deleted;
    }
}
