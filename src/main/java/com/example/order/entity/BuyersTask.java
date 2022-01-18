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
public class BuyersTask implements Serializable {
    public BigInteger id;

    public BigInteger orgid;
    public BigInteger selfid;
    public BigInteger offerid;
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
    public String sendto;
    public String status;

    public Integer Version;

    public Integer subquantity;

    //转换数据用
    public String selfsite;
    public String trackingnumber;
    public String type;
    public String tmpstatus;

    ExcelPackages excelPackages;
    //转换数据用

    public Date createdate;
    public Date lastupdate;
}
