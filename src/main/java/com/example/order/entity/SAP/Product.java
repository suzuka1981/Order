package com.example.order.entity.SAP;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    //"itItems" ---写死
    @JSONField(name="ItemType")
    public String ItemType;
    @JSONField(name="U_app_whseproduct_id")
    public String U_app_whseproduct_id;
    @JSONField(name="ItemCode")
    public String ItemCode;
    @JSONField(name="ItemName")
    public String ItemName;
    @JSONField(name="U_app_user_id")
    public String U_app_user_id;
    @JSONField(name="U_app_user_username")
    public String U_app_user_username;
    @JSONField(name="U_asin")
    public String U_asin;
    @JSONField(name="U_sku")
    public String U_sku;
    @JSONField(name="U_upc")
    public String U_upc;
}
