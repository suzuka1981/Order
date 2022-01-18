package com.example.order.pojo;

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
public class ProviderOfferPojo implements Serializable {
    public BigInteger id;

    public BigInteger orgid;
    public BigDecimal bonus;
    public Date expirationdate;
    public Integer minselfstoragequantity;
    public String sellerusername;
    public String sellerrelname;
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
    public String version;
    public String offerstatus;

    //task用
    public String sendto;
    public String status;
    public BigInteger selfid;
    public BigInteger offerid;
    public Integer subquantity;

    //卖家用
    public String selfname;
    public String selfrelname;

    public BigDecimal wprice;

    public Date createdate;
    public Date lastupdate;

    public WarehouseProduct warehouseProduct;
}
