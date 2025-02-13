package com.shopify.shopifywebhook.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "PROCESSED_ORDER")
public class ProcessedOrder {

    @Id
    private Long orderId;
}
