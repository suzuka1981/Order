package com.example.order.controller.provider;


import com.alibaba.fastjson.JSONObject;
import com.example.order.config.RestTemplateConfiguration;
import com.example.order.entity.SAP.Login;
import com.example.order.entity.SAP.Product;
import com.example.order.entity.SettingGeneral;
import com.example.order.entity.User;
import com.example.order.entity.WareHouseSite;
import com.example.order.entity.WarehouseProduct;
import com.example.order.pojo.BuyersAndSellerPojo;
import com.example.order.pojo.ProviderOfferPojo;
import com.example.order.service.PrefileService;
import com.example.order.service.SettingService;
import com.example.order.service.WarehouseService;
import com.example.order.util.RanStringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@CrossOrigin //跨域
@RequestMapping("orderapi/warehouse")
public class WarehouseController {
    @Resource
    private WarehouseService warehouseService;
    @Resource
    private SettingService settingService;
    @Resource
    private PrefileService prefileService;
    //    @Resource
//    RestTemplate restTemplate;
    @Value("${sapopenurl}")
    String sapopenurl;
    @Value("${sapCompanyDB}")
    String sapCompanyDB;
    @Value("${sapPassword}")
    String sapPassword;
    @Value("${sapUserName}")
    String sapUserName;

    //    添加仓库地址
    @PostMapping("/addsite")
    public Map<String, Object> addSite(@RequestBody WareHouseSite addSiteData) {
        addSiteData.setLastupdate(new Date());

        if (addSiteData.getChecked() == null) {
            addSiteData.setChecked("false");
        }

        Integer resultInt = 0;
        if (addSiteData.getId() == null || addSiteData.getId().equals("")) {
            addSiteData.setCreatedate(new Date());
            resultInt = warehouseService.addSite(addSiteData);
        } else {
            if (addSiteData.getChecked().equals("true")) {
                WareHouseSite chkTmp = new WareHouseSite();
                chkTmp.setChecked("false");
                chkTmp.setOrgid(addSiteData.getOrgid());
                resultInt = warehouseService.updateSite(chkTmp);
            }

            resultInt = warehouseService.updateSite(addSiteData);
        }


        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();
        if (resultInt <= 0) {
            info.put("msg", "Add error");
            info.put("status", "404");
        } else {
            info.put("msg", "Add ok");
            info.put("status", "200");
        }

        result.put("data", null);
        result.put("meta", info);

        return result;
    }

    // 搜索所有仓库地址
    @ResponseBody
    @PostMapping("/queryListPageWarehousesite")
    public Map<String, Object> queryListPageWarehousesite(@RequestBody JSONObject json) {
        Integer pageNum = json.getInteger("current");
        Integer pageSize = json.getInteger("pageSize");
        Integer orgid = json.getInteger("orgid");
        Integer selfid = json.getInteger("selfid");


        PageHelper.startPage(pageNum, pageSize); //
        List<WareHouseSite> wareHouseSite = warehouseService.queryListPageWarehousesite(orgid, selfid, pageNum, pageSize);

        PageInfo<WareHouseSite> pageInfo = new PageInfo<>(wareHouseSite);

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();
        if (wareHouseSite == null) {
            info.put("msg", "Select error");
            info.put("status", "404");
        } else {
            info.put("msg", "Select ok");
            info.put("status", "200");
            result.put("info", pageInfo);
        }

        result.put("data", wareHouseSite);
        result.put("meta", info);

        return result;
    }

