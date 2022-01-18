package com.example.order.pojo;

import com.example.order.entity.BuyersTask;
import com.example.order.entity.WarehouseProduct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskHistoryPojo implements Serializable {
    public BigInteger id;
    public BigInteger buyersid;
    public BigInteger sellerid;
    public Integer quantity;
    public String selfsite;
    public String trackingnumber;
    public String status;
    public BigDecimal price;
    public String type;
    public Date createdate;
    public Date lastupdate;
    public BigInteger buyerstaskid;

    //临时Seller name获取
    public String username;
    public String sellerrelname;
    public String selfusername;
    public String selfrelname;

    public BuyersTask buyersTaskList;
    public WarehouseProduct warehouseProduct;
}
