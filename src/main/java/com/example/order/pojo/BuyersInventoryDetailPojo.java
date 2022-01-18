package com.example.order.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BuyersInventoryDetailPojo implements Serializable {
    public BigInteger id;
    public BigInteger quantity;
    public String conditionvalue;
    public String name;
    public String sitename;
    public String address1;
    public String type;
    public String tracking;
    public String wsitename;
    public String waddress1;
}
