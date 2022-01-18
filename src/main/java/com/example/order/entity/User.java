package com.example.order.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/*
用户表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    public BigInteger id;
    public String username;
    public String name;

    public String password;
    //    买手添加卖家用
    public String code;
    //    状态：1已经确认通过 2 等待确认
    public Integer status;
    //    0 买家 1 卖家
    public Integer type;

    //    状态：0 等待确认 1已经确认通过
    public Integer buyersAndSellersstatus;

    public Date createdate;
    public Date lastupdate;
    public Integer isdelete;

    //    中转
    public BigInteger bsid;
}
