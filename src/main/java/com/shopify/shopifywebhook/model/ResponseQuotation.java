package com.shopify.shopifywebhook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseQuotation {
    @JsonProperty("algorithm")
    private int algorithm = 1;

    @JsonProperty("algorithm_days")
    private int algorithm_days = 3;

    @JsonProperty("courier_for_client")
    private String courier_for_client;

    @JsonProperty("prices")
    private List<Price> prices;

    @JsonProperty("lower_price")
    private Price lower_price;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Price {
        @JsonProperty("courier")
        private Courier courier;

        @JsonProperty("discount")
        private Discount discount;

        @JsonProperty("available_to_shipping")
        private boolean available_to_shipping;

        @JsonProperty("days")
        private int days;

        @JsonProperty("is_payable")
        private boolean is_payable;

        @JsonProperty("name")
        private String name;

        @JsonProperty("original_courier")
        private String original_courier;

        @JsonProperty("price")
        private float price;

        @JsonProperty("volumetric_weight")
        private float volumetric_weight;

        @JsonProperty("service_type")
        private String service_type;

        @JsonProperty("insurance")
        private Insurance insurance;

        @JsonProperty("delivery_type")
        private String delivery_type;

        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Courier {
            @JsonProperty("name")
            private String name;

            @JsonProperty("display_name")
            private String display_name;

            @JsonProperty("packet_from")
            private String packet_from;

            @JsonProperty("packet_to")
            private String packet_to;
        }

        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Discount {
            @JsonProperty("original_price")
            private float original_price;

            @JsonProperty("total")
            private float total;

            @JsonProperty("shipment_discounts")
            private List<ShipmentDiscount> shipment_discounts;

            @Getter
            @Setter
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class ShipmentDiscount {
                @JsonProperty("active")
                private boolean active;

                @JsonProperty("description")
                private String description;

                @JsonProperty("id")
                private int id;

                @JsonProperty("kind")
                private int kind;

                @JsonProperty("name")
                private String name;

                @JsonProperty("rules")
                private List<Rule> rules;

                @JsonProperty("value")
                private String value;

                public static class Rule {
                    @JsonProperty("active")
                    private boolean active;

                    @JsonProperty("condition")
                    private int condition;

                    @JsonProperty("id")
                    private int id;

                    @JsonProperty("name")
                    private int name;

                    @JsonProperty("value")
                    private String value;
                }
            }
        }

        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Insurance {
            @JsonProperty("max")
            private float max;

            @JsonProperty("min")
            private float min;

            @JsonProperty("percent")
            private float percent;
        }
    }
}

