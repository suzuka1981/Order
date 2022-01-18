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
public class PaymentRequest implements Serializable {
    public BigInteger id;
    public BigInteger methodsid;
    public BigInteger sellerid;
    public BigInteger buyersid;
    public BigDecimal amount;
    public String  status;

    //中转用
    public String  comment;

    public String  description;
    public String  displayname;
    public String  sellerusername;
    public String  sellerrelname;
    public String  buyersusername;
    public String  buyersrelname;
    public BigDecimal sellertotalbalance;
    public Date createdate;
    public Date lastupdate;
}
