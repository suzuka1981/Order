package com.example.order.controller.buyers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.order.entity.*;
import com.example.order.pojo.*;
import com.example.order.service.*;
import com.example.order.util.ExcelUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@CrossOrigin //跨域
@RequestMapping("orderapi/taskoffer")
public class TaskBuyersController {
    @Resource
    private PrefileService prefileService;
    @Resource
    private OfferService offerService;
    @Resource
    private WarehouseService warehouseService;
    @Resource
    private SettingService settingService;
    @Resource
    private OutBoundService outBoundService;

    private final ResourceLoader resourceLoader;

    @Value("${filepath}")
    public String filePath;

    @Autowired
    public TaskBuyersController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @ResponseBody
    @PostMapping("/queryBuyersTaskListPageActive")
    public Map<String, Object> queryBuyersTaskListPageActive(@RequestBody JSONObject json) {
        Integer pageNum = json.getInteger("current");
        Integer pageSize = json.getInteger("pageSize");
        String username = json.getString("username");
        String searchinput = json.getString("searchinput");
        String searchoffer = json.getString("searchoffer");
        String orguserselect = json.getString("orguserselect");


        String startDate = json.getString("startDate");
        String endDate = json.getString("endDate");

        //卖家搜索
        String selecttype = json.getString("selecttype");
        String userid = json.getString("userid");

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        List<BigInteger> orgidList = new ArrayList<>();
        BuyersAndSellerPojo basList = null;
        BigInteger selfid = null;


        if (selecttype == null) {
            basList = prefileService.getBuyersAndSeller(username, null, 1);

            if (basList != null && basList.getUserList().size() > 0) {
                if (orguserselect == null) {
                    for (User uTmp : basList.getUserList()) {
                        orgidList.add(uTmp.getId());
                    }
                } else {
                    orgidList.add(new BigInteger(orguserselect));
                }

                selfid = basList.getId();
            }
        } else if (selecttype.equals("provider")) {
            orgidList.add(new BigInteger(userid));
        }

        try {
            PageHelper.startPage(pageNum, pageSize); //

            List<ProviderOfferPojo> querylistOfferList = offerService.queryTasklistOffer(orgidList, selfid, pageNum, pageSize, searchinput, startDate, endDate);

            PageInfo<ProviderOfferPojo> pageInfoquerylistOfferList = new PageInfo<>(querylistOfferList);

            if (querylistOfferList != null && querylistOfferList.size() > 0) {
                result.put("querylistOfferList", querylistOfferList);
            }


            result.put("info", pageInfoquerylistOfferList);

            //获取买手自家地址
            List<WareHouseSite> wareHouseSite = null;
            if (selfid != null) {
                wareHouseSite = warehouseService.queryListPageWarehousesite(
                        null, Integer.parseInt(selfid.toString()), pageNum, pageSize);
            }

            if (wareHouseSite != null && wareHouseSite.size() > 0) {
                result.put("sitedata", wareHouseSite);
            }

            if (basList != null) {
                result.put("selleruserlist", basList.getUserList());
            }
        } catch (Exception e) {

        }
        info.put("msg", "Select ok");
        info.put("status", "200");

        result.put("meta", info);

        return result;
    }

    //confirm task
    @PostMapping("/taskconfirm")
    public Map<String, Object> taskConfirm(@RequestBody JSONObject json) {
        BuyersTask buyersTask = json.getObject("buyersTask", BuyersTask.class);
//        List<ExcelPackages> ExcelPackages = json.getObject("excelPackages", ExcelPackages.class);

        JSONArray objects = JSONArray.parseArray(json.getJSONArray("excelPackages").toString());
        List<ExcelPackages> categoryConstantInfos = objects.toJavaList(ExcelPackages.class);

        BuyersConfirm buyersConfirm = new BuyersConfirm();

        buyersConfirm.setLastupdate(new Date());
        buyersConfirm.setCreatedate(new Date());
        buyersTask.setLastupdate(new Date());

        //task数据整合
        buyersConfirm.setBuyersid(buyersTask.getSelfid());
        buyersConfirm.setSellerid(buyersTask.getOrgid());
        buyersConfirm.setQuantity(buyersTask.getUsedquantity());
        buyersConfirm.setSelfsite(buyersTask.getSelfsite());
        buyersConfirm.setTrackingnumber(buyersTask.getTrackingnumber());
        buyersConfirm.setStatus("confirm");
        buyersConfirm.setPrice(buyersTask.getPrice());
        buyersConfirm.setBuyerstaskid(buyersTask.getId());
        buyersConfirm.setType(buyersTask.getType());

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        Integer intResult = 0;
        Integer err = 0;

        for (ExcelPackages item : categoryConstantInfos) {
            Integer tmpUsedquantity = item.getUsedquantity();
            if (tmpUsedquantity == 0) {
                err++;
                break;
            }

            WarehouseConfirmPackages warehouseConfirmPackages = new WarehouseConfirmPackages();
            warehouseConfirmPackages.setExcelpackagesid(item.getId());
            warehouseConfirmPackages.setBuyersconfirmid(item.getBsid());

            Integer setQ = 0;
            if (buyersTask.getUsedquantity() > item.getUsedquantity()) {
                setQ = item.getUsedquantity();
            } else {
                setQ = buyersTask.getUsedquantity();
            }
            warehouseConfirmPackages.setQuantity(setQ);

            warehouseConfirmPackages.setTracking(item.getTracking());
            warehouseConfirmPackages.setStatus("Confirmed");
            warehouseConfirmPackages.setCreatedate(new Date());
            warehouseConfirmPackages.setLastupdate(new Date());
            intResult = warehouseService.addWarehouseConfirmPackages(warehouseConfirmPackages);

            if (intResult > 0) {
                intResult = settingService.updateExcelPackages(item, warehouseConfirmPackages.getQuantity());

                if (intResult > 0) {
                    buyersTask.setUsedquantity(tmpUsedquantity);
                    intResult = offerService.updateTaskOffer(buyersTask);

                    //添加
                    if (intResult > 0) {
                        buyersConfirm.setQuantity(warehouseConfirmPackages.getQuantity());
                        intResult = offerService.addBuyersConfirm(buyersConfirm);
                    } else {
                        err++;
                    }
                } else {
                    err++;
                }

            } else {
                err++;
            }
        }
        if (buyersTask.getType().equals("self")) {
            intResult = offerService.updateTaskOffer(buyersTask);

            //添加
            if (intResult > 0) {
                intResult = offerService.addBuyersConfirm(buyersConfirm);
            } else {
                err++;
            }
        }

        if (err > 0) {
            info.put("msg", "Error");
            info.put("status", "303");
        } else {
            {
                info.put("msg", "ok");
                info.put("status", "200");
            }
        }

        result.put("data", intResult);
        result.put("meta", info);

        return result;
    }