    //添加与编辑product
    @PostMapping("/addandeditproduct")
    public Map<String, Object> addSite(@RequestBody WarehouseProduct warehouseProduct) {
        warehouseProduct.setLastupdate(new Date());
        warehouseProduct.setCreatedate(new Date());
        warehouseProduct.setInbound(0);

        User regU = new User();
        regU.setId(warehouseProduct.getOrgid());

        User queryU = prefileService.queryLogin(regU);

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();
        Integer intResult = 0;

        Login login = new Login();
//        login.setCompanyDB("bizware_test");
        login.setCompanyDB(sapCompanyDB);
//        login.setPassword("vmM37Rh32H");
        login.setPassword(sapPassword);
//        login.setUserName("appdev");
        login.setUserName(sapUserName);

        String requestBody = JSONObject.toJSONString(login);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Cookie", "ROUTEID=.node4");

        String url = sapopenurl + "Login";

        HttpEntity entity = new HttpEntity<>(requestBody, httpHeaders);

        String SessionId = null;
        try {
            RestTemplate restTemplate = new RestTemplate(RestTemplateConfiguration.generateHttpRequestFactory());
            ResponseEntity response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            SessionId = JSONObject.parseObject(response.getBody().toString()).get("SessionId").toString();

            System.out.println(response.toString());
        } catch (Exception e) {
            if (intResult >= 0) {
                info.put("msg", "Sap Login ok");
                info.put("status", "303");
            }

            result.put("data", intResult);
            result.put("meta", info);

            return result;
        }

        //查询site 通过唯一的check获取仓库
//        List<WareHouseSite> siteTmplist = warehouseService.queryListWarehousesiteById(null, "", warehouseProduct.getOrgid(), "", "true");
//
//        if (siteTmplist.size() < 1) {
//            info.put("msg", "Upc Error");
//            info.put("status", "303");
//
//            result.put("data", intResult);
//            result.put("meta", info);
//
//            return result;
//        }
//
//        WareHouseSite siteTmp = siteTmplist.get(0);
//
//        //添加product
//        //我们存在
//        Product product = new Product();
//        product.setProductName(warehouseProduct.getName());
//        product.setEnProductName(warehouseProduct.getName());
//        String mfsku = null;
//        if (warehouseProduct.id == null) {
//            mfsku = warehouseProduct.getUpc() + System.currentTimeMillis();
//        } else {
//            mfsku = warehouseProduct.getMfsku();
//        }
//
//        product.setUpcBarcode(mfsku);
////        product.setUpcBarcode("BKFAGRBS1637044870375");
//
//        product.setExtraBarcode3(warehouseProduct.getUpc());
//
//        //必填
//        product.setHeight("50");
//        product.setWidth("40");
//        product.setLength("30");
//        product.setUnitPrice("100");
//        product.setInsurancePrice("100");
//        product.setIncludeBattery("1");
//        product.setSuitOrPacking("SUIT");
//        product.setProductType("SMALL");
//        product.setUnitChoose("KG-G-CM");
//        product.setFirstWeight("20");
//        product.setSecondWeight("10");
//
//        product.setFnsku("");
//
//        product.setUses("");
//        product.setBrand("");
//        product.setMaterial("");
//        product.setFormat("");
//        product.setProperties("");
//        product.setRemark("");
//        product.setWarningInventory("");
//        product.setExtraBarcode1("");
//        product.setExtraBarcode2("");
//        product.setCombinationType("");
//        product.setEan("");
//        product.setProductUrl("");
//        product.setNeedSerialNumber("");
//        product.setBatteiresContained("");
//        product.setBatteiresType("");
//        product.setBoxRulesList(null);
//
//        String requestBody = JSONObject.toJSONString(product);
//        String appSecret = siteTmp.getSignature(); //填入相应的密钥
//        String requestDateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//        StringBuilder sb = new StringBuilder();
//        sb.append(requestBody).append(appSecret).append(requestDateStr);
//        String content = sb.toString();
//
//        String wmsToken = RanStringUtil.MD5(content);
//
//        HttpHeaders httpHeaders = new HttpHeaders();
//
////        httpHeaders.setContentType(MediaType.APPLICATION_XML);
//
//        httpHeaders.set("appKey", siteTmp.getAppkey());
//
//        httpHeaders.set("signature", wmsToken);
//        httpHeaders.set("requestDate", requestDateStr);
//        httpHeaders.set("languageCode", "en");

        //查询sap 应需求 只可能出现一条upc 于是不做一对多
        String[] tmpPUPC = warehouseProduct.getUpc().split(",");
        String upcForSpq = "";
        for (int i = 0; i < tmpPUPC.length; i++) {
            if (i == 0) {
                upcForSpq = upcForSpq + "'" + tmpPUPC[i] + "'";
            } else {
                upcForSpq = upcForSpq + ",'" + tmpPUPC[i] + "'";
            }
        }

        Connection conn = MSSSConnection.getConn();
        String sql = "select * from OITM where U_upc in ( " +
                upcForSpq
                +
                ") and [validFor] = 'Y' and U_app_user_id ='" +
                warehouseProduct.getOrgid()
                +
                "'";
        PreparedStatement pstmt;
        ResultSet rs;

        String ItemCode = null;

        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ItemCode = rs.getNString("ItemCode");
                String tmpUPCOfItem = rs.getNString("U_upc");
            }
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (warehouseProduct.id == null) {
            //添加
//            warehouseProduct.setMfsku(mfsku);
            intResult = warehouseService.addProduct(warehouseProduct);

            //添加到sap
            Product productSap = new Product();
            productSap.setItemType("itItems");
            productSap.setU_app_whseproduct_id(warehouseProduct.getId().toString());
            productSap.setItemCode(warehouseProduct.getId().toString());

            //sap 名字不能超过199
            if (warehouseProduct.getName().length() > 199) {
                productSap.setItemName(warehouseProduct.getName().substring(0, 199));
            } else {
                productSap.setItemName(warehouseProduct.getName());
            }

            productSap.setU_app_user_id(warehouseProduct.getOrgid().toString());
            productSap.setU_app_user_username(queryU.getUsername());
            productSap.setU_asin(warehouseProduct.getAsin());
            productSap.setU_sku(warehouseProduct.getSku());
            productSap.setU_upc(warehouseProduct.getUpc());

            if (productSap.getU_asin() == null) {
                productSap.setU_asin("");
            }
            if (productSap.getU_sku() == null) {
                productSap.setU_sku("");
            }

            if (ItemCode == null) {
                requestBody = JSONObject.toJSONString(productSap);

                httpHeaders = new HttpHeaders();

                httpHeaders.set("Cookie", "B1SESSION=" + SessionId + "; Path=/b1s/v1; Secure; HttpOnly;");
//            httpHeaders.set("Cookie", "ROUTEID=.node4");
                httpHeaders.set("Content-Type", "text/plain");

                url = sapopenurl + "Items";

                entity = new HttpEntity<>(requestBody, httpHeaders);

                try {
                    RestTemplate restTemplate = new RestTemplate(RestTemplateConfiguration.generateHttpRequestFactory());
                    ResponseEntity response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

//                System.out.println(response.toString());
                } catch (Exception e) {
                    System.out.println(e.getStackTrace());
                }
            }
        } else {
            //更新
            intResult = warehouseService.updateProduct(warehouseProduct);

            if (ItemCode == null) {
                //添加到sap
                Product productSap = new Product();
                productSap.setItemType("itItems");
                productSap.setU_app_whseproduct_id(warehouseProduct.getId().toString());
                productSap.setItemCode(warehouseProduct.getId().toString());
                productSap.setItemName(warehouseProduct.getName());
                productSap.setU_app_user_id(warehouseProduct.getOrgid().toString());
                productSap.setU_app_user_username(queryU.getUsername());
                productSap.setU_asin(warehouseProduct.getAsin());
                productSap.setU_sku(warehouseProduct.getSku());
                productSap.setU_upc(warehouseProduct.getUpc());

                if (productSap.getU_asin() == null) {
                    productSap.setU_asin("");
                }
                if (productSap.getU_sku() == null) {
                    productSap.setU_sku("");
                }

                requestBody = JSONObject.toJSONString(productSap);

                httpHeaders = new HttpHeaders();

                httpHeaders.set("Cookie", "B1SESSION=" + SessionId + "; Path=/b1s/v1; Secure; HttpOnly;");
//            httpHeaders.set("Cookie", "ROUTEID=.node4");
                httpHeaders.set("Content-Type", "text/plain");

                url = sapopenurl + "Items" + "('" + warehouseProduct.getId() + "')";

                entity = new HttpEntity<>(requestBody, httpHeaders);

                try {
                    RestTemplate restTemplate = new RestTemplate(RestTemplateConfiguration.generateHttpRequestFactory());
                    ResponseEntity response = restTemplate.exchange(url, HttpMethod.PATCH, entity, String.class);

                    System.out.println(response.toString());
                } catch (Exception e) {
                    System.out.println(e.getStackTrace());
                }
            }

//            String url = mfopenurl + "fbaCargo/productModification";
//
//            HttpEntity entity = new HttpEntity<>(requestBody, httpHeaders);
//
//            try {
//                ResponseEntity response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
//                System.out.println(response.toString());
//            } catch (Exception e) {
//                System.out.println(e.getStackTrace());
//            }
        }

