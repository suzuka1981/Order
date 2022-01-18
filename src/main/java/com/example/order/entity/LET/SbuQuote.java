package com.example.order.entity.LET;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SbuQuote {
    public String api_key;
    public Content content;
    public String sign;
}
