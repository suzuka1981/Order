package com.example.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/*
买手支付卡表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyersPayment implements Serializable {
    public BigInteger id;
    public String category;
    public String displayname;
    public String receipientname;
    public String phonenumber;
    public String receipientaddress;

    public Date createdate;
    public Date lastupdate;
    public Integer isdelete;
}
