package com.example.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelPackages implements Serializable {
    public BigInteger id;
    public String tracking;
    public String upc;
    public Integer quantity;
    public Integer version;
    public Integer usedquantity;
    public String site;
    public Integer repeatquantity;
    public BigInteger orgid;


    public BigInteger bsid;

    public Date createdate;
    public Date lastupdate;

    List<WarehouseConfirmPackages> warehouseconfirmpackages;
}
