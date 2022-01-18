package com.example.order.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/*
买手与卖家多对多关系
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyersAndSellers implements Serializable {
    public BigInteger id;
    //    买家id
    public BigInteger buyersid;
    //    卖家id
    public BigInteger sellerid;
    //    状态：1已经确认通过 2 等待确认
    public Integer status;
    //    买手请求加入卖家留言
    public String note;

    public Date createdate;
    public Date lastupdate;
    public Integer isdelete;
}
