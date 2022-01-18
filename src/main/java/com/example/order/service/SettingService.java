package com.example.order.service;

import com.example.order.entity.ExcelPackages;
import com.example.order.entity.SettingGeneral;
import com.example.order.entity.WarehouseConfirmPackages;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface SettingService {
    Integer addGeneral(SettingGeneral settingGeneralData);

    Integer updateGeneral(SettingGeneral settingGeneralData);

    List<SettingGeneral>  queryGeneral(Integer orgid);

    ExcelPackages queryExcelpackagesLastOne(ExcelPackages excelPackages);

    Integer addExcelpackages(ExcelPackages excelPackages);

    Integer updateExcelpackages(ExcelPackages excelPackages);

    Integer updateExcelPackages(ExcelPackages item, Integer subquantity);
}
