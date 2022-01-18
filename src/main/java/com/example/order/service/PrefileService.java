package com.example.order.service;

import com.example.order.entity.BuyersAndSellers;
import com.example.order.entity.BuyersPaymentMethod;
import com.example.order.entity.Comments;
import com.example.order.entity.User;
import com.example.order.pojo.BuyersAndSellerPojo;

import java.math.BigInteger;
import java.util.List;

public interface PrefileService {
    //登录
    User queryLogin(User user);

    Integer registerUser(User user);

    Integer sendMail(User user);

    Integer updateUser(User user);

    BuyersAndSellerPojo getBuyersAndSeller(String username, String code, Integer status);

    BuyersAndSellerPojo getSellerSon(String username, String code, Integer status);

    List<User> getSellerList(String username, String code, Integer status, Integer pageNum, Integer pageSize, Integer sellerid, String searchinput);

    Integer updataJoinOrganization(BuyersAndSellers buyersAndSellers);

    Integer insertJoinOrganization(BuyersAndSellers buyersAndSellers);

    Integer deleteJoinOrganization(Integer id);

    BuyersAndSellers queryBuyersAndSellers(BuyersAndSellers buyersAndSellers);

    Integer insertbuyerspaymentmethod(BuyersPaymentMethod buyersPaymentMethod);

    Integer updatebuyerspaymentmethod(BuyersPaymentMethod buyersPaymentMethod);

    List<BuyersPaymentMethod> queryBuyersPaymentMethod(Integer buyersid, Integer pageNum, Integer pageSize, String searchinput);

    Integer insertComments(Comments comments);

    List<Comments> queryComments(String msgtype, BigInteger msgsourceid);

    Integer updateComments(Comments comments);
}
