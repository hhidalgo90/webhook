package com.shopify.shopifywebhook.feignclient;

import com.shopify.shopifywebhook.model.Regions;
import com.shopify.shopifywebhook.model.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "shipitSkuClient", url = "${shipit.apiSku.url}")
public interface ShipitSkuClient {

    @GetMapping(consumes = "application/json", headers = {
            "Content-Type=application/json","Accept=application/vnd.shipit.v4"
    })
    ResponseEntity<List<Sku>> getAllSku(
            @RequestHeader("X-Shipit-Email") String email,
            @RequestHeader("X-Shipit-Access-Token") String accessToken
    );
}
