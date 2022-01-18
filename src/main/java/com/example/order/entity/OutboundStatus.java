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
public class OutboundStatus implements Serializable {
    public BigInteger id;
    public BigInteger labelid;

    public String label_url;
    public String uploadfiles;
    public String tracking_number;
    public Integer packagequantity;
    public String destination;
    public String otherservices;
    public String carrier;
    public String notetoyourself;
    public String instruction;
    public String servicetype;

    public String unitedelectronicsdeals;
    public String uploadaddress;
    public String status;

    public BigInteger orgid;

    public Date createdate;
    public Date lastupdate;

    public String buyersexpedite;
    public String buyerslabel;
    public String buyersphoto;
    public String buyerssn;
    public String buyersuploadfiles;
    public String buyersnote;

    public Date buyerscreatedate;
    public Date buyerslastupdate;

    public String destinationcontent;
}
