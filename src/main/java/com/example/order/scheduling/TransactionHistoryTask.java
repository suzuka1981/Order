package com.example.order.scheduling;

import com.example.order.entity.BuyersConfirm;
import com.example.order.entity.SendMessage;
import com.example.order.entity.TransactionHistory;
import com.example.order.entity.User;
import com.example.order.pojo.EmailCreateShipment;
import com.example.order.service.OfferService;
import com.example.order.service.OutBoundService;
import com.example.order.util.SendMessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;


@Configuration
@EnableScheduling
public class TransactionHistoryTask {
    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${openurl}")
    public String openurl;

    @Resource
    private OfferService offerService;

    @Resource
    private OutBoundService outBoundService;

    //3.添加定时任务
    // Cron表达式范例：
    //    每隔5秒执行一次：*/5 * * * * ?
    //    每隔1分钟执行一次：0 */1 * * * ?
    //    每天23点执行一次：0 0 23 * * ?
    //    每天凌晨1点执行一次：0 0 1 * * ?
    //    每月1号凌晨1点执行一次：0 0 1 1 * ?
    //    每月最后一天23点执行一次：0 0 23 L * ?
    //    每周星期天凌晨1点实行一次：0 0 1 ? * L
    //    在26分、29分、33分执行一次：0 26,29,33 * * * ?
    //    每天的0点、13点、18点、21点都执行一次：0 0 0,13,18,21 * * ?
    //    fixedDelay ，fixedRate 延迟 填写相应的毫秒数即可
    int i = 0;

