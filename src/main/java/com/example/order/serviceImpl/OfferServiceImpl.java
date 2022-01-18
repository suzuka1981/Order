package com.example.order.serviceImpl;

import com.example.order.entity.*;
import com.example.order.mapper.OfferMapper;
import com.example.order.pojo.BuyersInventoryDetailPojo;
import com.example.order.pojo.BuyersInventoryPojo;
import com.example.order.pojo.ProviderOfferPojo;
import com.example.order.pojo.TaskHistoryPojo;
import com.example.order.service.OfferService;
import com.example.order.util.transation.Retry;
import com.example.order.util.transation.RetryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Service
public class OfferServiceImpl implements OfferService {
    @Autowired
    public OfferMapper offerMapper;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "querylistOffer", allEntries = true),
            @CacheEvict(value = "queryByupcdate", allEntries = true),
    })
    public Integer addOffer(Offer offer) {
        return offerMapper.addOffer(offer);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "querylistOffer", allEntries = true),
    })
    public Integer updateOffer(Offer offer) {
        return offerMapper.updateOffer(offer);
    }

    @Retry
    @Override
    @Caching(evict = {
            @CacheEvict(value = "querylistOffer", allEntries = true),
    })
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Integer updateOfferLock(Offer offer, Integer subquantity) {
        Map<String, String> mapOffer = offerMapper.queryIdOffer(offer.getId());
        Object usedquantity = mapOffer.get("usedquantity");
        Object version = mapOffer.get("version");

        Integer tmpRusult = 0;
        if (Integer.parseInt(usedquantity.toString()) >= subquantity) {
            offer.setUsedquantity(Integer.parseInt(usedquantity.toString()) - subquantity);

            offer.setVersion(Integer.parseInt(version.toString()));

            tmpRusult = offerMapper.updateOfferLock(offer);
            if (tmpRusult <= 0) {
                throw new RetryException("Transfer failed, retry.");
            }
        } else {
            return tmpRusult;
        }

        return tmpRusult;
    }

    @Override
    @Cacheable(cacheNames = "querylistOffer", key = "#orgid + '-' + #pageNum+ '-' + #pageSize+ '-' + #searchinput+ '-' + #pagetype")
    public List<ProviderOfferPojo> querylistOffer(List<BigInteger> orgid, Integer pageNum, Integer pageSize, String searchinput, String pagetype) {
        return offerMapper.querylistOffer(orgid, pageNum, pageSize, searchinput, pagetype);
    }

    @Override
    @Cacheable(cacheNames = "queryTasklistOffer", key = "#orgid + '-' + #selfid+ '-' + #pageNum+ '-' + #pageSize+ '-' + #searchinput+ '-' + #startDate+ '-' + #endDate")
    public List<ProviderOfferPojo> queryTasklistOffer(List<BigInteger> orgid, BigInteger selfid, Integer pageNum, Integer pageSize, String searchinput, String startDate, String endDate) {
        return offerMapper.queryTasklistOffer(orgid, selfid, pageNum, pageSize, searchinput, startDate, endDate);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryTasklistOffer", allEntries = true),
            @CacheEvict(value = "queryListTaskHistory", allEntries = true),
            @CacheEvict(value = "queryListBuyersInventory", allEntries = true),
            @CacheEvict(value = "queryBuyersInventoryDetailPojo", allEntries = true),
            @CacheEvict(value = "queryPendingTransactions", allEntries = true),
            @CacheEvict(value = "queryListTaskCacncel", allEntries = true),
            @CacheEvict(value = "queryConfirmOfProduct", allEntries = true),
            @CacheEvict(value = "queryByupcdate", allEntries = true),
            @CacheEvict(value = "querySumQuantityConfirmOfProduct", allEntries = true),
    })
    public Integer addTaskOffer(BuyersTask buyersTask) {
        return offerMapper.addTaskOffer(buyersTask);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryTasklistOffer", allEntries = true),
            @CacheEvict(value = "queryListTaskHistory", allEntries = true),
            @CacheEvict(value = "queryListBuyersInventory", allEntries = true),
            @CacheEvict(value = "queryBuyersInventoryDetailPojo", allEntries = true),
            @CacheEvict(value = "queryPendingTransactions", allEntries = true),
            @CacheEvict(value = "queryListTaskCacncel", allEntries = true),
            @CacheEvict(value = "queryConfirmOfProduct", allEntries = true),
            @CacheEvict(value = "queryByupcdate", allEntries = true),
            @CacheEvict(value = "querySumQuantityConfirmOfProduct", allEntries = true),
    })
    public Integer updateTaskOffer(BuyersTask buyersTask) {
        Map<String, String> mapOffer = offerMapper.queryIdTaskOffer(buyersTask.getId());
        Object usedquantity = mapOffer.get("usedquantity");
        Object subquantity = mapOffer.get("subquantity");

        Integer resultInt = 0;
        if (Integer.parseInt(usedquantity.toString()) >= buyersTask.getUsedquantity()) {
//            buyersTask.setSubquantity(Integer.parseInt(subquantity.toString()) - buyersTask.getUsedquantity());
            buyersTask.setUsedquantity(Integer.parseInt(usedquantity.toString()) - buyersTask.getUsedquantity());

            resultInt = offerMapper.updateTaskOffer(buyersTask);
        }

        return resultInt;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryConfirmList", allEntries = true),
            @CacheEvict(value = "queryListTaskHistory", allEntries = true),
            @CacheEvict(value = "queryListBuyersInventory", allEntries = true),
            @CacheEvict(value = "queryBuyersInventoryDetailPojo", allEntries = true),
            @CacheEvict(value = "queryPendingTransactions", allEntries = true),
            @CacheEvict(value = "queryListTaskCacncel", allEntries = true),
            @CacheEvict(value = "queryConfirmOfProduct", allEntries = true),
            @CacheEvict(value = "queryByupcdate", allEntries = true),
            @CacheEvict(value = "querySumQuantityConfirmOfProduct", allEntries = true),
            @CacheEvict(value = "queryByupcdate", allEntries = true),
    })
    public Integer addBuyersConfirm(BuyersConfirm buyersConfirm) {
        return offerMapper.addBuyersConfirm(buyersConfirm);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryConfirmList", allEntries = true),
            @CacheEvict(value = "queryListTaskHistory", allEntries = true),
            @CacheEvict(value = "queryListBuyersInventory", allEntries = true),
            @CacheEvict(value = "queryBuyersInventoryDetailPojo", allEntries = true),
            @CacheEvict(value = "queryPendingTransactions", allEntries = true),
            @CacheEvict(value = "queryListTaskCacncel", allEntries = true),
            @CacheEvict(value = "queryConfirmOfProduct", allEntries = true),
            @CacheEvict(value = "queryByupcdate", allEntries = true),
            @CacheEvict(value = "querySumQuantityConfirmOfProduct", allEntries = true),
    })
    public Integer updateConfirm(BuyersConfirm buyersConfirm) {
        return offerMapper.updateConfirm(buyersConfirm);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryConfirmList", allEntries = true),
            @CacheEvict(value = "queryListTaskHistory", allEntries = true),
            @CacheEvict(value = "queryListBuyersInventory", allEntries = true),
            @CacheEvict(value = "queryBuyersInventoryDetailPojo", allEntries = true),
            @CacheEvict(value = "queryPendingTransactions", allEntries = true),
            @CacheEvict(value = "queryListTaskCacncel", allEntries = true),
            @CacheEvict(value = "queryConfirmOfProduct", allEntries = true),
            @CacheEvict(value = "queryByupcdate", allEntries = true),
            @CacheEvict(value = "querySumQuantityConfirmOfProduct", allEntries = true),
    })
    public Integer addBuyersCancel(BuyersConfirm buyersCancel) {
        return offerMapper.addBuyersCancel(buyersCancel);
    }

    @Override
    @Cacheable(cacheNames = "queryConfirmList", key = "#buyersid + '-' + #sellerid+ '-' + #pageNum+ '-' + #pageSize+ '-' + #buyerstaskid+ '-' + #id")
    public List<BuyersConfirm> queryConfirmList(Integer buyersid, Integer sellerid, Integer buyerstaskid, Integer pageNum, Integer pageSize, Integer id) {
        return offerMapper.queryConfirmList(buyersid, sellerid, buyerstaskid, id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "querylistOfferProposed", allEntries = true),
            @CacheEvict(value = "queryListTaskHistory", allEntries = true),
            @CacheEvict(value = "queryListBuyersInventory", allEntries = true),
            @CacheEvict(value = "queryBuyersInventoryDetailPojo", allEntries = true),
            @CacheEvict(value = "queryPendingTransactions", allEntries = true),
            @CacheEvict(value = "queryListTaskCacncel", allEntries = true),
            @CacheEvict(value = "queryConfirmOfProduct", allEntries = true),
            @CacheEvict(value = "queryByupcdate", allEntries = true),
            @CacheEvict(value = "querySumQuantityConfirmOfProduct", allEntries = true),
    })
    public Integer addProposeOffer(BuyersTask buyersTask) {
        return offerMapper.addProposeOffer(buyersTask);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "querylistOfferProposed", allEntries = true),
            @CacheEvict(value = "queryListTaskHistory", allEntries = true),
            @CacheEvict(value = "queryListBuyersInventory", allEntries = true),
            @CacheEvict(value = "queryBuyersInventoryDetailPojo", allEntries = true),
            @CacheEvict(value = "queryPendingTransactions", allEntries = true),
            @CacheEvict(value = "queryListTaskCacncel", allEntries = true),
            @CacheEvict(value = "queryConfirmOfProduct", allEntries = true),
            @CacheEvict(value = "queryByupcdate", allEntries = true),
            @CacheEvict(value = "querySumQuantityConfirmOfProduct", allEntries = true),
    })
    public Integer updateProposeOffer(BuyersTask buyersTask) {
        return offerMapper.updateProposeOffer(buyersTask);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "querylistOfferProposed", allEntries = true),
            @CacheEvict(value = "queryListTaskHistory", allEntries = true),
            @CacheEvict(value = "queryListBuyersInventory", allEntries = true),
            @CacheEvict(value = "queryBuyersInventoryDetailPojo", allEntries = true),
            @CacheEvict(value = "queryPendingTransactions", allEntries = true),
            @CacheEvict(value = "queryListTaskCacncel", allEntries = true),
            @CacheEvict(value = "queryConfirmOfProduct", allEntries = true),
            @CacheEvict(value = "queryByupcdate", allEntries = true),
            @CacheEvict(value = "querySumQuantityConfirmOfProduct", allEntries = true),
    })
    public Integer deleteProposeOffer(Integer id) {
        return offerMapper.deleteProposeOffer(id);
    }

    @Override
    @Cacheable(cacheNames = "querylistOfferProposed", key = "#orgid + '-' + #selfid + '-' + #pageNum+ '-' + #pageSize+ '-' + #searchinput")
    public List<ProviderOfferPojo> querylistOfferProposed(List<BigInteger> orgid, BigInteger selfid, Integer pageNum, Integer pageSize, String searchinput) {
        return offerMapper.querylistOfferProposed(orgid, selfid, pageNum, pageSize, searchinput);
    }

    @Override
    @Cacheable(cacheNames = "queryListTaskHistory", key = "#orgid + '-' + #selfid+ '-' + #startDate+'-' + #endDate+'-' + #pageNum+ '-' + #pageSize+ '-' + #searchinput+ '-' + #sendtotype")
    public List<TaskHistoryPojo> queryListTaskHistory(
            List<BigInteger> orgid,
            BigInteger selfid,
            String startDate,
            String endDate,
            Integer pageNum,
            Integer pageSize,
            String searchinput,
            String sendtotype) {
        return offerMapper.queryListTaskHistory(orgid, selfid, startDate, endDate, pageNum, pageSize, searchinput, sendtotype);
    }

    @Override
    @Cacheable(cacheNames = "queryListTaskCacncel", key = "#orgid + '-' + #selfid+ '-' + #startDate+'-' + #endDate+'-' + #pageNum+ '-' + #pageSize+ '-' + #searchinput+ '-' + #sendtotype")
    public List<TaskHistoryPojo> queryListTaskCacncel(
            List<BigInteger> orgid,
            BigInteger selfid,
            String startDate,
            String endDate,
            Integer pageNum,
            Integer pageSize,
            String searchinput,
            String sendtotype) {
        return offerMapper.queryListTaskCacncel(orgid, selfid, startDate, endDate, pageNum, pageSize, searchinput, sendtotype);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "querylistOffer", allEntries = true),
    })
    public Integer updateCancelOffer(Offer offer) {
        Offer tmpOffer = new Offer();
        tmpOffer = offerMapper.queryOffer(offer);

        offer.setUsedquantity(offer.getQuantity() + tmpOffer.getUsedquantity());
        return offerMapper.updateOffer(offer);
    }

    @Override
    @Cacheable(cacheNames = "queryListBuyersInventory", key = "#sellerIdList + '-' + #selfid+ '-' + #startDate +'-' + #pageNum+ '-' + #pageSize+ '-' + #searchinput+ '-' + #type")
    public List<BuyersInventoryPojo> queryListBuyersInventory(List<BigInteger> sellerIdList, BigInteger selfid, Integer pageNum, Integer pageSize, String searchinput, String type) {
        return offerMapper.queryListBuyersInventory(sellerIdList, selfid, pageNum, pageSize, searchinput, type);
    }

    @Override
    @Cacheable(cacheNames = "queryBuyersInventoryDetailPojo", key = "#whpid + '-' + #sellerid+ '-' + #buyersid +'-' + #pageNum+ '-' + #pageSize+ '-' + #searchinput+ '-' + #type")
    public List<BuyersInventoryDetailPojo> queryListBuyersInventoryDetail(BigInteger whpid, BigInteger sellerid, BigInteger buyersid, Integer pageNum, Integer pageSize, String searchinput, String type) {
        return offerMapper.queryListBuyersInventoryDetail(whpid, sellerid, buyersid, pageNum, pageSize, searchinput, type);
    }

    @Override
    @Cacheable(cacheNames = "queryPendingTransactions", key = "#sellerIdList + '-' + #selfid +'-' + #pageNum+ '-' + #pageSize+ '-' + #searchinput+ '-' + #type")
    public List<Map<String, String>> queryPendingTransactions(List<BigInteger> sellerIdList, BigInteger selfid, Integer pageNum, Integer pageSize, String searchinput, String type) {
        return offerMapper.queryPendingTransactions(sellerIdList, selfid, pageNum, pageSize, searchinput, type);
    }

    @Override
    public Map<String, String> queryTransactionHistoryLastOne(BigInteger sellerid, BigInteger buyersid) {
        return offerMapper.queryTransactionHistoryLastOne(sellerid, buyersid);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryTransactionHistory", allEntries = true),
    })
    public Integer addtransactionhistory(TransactionHistory transactionHistory) {
        return offerMapper.addtransactionhistory(transactionHistory);
    }

    @Override
    @Cacheable(cacheNames = "queryTransactionHistory", key = "#sellerIdList + '-' + #selfid +'-' + #pageNum+ '-' + #pageSize+ '-' + #checked+ '-' + #endDate")
    public List<TransactionHistory> queryTransactionHistory(List<BigInteger> sellerIdList, BigInteger selfid, Integer pageNum, Integer pageSize, String checked, String endDate) {
        return offerMapper.queryTransactionHistory(sellerIdList, selfid, pageNum, pageSize, checked, endDate);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryPaymentRequest", allEntries = true),
    })
    public Integer addPaymentRequest(PaymentRequest paymentRequest) {
        return offerMapper.addPaymentRequest(paymentRequest);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryPaymentRequest", allEntries = true),
    })
    public Integer updatePaymentRequest(PaymentRequest paymentRequest) {
        return offerMapper.updatePaymentRequest(paymentRequest);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryPaymentRequest", allEntries = true),
    })
    public Integer deletePaymentRequest(Integer id) {
        return offerMapper.deletePaymentRequest(id);
    }

    @Override
    @Cacheable(cacheNames = "queryPaymentRequest", key = "#sellerIdList + '-' + #selfid +'-' + #pageNum+ '-' + #pageSize+ '-' + #searchinput+ '-' + #paymentrequestsstatus")
    public List<PaymentRequest> queryPaymentRequest(List<BigInteger> sellerIdList, BigInteger selfid, Integer pageNum, Integer pageSize, String searchinput, String paymentrequestsstatus) {
        return offerMapper.queryPaymentRequest(sellerIdList, selfid, pageNum, pageSize, searchinput, paymentrequestsstatus);
    }

    @Override
    @Cacheable(cacheNames = "queryConfirmCheckWarehouse", key = "#tracking + '-' + #selfid +'-' + #pageNum+ '-' + #pageSize+ '-' + #buyerstaskid")
    public List<ExcelPackages> queryConfirmCheckWarehouse(String[] tracking, Integer pageNum, Integer pageSize, Integer buyerstaskid) {
        return offerMapper.queryConfirmCheckWarehouse(tracking, pageNum, pageSize, buyerstaskid);
    }
}
