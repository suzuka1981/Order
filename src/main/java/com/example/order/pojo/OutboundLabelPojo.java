package com.example.order.pojo;

import com.example.order.entity.OutboundAddress;
import com.example.order.entity.OutboundPackageSize;
import com.example.order.entity.OutboundRate;
import com.example.order.entity.OutboundShipment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OutboundLabelPojo {
    public BigInteger id;
    public String valueOfShipService;
    public String valueOfTemplate;
    public String valueOfSignature;
    public Date shipdate;
    public String inputNote;
    public String inputMemo;

    public BigInteger orgid;
    public BigInteger selfid;

    public Date createdate;
    public Date lastupdate;

    OutboundAddress outsenderForm;
    OutboundAddress outrecipientForm;
    OutboundRate rateSub;
    OutboundShipment outboundShipment;
    List<OutboundPackageSize> outboundPackageSizeList;
}
