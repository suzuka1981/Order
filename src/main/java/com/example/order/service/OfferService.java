package com.example.order.service;

import com.example.order.entity.*;
import com.example.order.pojo.BuyersInventoryDetailPojo;
import com.example.order.pojo.BuyersInventoryPojo;
import com.example.order.pojo.ProviderOfferPojo;
import com.example.order.pojo.TaskHistoryPojo;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface OfferService {
    Integer addOffer(Offer offer);

    Integer updateOffer(Offer offer);

    Integer updateCancelOffer(Offer offer);

    Integer updateOfferLock(Offer offer, Integer subquantity);


    Integer addTaskOffer(BuyersTask buyersTask);

    Integer updateTaskOffer(BuyersTask buyersTask);

    List<ProviderOfferPojo> querylistOffer(List<BigInteger> orgid, Integer pageNum, Integer pageSize, String searchinput, String pagetype);

    List<ProviderOfferPojo> queryTasklistOffer(List<BigInteger> orgid, BigInteger selfid, Integer pageNum, Integer pageSize, String searchinput, String startDate, String endDate);

    Integer addBuyersConfirm(BuyersConfirm buyersConfirm);

    Integer updateConfirm(BuyersConfirm buyersConfirm);

    Integer addBuyersCancel(BuyersConfirm buyersCancel);

    List<BuyersConfirm> queryConfirmList(Integer buyersid, Integer sellerid, Integer buyerstaskid, Integer pageNum, Integer pageSize, Integer id);

    Integer addProposeOffer(BuyersTask buyersTask);

    Integer updateProposeOffer(BuyersTask buyersTask);

    Integer deleteProposeOffer(Integer id);

    List<ProviderOfferPojo> querylistOfferProposed(List<BigInteger> orgid, BigInteger selfid, Integer pageNum, Integer pageSize, String searchinput);

    List<TaskHistoryPojo> queryListTaskHistory(List<BigInteger> orgid, BigInteger selfid, String startDate, String endDate, Integer pageNum, Integer pageSize, String searchinput, String sendtotype);

    List<TaskHistoryPojo> queryListTaskCacncel(List<BigInteger> orgid, BigInteger selfid, String startDate, String endDate, Integer pageNum, Integer pageSize, String searchinput, String sendtotype);

    List<BuyersInventoryPojo> queryListBuyersInventory(List<BigInteger> sellerIdList, BigInteger selfid, Integer pageNum, Integer pageSize, String searchinput, String type);

    List<BuyersInventoryDetailPojo> queryListBuyersInventoryDetail(BigInteger whpid, BigInteger sellerid, BigInteger buyersid, Integer pageNum, Integer pageSize, String searchinput, String type);

    List<Map<String, String>> queryPendingTransactions(List<BigInteger> sellerIdList, BigInteger selfid, Integer pageNum, Integer pageSize, String searchinput, String type);

    Map<String, String> queryTransactionHistoryLastOne(BigInteger sellerid, BigInteger buyersid);

    Integer addtransactionhistory(TransactionHistory transactionHistory);

    List<TransactionHistory> queryTransactionHistory(List<BigInteger> sellerIdList, BigInteger selfid, Integer pageNum, Integer pageSize, String checked, String endDate);

    Integer addPaymentRequest(PaymentRequest paymentRequest);

    Integer updatePaymentRequest(PaymentRequest paymentRequest);

    Integer deletePaymentRequest(Integer id);

    List<PaymentRequest> queryPaymentRequest(List<BigInteger> sellerIdList, BigInteger selfid, Integer pageNum, Integer pageSize, String searchinput, String paymentrequestsstatus);

    List<ExcelPackages> queryConfirmCheckWarehouse(String[] tracking, Integer pageNum, Integer pageSize, Integer buyerstaskid);
//    List<Map<String, String>> queryConfirmCheckWarehouseOld(String[] tracking, Integer pageNum, Integer pageSize, Integer buyerstaskid);
}
