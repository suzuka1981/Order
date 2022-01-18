package com.example.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

//仓库地址
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WareHouseSite implements Serializable {
    public BigInteger id;
    public String sitename;
    public BigInteger orgid;
    public BigInteger selfid;
    public String address1;
    public String address2;
    public String state;
    public String city;
    public String zip;
    public String phone;
    public String contact;

    public String appkey;
    public String signature;
    public String mfyccid;

    public String checked;

    public Date createdate;
    public Date lastupdate;
}
