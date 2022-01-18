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
public class Comments implements Serializable {
    public BigInteger id;

    public BigInteger userid;

    public String username;
    public String userrelname;

    public String message;
    public String msgtype;
    public String unreadstatus;
    public BigInteger msgsourceid;
    public Date createdate;
    public Date lastupdate;


}
