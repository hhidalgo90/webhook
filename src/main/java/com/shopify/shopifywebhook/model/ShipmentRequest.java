package com.shopify.shopifywebhook.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShipmentRequest {
    private String reference;
    private int per;
    private int page;
    private String type;
    private String id;
    private String query;
    private String service;
    private String days;
    private String analytics;
    private Map<String, String> filters;
    private String state;
    private String shipment;

    public static ShipmentRequest createDefaultQuery() {
        ShipmentRequest query = new ShipmentRequest();
        query.setReference("");
        query.setPer(50);
        query.setPage(1);
        query.setType("shipments");
        query.setId("");
        query.setQuery("");
        query.setService("");
        query.setDays("");
        query.setAnalytics("");
        query.setState("");
        query.setShipment("");

        Map<String, String> filters = new HashMap<>();
        filters.put("reference", "");
        filters.put("courier_for_client", "");
        filters.put("status", "");
        filters.put("commune", "");
        filters.put("full_name", "");
        filters.put("delivery_type", "");
        query.setFilters(filters);

        return query;
    }
}