    @Scheduled(cron = "*/10 * * * * ?")
    //@Scheduled(fixedRate=5000)
    private void configureTasks() {
//        System.out.println("Threed i: " + i);
        if (i != 0) {
            System.out.println("Threed transactions Sleep" + LocalDateTime.now());
            return;
        } else {
            i++;
        }

        //transactions
        try {
            List<Map<String, String>> pendingTransactionsMap = offerService.queryPendingTransactions(null, null, null, null, null, "transactions");

            List<Map<String, String>> pendingRunList = new ArrayList<>();
            for (Map<String, String> item : pendingTransactionsMap) {
                Integer pendingperioddays = Integer.parseInt(item.get("pendingperioddays"));

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                String tmpStringDate = String.valueOf(item.get("createdate"));
                Date tmpDate = sdf.parse(tmpStringDate);

                Date now = new Date();

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(tmpDate);
                calendar.add(Calendar.DAY_OF_MONTH, pendingperioddays);

                tmpDate = calendar.getTime();

                if (tmpDate.before(now)) {
                    TransactionHistory transactionHistory = new TransactionHistory();

                    transactionHistory.setCreatedate(tmpDate);
                    transactionHistory.setSellerusername(item.get("username"));
                    transactionHistory.setType(item.get("type"));
                    transactionHistory.setProductname(item.get("name"));
                    if (item.get("price") != null) {
                        Object obj = item.get("price");
                        transactionHistory.setPrice(new BigDecimal(obj.toString()));
                    }

                    if (item.get("quantity") != null) {
                        Object obj = item.get("quantity");
                        transactionHistory.setQuantity(Integer.parseInt(obj.toString()));
                    }

                    transactionHistory.setOnlyshiptowarehouse(item.get("onlyshiptowarehouse"));
                    if (item.get("id") != null) {
                        Object obj = item.get("id");
                        transactionHistory.setBuyersconfirmid(new BigInteger(obj.toString()));
                    }
                    if (item.get("buyersid") != null) {
                        Object obj = item.get("buyersid");
                        transactionHistory.setBuyersid(new BigInteger(obj.toString()));
                    }
                    if (item.get("sellerid") != null) {
                        Object obj = item.get("sellerid");
                        transactionHistory.setSellerid(new BigInteger(obj.toString()));
                    }

                    transactionHistory.setAmount(
                            transactionHistory.getPrice()
                                    .multiply(new BigDecimal(transactionHistory.getQuantity())));

//                Object objQuantity = item.get("quantity");
//                Object objPrice = item.get("price");
//                if (item.get("onlyshiptowarehouse") != null &&
//                        item.get("onlyshiptowarehouse").equals("false")
//                        && item.get("bonus") != null) {
//                    Object objBonus = item.get("bonus");
//
//                    transactionHistory.setAmount(
//                            (
//                                    new BigDecimal(objPrice.toString()).add
//                                            (
//                                                    new BigDecimal(objBonus.toString())
//                                            )
//                            ).multiply(new BigDecimal(objQuantity.toString()))
//                    );
//                } else {
//                    transactionHistory.setAmount(
//                            new BigDecimal(objPrice.toString())
//                                    .multiply(new BigDecimal(objQuantity.toString())));
//                }
//
                    Map<String, String> balanceLastOne = offerService.queryTransactionHistoryLastOne(transactionHistory.getSellerid(), transactionHistory.getBuyersid());

                    if (balanceLastOne == null) {
                        transactionHistory.setBalance(transactionHistory.getAmount());
                    } else {
                        Object obj = balanceLastOne.get("balance");
                        BigDecimal balance = new BigDecimal(obj.toString());

                        if (transactionHistory.getQuantity() > 0) {
                            transactionHistory.setBalance(transactionHistory.getAmount().add(balance));
                        } else {
                            transactionHistory.setBalance(balance.add(transactionHistory.getAmount()));
                        }
                    }

                    //添加balance
                    Integer result = offerService.addtransactionhistory(transactionHistory);

                    //更新confirm表
                    if (result > 0) {
                        BuyersConfirm buyersConfirm = new BuyersConfirm();
                        Object obj = item.get("id");

                        buyersConfirm.setId(new BigInteger(obj.toString()));

                        if (transactionHistory.getQuantity() < 0) {
                            buyersConfirm.setHistorystatus("Reportmiss");
                        } else {
                            buyersConfirm.setHistorystatus("Inbound");
                        }

                        result = offerService.updateConfirm(buyersConfirm);
                    }
                } else {
//                System.out.println("没有过期" + tmpDate);
                }
            }
        } catch (Exception e) {

        }


        //adjust
        try {
            List<Map<String, String>> pendingTransactionsMap = offerService.queryPendingTransactions(null, null, null, null, null, "adjust");

            List<Map<String, String>> pendingRunList = new ArrayList<>();
            for (Map<String, String> item : pendingTransactionsMap) {
                TransactionHistory transactionHistory = new TransactionHistory();

                transactionHistory.setCreatedate(new Date());

                transactionHistory.setSellerusername(item.get("username"));
                transactionHistory.setTopaystatus(item.get("historystatus"));
                transactionHistory.setProductname(item.get("note"));

                if (item.get("price") != null) {
                    Object obj = item.get("price");
                    transactionHistory.setPrice(new BigDecimal(obj.toString()));
                }

                if (item.get("id") != null) {
                    Object obj = item.get("id");
                    transactionHistory.setBuyersconfirmid(new BigInteger(obj.toString()));
                }
                if (item.get("buyersid") != null) {
                    Object obj = item.get("buyersid");
                    transactionHistory.setBuyersid(new BigInteger(obj.toString()));
                }
                if (item.get("sellerid") != null) {
                    Object obj = item.get("sellerid");
                    transactionHistory.setSellerid(new BigInteger(obj.toString()));
                }

                transactionHistory.setAmount(
                        transactionHistory.getPrice()
                );

                Map<String, String> balanceLastOne = offerService.queryTransactionHistoryLastOne(transactionHistory.getSellerid(), transactionHistory.getBuyersid());

                if (balanceLastOne == null) {
                    transactionHistory.setBalance(new BigDecimal("0").subtract(transactionHistory.getAmount()));
                } else {
                    Object obj = balanceLastOne.get("balance");
                    BigDecimal balance = new BigDecimal(obj.toString());

                    transactionHistory.setBalance(balance.add(transactionHistory.getAmount()));

//                if(transactionHistory.getAmount().compareTo(new BigDecimal("0")) == 1 ){
//                    transactionHistory.setBalance(balance.add(transactionHistory.getAmount()));
//                } else {
//                    transactionHistory.setBalance(balance.subtract(transactionHistory.getAmount()));
//                }
                }

                //添加balance
                Integer result = offerService.addtransactionhistory(transactionHistory);

                //更新confirm表
                if (result > 0) {
                    BuyersConfirm buyersConfirm = new BuyersConfirm();
                    Object obj = item.get("id");

                    buyersConfirm.setId(new BigInteger(obj.toString()));
                    buyersConfirm.setHistorystatus("AdjustBalance");
                    result = offerService.updateConfirm(buyersConfirm);
                }

            }
        } catch (Exception e) {

        }

        //PaymentSleep
        try {
            List<Map<String, String>> pendingTransactionsMap = offerService.queryPendingTransactions(null, null, null, null, null, "PaymentSleep");

            List<Map<String, String>> pendingRunList = new ArrayList<>();
            for (Map<String, String> item : pendingTransactionsMap) {
                TransactionHistory transactionHistory = new TransactionHistory();

                transactionHistory.setCreatedate(new Date());

                transactionHistory.setSellerusername(item.get("username"));
                transactionHistory.setTopaystatus("Payment");
                transactionHistory.setProductname(item.get("note"));

                if (item.get("price") != null) {
                    Object obj = item.get("price");
                    transactionHistory.setPrice(new BigDecimal(obj.toString()));
                }

                if (item.get("id") != null) {
                    Object obj = item.get("id");
                    transactionHistory.setBuyersconfirmid(new BigInteger(obj.toString()));
                }
                if (item.get("buyersid") != null) {
                    Object obj = item.get("buyersid");
                    transactionHistory.setBuyersid(new BigInteger(obj.toString()));
                }
                if (item.get("sellerid") != null) {
                    Object obj = item.get("sellerid");
                    transactionHistory.setSellerid(new BigInteger(obj.toString()));
                }

                transactionHistory.setAmount(
                        transactionHistory.getPrice()
                );

                Map<String, String> balanceLastOne = offerService.queryTransactionHistoryLastOne(transactionHistory.getSellerid(), transactionHistory.getBuyersid());

                if (balanceLastOne == null) {
                    transactionHistory.setBalance(new BigDecimal("0").subtract(transactionHistory.getAmount()));
                } else {
                    Object obj = balanceLastOne.get("balance");
                    BigDecimal balance = new BigDecimal(obj.toString());

                    transactionHistory.setBalance(balance.subtract(transactionHistory.getAmount()));
                }

                //添加balance
                Integer result = offerService.addtransactionhistory(transactionHistory);

                //更新confirm表
                if (result > 0) {
                    BuyersConfirm buyersConfirm = new BuyersConfirm();
                    Object obj = item.get("id");

                    buyersConfirm.setId(new BigInteger(obj.toString()));
                    buyersConfirm.setHistorystatus("Payment");
                    result = offerService.updateConfirm(buyersConfirm);
                }

            }
        } catch (Exception e) {

        }

//        System.err.println("执行静态定时任务时间: transactions" + LocalDateTime.now());
        System.err.println("Thread transactions ready" + LocalDateTime.now());
        i--;
    }

