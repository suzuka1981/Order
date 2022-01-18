package com.example.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

//warehouse product
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseProduct implements Serializable {
    public BigInteger id;
    public Integer inbound;
    public BigInteger orgid;
    public BigDecimal price;

    public String name;
    public String conditionvalue;
    public String asin;
    public String sku;
    public String upc;
    public String checked;
    public String note;

    public String status;

    public String mfsku;

    //中转
    public Integer suminbound;
    public Integer suminstock;

    public String quantitylist;
    public String inboundlist;

    public BigDecimal sumvalue;

    public String buyersconfirmsize;

    public Date createdate;
    public Date lastupdate;
}
