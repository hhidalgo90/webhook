package com.shopify.shopifywebhook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShopifyProduct {

    // Getters and Setters
    private long id;
    private int grams;
    private String name;
    private String price;
    private long productId;
    private List<Object> properties;
    private int quantity;
    private String sku;
    private String title;
    private String totalDiscount;
    private long variantId;
    private String variantInventoryManagement;

}