    @PostMapping("/querywarehouselist")
    public Map<String, Object> queryWarehouseList(@RequestBody BuyersTask buyersTask) {
        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        //卖家仓库
        List<String> parList = null;
        List<WareHouseSite> sellerWareHouseSite = null;
        if (buyersTask.getWarehousesitesvalue() != null && buyersTask.getWarehousesitesvalue() != "") {
            buyersTask.setWarehousesitesvalue(buyersTask.getWarehousesitesvalue() + ',');
            parList = Arrays.asList(buyersTask.getWarehousesitesvalue().split(","));
            sellerWareHouseSite = warehouseService.queryListWarehousesiteById(parList, "", buyersTask.getOrgid(), "", "");
        }


        if (sellerWareHouseSite != null && sellerWareHouseSite.size() > 0) {
            result.put("sellersitedata", sellerWareHouseSite);
            info.put("msg", "ok");
            info.put("status", "200");
        } else {
            info.put("msg", "Error");
            info.put("status", "303");
        }

        result.put("meta", info);

        return result;
    }

    @ResponseBody
    @PostMapping("/queryConfirmCheckWarehouse")
    public Map<String, Object> queryConfirmList(@RequestBody JSONObject json) {
        Integer buyersid = json.getInteger("buyersid");
        Integer sellerid = json.getInteger("sellerid");
        Integer buyerstaskid = json.getInteger("buyerstaskid");

        String[] trackingMap = json.getString("tracking").split(",");
//        List<String> tracking = new ArrayList<>();
//        for (String item : trackingMap) {
//            tracking.add(item);
//        }

        Integer pageNum = json.getInteger("current");
        Integer pageSize = json.getInteger("pageSize");

        PageHelper.startPage(pageNum, pageSize); //

        List<ExcelPackages> queryConfirmCheckWarehouse = offerService.queryConfirmCheckWarehouse(trackingMap, pageNum, pageSize, buyerstaskid);
//        queryConfirmCheckWarehouseOld = offerService.queryConfirmCheckWarehouseOld(trackingMap, pageNum, pageSize, buyerstaskid);
//
//        for(Map<String, String> item:queryConfirmCheckWarehouseOld){
//            queryConfirmCheckWarehouse.add(item);
//        }

        PageInfo<ExcelPackages> pageConfirmCheckWarehouse = new PageInfo<ExcelPackages>(queryConfirmCheckWarehouse);


        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();


        if (queryConfirmCheckWarehouse != null && queryConfirmCheckWarehouse.size() > 0) {
            info.put("msg", "ok");
            info.put("status", "200");
        } else {
            info.put("msg", "Error");
            info.put("status", "303");
        }

        result.put("info", pageConfirmCheckWarehouse);

        result.put("meta", info);

        return result;
    }

