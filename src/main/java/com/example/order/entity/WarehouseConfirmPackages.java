package com.example.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/*
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseConfirmPackages implements Serializable {
    public BigInteger id;
    public BigInteger excelpackagesid;
    public BigInteger buyersconfirmid;
    public Integer quantity;
    public String tracking;
    public String status;
    public Date createdate;
    public Date lastupdate;
}
