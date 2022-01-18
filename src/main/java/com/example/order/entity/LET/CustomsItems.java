package com.example.order.entity.LET;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomsItems {
    public String description;
    public String brand;
    public String quantity;
    public String weight;
    public String price;
    public String origin_country;
}