    @ResponseBody
    @PostMapping("/queryconfirmofproduct")
    public Map<String, Object> queryConfirmOfProduct(@RequestBody JSONObject json) {
        //分页
        Integer orgid = json.getInteger("orgid");
        Integer productid = json.getInteger("productid");

        String searchinput = json.getString("searchinput");
        if (searchinput == null || searchinput.equals("")) {
            searchinput = "";
        }

        Integer pageNum = json.getInteger("current");
        Integer pageSize = json.getInteger("pageSize");

        Integer instcokpageNum = json.getInteger("instcokcurrent");
        Integer instcokpageSize = json.getInteger("instcokpageSize");

        if (instcokpageNum == null || instcokpageSize == null) {
            instcokpageNum = 1;
            instcokpageSize = 50;
        }

        Map<String, String> userobject = json.getObject("userobject", Map.class);
        String value = null;
        String label = "";
        if (userobject != null && userobject.size() > 0) {
            value = String.valueOf(userobject.get("value"));
            label = String.valueOf(userobject.get("label"));
        }

        PageHelper.startPage(pageNum, pageSize);
        List<BuyersConfirm> buyersConfirmList = warehouseService.queryConfirmOfProduct(orgid, productid, pageNum, pageSize);
        PageInfo<BuyersConfirm> pageInfobuyersConfirmList = new PageInfo<>(buyersConfirmList);

        PageHelper.startPage(instcokpageNum, instcokpageSize);
        List<SumQuantityConfirmOfProductforwarehouse> buyersSumConfirmListforwarehouse = new ArrayList<>();
        List<SumQuantityConfirmOfProductforwarehouse> buyersSumConfirmList = new ArrayList<>();

        buyersSumConfirmListforwarehouse = warehouseService.querySumQuantityConfirmOfProductforwarehouse(orgid, productid, instcokpageNum, instcokpageSize, searchinput);

        Integer selfid = null;
        if (!label.equals("Bizblock warehouse") && value != null) {
            selfid = Integer.parseInt(value);
        }

        Integer selfSize = instcokpageSize;
        if (!label.equals("Bizblock warehouse")) {
            selfSize = instcokpageSize - buyersSumConfirmListforwarehouse.size();
        }

        buyersSumConfirmList = warehouseService.querySumQuantityConfirmOfProduct(orgid, productid, instcokpageNum, selfSize, searchinput, selfid);

        //拼接 user list
        List<Map<String, String>> userList = new ArrayList<>();
        for (SumQuantityConfirmOfProductforwarehouse item : buyersSumConfirmListforwarehouse) {
            Map<String, String> tmp = new HashMap<>();
            tmp.put("userid", item.getUserid().toString());
            if (item.getType() != null) {
                tmp.put("type", item.getType());
            }
            tmp.put("username", item.getUsername().toString());
            userList.add(tmp);
        }
        for (SumQuantityConfirmOfProductforwarehouse item : buyersSumConfirmList) {
            Map<String, String> tmp = new HashMap<>();
            tmp.put("userid", item.getUserid().toString());
            if (item.getType() != null) {
                tmp.put("type", item.getType());
            }
            tmp.put("username", item.getUsername());
            userList.add(tmp);
        }
        HashSet hsu = new HashSet(userList);
        userList.clear();
        userList.addAll(hsu);

        if (label == null || label.equals("")) {
            if (buyersSumConfirmList != null && buyersSumConfirmList.size() > 0) {
                for (SumQuantityConfirmOfProductforwarehouse item : buyersSumConfirmList) {
                    Boolean addBool = true;
                    for (SumQuantityConfirmOfProductforwarehouse items : buyersSumConfirmListforwarehouse) {
                        if (item.getType().equals(items.getType())
                                && item.getProductid().equals(items.getProductid())
                                && item.getSiteid().equals(items.getSiteid())
                        ) {
                            if (addBool) {
                                addBool = false;
                            }
                        }
                    }

                    if (addBool) {
                        String[] quantitylistString = item.getQuantitylist().split(",");
                        for (String itmp : quantitylistString) {
                            if (item.quantity == null) {
                                item.quantity = 0;
                            }
                            if (itmp != null) {
                                item.quantity += Integer.parseInt(itmp);
                            }
                        }
                        buyersSumConfirmListforwarehouse.add(item);
                    }
                }
            }

            HashSet hs = new HashSet(buyersSumConfirmListforwarehouse);
            buyersSumConfirmListforwarehouse.clear();
            buyersSumConfirmListforwarehouse.addAll(hs);
        } else if (!label.equals("Bizblock warehouse")) {
            List<SumQuantityConfirmOfProductforwarehouse> tmpList = new ArrayList<>();

            if (buyersSumConfirmList != null && buyersSumConfirmList.size() > 0) {
                for (SumQuantityConfirmOfProductforwarehouse item : buyersSumConfirmList) {
                    Boolean addBool = true;
                    for (SumQuantityConfirmOfProductforwarehouse items : tmpList) {
                        if (item.getType().equals(items.getType())
                                && item.getProductid().equals(items.getProductid())
                                && item.getSiteid().equals(items.getSiteid())
                        ) {
                            if (addBool) {
                                addBool = false;
                            }
                        }
                    }

                    if (addBool) {
                        String[] quantitylistString = item.getQuantitylist().split(",");
                        for (String itmp : quantitylistString) {
                            if (item.quantity == null) {
                                item.quantity = 0;
                            }
                            if (itmp != null) {
                                item.quantity += Integer.parseInt(itmp);
                            }
                        }
                        tmpList.add(item);
                    }
                }
            }

            buyersSumConfirmListforwarehouse = tmpList;
        }


//        buyersSumConfirmList.addAll(buyersSumConfirmListforwarehouse);

//        PageInfo<Map<String, String>> pageInfobuyersSumConfirmList = new PageInfo<>(buyersSumConfirmList);

//        PageHelper.startPage(instcokpageNum, instcokpageSize);
//        List<Map<String, String>> buyersSumConfirmListforwarehouse = warehouseService.querySumQuantityConfirmOfProductforwarehouse(orgid, productid, instcokpageNum, instcokpageSize);
        PageInfo<SumQuantityConfirmOfProductforwarehouse> pageInfobuyersSumConfirmListforwarehouse = new PageInfo<>(buyersSumConfirmListforwarehouse);

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        if (buyersConfirmList != null && buyersConfirmList.size() >= 0) {
//            result.put("buyersconfirmlist", buyersConfirmList);
            info.put("msg", "ok");
            info.put("status", "200");
        } else {
            info.put("msg", "Error");
            info.put("status", "303");
        }

        result.put("info", pageInfobuyersConfirmList);
//        result.put("infoinsotck", pageInfobuyersSumConfirmList);
        result.put("infoinsotckforwarehouse", pageInfobuyersSumConfirmListforwarehouse);
        result.put("userList", userList);

        result.put("meta", info);

        return result;
    }

    @ResponseBody
    @PostMapping("/querytaskhistorylist")
    public Map<String, Object> queryTaskHistoryList(@RequestBody JSONObject json) {
        String username = json.getString("username");
        String searchinput = json.getString("searchinput");
        String startDate = json.getString("startDate");
        String endDate = json.getString("endDate");
        String sendtotype = json.getString("sendtotype");
        String orguserselect = json.getString("orguserselect");

        Integer pageNum = json.getInteger("current");
        Integer pageSize = json.getInteger("pageSize");

        //卖家搜索
        String selecttype = json.getString("selecttype");
        String userid = json.getString("userid");

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        List<BigInteger> sellerIdList = new ArrayList<>();
        BuyersAndSellerPojo basList = null;
        BigInteger selfid = null;

        if (selecttype == null) {
            basList = prefileService.getBuyersAndSeller(username, null, 1);

            if (basList != null && basList.getUserList().size() > 0) {
                if (orguserselect == null) {
                    for (User uTmp : basList.getUserList()) {
                        sellerIdList.add(uTmp.getId());
                    }
                } else {
                    sellerIdList.add(new BigInteger(orguserselect));
                }

                selfid = basList.getId();

            }
        } else if (selecttype.equals("provider")) {
            sellerIdList.add(new BigInteger(userid));
        }

        try {
            PageHelper.startPage(pageNum, pageSize); //

            List<TaskHistoryPojo> queryListTaskHistory = offerService.queryListTaskHistory(sellerIdList, selfid, startDate, endDate, pageNum, pageSize, searchinput, sendtotype);

            PageInfo<TaskHistoryPojo> pageInqueryListTaskHistory = new PageInfo<>(queryListTaskHistory);

            if (queryListTaskHistory != null && queryListTaskHistory.size() > 0) {
                result.put("querylistHistoryList", queryListTaskHistory);
            }

            if (basList != null) {
                result.put("selleruserlist", basList.getUserList());
            }

            result.put("info", pageInqueryListTaskHistory);
        } catch (Exception e) {

        }
        info.put("msg", "Select ok");
        info.put("status", "200");

        result.put("meta", info);

        return result;
    }