    int j = 0;

    //    @Scheduled(cron = "*/7 * * * * ?")
    //@Scheduled(fixedRate=5000)
    private void configureTasksAdJustBalance() throws ParseException {
//        System.out.println("Threed i: " + j);

        if (j != 0) {
            System.out.println("Threed Adjust Sleep" + LocalDateTime.now());
            return;
        } else {
            j++;
        }

        List<Map<String, String>> pendingTransactionsMap = offerService.queryPendingTransactions(null, null, null, null, null, "adjust");

        List<Map<String, String>> pendingRunList = new ArrayList<>();
        for (Map<String, String> item : pendingTransactionsMap) {
            TransactionHistory transactionHistory = new TransactionHistory();

            transactionHistory.setCreatedate(new Date());

            transactionHistory.setSellerusername(item.get("username"));
            transactionHistory.setTopaystatus(item.get("historystatus"));
            transactionHistory.setProductname(item.get("note"));

            if (item.get("price") != null) {
                Object obj = item.get("price");
                transactionHistory.setPrice(new BigDecimal(obj.toString()));
            }

            if (item.get("id") != null) {
                Object obj = item.get("id");
                transactionHistory.setBuyersconfirmid(new BigInteger(obj.toString()));
            }
            if (item.get("buyersid") != null) {
                Object obj = item.get("buyersid");
                transactionHistory.setBuyersid(new BigInteger(obj.toString()));
            }
            if (item.get("sellerid") != null) {
                Object obj = item.get("sellerid");
                transactionHistory.setSellerid(new BigInteger(obj.toString()));
            }

            transactionHistory.setAmount(
                    transactionHistory.getPrice()
            );

            Map<String, String> balanceLastOne = offerService.queryTransactionHistoryLastOne(transactionHistory.getSellerid(), transactionHistory.getBuyersid());

            if (balanceLastOne == null) {
                transactionHistory.setBalance(new BigDecimal("0").subtract(transactionHistory.getAmount()));
            } else {
                Object obj = balanceLastOne.get("balance");
                BigDecimal balance = new BigDecimal(obj.toString());

                transactionHistory.setBalance(balance.add(transactionHistory.getAmount()));

//                if(transactionHistory.getAmount().compareTo(new BigDecimal("0")) == 1 ){
//                    transactionHistory.setBalance(balance.add(transactionHistory.getAmount()));
//                } else {
//                    transactionHistory.setBalance(balance.subtract(transactionHistory.getAmount()));
//                }
            }

            //添加balance
            Integer result = offerService.addtransactionhistory(transactionHistory);

            //更新confirm表
            if (result > 0) {
                BuyersConfirm buyersConfirm = new BuyersConfirm();
                Object obj = item.get("id");

                buyersConfirm.setId(new BigInteger(obj.toString()));
                buyersConfirm.setHistorystatus("AdjustBalance");
                result = offerService.updateConfirm(buyersConfirm);
            }

        }

//        System.err.println("执行静态定时任务时间: transactions" + LocalDateTime.now());
        System.err.println("Thread adjust ready" + LocalDateTime.now());
        j--;
    }

