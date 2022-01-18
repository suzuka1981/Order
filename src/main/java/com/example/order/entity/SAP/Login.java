package com.example.order.entity.SAP;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Login {
    //"itItems" ---写死
    public String CompanyDB;
    public String Password;
    public String UserName;
}
