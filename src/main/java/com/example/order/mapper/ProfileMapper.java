package com.example.order.mapper;

import com.example.order.entity.BuyersAndSellers;
import com.example.order.entity.BuyersPaymentMethod;
import com.example.order.entity.Comments;
import com.example.order.entity.User;
import com.example.order.pojo.BuyersAndSellerPojo;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;

public interface ProfileMapper {
    //
    User queryLogin(User user);

    Integer registerUser(User user);

    BuyersAndSellerPojo getBuyersAndSeller(@Param("username") String username, @Param("code") String code, @Param("status") Integer status);

    BuyersAndSellerPojo getSellerSon(@Param("username") String username, @Param("code") String code, @Param("status") Integer status);

    List<User> getSellerList(@Param("username") String username, @Param("code") String code, @Param("status") Integer status, @Param("sellerid") Integer sellerid, @Param("searchinput") String searchinput);

    Integer updataJoinOrganization(BuyersAndSellers buyersAndSellers);

    Integer insertJoinOrganization(BuyersAndSellers buyersAndSellers);

    Integer deleteJoinOrganization(@Param("id") Integer id);

    BuyersAndSellers queryBuyersAndSellers(BuyersAndSellers buyersAndSellers);

    Integer updateUser(User user);

    Integer insertbuyerspaymentmethod(BuyersPaymentMethod buyersPaymentMethod);

    Integer updatebuyerspaymentmethod(BuyersPaymentMethod buyersPaymentMethod);

    List<BuyersPaymentMethod> queryBuyersPaymentMethod(Integer buyersid, Integer pageNum, Integer pageSize, String searchinput);

    Integer insertComments(Comments comments);

    List<Comments> queryComments(String msgtype, BigInteger msgsourceid);

    Integer updateComments(Comments comments);
}
