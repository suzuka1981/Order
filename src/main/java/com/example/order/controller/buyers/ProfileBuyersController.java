package com.example.order.controller.buyers;

import com.alibaba.fastjson.JSONObject;
import com.example.order.entity.BuyersAndSellers;
import com.example.order.entity.BuyersPaymentMethod;
import com.example.order.entity.Comments;
import com.example.order.entity.User;
import com.example.order.pojo.BuyersAndSellerPojo;
import com.example.order.pojo.ProviderOfferPojo;
import com.example.order.service.PrefileService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.*;

@RestController
@CrossOrigin //跨域
@RequestMapping("orderapi/profile")
public class ProfileBuyersController {
    @Resource
    private PrefileService prefileService;

    @GetMapping("/getbuyersandseller")
    public Map<String, Object> getBuyersAndSeller(String username) {
        BuyersAndSellerPojo basList = prefileService.getBuyersAndSeller(username, null, null);

        Map<String, String> info = new HashMap<String, String>();
        if (basList != null) {
            //数据整合
            for (int i = 0; i < basList.BuyersAndSellersList.size(); i++) {
                basList.UserList.get(i).buyersAndSellersstatus = basList.BuyersAndSellersList.get(i).status;
            }

            info.put("msg", "Login ok");
            info.put("status", "200");
        } else {
            info.put("msg", "Login error");
            info.put("status", "404");
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("data", basList);
        result.put("meta", info);

        return result;
    }

    @PostMapping("/updatajoinorganization")
    public Map<String, Object> updataJoinOrganization(@RequestBody JSONObject json) {
        String code = json.getJSONObject("data").getString("invitationcode");
        String note = json.getJSONObject("data").getString("note");
        String username = json.getString("username");

        User uTmp = new User();
        uTmp.setCode(code);
        User uResultSeller = prefileService.queryLogin(uTmp);

        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, String> info = new HashMap<String, String>();
        if (uResultSeller != null) {
            uTmp = new User();
            uTmp.setUsername(username);
            User uResultBuyers = prefileService.queryLogin(uTmp);

            BuyersAndSellers bTmp = new BuyersAndSellers();
            bTmp.setBuyersid(uResultBuyers.getId());
            bTmp.setSellerid(uResultSeller.getId());

            BuyersAndSellers bResult = prefileService.queryBuyersAndSellers(bTmp);

            if (bResult != null) {
                //update 已经存在 被卖家删除权限重新申请的买家
                if (bResult.getStatus() != null) {
                    boolean updateBl = false;
                    if (bResult.getSellerid() == uResultSeller.getId()) {
                        if (bResult.getStatus() == 1) {
                            info.put("msg", "Insert ok");
                            info.put("status", "200");
                        } else {
                            updateBl = true;
                        }
                    } else {
                        updateBl = true;
                    }

                    if (updateBl) {
                        // 通过以外任何状态
                        BuyersAndSellers buyersAndSellers = new BuyersAndSellers();
                        buyersAndSellers.setBuyersid(uResultBuyers.getId());
                        buyersAndSellers.setSellerid(uResultSeller.getId());
                        buyersAndSellers.setId(bResult.getId());
                        buyersAndSellers.setNote(note);
                        buyersAndSellers.setStatus(2);
                        buyersAndSellers.setLastupdate(new Date());

                        Integer updataResult = prefileService.updataJoinOrganization(buyersAndSellers);
                        if (updataResult > 0) {
                            info.put("msg", "Update ok");
                            info.put("status", "200");
                        } else {
                            info.put("msg", "Update error");
                            info.put("status", "404");
                        }
                    }
                } else {
                    info.put("msg", "No operation");
                    info.put("status", "200");
                }
            } else {
                // insert
                BuyersAndSellers buyersAndSellers = new BuyersAndSellers();
                buyersAndSellers.setStatus(2);
                buyersAndSellers.setBuyersid(uResultBuyers.getId());
                buyersAndSellers.setSellerid(uResultSeller.getId());
                buyersAndSellers.setNote(note);
                buyersAndSellers.setCreatedate(new Date());
                buyersAndSellers.setLastupdate(new Date());
                Integer insertResult = prefileService.insertJoinOrganization(buyersAndSellers);
                if (insertResult > 0) {
                    info.put("msg", "Insert ok");
                    info.put("status", "200");
                } else {
                    info.put("msg", "Insert error");
                    info.put("status", "404");
                }
            }
        } else {
            //查无此code 报错
            info.put("msg", "Code error");
            info.put("status", "404");
        }


//        result.put("data", basList);
        result.put("meta", info);
        return result;
    }

    @PostMapping("/renameuser")
    public Map<String, Object> renameUser(@RequestBody JSONObject json) {
        String username = json.getString("username");
        String userid = json.getString("userid");
        String name = json.getString("name");

        User renameU = new User();

        renameU.setId(new BigInteger(userid));
        renameU.setName(name);

        Integer uRenameResult = prefileService.updateUser(renameU);

        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, String> info = new HashMap<String, String>();

        if (uRenameResult > 0) {
            info.put("msg", "Rename ok");
            info.put("status", "200");
        } else {
            info.put("msg", "Rename error");
            info.put("status", "404");
        }

//        result.put("data", basList);
        result.put("meta", info);
        result.put("name", name);

        return result;
    }

    @PostMapping("/addbuyerspaymentmethod")
    public Map<String, Object> addBuyersPaymentMethod(@RequestBody BuyersPaymentMethod buyersPaymentMethod) {
        buyersPaymentMethod.setLastupdate(new Date());

        Integer addResult = 0;

        if (buyersPaymentMethod.getId() == null) {
            buyersPaymentMethod.setCreatedate(new Date());
            addResult = prefileService.insertbuyerspaymentmethod(buyersPaymentMethod);
        } else {
            addResult = prefileService.updatebuyerspaymentmethod(buyersPaymentMethod);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, String> info = new HashMap<String, String>();


        if (addResult > 0) {
            info.put("msg", "ok");
            info.put("status", "200");
        }  else {
            info.put("msg", "Insert error");
            info.put("status", "404");
        }

        result.put("meta", info);

        return result;
    }

    @ResponseBody
    @PostMapping("/querybuyerspaymentmethod")
    public Map<String, Object> queryBuyersPaymentMethod(@RequestBody JSONObject json) {
        Integer buyersid = json.getInteger("userid");
        String searchinput = json.getString("inputSearch");

        if(searchinput == null) {
            searchinput = "";
        }

        Integer pageNum = json.getInteger("current");
        Integer pageSize = json.getInteger("pageSize");


        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        PageHelper.startPage(pageNum, pageSize); //

        List<BuyersPaymentMethod> querylistBuyersPaymentMethod = prefileService.queryBuyersPaymentMethod(buyersid, pageNum, pageSize, searchinput);

        PageInfo<BuyersPaymentMethod> pageInfoBuyersPaymentMethod = new PageInfo<>(querylistBuyersPaymentMethod);

        if (querylistBuyersPaymentMethod != null && querylistBuyersPaymentMethod.size() > 0) {
            result.put("querylistBuyersPaymentMethod", querylistBuyersPaymentMethod);
        }

        result.put("info", pageInfoBuyersPaymentMethod);


        info.put("msg", "Select ok");
        info.put("status", "200");

        result.put("meta", info);

        return result;
    }

    @PostMapping("/addcomments")
    public Map<String, Object> addComments(@RequestBody Comments comments) {
        comments.setLastupdate(new Date());
        comments.setCreatedate(new Date());

        Integer retResult = null;
        if(comments.getId() == null) {
            retResult = prefileService.insertComments(comments);
        } else {
            Comments updateComments = new Comments();
            updateComments.setId(comments.getId());
            updateComments.setUnreadstatus("false");
            retResult = prefileService.updateComments(updateComments);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, String> info = new HashMap<String, String>();


        if (retResult > 0) {
            info.put("msg", "ok");
            info.put("status", "200");
        }  else {
            info.put("msg", "Insert error");
            info.put("status", "404");
        }

        result.put("meta", info);

        return result;
    }

    @ResponseBody
    @PostMapping("/querycomments")
    public Map<String, Object> queryComments(@RequestBody JSONObject json) {
        String msgtype = json.getString("msgtype");
        BigInteger msgsourceid = json.getBigInteger("msgsourceid");

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        List<Comments> commentsList = prefileService.queryComments(msgtype, msgsourceid);

        result.put("commentsList", commentsList);


        info.put("msg", "Select ok");
        info.put("status", "200");

        result.put("meta", info);

        return result;
    }
}
