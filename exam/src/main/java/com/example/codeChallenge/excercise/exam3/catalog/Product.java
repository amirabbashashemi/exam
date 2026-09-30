package com.example.codeChallenge.excercise.exam3.catalog;

import java.util.Date;

public class Product {
    Long productId;
    String productName;
    Date cacheTime;

    public Product(Long productId, String productName) {
        this.productId = productId;
        this.productName = productName;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Date getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(Date cacheTime) {
        this.cacheTime = cacheTime;
    }
}
