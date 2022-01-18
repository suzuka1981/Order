package com.example.order.serviceImpl;

import com.example.order.entity.BuyersAndSellers;
import com.example.order.entity.BuyersPaymentMethod;
import com.example.order.entity.Comments;
import com.example.order.entity.User;
import com.example.order.mapper.ProfileMapper;
import com.example.order.pojo.BuyersAndSellerPojo;
import com.example.order.service.PrefileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Service
public class PrefileServiceImpl implements PrefileService {
    @Autowired
    public ProfileMapper profileMapper;

    @Autowired
    JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    //
    @Override
    @Cacheable(cacheNames = "user", key = "#user")
    public User queryLogin(User user) {
        User u = profileMapper.queryLogin(user);

        return u;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "user", allEntries = true),
            @CacheEvict(value = "queryTasklistOffer", allEntries = true),
            @CacheEvict(value = "queryListTaskHistory", allEntries = true),
            @CacheEvict(value = "queryListTaskCacncel", allEntries = true),
            @CacheEvict(value = "querylistOfferProposed", allEntries = true),
            @CacheEvict(value = "queryListBuyersInventory", allEntries = true),
            @CacheEvict(value = "queryPendingTransactions", allEntries = true),
            @CacheEvict(value = "queryTransactionHistory", allEntries = true),
            @CacheEvict(value = "querylistOffer", allEntries = true),
            @CacheEvict(value = "getBuyersAndSeller", allEntries = true),
            @CacheEvict(value = "getSellerSon", allEntries = true),
            @CacheEvict(value = "getSellerList", allEntries = true),
            @CacheEvict(value = "queryBuyersAndSellers", allEntries = true),
    })
    public Integer registerUser(User user) {
        return profileMapper.registerUser(user);
    }

    @Override
    public Integer sendMail(User user) {
        try {
            String content = "<html>\n" +
                    "<body>\n" +
                    "    <h3>Password is Initial state</h3>\n" +
                    "    <h3>New password: 3333333</h3>\n" +
                    "</body>\n" +
                    "</html>";

            // 构建一个邮件对象
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // 设置邮件主题
            helper.setSubject("Password is Initial state");
            // 设置邮件发送者，这个跟application.yml中设置的要一致
            helper.setFrom(from);
            // 设置邮件接收者，可以有多个接收者，中间用逗号隔开，以下类似
            // message.setTo("10*****16@qq.com","12****32*qq.com");
            helper.setTo(user.getUsername());
            // 设置邮件抄送人，可以有多个抄送人
//            message.setCc("12****32*qq.com");
            // 设置隐秘抄送人，可以有多个
//            message.setBcc("7******9@qq.com");
            // 设置邮件发送日期
            helper.setSentDate(new Date());
            // 设置邮件的正文
            helper.setText(content, true);
            // 发送邮件
            javaMailSender.send(message);
            return 1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 2;
        }
    }

    @Override