        if (ItemCode != null) {
            Product productSapUpdata = new Product();
            productSapUpdata.setItemName(warehouseProduct.getName());
            productSapUpdata.setU_app_whseproduct_id(warehouseProduct.getId().toString());
            productSapUpdata.setU_app_user_id(warehouseProduct.getOrgid().toString());

            requestBody = JSONObject.toJSONString(productSapUpdata);

            httpHeaders = new HttpHeaders();

            httpHeaders.set("Cookie", "B1SESSION=" + SessionId + "; Path=/b1s/v1; Secure; HttpOnly;");
//            httpHeaders.set("Cookie", "ROUTEID=.node4");
            httpHeaders.set("Content-Type", "text/plain");

            url = sapopenurl + "Items" + "('" + ItemCode + "')";

            entity = new HttpEntity<>(requestBody, httpHeaders);

            try {
                RestTemplate restTemplate = new RestTemplate(RestTemplateConfiguration.generateHttpRequestFactory());
                ResponseEntity response = restTemplate.exchange(url, HttpMethod.PATCH, entity, String.class);

                System.out.println(response.toString());
            } catch (Exception e) {
                System.out.println(e.getStackTrace());
            }
        }


        if (intResult >= 0) {
            info.put("msg", "Insert ok");
            info.put("status", "200");
        }

