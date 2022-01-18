package com.example.order.serviceImpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.order.entity.*;
import com.example.order.entity.LET.Address;
import com.example.order.entity.LET.Packages;
import com.example.order.mapper.OutboundMapper;
import com.example.order.pojo.OutboundLabelPojo;
import com.example.order.pojo.OutboundStatusPojo;
import com.example.order.service.OutBoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OutBoundServiceImpl implements OutBoundService {
    @Autowired
    public OutboundMapper outboundMapper;

    @Override
    public Address getAddress(JSONObject object) {
        Address address = new Address();

        if (object.get("fullname") != null) {
            address.setName(object.get("fullname").toString());
        } else {
            address.setName("");
        }

        if (object.get("companyname") != null) {
            address.setCompany(object.get("companyname").toString());
        } else {
            address.setCompany("");
        }

        if (object.get("phone") != null) {
            address.setPhone_number(object.get("phone").toString());
        } else {
            address.setPhone_number("");
        }

        if (object.get("address1") != null) {
            address.setAddress(object.get("address1").toString());
        } else {
            address.setAddress("");
        }

        if (object.get("address_2") != null) {
            address.setAddress_2(object.get("address_2").toString());
        } else {
            address.setAddress_2("");
        }

        if (object.get("city") != null) {
            address.setCity(object.get("city").toString());
        } else {
            address.setCity("");
        }

        if (object.get("valueOfState") != null) {
            address.setState(object.get("valueOfState").toString());
        } else {
            address.setState("");
        }

        address.setCountry_code("US");

        if (object.get("zipcode") != null) {
            address.setPostal_code(object.get("zipcode").toString());
        } else {
            address.setPostal_code("");
        }

        address.setResidential("false");

        return address;
    }

    @Override
    public List<Packages> getPackages(JSONArray objectList) {
        List<Packages> packagesList = new ArrayList<>();

        for (int i = 0; i < objectList.size(); i++) {
            JSONObject object = objectList.getJSONObject(i);

            Packages packages = new Packages();

            if (object.get("height") != null) {
                packages.setHeight(object.get("height").toString());
            } else {
                packages.setHeight("");
            }

            if (object.get("width") != null) {
                packages.setWidth(object.get("width").toString());
            } else {
                packages.setWidth("");
            }

            if (object.get("length") != null) {
                packages.setLength(object.get("length").toString());
            } else {
                packages.setLength("");
            }

            if (object.get("weight") != null) {
                packages.setWeight(object.get("weight").toString());
            } else {
                packages.setWeight("");
            }

            packages.setSignature_option("signature_option");


//            packages.setDeclared_value("");

            if (object.get("declaredvalue") != null) {
                packages.setDeclared_value(object.get("declaredvalue").toString());
            } else {
                packages.setDeclared_value("");
            }

            packagesList.add(packages);
        }


        return packagesList;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryLabelListOfMap", allEntries = true),
            @CacheEvict(value = "queryStatusList", allEntries = true),
    })
    public Integer addOutboundLabel(OutboundLabel outboundLabel) {
        return outboundMapper.addOutboundLabel(outboundLabel);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryLabelListOfMap", allEntries = true),
            @CacheEvict(value = "queryOutboundAddress", allEntries = true),
    })
    public Integer addOutboundAddress(OutboundAddress outboundAddress) {
        return outboundMapper.addOutboundAddress(outboundAddress);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryLabelListOfMap", allEntries = true),
    })
    public Integer addOutboundRate(OutboundRate outboundRate) {
        return outboundMapper.addOutboundRate(outboundRate);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryLabelListOfMap", allEntries = true),
    })
    public Integer addOutboundPackageSize(List<OutboundPackageSize> outboundPackageSizeList) {
        return outboundMapper.addOutboundPackageSize(outboundPackageSizeList);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryLabelListOfMap", allEntries = true),
    })
    public Integer addOutboundShipment(OutboundShipment outboundShipment) {
        return outboundMapper.addOutboundShipment(outboundShipment);
    }

    @Override
    @Cacheable(cacheNames = "queryLabelListOfMap", key = "#pageNum + '-' + #pageSize+ '-' + #orgid+ '-' + #labelstatus+ '-' + #searchinput+ '-' + #startDate+ '-' + #endDate+ '-' + #labelid")
    public List<OutboundLabelPojo> queryLabelListOfMap(Integer pageNum, Integer pageSize, BigInteger orgid, String labelstatus, String searchinput, String startDate, String endDate, String labelid) {
        return outboundMapper.queryLabelListOfMap(pageNum, pageSize, orgid, labelstatus, searchinput, startDate, endDate, labelid);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryStatusList", allEntries = true),
            @CacheEvict(value = "queryLabelListOfMap", allEntries = true),
    })
    public Integer deleteLabel(BigInteger id) {
        return outboundMapper.deleteLabel(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryLabelListOfMap", allEntries = true),
            @CacheEvict(value = "queryStatusList", allEntries = true),
    })
    public Integer updateLabel(BigInteger labelid, String inputNote) {
        return outboundMapper.updateLabel(labelid, inputNote);
    }

    @Override
    @Cacheable(cacheNames = "queryOutboundAddress", key = "#orgid")
    public List<Map<String, String>> queryOutboundAddress(BigInteger orgid) {
        return outboundMapper.queryOutboundAddress(orgid);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryLabelListOfMap", allEntries = true),
            @CacheEvict(value = "queryOutboundAddress", allEntries = true),
    })
    public Integer updateOutboundaddress(OutboundAddress outboundAddress) {
        return outboundMapper.updateOutboundaddress(outboundAddress);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryLabelListOfMap", allEntries = true),
            @CacheEvict(value = "queryOutboundAddress", allEntries = true),
    })
    public Integer deleteAddress(BigInteger id) {
        return outboundMapper.deleteAddress(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryOutboundAddresstmplate", allEntries = true),
    })
    public Integer addOutboundAddresstmplate(OutboundAddress outboundAddress) {
        return outboundMapper.addOutboundAddresstmplate(outboundAddress);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryOutboundAddresstmplate", allEntries = true),
    })
    public Integer updateOutboundaddresstmplate(OutboundAddress outboundAddress) {
        return outboundMapper.updateOutboundaddresstmplate(outboundAddress);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryOutboundAddresstmplate", allEntries = true),
    })
    public Integer deleteAddresstmplate(BigInteger id) {
        return outboundMapper.deleteAddresstmplate(id);
    }

    @Override
    @Cacheable(cacheNames = "queryOutboundAddresstmplate", key = "#orgid")
    public List<Map<String, String>> queryOutboundAddresstmplate(BigInteger orgid) {
        return outboundMapper.queryOutboundAddresstmplate(orgid);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryLabelListOfMap", allEntries = true),
            @CacheEvict(value = "queryPackageSizeTemplate", allEntries = true),
    })
    public Integer addOutboundPackageSizetemplate(List<OutboundPackageSize> outboundPackageSizeList) {
        return outboundMapper.addOutboundPackageSizetemplate(outboundPackageSizeList);
    }

    @Override
    @Cacheable(cacheNames = "queryPackageSizeTemplate", key = "#orgid+#pageNum+#pageSize+#searchinput")
    public List<Map<String, String>> queryPackageSizeTemplate(BigInteger orgid, Integer pageNum, Integer pageSize, String searchinput) {
        return outboundMapper.queryPackageSizeTemplate(orgid, searchinput);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryLabelListOfMap", allEntries = true),
            @CacheEvict(value = "queryPackageSizeTemplate", allEntries = true),
    })
    public Integer updateOutboundPackageSizetemplate(OutboundPackageSize outboundPackageSize) {
        return outboundMapper.updateOutboundPackageSizetemplate(outboundPackageSize);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryLabelListOfMap", allEntries = true),
            @CacheEvict(value = "queryPackageSizeTemplate", allEntries = true),
    })
    public Integer deletePackageSizeTemplate(BigInteger id) {
        return outboundMapper.deletePackageSizeTemplate(id);
    }


    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryStatusList", allEntries = true),
    })
    public Integer addOutboundStatus(OutboundStatus outboundStatus) {
        return outboundMapper.addOutboundStatus(outboundStatus);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryStatusList", allEntries = true),
    })
    public Integer addOutboundStatusProductSize(List<OutboundStatusProductSize> outboundStatusProductSize) {
        return outboundMapper.addOutboundStatusProductSize(outboundStatusProductSize);
    }

    @Override
    @Cacheable(cacheNames = "queryStatusList", key = "#selfid+#orgid+#pageNum+#pageSize+#searchinput+#startDate+#endDate+#status+#userselect")
    public List<OutboundStatusPojo> queryStatusList(Integer pageNum, Integer pageSize, String orgid, String selfid, String searchinput, String startDate, String endDate, String status, String userselect) {
        return outboundMapper.queryStatusList(orgid, selfid, searchinput, startDate, endDate, status, userselect);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryStatusList", allEntries = true),
    })
    public Integer updateOutboundStatus(OutboundStatus outboundStatus) {
        return outboundMapper.updateOutboundStatus(outboundStatus);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryStatusList", allEntries = true),
    })
    public Integer deleteOutboundstatus(BigInteger id) {
        return outboundMapper.deleteOutboundstatus(id);
    }

    @Override
    @Cacheable(cacheNames = "queryOutboundStatus", key = "#id")
    public OutboundStatus queryOutboundStatus(Integer id) {
        return outboundMapper.queryOutboundStatus(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryStatusList", allEntries = true),
    })
    public void cacheClear() {
        System.out.println("Cache Clear!");
    }
}
