package com.example.demo.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class Order {
    UUID id;
    String address;
    String phone;
    String comment;
    Double finalPrice;
    UUID operatorId;
    UUID clientId;
}
