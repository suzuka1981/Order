package com.example.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Offer implements Serializable {
    public BigInteger id;

    public BigInteger orgid;
    public BigDecimal bonus;
    public Date expirationdate;
    public Integer minselfstoragequantity;
    public String movetotop;
    public String note;
    public String notifymembers;
    public BigInteger productid;
    public String onlyshiptowarehouse;
    public String pendingperioddays;
    public BigDecimal price;
    public Integer quantity;
    public Integer usedquantity;
    public String requiredservicetag;
    public String visibletoallmembers;
    public String warehousesitesvalue;
    public String offer;
    //    archived 归档
    public String status;
    public Integer version;

    public Date createdate;
    public Date lastupdate;
}
