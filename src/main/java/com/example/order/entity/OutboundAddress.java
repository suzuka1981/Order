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
public class OutboundAddress implements Serializable {
    private BigInteger id;

    public String zipcode;
    public String valueOfAddressName;
    public String phone;
    public String address2;
    public String city;
    public String companyname;
    public String address1;
    public String saveOfchecked;
    public String fullname;
    public String addressname;
    public String valueOfState;
    public String type;

    public String ein;
    public String email;
    public String valueOfCountry;

    public BigInteger orgid;
    public BigInteger selfid;
    public BigInteger outboundlabelid;

    public Date createdate;
    public Date lastupdate;
}
