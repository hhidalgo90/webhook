package com.shopify.shopifywebhook.repository;

import com.shopify.shopifywebhook.model.entity.ProcessedOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedOrderRepository extends JpaRepository<ProcessedOrder, Long> {
}