    @PostMapping("/taskcancel")
    public Map<String, Object> taskCancel(@RequestBody BuyersTask buyersTask) {
        BuyersConfirm buyersCancel = new BuyersConfirm();

        buyersCancel.setLastupdate(new Date());
        buyersCancel.setCreatedate(new Date());
        buyersTask.setLastupdate(new Date());

        //task数据整合
        buyersCancel.setBuyersid(buyersTask.getSelfid());
        buyersCancel.setSellerid(buyersTask.getOrgid());
        buyersCancel.setQuantity(buyersTask.getUsedquantity());
        buyersCancel.setSelfsite(buyersTask.getSelfsite());
        buyersCancel.setTrackingnumber(buyersTask.getTrackingnumber());
        buyersCancel.setStatus("cancel");
        buyersCancel.setPrice(buyersTask.getPrice());
        buyersCancel.setBuyerstaskid(buyersTask.getId());
        buyersCancel.setType(buyersTask.getType());

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        Integer intResult = 0;

        Offer offerTmp = new Offer();
        offerTmp.setId(buyersTask.getOfferid());
        offerTmp.setQuantity(buyersTask.getUsedquantity());

        intResult = offerService.updateCancelOffer(offerTmp);
        if (intResult > 0) {
            intResult = offerService.updateTaskOffer(buyersTask);

            //添加Cancel
            if (intResult > 0) {
                intResult = offerService.addBuyersCancel(buyersCancel);
            }
        }

        if (intResult > 0) {
            info.put("msg", "ok");
            info.put("status", "200");
        } else {
            info.put("msg", "Error");
            info.put("status", "303");
        }

        result.put("data", intResult);
        result.put("meta", info);

        return result;
    }

    @ResponseBody
    @PostMapping("/querytaskcancellist")
    public Map<String, Object> queryTaskCancelList(@RequestBody JSONObject json) {
        String username = json.getString("username");
        String searchinput = json.getString("searchinput");
        String startDate = json.getString("startDate");
        String endDate = json.getString("endDate");
        String sendtotype = json.getString("sendtotype");
        String orguserselect = json.getString("orguserselect");

        Integer pageNum = json.getInteger("current");
        Integer pageSize = json.getInteger("pageSize");

        //卖家搜索
        String selecttype = json.getString("selecttype");
        String userid = json.getString("userid");

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();
        List<BigInteger> sellerIdList = new ArrayList<>();
        BigInteger selfid = null;
        BuyersAndSellerPojo basList = null;

        if (selecttype == null) {
            basList = prefileService.getBuyersAndSeller(username, null, 1);

            if (basList != null && basList.getUserList().size() > 0) {
                if (orguserselect == null) {
                    for (User uTmp : basList.getUserList()) {
                        sellerIdList.add(uTmp.getId());
                    }
                } else {
                    sellerIdList.add(new BigInteger(orguserselect));
                }

                selfid = basList.getId();
            }
        } else if (selecttype.equals("provider")) {
            sellerIdList.add(new BigInteger(userid));
        }

        try {
            PageHelper.startPage(pageNum, pageSize); //

            List<TaskHistoryPojo> queryListTaskCancel = offerService.queryListTaskCacncel(sellerIdList, selfid, startDate, endDate, pageNum, pageSize, searchinput, sendtotype);

            PageInfo<TaskHistoryPojo> pageInqueryListTaskCancel = new PageInfo<>(queryListTaskCancel);

//        pageInqueryListTaskCancel.setList(queryListTaskCancel);

            if (queryListTaskCancel != null && queryListTaskCancel.size() > 0) {
                result.put("querylistCancelList", queryListTaskCancel);
            }

            if (basList != null) {
                result.put("selleruserlist", basList.getUserList());
            }

            result.put("info", pageInqueryListTaskCancel);
        } catch (Exception e) {

        }
        info.put("msg", "Select ok");
        info.put("status", "200");

        result.put("meta", info);

        return result;
    }

    //卖家proposed 生成task approve
    @PostMapping("/proposedtotaskapprove")
    public Map<String, Object> proposedToTaskApprove(@RequestBody BuyersTask buyersTask) {
        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        Integer intResult = 0;

        buyersTask.setLastupdate(new Date());

        //更新proposed
        buyersTask.setStatus("approve");
        intResult = offerService.updateProposeOffer(buyersTask);

        if (intResult > 0) {
            //添加task
            buyersTask.setId(null);
            buyersTask.setStatus("task");
            intResult = offerService.addTaskOffer(buyersTask);
        }

        if (intResult > 0) {
            info.put("msg", "ok");
            info.put("status", "200");
        } else {
            info.put("msg", "Error");
            info.put("status", "303");
        }

        result.put("data", intResult);
        result.put("meta", info);

        return result;
    }

    //卖家proposed 生成task Deny
    @PostMapping("/proposedtotaskdeny")
    public Map<String, Object> proposedToTaskDeny(@RequestBody BuyersTask buyersTask) {
        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        Integer intResult = 0;

        buyersTask.setLastupdate(new Date());

        //更新proposed
        buyersTask.setStatus("deny");
        intResult = offerService.updateProposeOffer(buyersTask);

        if (intResult > 0) {
            info.put("msg", "ok");
            info.put("status", "200");
        } else {
            info.put("msg", "Error");
            info.put("status", "303");
        }

        result.put("data", intResult);
        result.put("meta", info);

        return result;
    }

    @ResponseBody
    @PostMapping("/querylistbuyersinventory")
    public Map<String, Object> queryListBuyersInventory(@RequestBody JSONObject json) {
        String username = json.getString("username");
        String searchinput = json.getString("searchinput");

        BigInteger orguserselect = json.getBigInteger("orguserselect");

        String type = "self";

        Integer pageNum = json.getInteger("current");
        Integer pageSize = json.getInteger("pageSize");


        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        List<BigInteger> sellerIdList = new ArrayList<>();
        BuyersAndSellerPojo basList = null;
        BigInteger selfid = null;

        basList = prefileService.getBuyersAndSeller(username, null, 1);

        if (basList != null && basList.getUserList().size() > 0) {
            if (orguserselect != null) {
                sellerIdList.add(orguserselect);
            } else {
                for (User uTmp : basList.getUserList()) {
                    sellerIdList.add(uTmp.getId());
                }
            }

            selfid = basList.getId();
        }

        try {
            PageHelper.startPage(pageNum, pageSize); //

            List<BuyersInventoryPojo> buyersInventorylist = offerService.queryListBuyersInventory(sellerIdList, selfid, pageNum, pageSize, searchinput, type);

            PageInfo<BuyersInventoryPojo> pageBuyersInventory = new PageInfo<>(buyersInventorylist);

            if (buyersInventorylist != null && buyersInventorylist.size() > 0) {
                result.put("querybuyersInventorylist", buyersInventorylist);
            }

            if (basList != null) {
                result.put("selleruserlist", basList.getUserList());
            }

            result.put("info", pageBuyersInventory);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }

        info.put("msg", "Select ok");
        info.put("status", "200");

        result.put("meta", info);

        return result;
    }

