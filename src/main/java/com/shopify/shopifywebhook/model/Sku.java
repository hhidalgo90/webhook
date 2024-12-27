package com.shopify.shopifywebhook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Sku {
    private int id;
    private String name;
    private int company_id;
    private Object position;
    private String description;
    private Date created_at;
    private Date updated_at;
    private int warehouse_id;
    private int width;
    private int length;
    private int height;
    private double weight;
    private String barcode;
    private int amount;
    private int min_amount;
    private int ftp_stock;
    private int ftp_updated_at;
}
