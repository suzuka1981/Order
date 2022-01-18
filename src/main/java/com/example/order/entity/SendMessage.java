package com.example.order.entity;

import com.example.order.pojo.EmailCreateShipment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessage implements Serializable {
    public String subject;
    public String sentdate;
    public String content;
    public String sendtype;

    //找回密码
    public User user;

    //新建或者修改offer
    public Offer offer;
    public List<WareHouseSite> wareHouseSiteList;
    public WarehouseProduct whp;
    public List<User> sendToUserList;
    public String url;

    //create shipment
    public EmailCreateShipment emailCreateShipment;
    public String orguserName;
}