    int k = 0;

    //    @Scheduled(cron = "*/13 * * * * ?")
    //@Scheduled(fixedRate=5000)
    private void configureTasksPaymentBalance() throws ParseException {
//        System.out.println("Threed i: " + j);

        if (k != 0) {
            System.out.println("Threed Payment Sleep" + LocalDateTime.now());
            return;
        } else {
            k++;
        }

        List<Map<String, String>> pendingTransactionsMap = offerService.queryPendingTransactions(null, null, null, null, null, "PaymentSleep");

        List<Map<String, String>> pendingRunList = new ArrayList<>();
        for (Map<String, String> item : pendingTransactionsMap) {
            TransactionHistory transactionHistory = new TransactionHistory();

            transactionHistory.setCreatedate(new Date());

            transactionHistory.setSellerusername(item.get("username"));
            transactionHistory.setTopaystatus("Payment");
            transactionHistory.setProductname(item.get("note"));

            if (item.get("price") != null) {
                Object obj = item.get("price");
                transactionHistory.setPrice(new BigDecimal(obj.toString()));
            }

            if (item.get("id") != null) {
                Object obj = item.get("id");
                transactionHistory.setBuyersconfirmid(new BigInteger(obj.toString()));
            }
            if (item.get("buyersid") != null) {
                Object obj = item.get("buyersid");
                transactionHistory.setBuyersid(new BigInteger(obj.toString()));
            }
            if (item.get("sellerid") != null) {
                Object obj = item.get("sellerid");
                transactionHistory.setSellerid(new BigInteger(obj.toString()));
            }

            transactionHistory.setAmount(
                    transactionHistory.getPrice()
            );

            Map<String, String> balanceLastOne = offerService.queryTransactionHistoryLastOne(transactionHistory.getSellerid(), transactionHistory.getBuyersid());

            if (balanceLastOne == null) {
                transactionHistory.setBalance(new BigDecimal("0").subtract(transactionHistory.getAmount()));
            } else {
                Object obj = balanceLastOne.get("balance");
                BigDecimal balance = new BigDecimal(obj.toString());

                transactionHistory.setBalance(balance.subtract(transactionHistory.getAmount()));
            }

            //添加balance
            Integer result = offerService.addtransactionhistory(transactionHistory);

            //更新confirm表
            if (result > 0) {
                BuyersConfirm buyersConfirm = new BuyersConfirm();
                Object obj = item.get("id");

                buyersConfirm.setId(new BigInteger(obj.toString()));
                buyersConfirm.setHistorystatus("Payment");
                result = offerService.updateConfirm(buyersConfirm);
            }

        }

//        System.err.println("执行静态定时任务时间: transactions" + LocalDateTime.now());
        System.err.println("Thread Payment ready" + LocalDateTime.now());
        k--;
    }

