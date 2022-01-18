package com.example.order.entity.LET;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Packages {
    public String height;
    public String width;
    public String length;
    public String weight;
    public String signature_option;
    public String declared_value;
}
