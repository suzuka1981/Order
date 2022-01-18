package com.example.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

//confirm 数据
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyersConfirm implements Serializable {
    public BigInteger id;
    public BigInteger buyersid;
    public BigInteger sellerid;
    public Integer quantity;
    public String  selfsite;
    public String  trackingnumber;

    //payment 充当url地址
    public String  status;

    public BigDecimal price;
    public String type;
    public Date createdate;
    public Date lastupdate;
    public BigInteger buyerstaskid;

    public String historystatus;
    public String note;

    public BigInteger paymentrequestid;
    public BigInteger methodsid;

    //转换临时用不做数据存储
    public String username;
    public String name;
    public String sitename;
    public String address1;
    public String sendto;
    public String selfsitename;
    public String selfaddress1;

    //product
    public BigInteger productid;
    public String conditionvalue;
    public String productname;
    public BigDecimal productprice;
    public String productnote;

}
