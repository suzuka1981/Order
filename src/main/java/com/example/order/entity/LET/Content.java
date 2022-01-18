package com.example.order.entity.LET;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Content {
    public String customer_reference;
    public Address shipper;
    public Address recipient;
    public List<Packages> packages;
    public List<CustomsItems> customs_items;

    public String carrier;
    public String servicelevel_token;
    public String service_name;
    public String carrier_insurance;
}
