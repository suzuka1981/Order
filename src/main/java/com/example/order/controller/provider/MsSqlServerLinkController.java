package com.example.order.controller.provider;

import com.alibaba.fastjson.JSONObject;
import com.example.order.entity.ExcelPackages;
import com.example.order.entity.WareHouseSite;
import com.example.order.service.SettingService;
import com.example.order.service.WarehouseService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin //跨域
@RequestMapping("orderapi/msss")
public class MsSqlServerLinkController {
    @Resource
    private WarehouseService warehouseService;
    @Resource
    private SettingService settingService;

    @ResponseBody
    @PostMapping("/linkfromsapfordata")
    public Map<String, Object> queryListPageActive(@RequestBody JSONObject json) {
        Integer orgid = json.getInteger("userid");

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();
        int errorSize = 0;
        int successSize = 0;

        //获取uniidfromsit
        List<WareHouseSite> siteList = warehouseService.queryListPageWarehousesite(orgid, null, null, null);

        String uniid = "";
        for (WareHouseSite item : siteList) {
            if (item.getContact() != null) {
                if (uniid == "") {
                    uniid = "'" + item.getContact() + "'";
                } else {
                    uniid = uniid + "," + "'" + item.getContact() + "'";
                }
            }
        }

        Connection conn = MSSSConnection.getConn();

        Date tmpDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String newDate = dateFormat.format(tmpDate);

        String sql = "SELECT\n" +
                " T0.[U_realtracking] AS Trackingnumber,\n" +
                " T4.U_upc AS UPC,\n" +
                "T0.U_bizappsync,\n" +
                " 'Goods Receipt' AS 'Document Type',\n" +
                " T0.DocEntry,\n" +
                " T0.DocDate,\n" +
                " T4.[ItemCode],\n" +
                " T0.U_vendor_name,\n" +
                " T0.U_vendor_unitid,\n" +
                " T0.U_vendor_id,\n" +
                " T1.[Quantity],\n" +
                " T0.[U_vendor_unitid] AS Unitid,\n" +
                " T2.Cardname AS CustomerName,\n" +
                " T4.itemName,\n" +
                " T0.UpdateDate,\n" +
                " T4.[AvgPrice],\n" +
                " T4.[U_asin],\n" +
                " T4.[U_sku],\n" +
                " T4.[U_upc],\n" +
                " T4.U_app_whseproduct_id,\n" +
                " T4.U_app_user_id,\n" +
                " T4.U_app_user_username,\n" +
                " T4.U_weight,\n" +
                " T4.U_ozlbs,\n" +
                " T4.U_Length,\n" +
                " T4.U_Width,\n" +
                " T4.U_Height,\n" +
                " T4.[UserText],\n" +
                " T4.[CreateDate],\n" +
                " T4.[UpdateDate],\n" +
                " T4.[ItemName]\n" +
                "FROM\n" +
                " OIGN T0\n" +
                " INNER JOIN IGN1 T1 ON T0.[DocEntry] = T1.[DocEntry]\n" +
                " INNER JOIN OCRD T2 ON T0.U_vendor_id = T2.[CardCode]\n" +
                " INNER JOIN OITM T4 ON T1.ItemCode = T4.itemCode \n" +
                "WHERE\n" +
                "T0.U_bizappsync is null \n" +
                "and T0.[U_realtracking] is not null\n" +
                "and T4.U_upc is not null\n" +
                "and\n" +
                " T4.[validFor] = 'Y' \n" +
                " AND T0.\"DocDate\" >= '2021-12-1' \n" +
                " AND T0.\"DocDate\" <= '" +
                newDate +
                "' \n" +
                " and T0.[U_vendor_unitid] in (" +
                uniid +
                ")\n" +
                "ORDER BY\n" +
                " T0.Docentry DESC,\n" +
                " T0.\"DocDate\" DESC;";
        PreparedStatement pstmt;
        ResultSet rs;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String ItemCode = rs.getNString("ItemCode");
                String site = rs.getNString("U_vendor_unitid");

                String trackingnumber = rs.getNString("Trackingnumber").toUpperCase();
                String upc = rs.getNString("UPC");
                Integer docEntry = rs.getInt("DocEntry");
                Integer quantity = rs.getInt("Quantity");
                String DocDate = rs.getString("DocDate");

                if (site == null || site.equals("") ||
                        trackingnumber == null || trackingnumber.equals("") ||
                        upc == null || upc.equals("")
                ) {
                    errorSize++;
                    continue;
                }

                ExcelPackages excelPackages = new ExcelPackages();
                excelPackages.setUpc(upc);
                excelPackages.setQuantity(quantity);
                excelPackages.setSite(site);
                excelPackages.setTracking(trackingnumber);
                excelPackages.setOrgid(new BigInteger(String.valueOf(orgid)));

                //checksite是否存在
                List<WareHouseSite> siteTmplist = warehouseService.queryListWarehousesiteById(null, excelPackages.getSite(), new BigInteger(String.valueOf(orgid)), "", "");
                if (siteTmplist == null || siteTmplist.size() < 1) {
                    errorSize++;
                    continue;
                }

                ExcelPackages tmpMapLastOne = settingService.queryExcelpackagesLastOne(excelPackages);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


                Integer resultInt = 0;
                if (tmpMapLastOne == null) {
                    excelPackages.setCreatedate(new Date());
//                    excelPackages.setLastupdate(new Date());
                    excelPackages.setLastupdate(sdf.parse(DocDate));
                    excelPackages.setUsedquantity(excelPackages.getQuantity());
                    excelPackages.setRepeatquantity(0);
                    excelPackages.setVersion(0);

                    resultInt = settingService.addExcelpackages(excelPackages);
                } else {
                    if (tmpMapLastOne.getOrgid() == null || tmpMapLastOne.getOrgid().equals("")) {
                        tmpMapLastOne.setOrgid(new BigInteger(String.valueOf(orgid)));
                    }

                    tmpMapLastOne.setLastupdate(sdf.parse(DocDate));
//                    tmpMapLastOne.setLastupdate(new Date());
                    tmpMapLastOne.setRepeatquantity(tmpMapLastOne.getRepeatquantity() + 1);
                    tmpMapLastOne.setUsedquantity(tmpMapLastOne.getUsedquantity() + excelPackages.getQuantity());
                    tmpMapLastOne.setQuantity(tmpMapLastOne.getQuantity() + excelPackages.getQuantity());
                    resultInt = settingService.updateExcelpackages(tmpMapLastOne);
                }

                String sqlUpdate = " UPDATE OIGN SET U_bizappsync = 'y' WHERE DocEntry  = ?";
                PreparedStatement pstmtupdate = conn.prepareStatement(sqlUpdate);
                pstmtupdate.setInt(1, docEntry);

                int rsupdate = pstmtupdate.executeUpdate();

                successSize++;
            }
            pstmt.close();
            conn.close();
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }


        info.put("msg", "ok");
        info.put("status", "200");

        info.put("errorSize", String.valueOf(errorSize));
        info.put("successSize", String.valueOf(successSize));

//        result.put("data", buyersConfirm.getId());
        result.put("meta", info);

        return result;
    }
}
