package com.shopify.shopifywebhook.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "shipitClient", url = "${shipit.api.url}")
public interface ShipitClient {

    @PostMapping(consumes = "application/json", headers = {
            "Content-Type=application/json","Accept=application/vnd.shipit.v4"
    })
    ResponseEntity<Void> sendOrder(
            @RequestBody String body,
            @RequestHeader("X-Shipit-Email") String email,
            @RequestHeader("X-Shipit-Access-Token") String accessToken
    );
}

