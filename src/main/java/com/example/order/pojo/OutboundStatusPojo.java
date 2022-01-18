package com.example.order.pojo;

import com.example.order.entity.OutboundStatusProductSize;
import com.example.order.entity.User;
import com.example.order.entity.WareHouseSite;
import com.example.order.entity.WarehouseProduct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutboundStatusPojo implements Serializable {
    public BigInteger id;
    public BigInteger labelid;

    public String label_url;
    public String uploadfiles;
    public String tracking_number;
    public Integer packagequantity;
    public String destination;
    public String otherservices;
    public String carrier;
    public String notetoyourself;
    public String instruction;

    public BigInteger orgid;
    public String servicetype;

    public String rate_no;

    public String destinationcontent;

    public Date createdate;
    public Date lastupdate;

    public List<OutboundStatusProductSize> outboundStatusProductSize;
    public List<WarehouseProduct> warehouseProduct;
    public WareHouseSite wareHouseSite;
    public User user;
}
