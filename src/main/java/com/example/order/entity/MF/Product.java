package com.example.order.entity.MF;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product implements Serializable {
    //必填 - 有
    public String productName;
    //必填 - 有
    public String enProductName;
    //必填 - 有
    public String upcBarcode;
    //必填
    public String height;
    //必填
    public String width;
    //必填
    public String length;
    //必填
    public String unitPrice;
    //必填
    public String insurancePrice;
    //必填
    public String includeBattery;
    //必填
    public String suitOrPacking;
    //必填
    public String productType;
    //必填
    public String unitChoose;
    //必填
    public String firstWeight;
    //必填
    public String secondWeight;

    public String uses;
    public String fnsku;
    public String brand;
    public String material;
    public String format;
    public String properties;
    public String remark;
    public String warningInventory;
    public String extraBarcode1;
    public String extraBarcode2;
    public String extraBarcode3;
    public String combinationType;
    public String ean;
    public String productUrl;
    public String needSerialNumber;
    public String batteiresContained;
    public String batteiresType;
    public List<BoxRules> boxRulesList;
}
