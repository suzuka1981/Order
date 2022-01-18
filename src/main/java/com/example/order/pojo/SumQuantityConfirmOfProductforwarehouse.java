package com.example.order.pojo;

import com.example.order.entity.BuyersAndSellers;
import com.example.order.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.List;

//获取买手信息与对应卖家信息
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SumQuantityConfirmOfProductforwarehouse implements Serializable {
    public BigDecimal epquantity;
    public Integer quantity;
    public String quantitylist;
    public String sitename;
    public String address1;
    public String conditionvalue;
    public String name;
    public BigDecimal price;
    public String note;
    public BigInteger userid;
    public String username;
    public String relname;

    public String sendto;
    public String selfsitename;
    public String selfaddress1;
    public String whpname;
    public String type;

    public BigInteger siteid;
    public BigInteger productid;

}
