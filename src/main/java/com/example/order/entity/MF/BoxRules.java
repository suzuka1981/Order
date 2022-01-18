package com.example.order.entity.MF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoxRules implements Serializable {
    public String silgleBoxQuantity;
    public String boxWeight;
    public String boxLength;
    public String boxWidth;
    public String boxHeight;
    public String singleBoxRule;
}
