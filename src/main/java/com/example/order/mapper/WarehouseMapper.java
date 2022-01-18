package com.example.order.mapper;

import com.example.order.entity.*;
import com.example.order.pojo.SumQuantityConfirmOfProductforwarehouse;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface WarehouseMapper {
    //    添加仓库地址
    Integer addSite(WareHouseSite addSiteData);

    Integer updateSite(WareHouseSite addSiteData);

    //删除仓库
    Integer deleteSite(Integer id);

    List<WareHouseSite> queryListPageWarehousesite(Integer orgid, Integer selfid);

    //添加product
    Integer addProduct(WarehouseProduct warehouseProduct);

    //添加product
    Integer updateProduct(WarehouseProduct warehouseProduct);

    WarehouseProduct queryWarehouseProductByID(BigInteger id);

    List<WarehouseProduct> queryListPageProduct(Integer orgid, Integer pageNum, Integer pageSize, String searchinput, String status, String endDate);

    List<WareHouseSite> queryListWarehousesiteById(@Param("list") List<String> parList, @Param("contact") String contact, @Param("orgid") BigInteger orgid, @Param("mfyccid") String mfyccid, @Param("checked") String checked);

    List<BuyersConfirm> queryConfirmOfProduct(@Param("orgid") Integer orgid, @Param("productid") Integer productid);

    Integer addWarehouseConfirmPackages(WarehouseConfirmPackages warehouseConfirmPackages);

    List<SumQuantityConfirmOfProductforwarehouse> querySumQuantityConfirmOfProduct(@Param("orgid") Integer orgid, @Param("productid") Integer productid, @Param("searchinput") String searchinput, @Param("selfid") Integer selfid);

    List<Map<String, String>> queryByupcdate(Integer orgid, Integer pageNum, Integer pageSize, String searchinput, String startDate, String endDate);

    Integer deleteExcelpackageById(Integer id);

    List<SumQuantityConfirmOfProductforwarehouse> querySumQuantityConfirmOfProductforwarehouse(@Param("orgid") Integer orgid, @Param("productid") Integer productid, @Param("searchinput") String searchinput);
}
