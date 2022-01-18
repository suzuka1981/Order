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
public class OutboundPackageSize implements Serializable {
    public BigInteger id;

    public String length;
    public String width;
    public String weight;
    public String memo;
    public String index;
    public String declaredvalue;
    public String identicalpackageqty;
    public String height;
    public String weighttype;
    public String name;

    public BigInteger orgid;
    public BigInteger selfid;

    public BigInteger outboundlabelid;

    public Date createdate;
    public Date lastupdate;
}
