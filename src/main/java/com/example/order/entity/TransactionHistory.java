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
public class TransactionHistory implements Serializable {
    public BigInteger id;
    public BigInteger buyersconfirmid;
    public BigInteger buyersid;
    public BigInteger sellerid;
    public Date createdate;
    public String  sellerusername;
    public String  sellerrelname;
    public String  type;
    public String  productname;
    public String  topaystatus;

    public BigDecimal price;
    public Integer quantity;
    public String  onlyshiptowarehouse;

    public BigDecimal amount;
    public BigDecimal balance;

    //中转用
    public String historystatus;
    public String buyersname;
    public String buyersrelname;

    public String note;
    public BigInteger paymentrequestid;
    public BigInteger methodsid;

    public String  category;
    public String  displayname;
    public String  description;

    public Date lastupdate;
}
