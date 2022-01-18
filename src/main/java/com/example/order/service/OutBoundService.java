package com.example.order.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.order.entity.*;
import com.example.order.entity.LET.Address;
import com.example.order.entity.LET.Packages;
import com.example.order.pojo.OutboundLabelPojo;
import com.example.order.pojo.OutboundStatusPojo;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface OutBoundService {
    Address getAddress(JSONObject object);

    List<Packages> getPackages(JSONArray object);

    Integer addOutboundLabel(OutboundLabel outboundLabel);

    Integer addOutboundRate(OutboundRate outboundRate);

    Integer addOutboundPackageSize(List<OutboundPackageSize> outboundPackageSizeList);

    Integer addOutboundShipment(OutboundShipment outboundShipment);

    List<OutboundLabelPojo> queryLabelListOfMap(Integer pageNum, Integer pageSize, BigInteger orgid, String labelstatus, String searchinput, String startDate, String endDate, String labelid);

    Integer deleteLabel(BigInteger id);

    Integer updateLabel(BigInteger labelid, String inputNote);

    List<Map<String, String>> queryOutboundAddress(BigInteger orgid);

    Integer addOutboundAddress(OutboundAddress outboundAddress);

    Integer updateOutboundaddress(OutboundAddress outboundAddress);

    Integer deleteAddress(BigInteger id);

    Integer addOutboundAddresstmplate(OutboundAddress outboundAddress);

    Integer updateOutboundaddresstmplate(OutboundAddress outboundAddress);

    Integer deleteAddresstmplate(BigInteger id);

    List<Map<String, String>> queryOutboundAddresstmplate(BigInteger orgid);

    Integer addOutboundPackageSizetemplate(@Param("list") List<OutboundPackageSize> outboundPackageSizeList);

    List<Map<String, String>> queryPackageSizeTemplate(BigInteger orgid, Integer pageNum, Integer pageSize, String searchinput);

    Integer updateOutboundPackageSizetemplate(OutboundPackageSize outboundPackageSize);

    Integer deletePackageSizeTemplate(BigInteger id);

    Integer addOutboundStatus(OutboundStatus outboundStatus);

    Integer addOutboundStatusProductSize(List<OutboundStatusProductSize> outboundStatusProductSize);

    List<OutboundStatusPojo> queryStatusList(Integer pageNum, Integer pageSize, String orgid, String selfid, String searchinput, String startDate, String endDate, String status, String userselect);

    Integer updateOutboundStatus(OutboundStatus outboundStatus);

    Integer deleteOutboundstatus(BigInteger id);

    OutboundStatus queryOutboundStatus(Integer id);

    public void cacheClear();
}
