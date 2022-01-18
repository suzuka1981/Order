package com.example.order.serviceImpl;

import com.example.order.entity.*;
import com.example.order.mapper.WarehouseMapper;
import com.example.order.pojo.SumQuantityConfirmOfProductforwarehouse;
import com.example.order.service.WarehouseService;
import com.example.order.util.transation.Retry;
import com.example.order.util.transation.RetryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Service
public class WarehouseServiceImpl implements WarehouseService {
    @Autowired
    public WarehouseMapper warehouseMapper;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryListPageWarehousesite", allEntries = true),
            @CacheEvict(value = "queryListPageWarehousesitequeryListWarehousesiteById", allEntries = true),
            @CacheEvict(value = "queryConfirmCheckWarehouse", allEntries = true),
    })
    public Integer addSite(WareHouseSite addSiteData) {
        return warehouseMapper.addSite(addSiteData);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryListPageWarehousesite", allEntries = true),
            @CacheEvict(value = "queryListWarehousesiteById", allEntries = true),
            @CacheEvict(value = "queryConfirmCheckWarehouse", allEntries = true),
    })
    public Integer updateSite(WareHouseSite addSiteData) {
        return warehouseMapper.updateSite(addSiteData);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryListPageWarehousesite", allEntries = true),
            @CacheEvict(value = "queryListWarehousesiteById", allEntries = true),
            @CacheEvict(value = "queryConfirmCheckWarehouse", allEntries = true),
    })
    public Integer deleteSite(Integer id) {
        return warehouseMapper.deleteSite(id);
    }

    @Override
    @Cacheable(cacheNames = "queryListPageWarehousesite", key = "#orgid+'_'+#selfid+'_'+#pageNum +'_'+#pageSize")
    public List<WareHouseSite> queryListPageWarehousesite(Integer orgid, Integer selfid, Integer pageNum, Integer pageSize) {
        return warehouseMapper.queryListPageWarehousesite(orgid, selfid);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryListPageProduct", allEntries = true),
            @CacheEvict(value = "queryWarehouseProductByID", allEntries = true),
            @CacheEvict(value = "queryConfirmOfProduct", allEntries = true),
            @CacheEvict(value = "querySumQuantityConfirmOfProduct", allEntries = true),
            @CacheEvict(value = "querySumQuantityConfirmOfProductforwarehouse", allEntries = true),
            @CacheEvict(value = "queryByupcdate", allEntries = true),
            @CacheEvict(value = "queryConfirmCheckWarehouse", allEntries = true),
    })
    public Integer addProduct(WarehouseProduct warehouseProduct) {
        return warehouseMapper.addProduct(warehouseProduct);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryListPageProduct", allEntries = true),
            @CacheEvict(value = "queryWarehouseProductByID", allEntries = true),
            @CacheEvict(value = "queryConfirmOfProduct", allEntries = true),
            @CacheEvict(value = "querySumQuantityConfirmOfProduct", allEntries = true),
            @CacheEvict(value = "querySumQuantityConfirmOfProductforwarehouse", allEntries = true),
            @CacheEvict(value = "queryByupcdate", allEntries = true),
            @CacheEvict(value = "queryConfirmCheckWarehouse", allEntries = true),
    })
    public Integer updateProduct(WarehouseProduct warehouseProduct) {
        return warehouseMapper.updateProduct(warehouseProduct);
    }

    @Override
    @Cacheable(cacheNames = "queryWarehouseProductByID", key = "#id")
    public WarehouseProduct queryWarehouseProductByID(BigInteger id) {
        return warehouseMapper.queryWarehouseProductByID(id);
    }

    @Override
