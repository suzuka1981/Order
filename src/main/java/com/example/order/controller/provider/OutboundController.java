package com.example.order.controller.provider;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.body.MultipartBody;
import cn.hutool.http.server.HttpServerRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.order.entity.*;
import com.example.order.entity.LET.*;
import com.example.order.pojo.*;
import com.example.order.service.OutBoundService;
import com.example.order.service.PrefileService;
import com.example.order.service.WarehouseService;
import com.example.order.util.RanStringUtil;
import com.example.order.util.SendMessageUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.MultiValuedMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@CrossOrigin //跨域
@RequestMapping("orderapi/outbound")
public class OutboundController {
    @Resource
    private WarehouseService warehouseService;
    @Resource
    private PrefileService prefileService;
    @Value("${filepath}")
    public String filePath;
    @Resource
    private OutBoundService outBoundService;
    @Resource
    RestTemplate restTemplate;
    @Value("${letopenurl}")
    String letopenurl;
    @Value("${letapikey}")
    String letapikey;
    @Value("${letapi_token}")
    String api_token;
    @Value("${openurl}")
    public String openurl;

    @ResponseBody
    @PostMapping("/label/getRate")
    public Map<String, Object> queryGeneral(@RequestBody JSONObject json) {
        JSONObject labelDetailsForm = json.getJSONObject("labelDetailsForm");
        JSONObject senderForm = json.getJSONObject("senderForm");
        JSONObject recipientForm = json.getJSONObject("recipientForm");
        JSONArray providerCreateInputTableData = json.getJSONArray("providerCreateInputTableData");
        String inputNote = json.getString("inputNote");
        String inputMemo = json.getString("inputMemo");

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        String url = letopenurl + "shipments/quote.json";
        SbuQuote sbuQuote = new SbuQuote();
        sbuQuote.setApi_key(letapikey);

        Content content = new Content();
        content.setCustomer_reference("beizhu");
        Address shipper = outBoundService.getAddress(senderForm);
        content.setShipper(shipper);
        Address recipient = outBoundService.getAddress(recipientForm);
        content.setRecipient(recipient);
        List<Packages> packages = outBoundService.getPackages(providerCreateInputTableData);
        for (Packages item : packages) {
            item.setSignature_option(labelDetailsForm.getString("valueOfSignature"));
        }
        content.setPackages(packages);

        CustomsItems customsItems = new CustomsItems();
        customsItems.setDescription(labelDetailsForm.getString("valueOfTemplate"));
        customsItems.setBrand("brand");
        customsItems.setQuantity("1");
        customsItems.setWeight("1.11");
        customsItems.setPrice("1");
        customsItems.setOrigin_country("US");
        List<CustomsItems> customsItemsList = new ArrayList<>();
        customsItemsList.add(customsItems);
        content.setCustoms_items(customsItemsList);

        content.setCarrier_insurance("");
        content.setCarrier("");
        content.setServicelevel_token("");
        content.setService_name("");
        sbuQuote.setContent(content);

//        SbuContent sbuContent = new SbuContent();
//        sbuContent.setContent(content);
//        String sbuContentjsonData = JSON.toJSONString(sbuContent);

        String contentjsonData = JSON.toJSONString(content);

        sbuQuote.setSign(MD5.create().digestHex(contentjsonData + api_token));


        // form-data 仿照 body 发送
        /*
        JSONObject objectSub = new JSONObject();
        objectSub.put("content",content);
        objectSub.put("api_key", sbuQuote.getApi_key());
        objectSub.put("sign", sbuQuote.getSign());


        String jsonData = JSON.toJSONString(sbuQuote);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("api_key", sbuQuote.getApi_key());
        map.add("sign", sbuQuote.getSign());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(sbuContentjsonData, headers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParams(map);
        try {
            ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(builder.toUriString(), request, JSONObject.class);
            System.out.println(responseEntity.getBody());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        */

        //原生form-data
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("api_key", sbuQuote.getApi_key(), MediaType.TEXT_PLAIN);
        multipartBodyBuilder.part("sign", sbuQuote.getSign(), MediaType.TEXT_PLAIN);
        multipartBodyBuilder.part("content", contentjsonData, MediaType.APPLICATION_JSON);

        MultiValueMap<String, HttpEntity<?>> multipartBody = multipartBodyBuilder.build();


        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, multipartBody, String.class);
            if (responseEntity.getBody() == null) {
                info.put("msg", "Select error");
                info.put("status", "404");
            } else {
                info.put("msg", "Select ok");
                info.put("status", "200");

                result.put("data", JSON.parse(responseEntity.getBody()));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());

