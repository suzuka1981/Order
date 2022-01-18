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
public class EmailCreateShipment implements Serializable {
    public BigInteger id;
    public String email;
    public String servicetype;
    public String otherservices;
    public Integer packagequantity;
    public String product;
    public String location;
    public String upc;
    public String url;
    public String instruction;
    public String urlunsubscribe;
    public Integer quantity;
}