        result.put("data", intResult);
        result.put("meta", info);

        return result;
    }

    // 搜索所有product
    @ResponseBody
    @PostMapping("/queryListPageProduct")
    public Map<String, Object> queryListPageProduct(@RequestBody JSONObject json) {
        Integer pageNum = json.getInteger("current");
        Integer pageSize = json.getInteger("pageSize");
        Integer orgid = json.getInteger("orgid");
        String searchinput = json.getString("searchinput");

        String status = json.getString("status");
        String endDate = json.getString("endDate");

        PageHelper.startPage(pageNum, pageSize); //
        List<WarehouseProduct> warehouseProduct = warehouseService.queryListPageProduct(orgid, pageNum, pageSize, searchinput, status, endDate);

        for (WarehouseProduct item : warehouseProduct) {
            if (item.getQuantitylist() != null) {
                String[] quantitylistString = item.getQuantitylist().split(",");
                List<String> list = new ArrayList<>();
                for (int i = 0; i < quantitylistString.length; i++) {
                    if (!list.contains(quantitylistString[i])) {
                        if (item.getSuminstock() == null) {
                            item.setSuminstock(0);
                        }

                        String[] tmp = quantitylistString[i].split(":");
                        if (tmp[1] != null) {
                            item.setSuminstock(item.getSuminstock() + Integer.parseInt(tmp[1]));
                        }

                        list.add(quantitylistString[i]);
                    }
                }

                String[] inboundlistString = item.getInboundlist().split(",");
                list = new ArrayList<>();
                for (int i = 0; i < inboundlistString.length; i++) {
                    if (!list.contains(inboundlistString[i])) {
                        if (item.getSuminbound() == null) {
                            item.setSuminbound(0);
                        }

                        String[] tmp = inboundlistString[i].split(":");
                        if (tmp[1] != null) {
                            item.setSuminbound(item.getSuminbound() + Integer.parseInt(tmp[1]));
                        }

                        list.add(inboundlistString[i]);
                    }
                }


//                if (quantitylistString.length > 0) {
//                    for (String itmp : quantitylistString) {
//                        if (item.getSuminstock() == null) {
//                            item.setSuminstock(0);
//                        }
//                        if (itmp != null) {
//                            item.setSuminstock(item.getSuminstock() + Integer.parseInt(itmp));
//                        }
//                    }
//                }
            }
        }

        PageInfo<WarehouseProduct> pageInfo = new PageInfo<>(warehouseProduct);

        List<SettingGeneral> settingGeneral = settingService.queryGeneral(orgid);

        List<WareHouseSite> wareHouseSite = warehouseService.queryListPageWarehousesite(orgid, null, 1, 5);

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        if (wareHouseSite != null && wareHouseSite.size() > 0) {
            result.put("sitedata", wareHouseSite);
        }

        if (settingGeneral != null && settingGeneral.size() > 0) {
            result.put("sittingdata", settingGeneral.get(0));
        }

        if (warehouseProduct == null) {
            info.put("msg", "Select error");
            info.put("status", "404");
        } else {
            info.put("msg", "Select ok");
            info.put("status", "200");
            result.put("info", pageInfo);
        }

        result.put("data", warehouseProduct);
        result.put("meta", info);

        return result;
    }


    @PostMapping("/deleteWarehousesite")
    public Map<String, Object> deleteWarehousesite(@RequestBody JSONObject json) {
        Integer id = json.getInteger("id");
        Integer resultInt = warehouseService.deleteSite(id);

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();
        if (resultInt <= 0) {
            info.put("msg", "Delete error");
            info.put("status", "404");
        } else {
            info.put("msg", "Delete ok");
            info.put("status", "200");
        }

        result.put("data", null);
        result.put("meta", info);

        return result;
    }

    @ResponseBody
    @PostMapping("/querypackagebyupcdata")
    public Map<String, Object> queryPackageByUpcData(@RequestBody JSONObject json) {
        Integer pageNum = json.getInteger("current");
        Integer pageSize = json.getInteger("pageSize");

        Integer userid = json.getInteger("userid");
//        String username = json.getString("username");

        String searchinput = json.getString("searchinput");

        String startDate = json.getString("startDate");
        String endDate = json.getString("endDate");


        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();


        PageHelper.startPage(pageNum, pageSize); //

        List<Map<String, String>> queryPackageByUpcDataList = warehouseService.queryByupcdate(userid, pageNum, pageSize, searchinput, startDate, endDate);

        PageInfo<Map<String, String>> pageInfoquerylistOfferList = new PageInfo<>(queryPackageByUpcDataList);

        result.put("info", pageInfoquerylistOfferList);

        info.put("msg", "Select ok");
        info.put("status", "200");

        result.put("meta", info);

        return result;
    }

    @PostMapping("/deleteexcelpackagebyid")
    public Map<String, Object> deleteExcelpackageById(@RequestBody JSONObject json) {
        Integer id = json.getInteger("id");

        Integer resultInt = warehouseService.deleteExcelpackageById(id);

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();
        if (resultInt <= 0) {
            info.put("msg", "Delete error");
            info.put("status", "404");
        } else {
            info.put("msg", "Delete ok");
            info.put("status", "200");
        }

        result.put("data", null);
        result.put("meta", info);

        return result;
    }
}
