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
public class OutboundRate implements Serializable {
    public BigInteger id;

    public String servicelevel_token;
    public String servicelevel_name;
    public String carrier;
    public BigDecimal amount;
    public String currency;
    public BigDecimal retail_amount;
    public String bussiness_days;

    public BigInteger orgid;
    public BigInteger selfid;
    public BigInteger outboundlabelid;

    public Date createdate;
    public Date lastupdate;
}
