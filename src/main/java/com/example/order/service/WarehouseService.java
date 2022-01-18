package com.example.order.service;

import com.example.order.entity.*;
import com.example.order.pojo.SumQuantityConfirmOfProductforwarehouse;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface WarehouseService {
    //    添加仓库地址
    Integer addSite(WareHouseSite addSiteData);

    Integer updateSite(WareHouseSite addSiteData);

    //删除仓库
    Integer deleteSite(Integer id);

    // 搜索全部仓库地址
    List<WareHouseSite> queryListPageWarehousesite(Integer orgid, Integer selfid, Integer pageNum, Integer pageSize);

    //添加product
    Integer addProduct(WarehouseProduct warehouseProduct);

    //添加product
    Integer updateProduct(WarehouseProduct warehouseProduct);

    WarehouseProduct queryWarehouseProductByID(BigInteger id);

    // 搜索全部仓库地址
    List<WarehouseProduct> queryListPageProduct(Integer orgid, Integer pageNum, Integer pageSize, String searchinput, String status, String endDate);

    List<WareHouseSite> queryListWarehousesiteById(List<String> parList, String contact, BigInteger orgid, String mfyccid, String checked);

    List<BuyersConfirm> queryConfirmOfProduct(Integer orgid, Integer productid, Integer pageNum, Integer pageSize);

    Integer addWarehouseConfirmPackages(WarehouseConfirmPackages warehouseConfirmPackages);

    List<SumQuantityConfirmOfProductforwarehouse> querySumQuantityConfirmOfProduct(Integer orgid, Integer productid, Integer pageNum, Integer pageSize, String searchinput,Integer selfid);

    List<Map<String, String>> queryByupcdate(Integer userid, Integer pageNum, Integer pageSize, String searchinput, String startDate, String endDate);

    Integer deleteExcelpackageById(Integer id);

    List<SumQuantityConfirmOfProductforwarehouse> querySumQuantityConfirmOfProductforwarehouse(Integer orgid, Integer productid, Integer pageNum, Integer pageSize, String searchinput);
}
