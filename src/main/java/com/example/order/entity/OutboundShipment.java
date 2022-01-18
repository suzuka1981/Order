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
public class OutboundShipment implements Serializable {
    public BigInteger id;

    public String rate_no;
    public String carrier_no;
    public String carrier;
    public String currency;
    public String weight;
    public String retail_amount;
    public String amount;
    public String servicelevel_token;
    public String servicelevel_name;
    public String bussiness_days;
    public String tracking_number;
    public String label_url;
    public String label_download_url;

    public BigInteger orgid;
    public BigInteger selfid;
    public BigInteger outboundlabelid;

    public Date createdate;
    public Date lastupdate;
}
