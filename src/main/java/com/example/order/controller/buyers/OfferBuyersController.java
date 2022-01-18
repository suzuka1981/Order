package com.example.order.controller.buyers;


import com.alibaba.fastjson.JSONObject;
import com.example.order.entity.BuyersTask;
import com.example.order.entity.Offer;
import com.example.order.entity.User;
import com.example.order.pojo.BuyersAndSellerPojo;
import com.example.order.pojo.ProviderOfferPojo;
import com.example.order.service.OfferService;
import com.example.order.service.PrefileService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.*;

@RestController
@CrossOrigin //跨域
@RequestMapping("orderapi/buyersoffer")
public class OfferBuyersController {
    @Resource
    private OfferService offerService;
    @Resource
    private PrefileService prefileService;

    @ResponseBody
    @PostMapping("/queryBuyersListPageActive")
    public Map<String, Object> queryListPageActive(@RequestBody JSONObject json) {
        Integer pageNum = json.getInteger("current");
        Integer pageSize = json.getInteger("pageSize");
        String username = json.getString("username");
        String searchinput = json.getString("searchinput");
        String pagetype = json.getString("pagetype");
        String orguserselect = json.getString("orguserselect");

        BuyersAndSellerPojo basList = prefileService.getBuyersAndSeller(username, null, 1);

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        List<BigInteger> sellerIdList = new ArrayList<>();
        if (basList != null && basList.getUserList().size() > 0) {
            if (orguserselect == null) {
                for (User uTmp : basList.getUserList()) {
                    sellerIdList.add(uTmp.getId());
                }
            } else {
                sellerIdList.add(new BigInteger(orguserselect));
            }


            PageHelper.startPage(pageNum, pageSize); //

            List<ProviderOfferPojo> querylistOfferList = offerService.querylistOffer(sellerIdList, pageNum, pageSize, searchinput, pagetype);

            PageInfo<ProviderOfferPojo> pageInfoquerylistOfferList = new PageInfo<>(querylistOfferList);

            if (querylistOfferList != null && querylistOfferList.size() > 0) {
                result.put("querylistOfferList", querylistOfferList);
            } else {
                result.put("info", pageInfoquerylistOfferList);
            }

            result.put("selleruserlist", basList.getUserList());

            result.put("info", pageInfoquerylistOfferList);
        }

        info.put("msg", "Select ok");
        info.put("status", "200");

        result.put("meta", info);

        return result;
    }

    //Task Offer
    @PostMapping("/taskorconfirmoffer")
    public Map<String, Object> taskOrConfirmoffer(@RequestBody BuyersTask buyersTask) {
        buyersTask.setLastupdate(new Date());

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        Integer intResult = 0;
        if (buyersTask.getTmpstatus() == null && buyersTask.status.equals("task")) {
            //先更新offer 已经使用
            Offer tmpOffer = new Offer();
            BeanUtils.copyProperties(buyersTask, tmpOffer);

            //insert 之前还缺 querey 当前实际剩余数值 并修改verson 重试没写

            try {
                intResult = offerService.updateOfferLock(tmpOffer, buyersTask.getSubquantity());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            //添加
            if (intResult > 0) {
                buyersTask.setOfferid(buyersTask.getId());
                buyersTask.setId(null);
                buyersTask.setUsedquantity(buyersTask.getSubquantity());
                buyersTask.setCreatedate(new Date());

                intResult = offerService.addTaskOffer(buyersTask);
            }
        } else if (buyersTask.getTmpstatus() == null && buyersTask.status.equals("propose")) {
            //添加propose 与task结构一样 所以通用 BuyersTask

            buyersTask.setOfferid(buyersTask.getId());
            buyersTask.setId(null);
            buyersTask.setUsedquantity(buyersTask.getSubquantity());
            buyersTask.setCreatedate(new Date());
            buyersTask.setLastupdate(new Date());

            intResult = offerService.addProposeOffer(buyersTask);
        } else if (buyersTask.getTmpstatus() != null && buyersTask.getTmpstatus().equals("editpropose")) {
            if (buyersTask.getStatus().equals("propose")) {
                if (buyersTask.getUsedquantity() >= buyersTask.getSubquantity()) {
                    buyersTask.setUsedquantity(buyersTask.getSubquantity());
                    buyersTask.setQuantity(buyersTask.getSubquantity());

                    intResult = offerService.updateProposeOffer(buyersTask);
                }
            }
        } else if (buyersTask.getTmpstatus() != null && buyersTask.getTmpstatus().equals("cancelpropose")) {
            intResult = offerService.deleteProposeOffer(Integer.parseInt(buyersTask.getId().toString()));
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
    @PostMapping("/queryBuyersListPageProposed")
    public Map<String, Object> queryListPageProposed(@RequestBody JSONObject json) {
        Integer pageNum = json.getInteger("current");
        Integer pageSize = json.getInteger("pageSize");
        String username = json.getString("username");
        Integer type = json.getInteger("type");
        BigInteger id = new BigInteger(json.getString("id"));
        String searchinput = json.getString("searchinput");
        String orguserselect = json.getString("orguserselect");

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        List<BigInteger> sellerIdList = new ArrayList<>();
        BigInteger selfid = null;
        PageHelper.startPage(pageNum, pageSize); //


        if (type == 0) {
            //买手
            //被删除的买手提交的数据也存在
            BuyersAndSellerPojo basList = prefileService.getBuyersAndSeller(username, null, 1);

            if (basList != null && (basList.getUserList().size() > 0 || basList.type == 1)) {
                if (basList.type == 0) {
                    if (orguserselect == null) {
                        for (User uTmp : basList.getUserList()) {
                            sellerIdList.add(uTmp.getId());
                        }
                    } else {
                        sellerIdList.add(new BigInteger(orguserselect));
                    }
                }

                result.put("selleruserlist", basList.getUserList());

                selfid = basList.getId();
            }
        } else {
            //卖家
            sellerIdList.add(id);
        }

        try {
            List<ProviderOfferPojo> querylistOfferList = offerService.querylistOfferProposed(sellerIdList, selfid, pageNum, pageSize, searchinput);

            PageInfo<ProviderOfferPojo> pageInfoquerylistOfferList = new PageInfo<>(querylistOfferList);

            if (querylistOfferList != null && querylistOfferList.size() > 0) {
                result.put("querylistOfferList", querylistOfferList);
            } else {
                result.put("info", pageInfoquerylistOfferList);
            }

            result.put("info", pageInfoquerylistOfferList);
        } catch (Exception e) {

        }


        info.put("msg", "Select ok");
        info.put("status", "200");

        result.put("meta", info);

        return result;
    }
}
