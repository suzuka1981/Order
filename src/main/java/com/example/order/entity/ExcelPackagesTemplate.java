package com.example.order.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class ExcelPackagesTemplate implements Serializable {
    private static final long serialVersionUID = 1L;

    @Excel(name = "Tracking", orderNum = "0", width = 15)
    public String tracking;
    @Excel(name = "UPC", orderNum = "1", width = 15)
    public String upc;
    @Excel(name = "Quantity", orderNum = "2", width = 15)
    public Integer quantity;
    @Excel(name = "site", orderNum = "3", width = 15)
    public String site;
}