    @ResponseBody
    @PostMapping("/queryinventorydetail")
    public Map<String, Object> queryInventoryDetail(@RequestBody JSONObject json) {
        BuyersInventoryPojo buyersInventoryPojo = json.getObject("data", BuyersInventoryPojo.class);


        String searchinputdetail = json.getString("searchinputdetail");
        String type = "self";

        Integer pageNum = json.getInteger("current");
        Integer pageSize = json.getInteger("pageSize");


        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();


        PageHelper.startPage(pageNum, pageSize); //

        List<BuyersInventoryDetailPojo> buyersInventoryDetailPojo = offerService.queryListBuyersInventoryDetail(buyersInventoryPojo.getId(), buyersInventoryPojo.getSellerid(), buyersInventoryPojo.getBuyersid(), pageNum, pageSize, searchinputdetail, type);

        PageInfo<BuyersInventoryDetailPojo> pagebuyersInventoryDetailPojo = new PageInfo<>(buyersInventoryDetailPojo);

        if (buyersInventoryDetailPojo != null && buyersInventoryDetailPojo.size() > 0) {
            result.put("querybuyersInventoryDetailPojo", buyersInventoryDetailPojo);
        }

        result.put("info", pagebuyersInventoryDetailPojo);

        info.put("msg", "Select ok");
        info.put("status", "200");

        result.put("meta", info);

        return result;
    }

    @ResponseBody
    @PostMapping("/reportreduceconfirm")
    public Map<String, Object> reportReduceConfirm(@RequestBody JSONObject json) {
        Integer id = json.getInteger("id");
        Integer quantity = json.getInteger("quantity");
        Integer tmpquantity = json.getInteger("tmpquantity");

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        Integer intResult = 0;

        // 错误99999999999999999999999999999999999999999999999999999999999999999999
        // 添加mis 减款申请
//        BuyersConfirm buyersConfirm = new BuyersConfirm();
//        buyersConfirm.setId(id);
//        buyersConfirm.setQuantity(quantity - tmpquantity);
//        intResult = offerService.updateConfirm(buyersConfirm);

        List<BuyersConfirm> buyersConfirmList = offerService.queryConfirmList(null, null, null, null, null, id);
        if (buyersConfirmList.size() > 0) {
            BuyersConfirm buyersConfirm = buyersConfirmList.get(0);
            buyersConfirm.setId(null);
            buyersConfirm.setQuantity(-tmpquantity);
            buyersConfirm.setLastupdate(new Date());
            buyersConfirm.setHistorystatus(null);
            intResult = offerService.addBuyersConfirm(buyersConfirm);
        }

        if (intResult > 0) {
            info.put("msg", "ok");
            info.put("status", "200");
        } else {
            info.put("msg", "Error");
            info.put("status", "303");
        }

        result.put("meta", info);

        return result;
    }

    @ResponseBody
    @PostMapping("/querypendingtransactions")
    public Map<String, Object> queryPendingTransactions(@RequestBody JSONObject json) {
        String username = json.getString("username");
        String searchinput = json.getString("searchinput");
        String type = json.getString("type");
        String usertype = json.getString("usertype");
        String userid = json.getString("userid");

        Integer pageNum = json.getInteger("current");
        Integer pageSize = json.getInteger("pageSize");


        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        List<BigInteger> sellerIdList = new ArrayList<>();
        BuyersAndSellerPojo basList = null;
        BigInteger selfid = null;

        basList = prefileService.getBuyersAndSeller(username, null, 1);

        if (usertype == null) {
            if (basList != null && basList.getUserList().size() > 0) {
                for (User uTmp : basList.getUserList()) {
                    sellerIdList.add(uTmp.getId());
                }
                selfid = basList.getId();
            }
        } else {
            sellerIdList.add(new BigInteger(userid));
        }

        try {
            PageHelper.startPage(pageNum, pageSize); //

            List<Map<String, String>> pendingTransactionsMap = offerService.queryPendingTransactions(sellerIdList, selfid, pageNum, pageSize, searchinput, type);

            PageInfo<Map<String, String>> pageBuyersInventory = new PageInfo<>(pendingTransactionsMap);

            if (pendingTransactionsMap != null && pendingTransactionsMap.size() > 0) {
                result.put("pendingTransactionsMap", pendingTransactionsMap);
            }

            result.put("info", pageBuyersInventory);
        } catch (Exception e) {

        }
        info.put("msg", "Select ok");
        info.put("status", "200");

        result.put("meta", info);

        return result;
    }

    @ResponseBody
    @PostMapping("/querytransactionhistory")
    public Map<String, Object> queryTransactionHistory(@RequestBody JSONObject json) {
        String username = json.getString("username");
        String checked = json.getString("checked");
        if (checked.equals("false")) {
            checked = null;
        }
        String endDate = json.getString("endDate");

        String usertype = json.getString("usertype");
        String userid = json.getString("userid");
        String buyersselect = json.getString("buyersselect");

        Integer pageNum = json.getInteger("current");
        Integer pageSize = json.getInteger("pageSize");


        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        List<BigInteger> sellerIdList = new ArrayList<>();
        BuyersAndSellerPojo basList = null;
        BigInteger selfid = null;

        try {
            if (usertype == null) {
                basList = prefileService.getBuyersAndSeller(username, null, 1);
                if (basList != null && basList.getUserList().size() > 0) {
                    for (User uTmp : basList.getUserList()) {
                        sellerIdList.add(uTmp.getId());
                    }
                    selfid = basList.getId();
                }
            } else {
                basList = prefileService.getSellerSon(username, null, 1);
                sellerIdList.add(new BigInteger(userid));
                result.put("buyersList", basList.getUserList());

                if (buyersselect != null) {
                    selfid = new BigInteger(buyersselect);
                }
            }

            PageHelper.startPage(pageNum, pageSize); //

            List<TransactionHistory> transactionHistoryMap = offerService.queryTransactionHistory(sellerIdList, selfid, pageNum, pageSize, checked, endDate);

            PageInfo<TransactionHistory> pageBuyersInventory = new PageInfo<>(transactionHistoryMap);

            if (transactionHistoryMap != null && transactionHistoryMap.size() > 0) {
                result.put("transactionHistoryMap", transactionHistoryMap);
            }

            result.put("info", pageBuyersInventory);
        } catch (Exception e) {

        }

        info.put("msg", "Select ok");
        info.put("status", "200");

        result.put("meta", info);

        return result;
    }

