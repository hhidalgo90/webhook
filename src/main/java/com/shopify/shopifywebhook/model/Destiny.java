package com.shopify.shopifywebhook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Destiny {

    private String street; // Calle de la dirección de destino
    private String number; // Número de la calle de destino
    private String complement; // Complemento de la dirección (piso, depto, etc.)

    @JsonProperty(namespace = "commune_id")
    private int commune_id; // ID de la comuna de destino

    @JsonProperty(namespace = "commune_name")
    private String commune_name; // Nombre de la comuna de destino

    @JsonProperty(namespace = "full_name")
    private String full_name; // Nombre completo del destinatario
    private String email; // Correo de contacto del destinatario
    private String phone; // Teléfono de contacto del destinatario
    private String kind; // Tipo de destino (home_delivery, courier_branch_office, fulfillment_delivery)
    private Integer courierBranchOfficeId; // ID de la sucursal del courier (opcional)
}
