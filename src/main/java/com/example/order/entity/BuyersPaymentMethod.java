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
public class BuyersPaymentMethod implements Serializable {
    public BigInteger id;
    public BigInteger buyersid;
    public String  category;
    public String  displayname;
    public String  description;

    public String  creditcardcompany;
    public String  cardholdername;
    public String  accountnumber;
    public String  bankpobox;
    public String  bankname;
    public String  accountholdername;
    public String  currency;
    public String  routingnumber;
    public String  bankaddress;
    public String  billingaddress;
    public String  receipientname;
    public String  phonenumber;
    public String  receipientaddress;

    //中转
    public String  inputSearch;

    public Date createdate;
    public Date lastupdate;

}