//    @CacheEvict(value = {
//            "user",
//            "getBuyersAndSeller",
//            "getSellerSon",
//            "getSellerList",
//            "queryBuyersAndSellers"}
//            , allEntries = true, beforeInvocation = true)
    @Caching(evict = {
            @CacheEvict(value = "user", allEntries = true),
            @CacheEvict(value = "queryTasklistOffer", allEntries = true),
            @CacheEvict(value = "queryListTaskHistory", allEntries = true),
            @CacheEvict(value = "queryListTaskCacncel", allEntries = true),
            @CacheEvict(value = "querylistOfferProposed", allEntries = true),
            @CacheEvict(value = "queryListBuyersInventory", allEntries = true),
            @CacheEvict(value = "queryPendingTransactions", allEntries = true),
            @CacheEvict(value = "queryTransactionHistory", allEntries = true),
            @CacheEvict(value = "querylistOffer", allEntries = true),
            @CacheEvict(value = "getBuyersAndSeller", allEntries = true),
            @CacheEvict(value = "getSellerSon", allEntries = true),
            @CacheEvict(value = "getSellerList", allEntries = true),
            @CacheEvict(value = "queryBuyersAndSellers", allEntries = true),
    })
    public Integer updateUser(User user) {
        return profileMapper.updateUser(user);
    }

    @Override
    @Cacheable(cacheNames = "getBuyersAndSeller", key = "#username+#code+#status")
    public BuyersAndSellerPojo getBuyersAndSeller(String username, String code, Integer status) {
        return profileMapper.getBuyersAndSeller(username, code, status);
    }

    @Override
    @Cacheable(cacheNames = "getSellerSon", key = "#username+#code+#status")
    public BuyersAndSellerPojo getSellerSon(String username, String code, Integer status) {
        return profileMapper.getSellerSon(username, code, status);
    }

    @Override
    @Cacheable(cacheNames = "getSellerList", key = "#username+#code+#status+#pageNum+#pageSize+#sellerid+#searchinput")
    public List<User> getSellerList(String username, String code, Integer status, Integer pageNum, Integer pageSize, Integer sellerid, String searchinput) {
        return profileMapper.getSellerList(username, code, status, sellerid, searchinput);
    }

    @Override
    @Cacheable(cacheNames = "queryBuyersAndSellers", key = "#buyersAndSellers")
    public BuyersAndSellers queryBuyersAndSellers(BuyersAndSellers buyersAndSellers) {
        return profileMapper.queryBuyersAndSellers(buyersAndSellers);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "getBuyersAndSeller", allEntries = true),
            @CacheEvict(value = "getSellerSon", allEntries = true),
            @CacheEvict(value = "getSellerList", allEntries = true),
            @CacheEvict(value = "queryBuyersAndSellers", allEntries = true)
    })
    public Integer insertJoinOrganization(BuyersAndSellers buyersAndSellers) {
        return profileMapper.insertJoinOrganization(buyersAndSellers);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "getBuyersAndSeller", allEntries = true),
            @CacheEvict(value = "getSellerSon", allEntries = true),
            @CacheEvict(value = "getSellerList", allEntries = true),
            @CacheEvict(value = "queryBuyersAndSellers", allEntries = true)
    })
    public Integer deleteJoinOrganization(Integer id) {
        return profileMapper.deleteJoinOrganization(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "user", allEntries = true),
            @CacheEvict(value = "getBuyersAndSeller", allEntries = true),
            @CacheEvict(value = "getSellerSon", allEntries = true),
            @CacheEvict(value = "getSellerList", allEntries = true),
            @CacheEvict(value = "queryBuyersAndSellers", allEntries = true),
    })
    public Integer updataJoinOrganization(BuyersAndSellers buyersAndSellers) {
        return profileMapper.updataJoinOrganization(buyersAndSellers);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryBuyersPaymentMethod", allEntries = true),
    })
    public Integer insertbuyerspaymentmethod(BuyersPaymentMethod buyersPaymentMethod) {
        return profileMapper.insertbuyerspaymentmethod(buyersPaymentMethod);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryBuyersPaymentMethod", allEntries = true),
    })
    public Integer updatebuyerspaymentmethod(BuyersPaymentMethod buyersPaymentMethod) {
        return profileMapper.updatebuyerspaymentmethod(buyersPaymentMethod);
    }

    @Override
    @Cacheable(cacheNames = "queryBuyersPaymentMethod", key = "#buyersid + #pageNum + #pageSize + #searchinput")
    public List<BuyersPaymentMethod> queryBuyersPaymentMethod(Integer buyersid, Integer pageNum, Integer pageSize, String searchinput) {
        return profileMapper.queryBuyersPaymentMethod(buyersid, pageNum, pageSize, searchinput);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryComments", allEntries = true),
    })
    public Integer insertComments(Comments comments) {
        return profileMapper.insertComments(comments);
    }

    @Override
    @Cacheable(cacheNames = "queryComments", key = "#msgtype+#msgsourceid")
    public List<Comments> queryComments(String msgtype, BigInteger msgsourceid) {
        return profileMapper.queryComments(msgtype, msgsourceid);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "queryComments", allEntries = true),
    })
    public Integer updateComments(Comments comments) {
        return profileMapper.updateComments(comments);
    }
}