//    @Cacheable(cacheNames = "queryListPageProduct", key = "#orgid+'_'+#pageNum +'_'+#pageSize+'_'+#searchinput+'_'+#status+'_'+#endDate")
    public List<WarehouseProduct> queryListPageProduct(Integer orgid, Integer pageNum, Integer pageSize, String searchinput, String status, String endDate) {
        return warehouseMapper.queryListPageProduct(orgid, pageNum, pageSize, searchinput, status, endDate);
    }

    @Override
    @Cacheable(cacheNames = "queryListWarehousesiteById", key = "#parList+#contact+#orgid+#mfyccid+#checked")
    public List<WareHouseSite> queryListWarehousesiteById(List<String> parList, String contact, BigInteger orgid, String mfyccid,String checked) {
        return warehouseMapper.queryListWarehousesiteById(parList, contact, orgid, mfyccid,checked);
    }

    @Override
    @Cacheable(cacheNames = "queryConfirmOfProduct")
//    @Cacheable(cacheNames = "queryConfirmOfProduct", key = "#orgid+#productid +#pageNum+#pageSize")
    public List<BuyersConfirm> queryConfirmOfProduct(Integer orgid, Integer productid, Integer pageNum, Integer pageSize) {
        return warehouseMapper.queryConfirmOfProduct(orgid, productid);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryConfirmCheckWarehouse", allEntries = true),
            @CacheEvict(value = "queryConfirmCheckWarehouseOld", allEntries = true),
    })
    public Integer addWarehouseConfirmPackages(WarehouseConfirmPackages warehouseConfirmPackages) {
        return warehouseMapper.addWarehouseConfirmPackages(warehouseConfirmPackages);
    }

    @Override
//    @Cacheable(cacheNames = "querySumQuantityConfirmOfProduct", key = "#orgid+'_'+#productid +'_'+#pageNum+'_'+#pageSize+'_'+#searchinput+'_'+#selfid")
    public List<SumQuantityConfirmOfProductforwarehouse> querySumQuantityConfirmOfProduct(Integer orgid, Integer productid, Integer pageNum, Integer pageSize,String searchinput,Integer selfid) {
        return warehouseMapper.querySumQuantityConfirmOfProduct(orgid, productid,searchinput,selfid);
    }

    @Override
    @Cacheable(cacheNames = "queryByupcdate", key = "#userid +'_'+#pageNum+'_'+#pageSize +'_'+#searchinput +'_'+#startDate +'_'+#endDate")
    public List<Map<String, String>> queryByupcdate(Integer userid, Integer pageNum, Integer pageSize, String searchinput, String startDate, String endDate) {
        return warehouseMapper.queryByupcdate(userid, pageNum, pageSize, searchinput, startDate, endDate);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryListPageProduct", allEntries = true),
            @CacheEvict(value = "queryWarehouseProductByID", allEntries = true),
            @CacheEvict(value = "querySumQuantityConfirmOfProduct", allEntries = true),
            @CacheEvict(value = "queryByupcdate", allEntries = true),
            @CacheEvict(value = "queryConfirmCheckWarehouse", allEntries = true),
            @CacheEvict(value = "queryTasklistOffer", allEntries = true),
            @CacheEvict(value = "queryListTaskHistory", allEntries = true),
            @CacheEvict(value = "queryListBuyersInventory", allEntries = true),
            @CacheEvict(value = "queryBuyersInventoryDetailPojo", allEntries = true),
            @CacheEvict(value = "queryPendingTransactions", allEntries = true),
            @CacheEvict(value = "queryListTaskCacncel", allEntries = true),
            @CacheEvict(value = "queryConfirmOfProduct", allEntries = true),
            @CacheEvict(value = "queryByupcdate", allEntries = true),
            @CacheEvict(value = "querySumQuantityConfirmOfProductforwarehouse", allEntries = true),
    })
    public Integer deleteExcelpackageById(Integer id) {
        return warehouseMapper.deleteExcelpackageById(id);
    }

    @Override
//    @Cacheable(cacheNames = "querySumQuantityConfirmOfProductforwarehouse")
//    @Cacheable(cacheNames = "querySumQuantityConfirmOfProductforwarehouse", key = "#orgid+#productid+#pageNum+#pageSize")
    public List<SumQuantityConfirmOfProductforwarehouse> querySumQuantityConfirmOfProductforwarehouse(Integer orgid, Integer productid, Integer pageNum, Integer pageSize, String searchinput) {
        return warehouseMapper.querySumQuantityConfirmOfProductforwarehouse(orgid, productid,searchinput);
    }
}
