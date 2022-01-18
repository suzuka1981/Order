package com.example.order.controller.provider;

import com.alibaba.fastjson.JSONObject;
import com.example.order.entity.BuyersAndSellers;
import com.example.order.entity.SettingGeneral;
import com.example.order.entity.User;
import com.example.order.service.PrefileService;
import com.example.order.service.SettingService;
import com.example.order.util.RanStringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@RestController
@CrossOrigin //跨域
@RequestMapping("orderapi/setting")
public class SettingController {
    @Resource
    private SettingService settingService;
    @Resource
    private PrefileService prefileService;

    //    添加Setting
    @PostMapping("/savegeneral")
    public Map<String, Object> addSite(@RequestBody SettingGeneral settingGeneral) {
        settingGeneral.setLastupdate(new Date());
        settingGeneral.setCreatedate(new Date());

        Integer resultInt = 0;

        if (settingGeneral.id == null) {
            resultInt = settingService.addGeneral(settingGeneral);
        } else {
            resultInt = settingService.updateGeneral(settingGeneral);
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
    @PostMapping("/querygeneral")
    public Map<String, Object> queryGeneral(@RequestBody JSONObject json) {
        Integer orgid = json.getInteger("orgid");

        List<SettingGeneral> settingGeneral = settingService.queryGeneral(orgid);

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

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();
        if (settingGeneral == null) {
            info.put("msg", "Select error");
            info.put("status", "404");
        } else {
            info.put("msg", "Select ok");
            info.put("status", "200");
        }

        if (settingGeneral != null && settingGeneral.size() > 0) {
            result.put("data", settingGeneral.get(0));
        }

        result.put("meta", info);

        return result;
    }

    @ResponseBody
    @PostMapping("/queryuserlist")
    public Map<String, Object> queryUserList(@RequestBody JSONObject json) {
        String username = json.getString("username");
        Integer pageNum = json.getInteger("current");
        Integer pageSize = json.getInteger("pageSize");
        String searchinput = json.getString("searchinput");
        Integer sellerid = json.getInteger("sellerid");

        PageHelper.startPage(pageNum, pageSize);

        List<User> basList = prefileService.getSellerList(username, null, null, pageNum, pageSize, sellerid, searchinput);

        PageInfo<User> pagebasList = new PageInfo<>(basList);

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        List<BigInteger> sellerIdList = new ArrayList<>();

        if (basList != null && basList.size() > 0) {
            for (User uTmp : basList) {
                sellerIdList.add(uTmp.getId());
            }

            result.put("selleruserlist", basList);
            result.put("info", pagebasList);
        }

        info.put("msg", "Select ok");
        info.put("status", "200");

        result.put("meta", info);

        return result;
    }

    //    添加Setting
    @PostMapping("/updatebuyerstatus")
    public Map<String, Object> updateBuyerStatus(@RequestBody JSONObject json) {
        Integer buyersid = json.getInteger("buyersid");
        Integer sellerid = json.getInteger("sellerid");
        Integer status = json.getInteger("status");

        BuyersAndSellers bsUpdateTmp = new BuyersAndSellers();

        bsUpdateTmp.setBuyersid(new BigInteger(buyersid.toString()));
        bsUpdateTmp.setSellerid(new BigInteger(sellerid.toString()));
        bsUpdateTmp.setStatus(status);

        Integer resultInt = prefileService.updataJoinOrganization(bsUpdateTmp);

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
    @PostMapping("/createcode")
    public Map<String, Object> createCode(@RequestBody JSONObject json) {
        String username = json.getString("username");

        String code = RanStringUtil.generateByRandom(15);

        User u = null;
        User utmp = new User();
        utmp.setCode(code);

        u = prefileService.queryLogin(utmp);

        while (u != null) {
            code = RanStringUtil.generateByRandom(15);
            utmp.setCode(code);
            u = prefileService.queryLogin(utmp);
        }

        utmp.setUsername(username);
        utmp.setLastupdate(new Date());
        prefileService.updateUser(utmp);

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();
        if (code == null) {
            info.put("msg", "Select error");
            info.put("status", "404");
        } else {
            result.put("code", code);
            info.put("msg", "Select ok");
            info.put("status", "200");
        }

        result.put("meta", info);

        return result;
    }

    @PostMapping("/deletebuyerstatus")
    public Map<String, Object> deleteBuyerStatus(@RequestBody JSONObject json) {
        Integer id = json.getInteger("id");

        Integer resultInt = prefileService.deleteJoinOrganization(id);

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
}
