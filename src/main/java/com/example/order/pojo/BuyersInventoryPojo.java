package com.example.order.pojo;

import com.example.order.entity.BuyersTask;
import com.example.order.entity.WarehouseProduct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BuyersInventoryPojo implements Serializable {
    public BigInteger id;
    public BigInteger sellerid;
    public BigInteger buyersid;
    public String username;
    public String conditionvalue;
    public String name;
    public String upc;
    public BigInteger quantity;

    //转储
    public String sellerrelname;
}
