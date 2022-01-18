package com.example.order.pojo;

import com.example.order.entity.BuyersAndSellers;
import com.example.order.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

//获取买手信息与对应卖家信息
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BuyersAndSellerPojo implements Serializable {
    // buyers
    public BigInteger id;
    public String username;
    public String name;
    public String password;
    //    买手添加卖家用
    public String code;
    //    是否删除 0 正常 1 删除
    public Integer status;
    //    0 买家 1 卖家
    public Integer type;

    public List<BuyersAndSellers> BuyersAndSellersList;
    public List<User> UserList;
}
