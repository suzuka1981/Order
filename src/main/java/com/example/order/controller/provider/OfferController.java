package com.example.order.controller.provider;

import com.alibaba.fastjson.JSONObject;
import com.example.order.entity.*;
import com.example.order.pojo.ProviderOfferPojo;
import com.example.order.service.OfferService;
import com.example.order.service.PrefileService;
import com.example.order.service.SettingService;
import com.example.order.service.WarehouseService;
import com.example.order.util.SendMessageUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@RestController
@CrossOrigin //跨域
@RequestMapping("orderapi/offer")
public class OfferController {
    @Resource
    private WarehouseService warehouseService;
    @Resource
    private SettingService settingService;
    @Resource
    private OfferService offerService;
    @Resource
    private PrefileService prefileService;


    // offer Active 页面数据
    @ResponseBody
    @PostMapping("/queryListPageActive")
    public Map<String, Object> queryListPageActive(@RequestBody JSONObject json) {
        Integer pageNum = json.getInteger("current");
        Integer pageSize = json.getInteger("pageSize");
        Integer orgid = json.getInteger("orgid");
        String searchinput = json.getString("searchinput");
        String searchoffer = json.getString("searchoffer");
        String pagetype = json.getString("pagetype");

        List<BigInteger> orgidList = new ArrayList<>();
        orgidList.add(new BigInteger(orgid.toString()));

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        List<WareHouseSite> wareHouseSite = warehouseService.queryListPageWarehousesite(orgid, null, 1, 5);

        //获取配置
        List<SettingGeneral> settingGeneral = settingService.queryGeneral(orgid);
        //补充配置
        if (settingGeneral.size() == 0) {
            SettingGeneral settingGeneralInsert = new SettingGeneral();
            settingGeneralInsert.setLastupdate(new Date());
            settingGeneralInsert.setCreatedate(new Date());
            settingGeneralInsert.setPendingperioddays("7");
            settingGeneralInsert.setCheckedVisibletoallmembers("false");
            settingGeneralInsert.setDefaultusers("");
            settingGeneralInsert.setMinimumpaymentamount(new BigDecimal(1000));
            settingGeneralInsert.setCheckedComments("false");
            settingGeneralInsert.setValueOfSwitch("false");
            settingGeneralInsert.setCheckedEnableAmazonSites("false");
            settingGeneralInsert.setOrgid(new BigInteger(orgid.toString()));

            Integer resultInt = settingService.addGeneral(settingGeneralInsert);
            settingGeneralInsert.setId(new BigInteger(resultInt.toString()));
            settingGeneral.add(settingGeneralInsert);
        }


        //获取product 全部
        List<WarehouseProduct> warehouseProduct = warehouseService.queryListPageProduct(orgid, 1, 10, searchoffer, "", "");

        PageHelper.startPage(pageNum, pageSize); //

        List<ProviderOfferPojo> querylistOfferList = offerService.querylistOffer(orgidList, pageNum, pageSize, searchinput, pagetype);

        PageInfo<ProviderOfferPojo> pageInfoquerylistOfferList = new PageInfo<>(querylistOfferList);


        info.put("msg", "Select ok");
        info.put("status", "200");

        if (wareHouseSite != null && wareHouseSite.size() > 0) {
            result.put("sitedata", wareHouseSite);
        }

        if (warehouseProduct != null && warehouseProduct.size() > 0) {
            result.put("productdata", warehouseProduct);
        }

        if (settingGeneral != null && settingGeneral.size() > 0) {
            result.put("sittingdata", settingGeneral.get(0));
        }

        if (querylistOfferList != null && querylistOfferList.size() > 0) {
            result.put("querylistOfferList", querylistOfferList);
        } else {
            result.put("info", pageInfoquerylistOfferList);
        }

        result.put("info", pageInfoquerylistOfferList);

        result.put("meta", info);

        return result;
    }

    //添加与编辑Offer
    @PostMapping("/addandeditoffer")
    public Map<String, Object> addAndEditOffer(@RequestBody Offer offer, HttpServletRequest request) {
        offer.setLastupdate(new Date());
        offer.setCreatedate(new Date());

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        Integer intResult = 0;
        String sendType = null;
        if (offer.id == null) {
            //添加
            offer.setVersion(0);
            offer.setUsedquantity(offer.getQuantity());
            intResult = offerService.addOffer(offer);
            sendType = "add";
        } else {
            //更新
            intResult = offerService.updateOffer(offer);
            sendType = "update";
        }

        //Email
        User uTmp = new User();
        uTmp.setId(offer.getOrgid());
        User u = prefileService.queryLogin(uTmp);

        List<String> whsIdList = Arrays.asList(offer.getWarehousesitesvalue().split(","));
        List<WareHouseSite> tmpWareHouseSiteList = warehouseService.queryListWarehousesiteById(whsIdList, "", offer.getOrgid(), "", "");

        List<WareHouseSite> wareHouseSiteList = new ArrayList<>();

        for(WareHouseSite itemTmp: tmpWareHouseSiteList){
            WareHouseSite objectTmp = new WareHouseSite();
            BeanUtils.copyProperties(itemTmp,objectTmp);

            wareHouseSiteList.add(objectTmp);
        }

        for(WareHouseSite item:wareHouseSiteList){
            item.setContact("UNIT " + item.getContact());
        }

        WarehouseProduct whp = warehouseService.queryWarehouseProductByID(offer.getProductid());

        List<User> basList = prefileService.getSellerList(u.getUsername(), null, null, null, null, Integer.parseInt(u.getId().toString()), null);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setUser(u);
        sendMessage.setWareHouseSiteList(wareHouseSiteList);
        sendMessage.setWhp(whp);
        sendMessage.setSendToUserList(basList);
        sendMessage.setOffer(offer);

//        String url = "http://" + request.getServerName() + ":" +request.getServerPort();
//        sendMessage.setUrl(url);

        String orgName = null;
        if (u.getName() != null && !u.getName().equals("")) {
            orgName = u.getName();
        } else {
            orgName = u.getUsername().split("@")[0];
        }

        if (sendType.equals("add")) {
            sendMessage.setSubject(orgName + " | " + "New Offer (#" + offer.getId() + ")" + " - " + whp.getName());
        } else if (sendType.equals("update")) {
            sendMessage.setSubject(orgName + " | " + "Updated Offer (#" + offer.getId() + ")" + " - " + whp.getName());
        }

        sendMessage.setSendtype("addoreditoffer");

        SendMessageUtil.setSendMessageQueue(sendMessage);

        if (intResult >= 0) {
            info.put("msg", "Insert ok");
            info.put("status", "200");
        }

        result.put("data", intResult);
        result.put("meta", info);

        return result;
    }
}