    int l = 0;

    @Scheduled(cron = "*/1 * * * * ?")
    //@Scheduled(fixedRate=5000)
    private void sendMail() throws Exception {
        if (l != 0) {
            System.out.println("Threed sendMail Sleep" + LocalDateTime.now());
            return;
        } else {
            l++;
        }

        SendMessage sendMessageGet = SendMessageUtil.getSendMessage();

        if (sendMessageGet == null) {
            l--;
            return;
        }

        // 构建一个邮件对象
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // 设置邮件主题
        helper.setSubject(sendMessageGet.getSubject());
        // 设置邮件发送者，这个跟application.yml中设置的要一致
        helper.setFrom(from);
        // 设置邮件接收者，可以有多个接收者，中间用逗号隔开，以下类似
        // message.setTo("10*****16@qq.com","12****32*qq.com");
//            helper.setTo(new String[]{"420416140@qq.com","464519908@qq.com"});

        // 设置邮件抄送人，可以有多个抄送人
//            message.setCc("12****32*qq.com");
        // 设置隐秘抄送人，可以有多个
//            message.setBcc("7******9@qq.com");
        // 设置邮件发送日期
        helper.setSentDate(new Date());
        // 设置邮件的正文
//            helper.setText(sendMessageGet.getContent(), true);

        String process = null;

        if (sendMessageGet.getSendtype().equals("forgotpassword")) {
            User uTmp = sendMessageGet.getUser();

            helper.setTo(uTmp.getUsername());

            // 这里引入的是Template的Context
            Context context = new Context();
            // 设置模板中的变量
            context.setVariable("password", uTmp.getPassword());

            // 第一个参数为模板的名称
            process = templateEngine.process("psw.html", context);

            // 第二个参数true表示这是一个html文本
            helper.setText(process, true);

            // 发送邮件
            javaMailSender.send(message);
        }

        if (sendMessageGet.getSendtype().equals("addoreditoffer")) {
            for (User uItem : sendMessageGet.getSendToUserList()) {
                helper.setTo(uItem.getUsername());

                // 这里引入的是Template的Context
                Context context = new Context();
                // 设置模板中的变量
                context.setVariable("officeid", sendMessageGet.getOffer().getId());
                context.setVariable("conditionvalue", sendMessageGet.getWhp().getConditionvalue());
                context.setVariable("name", sendMessageGet.getWhp().getName());
                context.setVariable("price", sendMessageGet.getOffer().getPrice());
                context.setVariable("quantity", sendMessageGet.getOffer().getQuantity());
                context.setVariable("expirationdate", sendMessageGet.getOffer().getExpirationdate());
                context.setVariable("note", sendMessageGet.getOffer().getNote());
                context.setVariable("wareHouseSiteList", sendMessageGet.getWareHouseSiteList());

                context.setVariable("url", openurl + "home/offer");

                // 第一个参数为模板的名称
                process = templateEngine.process("offer.html", context);

                // 第二个参数true表示这是一个html文本
                helper.setText(process, true);

                try {
                    // 发送邮件
                    javaMailSender.send(message);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        if (sendMessageGet.getSendtype().equals("createShipment")) {
            User uTmp = sendMessageGet.getUser();

            helper.setTo(uTmp.getUsername());

            EmailCreateShipment emailCreateShipment = sendMessageGet.getEmailCreateShipment();

            // 这里引入的是Template的Context
            Context context = new Context();
            // 设置模板中的变量
            context.setVariable("email", emailCreateShipment.getEmail());
            context.setVariable("id", emailCreateShipment.getId());
            context.setVariable("servicetype", emailCreateShipment.getServicetype());
            context.setVariable("otherservices", emailCreateShipment.getOtherservices());
            context.setVariable("packagequantity", emailCreateShipment.getPackagequantity());
            context.setVariable("product", emailCreateShipment.getProduct());
            context.setVariable("quantity", emailCreateShipment.getQuantity());
            context.setVariable("location", emailCreateShipment.getLocation());
            context.setVariable("upc", emailCreateShipment.getUpc());
            context.setVariable("url", emailCreateShipment.getUrl());
            context.setVariable("instruction", emailCreateShipment.getInstruction());
            context.setVariable("urlunsubscribe", emailCreateShipment.getUrlunsubscribe());

            context.setVariable("sellername", sendMessageGet.getOrguserName());

            // 第一个参数为模板的名称
            process = templateEngine.process("createshipment.html", context);

            // 第二个参数true表示这是一个html文本
            helper.setText(process, true);

            // 发送邮件
            javaMailSender.send(message);
        }

        l--;
    }

//    int m = 0;
//
//    @Scheduled(cron = "*/10 * * * * ?")
//    //@Scheduled(fixedRate=5000)
//    private void dataFromSapTasks() {
//        if (m != 0) {
//            System.out.println("Threed Date From SAP Sleep" + LocalDateTime.now());
//            return;
//        } else {
//            m++;
//        }
//
//        try {
//            Connection conn = MSSSConnection.getConn();
//
//            Date tmpDate = new Date();
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            String newDate = dateFormat.format(tmpDate);
//
//            String sql = "SELECT\n" +
//                    "T0.U_bizappsync,\n" +
//                    " 'Goods Receipt' AS 'Document Type',\n" +
//                    " T0.DocEntry,\n" +
//                    " T0.DocDate,\n" +
//                    " T4.[ItemCode],\n" +
//                    " T0.U_vendor_name,\n" +
//                    " T0.U_vendor_unitid,\n" +
//                    " T0.U_vendor_id,\n" +
//                    " T0.[U_trackingno] AS Trackingnumber,\n" +
//                    " T4.U_upc AS UPC,\n" +
//                    " T1.[Quantity],\n" +
//                    " T0.[U_vendor_unitid] AS Unitid,\n" +
//                    " T2.Cardname AS CustomerName,\n" +
//                    " T4.itemName,\n" +
//                    " T0.UpdateDate,\n" +
//                    " T4.[AvgPrice],\n" +
//                    " T4.[U_asin],\n" +
//                    " T4.[U_sku],\n" +
//                    " T4.[U_upc],\n" +
//                    " T4.U_app_whseproduct_id,\n" +
//                    " T4.U_app_user_id,\n" +
//                    " T4.U_app_user_username,\n" +
//                    " T4.U_weight,\n" +
//                    " T4.U_ozlbs,\n" +
//                    " T4.U_Length,\n" +
//                    " T4.U_Width,\n" +
//                    " T4.U_Height,\n" +
//                    " T4.[UserText],\n" +
//                    " T4.[CreateDate],\n" +
//                    " T4.[UpdateDate],\n" +
//                    " T4.[ItemName]\n" +
//                    "FROM\n" +
//                    " OIGN T0\n" +
//                    " INNER JOIN IGN1 T1 ON T0.[DocEntry] = T1.[DocEntry]\n" +
//                    " INNER JOIN OCRD T2 ON T0.U_vendor_id = T2.[CardCode]\n" +
//                    " INNER JOIN OITM T4 ON T1.ItemCode = T4.itemCode \n" +
//                    "WHERE\n" +
//                    "T0.U_bizappsync is null \n" +
//                    "and\n" +
//                    " T4.[validFor] = 'Y' \n" +
//                    " AND T0.\"DocDate\" <= '" +
//                    newDate +
//                    "' \n" +
//                    "ORDER BY\n" +
//                    " T0.Docentry DESC,\n" +
//                    " T0.\"DocDate\" DESC;";
//            PreparedStatement pstmt;
//            ResultSet rs;
//
//            pstmt = (PreparedStatement) conn.prepareStatement(sql);
//            rs = pstmt.executeQuery();
//            while (rs.next()) {
//                String ItemCode = rs.getNString("ItemCode");
//                String site = rs.getNString("U_vendor_unitid");
//                String trackingnumber = rs.getNString("Trackingnumber");
//                String upc = rs.getNString("UPC");
//                Integer quantity = rs.getInt("Quantity");
//
//                //与upload一样
//
//                System.out.println(ItemCode);
//            }
//            pstmt.close();
//            conn.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        m--;
//    }

    int m = 0;

    @Scheduled(cron = "0 */120 * * * ?")
    //@Scheduled(fixedRate=5000)
    private void cacheClearTasks() {
//        System.out.println("Threed i: " + i);
        if (m != 0) {
            System.out.println("Threed transactions cache clear Sleep" + LocalDateTime.now());
            return;
        } else {
            m++;
        }
        outBoundService.cacheClear();
        m--;
    }

}
