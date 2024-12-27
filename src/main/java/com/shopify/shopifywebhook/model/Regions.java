package com.shopify.shopifywebhook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown=true)
public class Regions {
    private int country_id;
    private String country_name;
    private int id;
    private String name;
    private String postal_code;
    private int region_id;
    private String region_name;
}