            info.put("msg", "Select error");
            info.put("status", "404");
        }

        result.put("meta", info);

        return result;
    }


    @ResponseBody
    @PostMapping("/label/createlabelsave")
    public Map<String, Object> createLabelSave(@RequestBody JSONObject json) {
        //let
        JSONObject senderForm = json.getJSONObject("senderForm");
        JSONObject recipientForm = json.getJSONObject("recipientForm");
        JSONArray providerCreateInputTableData = json.getJSONArray("providerCreateInputTableData");
        JSONObject labelDetailsForm = json.getJSONObject("labelDetailsForm");

        //本地保存
        OutboundAddress outsenderForm = json.getObject("senderForm", OutboundAddress.class);
        OutboundAddress outrecipientForm = json.getObject("recipientForm", OutboundAddress.class);
        OutboundRate rateSub = json.getObject("rateSub", OutboundRate.class);
        List<OutboundPackageSize> outboundPackageSizeList = JSONArray.parseArray(json.getJSONArray("providerCreateInputTableData").toString(), OutboundPackageSize.class);

        OutboundLabel outboundLabel = json.getObject("labelDetailsForm", OutboundLabel.class);
        BigInteger orgid = json.getBigInteger("orgid");

        String url = letopenurl + "shipments/label.json";
        SbuQuote sbuQuote = new SbuQuote();
        sbuQuote.setApi_key(letapikey);

        Content content = new Content();
        content.setCustomer_reference("beizhu");
        Address shipper = outBoundService.getAddress(senderForm);
        content.setShipper(shipper);
        Address recipient = outBoundService.getAddress(recipientForm);
        content.setRecipient(recipient);
        List<Packages> packages = outBoundService.getPackages(providerCreateInputTableData);
        for (Packages item : packages) {
            item.setSignature_option(labelDetailsForm.getString("valueOfSignature"));
        }
        content.setPackages(packages);

        CustomsItems customsItems = new CustomsItems();
        customsItems.setDescription(labelDetailsForm.getString("valueOfTemplate"));
        customsItems.setBrand("brand");
        customsItems.setQuantity("1");
        customsItems.setWeight("1.11");
        customsItems.setPrice("1");
        customsItems.setOrigin_country("US");
        List<CustomsItems> customsItemsList = new ArrayList<>();
        customsItemsList.add(customsItems);
        content.setCustoms_items(customsItemsList);

        content.setCarrier_insurance("");
        content.setCarrier(rateSub.getCarrier());
        content.setServicelevel_token(rateSub.getServicelevel_token());
        content.setService_name("");
        sbuQuote.setContent(content);

//        SbuContent sbuContent = new SbuContent();
//        sbuContent.setContent(content);
//        String sbuContentjsonData = JSON.toJSONString(sbuContent);

        String contentjsonData = JSON.toJSONString(content);

        sbuQuote.setSign(MD5.create().digestHex(contentjsonData + api_token));

//原生form-data
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("api_key", sbuQuote.getApi_key(), MediaType.TEXT_PLAIN);
        multipartBodyBuilder.part("sign", sbuQuote.getSign(), MediaType.TEXT_PLAIN);
        multipartBodyBuilder.part("content", contentjsonData, MediaType.APPLICATION_JSON);

        MultiValueMap<String, HttpEntity<?>> multipartBody = multipartBodyBuilder.build();

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, multipartBody, String.class);
            if (responseEntity.getBody().indexOf("error") != -1) {
                info.put("msg", responseEntity.getBody());
                info.put("status", "404");

                result.put("meta", info);

                return result;
            }

            OutboundShipment outboundShipment = JSONObject.parseObject(responseEntity.getBody().toString(), OutboundShipment.class);

            //插入主表
            outboundLabel.setOrgid(orgid);
            outboundLabel.setCreatedate(new Date());
            outboundLabel.setLastupdate(new Date());
            if (outboundLabel.getShipdate() != null) {
                outboundLabel.setShipdate(new Date(outboundLabel.getShipdate().toString()));
            }

            outBoundService.addOutboundLabel(outboundLabel);

            //插入发送与接受地址表 OutboundAddress
            outsenderForm.setType("sender");
            outsenderForm.setOrgid(orgid);
            outsenderForm.setCreatedate(new Date());
            outsenderForm.setLastupdate(new Date());
            outsenderForm.setOutboundlabelid(outboundLabel.getId());
            outBoundService.addOutboundAddress(outsenderForm);

            outrecipientForm.setType("recipient");
            outrecipientForm.setOrgid(orgid);
            outrecipientForm.setCreatedate(new Date());
            outrecipientForm.setLastupdate(new Date());
            outrecipientForm.setOutboundlabelid(outboundLabel.getId());
            outBoundService.addOutboundAddress(outrecipientForm);

            //保存价格 OutboundRate rateSub
            rateSub.setOrgid(orgid);
            rateSub.setOutboundlabelid(outboundLabel.getId());
            rateSub.setCreatedate(new Date());
            rateSub.setLastupdate(new Date());
            outBoundService.addOutboundRate(rateSub);

            //保存package List<OutboundPackageSize> outboundPackageSizeList
            for (OutboundPackageSize item : outboundPackageSizeList) {
                item.setCreatedate(new Date());
                item.setLastupdate(new Date());
                item.setOutboundlabelid(outboundLabel.getId());
                item.setOrgid(orgid);
            }
            outBoundService.addOutboundPackageSize(outboundPackageSizeList);

            //label结果 OutboundShipment outboundShipment
            outboundShipment.setOrgid(orgid);
            outboundShipment.setOutboundlabelid(outboundLabel.getId());
            outboundShipment.setCreatedate(new Date());
            outboundShipment.setLastupdate(new Date());
            outBoundService.addOutboundShipment(outboundShipment);

            if (responseEntity.getBody() == null) {
                info.put("msg", "Create Label error");
                info.put("status", "404");
            } else {
                info.put("msg", "Create Label ok");
                info.put("status", "200");

                result.put("data", JSON.parse(responseEntity.getBody()));
                result.put("outboundLabel", outboundLabel);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());

            info.put("msg", "Create Label error");
            info.put("status", "404");
        }

        result.put("meta", info);

        return result;
    }

    @ResponseBody
    @PostMapping("/label/queryoutboundlabellist")
    public Map<String, Object> queryOutboundLabelList(@RequestBody JSONObject json) {
        Integer pageNum = json.getInteger("current");
        Integer pageSize = json.getInteger("pageSize");
        String username = json.getString("username");
        BigInteger userid = json.getBigInteger("userid");

        String labelstatus = json.getString("labelstatus");
        String searchinput = json.getString("searchinput");

        String startDate = json.getString("startDate");
        String endDate = json.getString("endDate");

        String labelid = json.getString("labelid");

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        PageHelper.startPage(pageNum, pageSize);

        if (labelid == null) {
            labelid = "";
        }

        List<OutboundLabelPojo> labelList = outBoundService.queryLabelListOfMap(pageNum, pageSize, userid, labelstatus, searchinput, startDate, endDate, labelid);

        PageInfo<OutboundLabelPojo> pageInfolabelList = new PageInfo<>(labelList);

        result.put("pageInfolabelList", pageInfolabelList);

        //获取address template
        List<Map<String, String>> OutboundAddressList = outBoundService.queryOutboundAddresstmplate(userid);
        result.put("addresstmplatelist", OutboundAddressList);

        //packagesize template
        List<Map<String, String>> packagesizetemplateList = outBoundService.queryPackageSizeTemplate(userid, 1, 1000000, "");
        result.put("packagesizetemplatelist", packagesizetemplateList);

        info.put("msg", "Select ok");
        info.put("status", "200");

//        if (pageInfolabelList.getList() != null && pageInfolabelList.getList().size() > 0) {
//
//        } else {
//            info.put("msg", "Select error");
//            info.put("status", "303");
//        }

        result.put("meta", info);

        return result;
    }

    @ResponseBody
    @PostMapping("/label/cancellabel")
    public Map<String, Object> cancelLabel(@RequestBody JSONObject json) {
        BigInteger labelid = json.getBigInteger("labelid");
        String rate_no = json.getString("rate_no");

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        //cancellabale
        ContentCancel contentCancel = new ContentCancel();
        contentCancel.setRate_no(rate_no);
        String contentjsonData = JSON.toJSONString(contentCancel);

        String url = letopenurl + "shipments/cancel.json";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();

        String letcancelsign = MD5.create().digestHex(contentjsonData + api_token);


        multipartBodyBuilder.part("api_key", letapikey, MediaType.TEXT_PLAIN);
        multipartBodyBuilder.part("sign", letcancelsign, MediaType.TEXT_PLAIN);
        multipartBodyBuilder.part("content", contentjsonData, MediaType.APPLICATION_JSON);

        MultiValueMap<String, HttpEntity<?>> multipartBody = multipartBodyBuilder.build();

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, multipartBody, String.class);

            Integer resultInteger = outBoundService.deleteLabel(labelid);

            if (responseEntity.getBody().indexOf("error") != -1) {
                info.put("msg", responseEntity.getBody());
                info.put("status", "404");

                result.put("meta", info);

                return result;
            }

            if (resultInteger > 0) {
                info.put("msg", "Delete ok");
                info.put("status", "200");
            } else {
                info.put("msg", "Delete 0");
                info.put("status", "200");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        result.put("meta", info);

        return result;
    }

    @ResponseBody
    @PostMapping("/label/labelnoteupdate")
    public Map<String, Object> labelNoteUpdate(@RequestBody JSONObject json) {
        BigInteger labelid = json.getBigInteger("labelid");
        String inputNote = json.getString("inputNote");

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        Integer resultInteger = outBoundService.updateLabel(labelid, inputNote);

        if (resultInteger > 0) {
            info.put("msg", "Delete ok");
            info.put("status", "200");
        } else {
            info.put("msg", "Delete error");
            info.put("status", "303");
        }


        result.put("meta", info);

        return result;
    }

    @PostMapping("/label/addaddress")
    public Map<String, Object> addAddress(@RequestBody OutboundAddress address) {
        address.setLastupdate(new Date());


        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        Integer intResult = 0;
        if (address.getId() == null) {
            address.setCreatedate(new Date());
            //添加
            intResult = outBoundService.addOutboundAddresstmplate(address);
        } else {
            //更新
            intResult = outBoundService.updateOutboundaddresstmplate(address);
        }

        if (intResult >= 0) {
            info.put("msg", "Insert ok");
            info.put("status", "200");
        } else {
            info.put("msg", "Insert error");
            info.put("status", "303");
        }

        result.put("data", intResult);
        result.put("meta", info);

        return result;
    }

    @ResponseBody
    @PostMapping("/label/queryoutboundaddress")
    public Map<String, Object> queryOutboundAddress(@RequestBody JSONObject json) {
        BigInteger orgid = json.getBigInteger("orgid");

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        List<Map<String, String>> OutboundAddressList = outBoundService.queryOutboundAddresstmplate(orgid);

        result.put("data", OutboundAddressList);

        info.put("msg", "Select ok");
        info.put("status", "200");

        result.put("meta", info);

        return result;
    }

    @PostMapping("/label/deleteaddress")
    public Map<String, Object> deleteAddress(@RequestBody JSONObject json) {
        BigInteger id = json.getBigInteger("id");

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        Integer intResult = outBoundService.deleteAddresstmplate(id);

        if (intResult >= 0) {
            info.put("msg", "Insert ok");
            info.put("status", "200");
        } else {
            info.put("msg", "Insert error");
            info.put("status", "303");
        }

        result.put("data", intResult);
        result.put("meta", info);

        return result;
    }

    @PostMapping("/label/addpackagesizetemplate")
    public Map<String, Object> addPackageSizeTemplate(@RequestBody OutboundPackageSize outboundPackageSize) {
        outboundPackageSize.setLastupdate(new Date());


        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        Integer intResult = 0;
        if (outboundPackageSize.getId() == null) {
            outboundPackageSize.setCreatedate(new Date());
            List<OutboundPackageSize> outboundPackageSizeList = new ArrayList<>();
            outboundPackageSizeList.add(outboundPackageSize);

            //添加
            outBoundService.addOutboundPackageSizetemplate(outboundPackageSizeList);
        } else {
            //更新
            intResult = outBoundService.updateOutboundPackageSizetemplate(outboundPackageSize);
        }

        if (intResult >= 0) {
            info.put("msg", "Insert ok");
            info.put("status", "200");
        } else {
            info.put("msg", "Insert error");
            info.put("status", "303");
        }

        result.put("data", intResult);
        result.put("meta", info);

        return result;
    }

    @ResponseBody
    @PostMapping("/label/querypackagesizetemplate")
    public Map<String, Object> queryPackageSizeTemplate(@RequestBody JSONObject json) {
        BigInteger orgid = json.getBigInteger("orgid");

        Integer pageNum = json.getInteger("current");
        Integer pageSize = json.getInteger("pageSize");
        String searchinput = json.getString("searchinput");


        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        PageHelper.startPage(pageNum, pageSize); //
        List<Map<String, String>> packagesizetemplateList = outBoundService.queryPackageSizeTemplate(orgid, pageNum, pageSize, searchinput);
        PageInfo<Map<String, String>> pagepackagesizetemplateList = new PageInfo<>(packagesizetemplateList);

        result.put("info", pagepackagesizetemplateList);

        info.put("msg", "Select ok");
        info.put("status", "200");

        result.put("meta", info);

        return result;
    }

    @PostMapping("/label/deletepackagesizetemplate")
    public Map<String, Object> deletePackageSizeTemplate(@RequestBody JSONObject json) {
        BigInteger id = json.getBigInteger("id");

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        Integer intResult = outBoundService.deletePackageSizeTemplate(id);

        if (intResult >= 0) {
            info.put("msg", "Insert ok");
            info.put("status", "200");
        } else {
            info.put("msg", "Insert error");
            info.put("status", "303");
        }

        result.put("data", intResult);
        result.put("meta", info);

        return result;
    }

    @ResponseBody
    @PostMapping("/label/createlabelanduploadsave")
    public Map<String, Object> createlabelanduploadsave(
            @RequestParam(value = "file", required = false) MultipartFile[] fileList,
            @RequestParam("uploadData") String json,
            HttpServerRequest request
    ) {
        //上传
        String uploadfiles = "";
        if (fileList != null && fileList.length > 0) {
            for (MultipartFile filepart : fileList) {
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd");
                String date = sDateFormat.format(new Date());
                String fileD = RanStringUtil.generateByRandom(3);

                try {
                    File fileDirPath = new File(filePath + date);
                    if (!fileDirPath.exists()) {
                        fileDirPath.mkdirs();
                    }

                    String suffix = filepart.getOriginalFilename().substring(filepart.getOriginalFilename().lastIndexOf(".") + 1);

                    File file = File.createTempFile(UUID.randomUUID().toString() + fileD, "." + suffix, fileDirPath);

                    filepart.transferTo(file);

                    if (uploadfiles.equals("")) {
                        uploadfiles = filePath + date + "/" + file.getName();
                    } else {
                        uploadfiles = uploadfiles + "," + filePath + date + "/" + file.getName();
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONArray tableData = jsonObject.getJSONArray("tableData");

        BigInteger orgid = jsonObject.getBigInteger("orgid");

        OutboundStatus outboundStatus = new OutboundStatus();
        outboundStatus.setUploadfiles(uploadfiles);
        outboundStatus.setLabelid(jsonObject.getBigInteger("labelid"));
        outboundStatus.setLabel_url(jsonObject.get("label_url") + "");
        outboundStatus.setTracking_number(jsonObject.get("tracking_number") + "");

        if (jsonObject.getString("packagequantity") != null) {
            outboundStatus.setPackagequantity(Integer.parseInt(jsonObject.getString("packagequantity")));
        }

//        JSONArray destination = jsonObject.getJSONArray("destination");
//        outboundStatus.setDestination(JSONArray.toJSONString(destination));

        outboundStatus.setDestination(jsonObject.getString("destination"));
        outboundStatus.setDestinationcontent(jsonObject.getString("destinationcontent"));
        JSONArray otherservices = jsonObject.getJSONArray("otherservices");
        outboundStatus.setOtherservices(JSONArray.toJSONString(otherservices));
        outboundStatus.setCarrier(jsonObject.get("carrier") + "");
        outboundStatus.setNotetoyourself(jsonObject.getString("notetoyourself"));
        outboundStatus.setInstruction(jsonObject.get("instruction") + "");
        outboundStatus.setServicetype(jsonObject.getString("servicetype"));
        outboundStatus.setOrgid(orgid);
        outboundStatus.setCreatedate(new Date());
        outboundStatus.setLastupdate(new Date());

        Integer intResult = 0;

        intResult = outBoundService.addOutboundStatus(outboundStatus);

        List<OutboundStatusProductSize> outboundStatusProductSizeList = new ArrayList<>();
        for (int i = 0; i < tableData.size(); i++) {
            JSONObject objitem = tableData.getJSONObject(i);
            OutboundStatusProductSize outboundStatusProductSize = new OutboundStatusProductSize();
            JSONObject instockdata = objitem.getJSONObject("instockdata");
            JSONObject productdata = objitem.getJSONObject("productdata");
            outboundStatusProductSize.setSiteid(instockdata.getBigInteger("siteid"));
            outboundStatusProductSize.setProductid(productdata.getBigInteger("productid"));
            outboundStatusProductSize.setUserid(instockdata.getBigInteger("userid"));
            outboundStatusProductSize.setType(instockdata.get("type").toString());
            outboundStatusProductSize.setToship(instockdata.getInteger("toship"));
            outboundStatusProductSize.setUnitprice(instockdata.getBigDecimal("unitprice"));
            outboundStatusProductSize.setUnitcost(instockdata.getBigDecimal("unitcost"));
            outboundStatusProductSize.setOrgid(orgid);
            outboundStatusProductSize.setCreatedate(new Date());
            outboundStatusProductSize.setLastupdate(new Date());
            outboundStatusProductSize.setOutboundStatusid(outboundStatus.getId());

            outboundStatusProductSize.setAvailable(instockdata.getInteger("available"));

            outboundStatusProductSizeList.add(outboundStatusProductSize);
        }

        intResult = outBoundService.addOutboundStatusProductSize(outboundStatusProductSizeList);

        for (OutboundStatusProductSize item : outboundStatusProductSizeList) {
            //EMail
            User uTmp = new User();
            uTmp.setId(item.getUserid());
            User u = prefileService.queryLogin(uTmp);

            User uTmpORG = new User();
            uTmpORG.setId(item.getOrgid());
            User uORG = prefileService.queryLogin(uTmpORG);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setUser(u);

            sendMessage.setSendtype("createShipment");
            String tmpSellerName = null;
            if (uORG.getName() != null && !uORG.getName().equals("")) {
                tmpSellerName = uORG.getName();
            } else {
                tmpSellerName = uORG.getUsername().split("@")[0];
            }
            sendMessage.setSubject(tmpSellerName + " | " + "New shipment request " + item.getId());


            sendMessage.setOrguserName(tmpSellerName);

            EmailCreateShipment emailCreateShipment = new EmailCreateShipment();
            emailCreateShipment.setId(item.getId());
            emailCreateShipment.setEmail(u.getUsername());
            emailCreateShipment.setServicetype(outboundStatus.getServicetype());
            emailCreateShipment.setOtherservices(outboundStatus.getOtherservices());
            emailCreateShipment.setPackagequantity(outboundStatus.getPackagequantity());

            //获取product
            WarehouseProduct wpTmp = warehouseService.queryWarehouseProductByID(item.getProductid());
            emailCreateShipment.setProduct(wpTmp.getConditionvalue() + "-" + wpTmp.getName());

            //获取site
            //获取对应site合集
            List<String> parList = new ArrayList<>();
            parList.add(item.getSiteid() + "");
            List<WareHouseSite> whsList = warehouseService.queryListWarehousesiteById(parList, "", null, "", "");
            if (whsList.size() > 0) {
                emailCreateShipment.setLocation(whsList.get(0).getSitename() + "-" + whsList.get(0).getAddress1());
            }

            emailCreateShipment.setUpc(wpTmp.getUpc());
            String rulTmp = "";
            if (outboundStatus.getLabel_url() != null && !outboundStatus.getLabel_url().equals("")) {
                rulTmp = outboundStatus.getLabel_url();
            } else {
                ///99999
                rulTmp = openurl + "orderapi/taskoffer/download/label/" + outboundStatus.getId();
            }
            emailCreateShipment.setUrl(rulTmp);

            emailCreateShipment.setInstruction(outboundStatus.getInstruction());

            emailCreateShipment.setUrlunsubscribe(openurl + "home/outbound");

            emailCreateShipment.setQuantity(item.getAvailable());

            sendMessage.setEmailCreateShipment(emailCreateShipment);
            SendMessageUtil.setSendMessageQueue(sendMessage);
        }

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        if (intResult >= 0) {
            info.put("msg", "Insert ok");
            info.put("status", "200");
        } else {
            info.put("msg", "Insert error");
            info.put("status", "303");
        }

        result.put("data", intResult);
        result.put("meta", info);

        return result;
    }

    @ResponseBody
    @PostMapping("/label/querystatuslist")
    public Map<String, Object> queryStatusList(@RequestBody JSONObject json) {
        Integer pageNum = json.getInteger("current");
        Integer pageSize = json.getInteger("pageSize");

        String orgid = json.getString("orgid");
        String status = json.getString("status");

        String userselect = json.getString("userselect");

        if (orgid == null) {
            orgid = "";
        }
        String selfid = json.getString("selfid");
        if (selfid == null) {
            selfid = "";
        }

        String searchinput = json.getString("searchinput");

        String startDate = json.getString("startDate");
        if (startDate == null) {
            startDate = "";
        }
        String endDate = json.getString("endDate");
        if (endDate == null) {
            endDate = "";
        }

        PageHelper.startPage(pageNum, pageSize);
        List<OutboundStatusPojo> outboundStatusPojoList = outBoundService.queryStatusList(pageNum, pageSize, orgid, selfid, searchinput, startDate, endDate, status, userselect);
        PageInfo<OutboundStatusPojo> outboundStatusPojoInfo = new PageInfo<>(outboundStatusPojoList);

        //获取user
        String username = json.getString("username");
        if (username == null) {
            username = "";
        }

//        if (orgid.equals("") || orgid == null) {
//            if (outboundStatusPojoList.size() > 0) {
//                orgid = outboundStatusPojoList.get(0).getOrgid() + "";
//            }
//        }

        List<User> basList = null;
        if (!orgid.equals("") && orgid != null) {
            basList = prefileService.getSellerList(username, null, null, pageNum, pageSize, Integer.parseInt(orgid), searchinput);
        }


        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        result.put("info", outboundStatusPojoInfo);
        result.put("selleruserlist", basList);

        info.put("msg", "Insert ok");
        info.put("status", "200");

        result.put("meta", info);
        return result;
    }

    @ResponseBody
    @PostMapping("/label/labelstatusuploadsave")
    public Map<String, Object> labelStatusUploadSave(
            @RequestParam(value = "file", required = false) MultipartFile[] fileList,
            @RequestParam("uploadData") String json,
            HttpServerRequest request
    ) {
        //上传
        String uploadfiles = "";
        if (fileList != null && fileList.length > 0) {
            for (MultipartFile filepart : fileList) {
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd");
                String date = sDateFormat.format(new Date());
                String fileD = RanStringUtil.generateByRandom(4);

                try {
                    File fileDirPath = new File(filePath + date + "/");
                    if (!fileDirPath.exists()) {
                        fileDirPath.mkdirs();
                    }

                    String suffix = filepart.getOriginalFilename().substring(filepart.getOriginalFilename().lastIndexOf(".") + 1);

                    File file = File.createTempFile(UUID.randomUUID().toString() + fileD, "." + suffix, fileDirPath);

                    filepart.transferTo(file);

                    if (uploadfiles.equals("")) {
                        uploadfiles = filePath + date + "/" + file.getName();
                    } else {
                        uploadfiles = uploadfiles + "," + filePath + date + "/" + file.getName();
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        JSONObject jsonObject = JSONObject.parseObject(json);
        BigInteger statusid = jsonObject.getBigInteger("id");
        String unitedelectronicsdeals = jsonObject.getString("unitedelectronicsdeals");

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        OutboundStatus outboundStatus = new OutboundStatus();
        outboundStatus.setId(statusid);
        outboundStatus.setUnitedelectronicsdeals(unitedelectronicsdeals);
        outboundStatus.setUploadaddress(uploadfiles);

        Integer resultInt = outBoundService.updateOutboundStatus(outboundStatus);
//        result.put("info", outboundStatusPojoInfo);

        info.put("msg", "Update ok");
        info.put("status", "200");

        result.put("meta", info);
        return result;
    }

    @ResponseBody
    @PostMapping("/label/labelstatuscancel")
    public Map<String, Object> labelStatusCancel(@RequestBody JSONObject json) {
        BigInteger id = json.getBigInteger("id");

        OutboundStatus outboundStatus = new OutboundStatus();
        outboundStatus.setId(id);
        outboundStatus.setStatus("cancel");
        Integer resultInt = outBoundService.updateOutboundStatus(outboundStatus);
//        int resultInt = outBoundService.deleteOutboundstatus(id);

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        if (resultInt > 0) {
            info.put("msg", "Update ok");
            info.put("status", "200");
        } else {
            info.put("msg", "Update 0");
            info.put("status", "200");
        }

        result.put("meta", info);
        return result;
    }

    @ResponseBody
    @PostMapping("/label/buyersconfirmshipment")
    public Map<String, Object> buyersConfirmShipment(
            @RequestParam(value = "file", required = false) MultipartFile[] fileList,
            @RequestParam("uploadData") String json,
            HttpServerRequest request
    ) {
        //上传
        String uploadfiles = "";
        if (fileList != null && fileList.length > 0) {
            for (MultipartFile filepart : fileList) {
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd");
                String date = sDateFormat.format(new Date());
                String fileD = RanStringUtil.generateByRandom(5);

                try {
                    File fileDirPath = new File(filePath + date + "/");
                    if (!fileDirPath.exists()) {
                        fileDirPath.mkdirs();
                    }

                    String suffix = filepart.getOriginalFilename().substring(filepart.getOriginalFilename().lastIndexOf(".") + 1);

                    File file = File.createTempFile(UUID.randomUUID().toString() + fileD, "." + suffix, fileDirPath);

                    filepart.transferTo(file);

                    if (uploadfiles.equals("")) {
                        uploadfiles = filePath + date + "/" + file.getName();
                    } else {
                        uploadfiles = uploadfiles + "," + filePath + date + "/" + file.getName();
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        JSONObject jsonObject = JSONObject.parseObject(json);
        String expedite = jsonObject.getString("ExpEdite");
        String label = jsonObject.getString("Label");
        String photo = jsonObject.getString("Photo");
        String sn = jsonObject.getString("Sn");
        String textareaofNote = jsonObject.getString("textareaofNote");

        OutboundStatus outboundStatus = new OutboundStatus();
        outboundStatus.setId(jsonObject.getBigInteger("id"));
        outboundStatus.setBuyersexpedite(expedite);
        outboundStatus.setBuyerslabel(label);
        outboundStatus.setBuyersphoto(photo);
        outboundStatus.setBuyerssn(sn);
        outboundStatus.setBuyersnote(textareaofNote);
        outboundStatus.setBuyersuploadfiles(uploadfiles);
        outboundStatus.setStatus("history");
        outboundStatus.setBuyerscreatedate(new Date());
        outboundStatus.setBuyerslastupdate(new Date());

        Integer intResult = 0;

        intResult = outBoundService.updateOutboundStatus(outboundStatus);

        Map<String, String> info = new HashMap<String, String>();
        Map<String, Object> result = new HashMap<String, Object>();

        if (intResult >= 0) {
            info.put("msg", "Insert ok");
            info.put("status", "200");
        } else {
            info.put("msg", "Insert error");
            info.put("status", "303");
        }

        result.put("data", intResult);
        result.put("meta", info);

        return result;
    }
}
