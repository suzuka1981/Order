package com.example.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/*
买手自家地址
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyersSelfStorage implements Serializable {
    public BigInteger id;
    public String sitename;
    public String addressline1;
    public String addressline2;
    public String city;
    public String state;
    public String zipcode;

    public Date createdate;
    public Date lastupdate;
    public Integer isdelete;
}
