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
public class SettingGeneral implements Serializable {
    public BigInteger id;
    public String pendingperioddays;
    public String checkedVisibletoallmembers;
    public String defaultusers;
    public BigDecimal minimumpaymentamount;

    public String checkedComments;
    public String valueOfSwitch;
    public String checkedEnableAmazonSites;
    public BigInteger orgid;

    public Date createdate;
    public Date lastupdate;
}
