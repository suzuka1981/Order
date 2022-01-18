package com.example.order.entity.LET;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    public String name;
    public String company;
    public String phone_number;
    public String address;
    public String address_2;
    public String city;
    public String state;
    public String country_code;
    public String postal_code;
    public String residential;
}
