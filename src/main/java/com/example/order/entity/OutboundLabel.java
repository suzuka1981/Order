package com.example.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutboundLabel implements Serializable {
    public BigInteger id;
    public String valueOfShipService;
    public String valueOfTemplate;
    public String valueOfSignature;
    public Date shipdate;
    public String inputNote;
    public String inputMemo;

    public BigInteger orgid;
    public BigInteger selfid;

    public Date createdate;
    public Date lastupdate;
}
