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
public class OutboundStatusProductSize implements Serializable {
    public BigInteger id;
    public BigInteger siteid;
    public BigInteger productid;
    //selfid
    public BigInteger userid;
    public String type;
    public Integer toship;
    public BigDecimal unitprice;
    public BigDecimal unitcost;

    public BigInteger orgid;

    public BigInteger OutboundStatusid;

    public Date createdate;
    public Date lastupdate;

    public Integer available;
}