    @ResponseBody
    @PostMapping("/querygetpaymentrequests")
    public Map<String, Object> querygetPaymentRequests(@RequestBody JSONObject json) {
        String username = json.getString("username");
        String orguserselect = json.getString("orguserselect");
        String searchinput = json.getString("searchinput");

        String paymentrequestsstatus = json.getString("paymentrequestsstatus");

        String type = "transactions";

        Integer pageNum = json.getInteger("current");
        Integer pageSize = json.getInteger("pageSize");

        String userid = json.getString("userid");
        String usertype = json.getString("usertype");


        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        List<BigInteger> sellerIdList = new ArrayList<>();
        BuyersAndSellerPojo basList = null;
        BigInteger selfid = null;

        try {
            if (usertype == null) {
                basList = prefileService.getBuyersAndSeller(username, null, 1);
                if (basList != null && basList.getUserList().size() > 0) {
                    if (orguserselect == null) {
                        for (User uTmp : basList.getUserList()) {
                            sellerIdList.add(uTmp.getId());
                        }
                    } else {
                        sellerIdList.add(new BigInteger(orguserselect));
                    }

                    selfid = basList.getId();

                    result.put("selleruserlist", basList.getUserList());
                }
            } else {
                sellerIdList.add(new BigInteger(userid));
                basList = prefileService.getSellerSon(username, null, 1);

                result.put("buyersList", basList.getUserList());

            }

            //计算 total amount
            BigDecimal amountTotal = new BigDecimal("0");

            List<PaymentRequest> tmpPaymentRequestMap = offerService.queryPaymentRequest(sellerIdList, selfid, null, null, null, paymentrequestsstatus);
            for (PaymentRequest item : tmpPaymentRequestMap) {
                amountTotal = amountTotal.add(item.getAmount());
            }

            result.put("amountTotal", amountTotal);


            BigDecimal releasedBalance = new BigDecimal("0");

            List<Map<String, String>> pendingTransactionsMap = offerService.queryPendingTransactions(sellerIdList, selfid, null, null, null, type);

            for (Map<String, String> item : pendingTransactionsMap) {
                Object objprice = item.get("price");
                Object objquantity = item.get("quantity");

                if (objprice != null && objprice != "" && objquantity != null && objquantity != "") {
                    BigDecimal price = new BigDecimal(objprice.toString());
                    BigDecimal quantity = new BigDecimal(objquantity.toString());

                    BigDecimal tmp = price.multiply(quantity);

                    releasedBalance = releasedBalance.add(tmp);
                }
            }

            result.put("releasedBalance", releasedBalance);

            BigDecimal pendingBalance = new BigDecimal("0");

            List<TransactionHistory> transactionHistoryMap = offerService.queryTransactionHistory(sellerIdList, selfid, null, null, null, null);

            for (TransactionHistory thItem : transactionHistoryMap) {
                if (thItem.getTopaystatus() != "" && thItem.getTopaystatus() != null) {
                    pendingBalance = pendingBalance.subtract(thItem.getPrice());
                } else {
                    pendingBalance = pendingBalance.add(thItem.getPrice().multiply(new BigDecimal(thItem.getQuantity())));
                }
            }


            result.put("pendingBalance", pendingBalance.subtract(amountTotal));

            //获取table
            PageHelper.startPage(pageNum, pageSize); //

            List<PaymentRequest> PaymentRequestMap = offerService.queryPaymentRequest(sellerIdList, selfid, pageNum, pageSize, searchinput, paymentrequestsstatus);

            PageInfo<PaymentRequest> pageInfoPaymentRequestMap = new PageInfo<>(PaymentRequestMap);

            result.put("pageInfoPaymentRequestMap", pageInfoPaymentRequestMap);

            //计算卖家下 各个买手total balance
            if (usertype != null) {
                Map<BigInteger, BigDecimal> buyersidTmpList = new HashMap<>();
                BigDecimal sellerTmpAmountTotal = new BigDecimal("0");

                for (PaymentRequest itempq : pageInfoPaymentRequestMap.getList()) {
                    sellerTmpAmountTotal = sellerTmpAmountTotal.add(itempq.getAmount());

                    if (!buyersidTmpList.containsKey(itempq.getBuyersid())) {
                        BigDecimal tmpamountTotal = new BigDecimal("0");
                        List<PaymentRequest> tmpItemPaymentRequestMap = offerService.queryPaymentRequest(sellerIdList, itempq.getBuyersid(), null, null, null, paymentrequestsstatus);
                        for (PaymentRequest item : tmpItemPaymentRequestMap) {
                            tmpamountTotal = tmpamountTotal.add(item.getAmount());
                        }

                        List<Map<String, String>> tmppendingTransactionsMap = offerService.queryPendingTransactions(sellerIdList, itempq.getBuyersid(), null, null, null, type);
                        BigDecimal tmpreleasedBalance = new BigDecimal("0");
                        for (Map<String, String> item : tmppendingTransactionsMap) {
                            Object objprice = item.get("price");
                            Object objquantity = item.get("quantity");

                            if (objprice != null && objprice != "" && objquantity != null && objquantity != "") {
                                BigDecimal price = new BigDecimal(objprice.toString());
                                BigDecimal quantity = new BigDecimal(objquantity.toString());

                                BigDecimal tmp = price.multiply(quantity);

                                tmpreleasedBalance = tmpreleasedBalance.add(tmp);
                            }
                        }

//                    result.put("releasedBalanceSeller", tmpreleasedBalance.subtract(tmpamountTotal));

                        List<TransactionHistory> tmpTransactionHistoryMap = offerService.queryTransactionHistory(sellerIdList, itempq.getBuyersid(), null, null, null, null);
                        BigDecimal tmpPendingBalance = new BigDecimal("0");
                        for (TransactionHistory thItem : tmpTransactionHistoryMap) {
                            try {
                                if (thItem.getTopaystatus() != "" && thItem.getTopaystatus() != null) {
                                    tmpPendingBalance = tmpPendingBalance.subtract(thItem.getPrice());
                                } else {
                                    tmpPendingBalance = tmpPendingBalance.add(thItem.getPrice().multiply(new BigDecimal(thItem.getQuantity())));
                                }
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                        }
//                    result.put("pendingBalanceSeller", tmpPendingBalance);

                        buyersidTmpList.put(itempq.getBuyersid(), tmpreleasedBalance.add(tmpPendingBalance).subtract(tmpamountTotal));
                    }

                    itempq.setSellertotalbalance(buyersidTmpList.get(itempq.getBuyersid()));
                }

                result.put("sellerTmpAmountTotal", sellerTmpAmountTotal);
            }
        } catch (Exception e) {

        }
        info.put("msg", "Select ok");
        info.put("status", "200");

        result.put("meta", info);

        return result;
    }

    @PostMapping("/addpaymentrequest")
    public Map<String, Object> addPaymentRequest(@RequestBody PaymentRequest paymentRequest) {
        paymentRequest.setLastupdate(new Date());

        Integer resultInt = 0;

        if (paymentRequest.getId() == null) {
            paymentRequest.setCreatedate(new Date());
            resultInt = offerService.addPaymentRequest(paymentRequest);
        } else {
            resultInt = offerService.updatePaymentRequest(paymentRequest);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, String> info = new HashMap<String, String>();


        if (resultInt > 0) {
            info.put("msg", "ok");
            info.put("status", "200");
        } else {
            info.put("msg", "error");
            info.put("status", "404");
        }

        result.put("meta", info);

        return result;
    }

    @PostMapping("/cancelpaymentrequest")
    public Map<String, Object> cancelPaymentRequest(@RequestBody JSONObject json) {
        Integer id = json.getInteger("id");

        Integer resultInt = offerService.deletePaymentRequest(id);

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

    @GetMapping("/download/{filename}")
    public void downLoad(HttpServletResponse response, @PathVariable String filename, HttpServletRequest request) throws Exception {
        try {
// path是指想要下载的文件的路径
            File file = new File(filePath + filename);
// 获取文件名
            String filenameT = file.getName();
// 获取文件后缀名
            String ext = filenameT.substring(filenameT.lastIndexOf(".") + 1).toLowerCase();

// 将文件写入输入流
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStream fis = new BufferedInputStream(fileInputStream);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

// 清空response
            response.reset();
// 设置response的Header
            response.setCharacterEncoding("UTF-8");
//Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
//attachment表示以附件方式下载 inline表示在线打开 "Content-Disposition: inline; filename=文件名.mp3"
// filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filenameT, "UTF-8"));
// 告知浏览器文件的大小
            response.addHeader("Content-Length", "" + file.length());
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @GetMapping("/download/label/{id}")
    public void downLoadLabel(HttpServletResponse response, @PathVariable String id, HttpServletRequest request) throws Exception {
        OutboundStatus outboundStatus = outBoundService.queryOutboundStatus(Integer.parseInt(id));

        if(outboundStatus!=null && outboundStatus.getUploadfiles() !=null){
            String filename = outboundStatus.getUploadfiles();
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("download.zip", "UTF-8"));
            response.setContentType("application/zip;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());

            String[] filenames = filename.split(",");

            //响应头的设置
            response.reset();
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            String downloadName = "download" + ".zip";
            //返回客户端浏览器的版本号、类型
            String agent = request.getHeader("USER-AGENT");
            try {
                //针对IE或者以IE为内核的浏览器：
                if (agent.contains("MSIE") || agent.contains("Trident")) {
                    downloadName = java.net.URLEncoder.encode(downloadName, "UTF-8");
                } else {
                    //非IE浏览器的处理：
                    downloadName = new String(downloadName.getBytes("UTF-8"), "ISO-8859-1");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.setHeader("Content-Disposition", "attachment;fileName=\"" + downloadName + "\"");

            //设置压缩流：直接写入response，实现边压缩边下载
            ZipOutputStream zipos = null;
            try {
                zipos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
                zipos.setMethod(ZipOutputStream.DEFLATED); //设置压缩方法
            } catch (Exception e) {
                e.printStackTrace();
            }
            //循环将文件写入压缩流
            DataOutputStream os = null;

            for (String url : filenames) {
                try {
                    URL link = new URL("file:///" + url);

                    File newFile = new File(url);

                    //添加ZipEntry，并ZipEntry中写入文件流
                    //这里，加上i是防止要下载的文件有重名的导致下载失败
                    zipos.putNextEntry(new ZipEntry(newFile.getName()));
                    os = new DataOutputStream(zipos);
                    InputStream is = new BufferedInputStream(link.openStream());
                    byte[] b = new byte[1000];
                    int length = 0;
                    while ((length = is.read(b)) != -1) {
                        os.write(b, 0, length);
                    }
                    is.close();
                    zipos.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //关闭流
            try {
                os.flush();
                os.close();
                zipos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //单文件上传
    @PostMapping("/upload")
    public Map<String, Object> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "buyersConfirmid") String id
    ) throws Exception {

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

//        Integer intResult = offerService.addBuyersConfirm(buyersConfirm);

        if (file.isEmpty()) {
            info.put("msg", "Error");
            info.put("status", "303");
            result.put("meta", info);

            return result;
        }

        Random tmpR = new Random();

        String filenumeTmp = String.valueOf(new Date().getTime()) + (tmpR.nextInt(8999) + 1000);

        try {
            byte[] bytes = file.getBytes();

            Calendar c = Calendar.getInstance();
            String tmpDir = c.get(Calendar.YEAR) + "" + c.get(Calendar.MONTH) + "" + c.get(Calendar.DATE) + "";

            File dir = new File(filePath + tmpDir);
            if (!dir.exists()) {// 判断目录是否存在
                dir.mkdir();
            }

            Path path = Paths.get(dir + "\\" + filenumeTmp + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
            Files.write(path, bytes);
            BuyersConfirm buyersConfirm = new BuyersConfirm();
            buyersConfirm.setId(new BigInteger(id));
            buyersConfirm.setStatus(tmpDir + "\\" + filenumeTmp + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
            offerService.updateConfirm(buyersConfirm);
        } catch (IOException e) {
            // TODO Auto-generated catch block
//            e.printStackTrace();

            info.put("msg", "Error");
            info.put("status", "303");
            result.put("meta", info);

            return result;
        }


        info.put("msg", "ok");
        info.put("status", "200");

//        result.put("data", buyersConfirm.getId());
        result.put("meta", info);

        return result;
    }

    //单文件上传
    @PostMapping("/uploadexcel")
    public Map<String, Object> uploadexcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "exceltype") String exceltype,
            @RequestParam(value = "orgid") BigInteger orgid
    ) {
        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        if (file.isEmpty()) {
            info.put("msg", "Error");
            info.put("status", "303");
            result.put("meta", info);

            return result;
        }

        List<ExcelPackagesTemplate> excelPackagesTemplateList = new ArrayList<>();
        try {
            excelPackagesTemplateList = ExcelUtils.importExcel(file, ExcelPackagesTemplate.class);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            info.put("msg", "error");
            info.put("status", "303");
        }

        int errorSize = 0;
        for (ExcelPackagesTemplate tmp : excelPackagesTemplateList) {
            ExcelPackages excelPackages = new ExcelPackages();
            BeanUtils.copyProperties(tmp, excelPackages);
            excelPackages.setOrgid(orgid);

            if (excelPackages.getSite() == null || excelPackages.getSite().equals("")) {
                errorSize++;
                continue;
            }

            //checksite是否存在
            List<WareHouseSite> siteTmplist = warehouseService.queryListWarehousesiteById(null, excelPackages.getSite(), orgid, "", "");
            if (siteTmplist == null || siteTmplist.size() < 1) {
                errorSize++;
                continue;
            }

            ExcelPackages tmpMapLastOne = settingService.queryExcelpackagesLastOne(excelPackages);

            Integer resultInt = 0;
            if (tmpMapLastOne == null) {
                excelPackages.setCreatedate(new Date());
                excelPackages.setLastupdate(new Date());
                excelPackages.setUsedquantity(excelPackages.getQuantity());
                excelPackages.setRepeatquantity(0);
                excelPackages.setVersion(0);

                resultInt = settingService.addExcelpackages(excelPackages);
            } else {
                if (tmpMapLastOne.getOrgid() == null || tmpMapLastOne.getOrgid().equals("")) {
                    tmpMapLastOne.setOrgid(orgid);
                }

                tmpMapLastOne.setLastupdate(new Date());
                tmpMapLastOne.setRepeatquantity(tmpMapLastOne.getRepeatquantity() + 1);
                tmpMapLastOne.setUsedquantity(tmpMapLastOne.getUsedquantity() + excelPackages.getQuantity());
                tmpMapLastOne.setQuantity(tmpMapLastOne.getQuantity() + excelPackages.getQuantity());
                resultInt = settingService.updateExcelpackages(tmpMapLastOne);
            }
        }

        info.put("msg", "ok");
        info.put("status", "200");

        info.put("errorSize", String.valueOf(errorSize));
        info.put("successSize", String.valueOf(excelPackagesTemplateList.size() - errorSize));

//        result.put("data", buyersConfirm.getId());
        result.put("meta", info);

        return result;
    }

    /**
     * 显示单张图片
     *
     * @return
     */
    @RequestMapping("imgshow")
    public ResponseEntity showPhotos(String fileName) {

        try {
            // 由于是读取本机的文件，file是一定要加上的， path是在application配置文件中的路径
            return ResponseEntity.ok(resourceLoader.getResource("file:" + filePath + fileName));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/addAdjustbalanceConfirm")
    public Map<String, Object> addAdjustbalanceConfirm(@RequestBody JSONObject json) {
        BigInteger paymentRequestid = json.getBigInteger("id");
        BigInteger sellerid = json.getBigInteger("sellerid");
        BigInteger buyersid = json.getBigInteger("buyersid");
        BigDecimal amount = json.getBigDecimal("amount");
        String note = json.getString("note");

        BigInteger paymentrequestid = json.getBigInteger("id");
        BigInteger methodsid = json.getBigInteger("methodsid");


        String paytype = json.getString("paytype");
        Date estimatedeliverdate = json.getDate("estimatedeliverdate");

        BuyersConfirm buyersConfirm = new BuyersConfirm();

        buyersConfirm.setCreatedate(new Date());

        if (paytype == null) {
            //Adjust balance
            buyersConfirm.setStatus("adjust");
            buyersConfirm.setHistorystatus("adjust");
            buyersConfirm.setLastupdate(new Date());
        } else {
            //Payment confirm
            buyersConfirm.setStatus(paytype);
            buyersConfirm.setHistorystatus(paytype);

            buyersConfirm.setPaymentrequestid(paymentrequestid);
            buyersConfirm.setMethodsid(methodsid);

            //中转 用于Estimate deliver date
            buyersConfirm.setLastupdate(estimatedeliverdate);
        }

        buyersConfirm.setSellerid(sellerid);
        buyersConfirm.setBuyersid(buyersid);
        buyersConfirm.setPrice(amount);
        buyersConfirm.setNote(note);

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        Integer intResult = offerService.addBuyersConfirm(buyersConfirm);

        if (intResult > 0 && paymentRequestid != null) {
            //更新PaymentRequest
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setId(paymentRequestid);
            paymentRequest.setStatus("topay");
            paymentRequest.setLastupdate(new Date());

            intResult = offerService.updatePaymentRequest(paymentRequest);
            if (intResult > 0) {
                info.put("msg", "ok");
                info.put("status", "200");
            } else {
                info.put("msg", "Error");
                info.put("status", "303");
            }
        } else if (intResult > 0) {
            info.put("msg", "ok");
            info.put("status", "200");
        } else {
            info.put("msg", "Error");
            info.put("status", "303");
        }

        result.put("data", buyersConfirm.getId());
        result.put("meta", info);

        return result;
    }
}
