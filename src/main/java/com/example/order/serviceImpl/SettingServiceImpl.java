package com.example.order.serviceImpl;

import com.example.order.entity.ExcelPackages;
import com.example.order.entity.SettingGeneral;
import com.example.order.entity.WarehouseConfirmPackages;
import com.example.order.mapper.SettingMapper;
import com.example.order.service.SettingService;
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
public class SettingServiceImpl implements SettingService {
    @Autowired
    public SettingMapper settingMapper;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryGeneral", allEntries = true)
    })
    public Integer addGeneral(SettingGeneral settingGeneralData) {
        return settingMapper.addGeneral(settingGeneralData);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryGeneral", allEntries = true)
    })
    public Integer updateGeneral(SettingGeneral settingGeneralData) {
        return settingMapper.updateGeneral(settingGeneralData);
    }

    @Override
    @Cacheable(cacheNames = "queryGeneral", key = "#orgid")
    public List<SettingGeneral> queryGeneral(Integer orgid) {
        return settingMapper.queryGeneral(orgid);
    }

    @Override
    @Cacheable(cacheNames = "queryExcelpackagesLastOne", key = "#excelPackages")
    public ExcelPackages queryExcelpackagesLastOne(ExcelPackages excelPackages) {
        return settingMapper.queryExcelpackagesLastOne(excelPackages);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryExcelpackagesLastOne", allEntries = true),
            @CacheEvict(value = "queryConfirmCheckWarehouse", allEntries = true),
            @CacheEvict(value = "queryByupcdate", allEntries = true),
    })
    public Integer addExcelpackages(ExcelPackages excelPackages) {
        return settingMapper.addExcelpackages(excelPackages);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryExcelpackagesLastOne", allEntries = true),
            @CacheEvict(value = "queryConfirmCheckWarehouse", allEntries = true),
            @CacheEvict(value = "queryByupcdate", allEntries = true),
    })
    public Integer updateExcelpackages(ExcelPackages excelPackages) {
        return settingMapper.updateExcelpackages(excelPackages);
    }

    @Retry
    @Override
//    @Caching(evict = {
//            @CacheEvict(value = "querylistOffer", allEntries = true),
//    })
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Integer updateExcelPackages(ExcelPackages item, Integer subquantity) {
        Map<String, String> mapWarehouseConfirmPackages = settingMapper.queryIdExcelpackages(item.getId());
        Object usedquantity = mapWarehouseConfirmPackages.get("usedquantity");
        Object version = mapWarehouseConfirmPackages.get("version");

        Integer tmpRusult = 0;
        if (Integer.parseInt(usedquantity.toString()) >= subquantity) {
            item.setUsedquantity(Integer.parseInt(usedquantity.toString()) - subquantity);
        } else {
            item.setUsedquantity(0);
        }

        item.setVersion(Integer.parseInt(version.toString()));

        tmpRusult = settingMapper.updateExcelpackagesLock(item);
        if (tmpRusult <= 0) {
//            throw new RetryException("Transfer failed, retry.");
        }

        return tmpRusult;
    }
}
