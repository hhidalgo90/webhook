package com.shopify.shopifywebhook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Courier {

    private int id; // ID del courier seleccionado
    private String client; // Nombre del courier seleccionado
    private boolean payable = false; // Indica si el envío será con pago a contra entrega
    private boolean selected = false; // Indica si el envío va con courier seleccionado
    private int algorithm = 1; // Algoritmo predilecto para realizar el envío
    private int algorithmDays = 0; // Cantidad de días a evaluar sobre el algoritmo 2
    private boolean withoutCourier = false; // Indica si el envío necesita otro tipo de despachos
}
