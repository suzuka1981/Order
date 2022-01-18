/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.3.137
 Source Server Type    : MySQL
 Source Server Version : 80021
 Source Host           : 192.168.3.137:3306
 Source Schema         : orderdb

 Target Server Type    : MySQL
 Target Server Version : 80021
 File Encoding         : 65001

 Date: 19/11/2021 10:28:13
*/

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for buyersandsellers
-- ----------------------------
DROP TABLE IF EXISTS `buyersandsellers`;
CREATE TABLE `buyersandsellers`
(
    `id`         bigint(0) NOT NULL AUTO_INCREMENT,
    `buyersid`   bigint(0) NULL DEFAULT NULL COMMENT '买家id',
    `sellerid`   bigint(0) NULL DEFAULT NULL COMMENT '卖家id',
    `status`     bigint(0) NULL DEFAULT NULL COMMENT '状态：1已经确认通过 2 等待确认',
    `note`       varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '买手请求加入卖家留言',
    `createdate` datetime(0) NULL DEFAULT NULL,
    `lastupdate` datetime(0) NULL DEFAULT NULL,
    `isdelete`   int(0) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    index        idx1(buyersid,sellerid,createdate,lastupdate,status,note)
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for buyerscancel
-- ----------------------------
DROP TABLE IF EXISTS `buyerscancel`;
CREATE TABLE `buyerscancel`
(
    `id`             bigint(0) NOT NULL AUTO_INCREMENT,
    `buyersid`       bigint(0) NULL DEFAULT NULL COMMENT '买家id',
    `sellerid`       bigint(0) NULL DEFAULT NULL COMMENT '卖家id',
    `quantity`       int(0) NULL DEFAULT NULL,
    `selfsite`       varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `trackingnumber` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `status`         varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `price`          decimal(10, 2) NULL DEFAULT NULL,
    `type`           varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'self or warehouse',
    `createdate`     datetime(0) NULL DEFAULT NULL,
    `lastupdate`     datetime(0) NULL DEFAULT NULL,
    `buyerstaskid`   bigint(0) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    index            idx1(buyersid,sellerid,createdate,trackingnumber,buyerstaskid),
    index            idx2(lastupdate,quantity,selfsite,status,price,`type`)
) ENGINE = InnoDB AUTO_INCREMENT = 58 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for buyersconfirm
-- ----------------------------
DROP TABLE IF EXISTS `buyersconfirm`;
CREATE TABLE `buyersconfirm`
(
    `id`               bigint(0) NOT NULL AUTO_INCREMENT,
    `buyersid`         bigint(0) NULL DEFAULT NULL COMMENT '买家id',
    `sellerid`         bigint(0) NULL DEFAULT NULL COMMENT '卖家id',
    `quantity`         int(0) NULL DEFAULT NULL,
    `selfsite`         varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `trackingnumber`   varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `status`           varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `price`            decimal(10, 2) NULL DEFAULT NULL,
    `type`             varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'self or warehouse',
    `createdate`       datetime(0) NULL DEFAULT NULL,
    `lastupdate`       datetime(0) NULL DEFAULT NULL,
    `buyerstaskid`     bigint(0) NULL DEFAULT NULL,
    `historystatus`    varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '是否执行过定时器',
    `note`             varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `paymentrequestid` bigint(0) NULL DEFAULT NULL,
    `methodsid`        bigint(0) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    index            idx1(buyersid,sellerid,createdate,trackingnumber,quantity,selfsite),
    index            idx2(status,price,`type` ,lastupdate,buyerstaskid),
    index            idx3(historystatus,note,`paymentrequestid` ,methodsid)
) ENGINE = InnoDB AUTO_INCREMENT = 158 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for buyerspayment
-- ----------------------------
DROP TABLE IF EXISTS `buyerspayment`;
CREATE TABLE `buyerspayment`
(
    `id`                bigint(0) NOT NULL,
    `category`          varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `displayname`       varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `receipientname`    varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `phonenumber`       varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `receipientaddress` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `createdate`        datetime(0) NULL DEFAULT NULL,
    `lastupdate`        datetime(0) NULL DEFAULT NULL,
    `isdelete`          int(0) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    index            idx1(category,displayname,receipientname),
    index            idx2(phonenumber,receipientaddress,createdate,lastupdate)
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for buyerspaymentmethod
-- ----------------------------
DROP TABLE IF EXISTS `buyerspaymentmethod`;
CREATE TABLE `buyerspaymentmethod`
(
    `id`                bigint(0) NOT NULL AUTO_INCREMENT,
    `displayname`       varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `description`       varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `creditcardcompany` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `cardholdername`    varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `accountnumber`     varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `bankpobox`         varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `bankname`          varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `accountholdername` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `currency`          varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `routingnumber`     varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `bankaddress`       varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `billingaddress`    varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `receipientname`    varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `phonenumber`       varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `receipientaddress` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `createdate`        datetime(0) NULL DEFAULT NULL,
    `lastupdate`        datetime(0) NULL DEFAULT NULL,
    `isdelete`          int(0) NULL DEFAULT NULL,
    `buyersid`          bigint(0) NULL DEFAULT NULL,
    `category`          varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    index            idx1(displayname,description,creditcardcompany,cardholdername),
    index            idx2(bankpobox,bankname,accountholdername,currency),
    index            idx3(bankaddress,billingaddress,receipientname,phonenumber),
    index            idx4(buyersid,category,createdate,lastupdate),
    index            idx5(accountnumber,routingnumber,receipientaddress)
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for buyerspropose
-- ----------------------------
DROP TABLE IF EXISTS `buyerspropose`;
CREATE TABLE `buyerspropose`
(
    `id`                     bigint(0) NOT NULL AUTO_INCREMENT,
    `bonus`                  decimal(10, 0) NULL DEFAULT NULL,
    `expirationdate`         datetime(0) NULL DEFAULT NULL,
    `minselfstoragequantity` int(0) NULL DEFAULT NULL,
    `movetotop`              varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `note`                   varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `notifymembers`          varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `productid`              bigint(0) NULL DEFAULT NULL,
    `onlyshiptowarehouse`    varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `orgid`                  bigint(0) NULL DEFAULT NULL,
    `pendingperioddays`      varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `price`                  decimal(10, 2) NULL DEFAULT NULL,
    `quantity`               int(0) NULL DEFAULT NULL,
    `requiredservicetag`     varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `visibletoallmembers`    varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `warehousesitesvalue`    varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `createdate`             datetime(0) NULL DEFAULT NULL,
    `lastupdate`             datetime(0) NULL DEFAULT NULL,
    `isdelete`               int(0) NULL DEFAULT NULL,
    `offer`                  varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `usedquantity`           int(0) NULL DEFAULT NULL,
    `sendto`                 varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `status`                 varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `selfid`                 bigint(0) NULL DEFAULT NULL,
    `subquantity`            bigint(0) NULL DEFAULT NULL,
    `offerid`                bigint(0) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    index            idx1(offerid,bonus,minselfstoragequantity,movetotop),
    index            idx2(note,notifymembers,productid,onlyshiptowarehouse),
    index            idx3(orgid,pendingperioddays,price,quantity),
    index            idx4(requiredservicetag,visibletoallmembers,warehousesitesvalue,createdate),
    index            idx5(lastupdate,offer,usedquantity,sendto),
    index            idx6(status,expirationdate,selfid,subquantity)
) ENGINE = InnoDB AUTO_INCREMENT = 84 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for buyersselfstorage
-- ----------------------------
DROP TABLE IF EXISTS `buyersselfstorage`;
CREATE TABLE `buyersselfstorage`
(
    `id`           bigint(0) NOT NULL AUTO_INCREMENT,
    `sitename`     varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `addressline1` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `addressline2` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `city`         varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `state`        varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `zipcode`      varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `createdate`   datetime(0) NULL DEFAULT NULL,
    `lastupdate`   datetime(0) NULL DEFAULT NULL,
    `isdelete`     int(0) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    index            idx1(sitename,addressline1,addressline2,city),
    index            idx2(`state`,zipcode,createdate,lastupdate)
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for buyerstask
-- ----------------------------
DROP TABLE IF EXISTS `buyerstask`;
CREATE TABLE `buyerstask`
(
    `id`                     bigint(0) NOT NULL AUTO_INCREMENT,
    `bonus`                  decimal(10, 0) NULL DEFAULT NULL,
    `expirationdate`         datetime(0) NULL DEFAULT NULL,
    `minselfstoragequantity` int(0) NULL DEFAULT NULL,
    `movetotop`              varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `note`                   varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `notifymembers`          varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `productid`              bigint(0) NULL DEFAULT NULL,
    `onlyshiptowarehouse`    varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `orgid`                  bigint(0) NULL DEFAULT NULL,
    `pendingperioddays`      varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `price`                  decimal(10, 2) NULL DEFAULT NULL,
    `quantity`               int(0) NULL DEFAULT NULL,
    `requiredservicetag`     varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `visibletoallmembers`    varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `warehousesitesvalue`    varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `createdate`             datetime(0) NULL DEFAULT NULL,
    `lastupdate`             datetime(0) NULL DEFAULT NULL,
    `isdelete`               int(0) NULL DEFAULT NULL,
    `offer`                  varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `usedquantity`           int(0) NULL DEFAULT NULL,
    `sendto`                 varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `status`                 varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `selfid`                 bigint(0) NULL DEFAULT NULL,
    `subquantity`            bigint(0) NULL DEFAULT NULL,
    `offerid`                bigint(0) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    index            idx1(bonus,expirationdate,minselfstoragequantity,movetotop),
    index            idx2(note,notifymembers,productid,onlyshiptowarehouse),
    index            idx3(orgid,pendingperioddays,price,quantity),
    index            idx4(requiredservicetag,visibletoallmembers,warehousesitesvalue,createdate),
    index            idx5(lastupdate,offer,usedquantity,sendto),
    index            idx6(`status`,selfid,subquantity,offerid)
) ENGINE = InnoDB AUTO_INCREMENT = 114 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`
(
    `id`           bigint(0) NOT NULL AUTO_INCREMENT,
    `userid`       bigint(0) NULL DEFAULT NULL,
    `username`     varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `message`      varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `msgtype`      varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `msgsourceid`  bigint(0) NULL DEFAULT NULL,
    `createdate`   datetime(0) NULL DEFAULT NULL,
    `lastupdate`   datetime(0) NULL DEFAULT NULL,
    `isdelete`     int(0) NULL DEFAULT NULL,
    `unreadstatus` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    index            idx1(userid,username,message,msgtype),
    index            idx2(msgsourceid,createdate,lastupdate,unreadstatus)
) ENGINE = InnoDB AUTO_INCREMENT = 44 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for excelpackages
-- ----------------------------
DROP TABLE IF EXISTS `excelpackages`;
CREATE TABLE `excelpackages`
(
    `id`             bigint(0) NOT NULL AUTO_INCREMENT,
    `tracking`       varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `upc`            varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `quantity`       int(0) NULL DEFAULT NULL,
    `site`           varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `createdate`     datetime(0) NULL DEFAULT NULL,
    `lastupdate`     datetime(0) NULL DEFAULT NULL,
    `repeatquantity` int(0) NULL DEFAULT NULL,
    `usedquantity`   int(0) NULL DEFAULT NULL,
    `version`        int(0) NULL DEFAULT NULL,
    `orgid`          bigint(0) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    index            idx1(tracking,upc,quantity,site),
    index            idx2(createdate,lastupdate,repeatquantity,usedquantity),
    index            idx3(version,orgid)
) ENGINE = InnoDB AUTO_INCREMENT = 255 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for offer
-- ----------------------------
DROP TABLE IF EXISTS `offer`;
CREATE TABLE `offer`
(
    `id`                     bigint(0) NOT NULL AUTO_INCREMENT,
    `bonus`                  decimal(10, 0) NULL DEFAULT NULL,
    `expirationdate`         datetime(0) NULL DEFAULT NULL,
    `minselfstoragequantity` int(0) NULL DEFAULT NULL,
    `movetotop`              varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `note`                   varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `notifymembers`          varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `productid`              bigint(0) NULL DEFAULT NULL,
    `onlyshiptowarehouse`    varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `orgid`                  bigint(0) NULL DEFAULT NULL,
    `pendingperioddays`      varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `price`                  decimal(10, 2) NULL DEFAULT NULL,
    `quantity`               int(0) NULL DEFAULT NULL,
    `requiredservicetag`     varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `visibletoallmembers`    varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `warehousesitesvalue`    varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `createdate`             datetime(0) NULL DEFAULT NULL,
    `lastupdate`             datetime(0) NULL DEFAULT NULL,
    `isdelete`               int(0) NULL DEFAULT NULL,
    `offer`                  varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `usedquantity`           int(0) NULL DEFAULT NULL,
    `version`                int(0) NULL DEFAULT NULL,
    `status`                 varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    index            idx1(bonus,expirationdate,minselfstoragequantity,movetotop),
    index            idx2(note,notifymembers,productid,onlyshiptowarehouse),
    index            idx3(orgid,pendingperioddays,price,quantity),
    index            idx4(requiredservicetag,visibletoallmembers,warehousesitesvalue,createdate),
    index            idx5(lastupdate,offer,usedquantity,version,status)
) ENGINE = InnoDB AUTO_INCREMENT = 60 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for paymentrequest
-- ----------------------------
DROP TABLE IF EXISTS `paymentrequest`;
CREATE TABLE `paymentrequest`
(
    `id`         bigint(0) NOT NULL AUTO_INCREMENT,
    `methodsid`  bigint(0) NULL DEFAULT NULL,
    `amount`     decimal(12, 2) NULL DEFAULT NULL,
    `sellerid`   bigint(0) NULL DEFAULT NULL,
    `buyersid`   bigint(0) NULL DEFAULT NULL,
    `createdate` datetime(0) NULL DEFAULT NULL,
    `lastupdate` datetime(0) NULL DEFAULT NULL,
    `isdelete`   int(0) NULL DEFAULT NULL,
    `status`     varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    index            idx1(methodsid,amount,sellerid,buyersid),
    index            idx2(createdate,lastupdate,`status`)
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for settinggeneral
-- ----------------------------
DROP TABLE IF EXISTS `settinggeneral`;
CREATE TABLE `settinggeneral`
(
    `id`                         bigint(0) NOT NULL AUTO_INCREMENT,
    `pendingperioddays`          varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `checkedVisibletoallmembers` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `defaultusers`               varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `minimumpaymentamount`       decimal(10, 0) NULL DEFAULT NULL,
    `checkedComments`            varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `valueOfSwitch`              varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `checkedEnableAmazonSites`   varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `orgid`                      bigint(0) NULL DEFAULT NULL,
    `createdate`                 datetime(0) NULL DEFAULT NULL,
    `lastupdate`                 datetime(0) NULL DEFAULT NULL,
    `isdelete`                   int(0) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    index            idx1(pendingperioddays,checkedVisibletoallmembers,defaultusers,minimumpaymentamount),
    index            idx2(checkedComments,valueOfSwitch,checkedEnableAmazonSites),
    index            idx3(orgid,createdate,lastupdate)
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for transactionhistory
-- ----------------------------
DROP TABLE IF EXISTS `transactionhistory`;
CREATE TABLE `transactionhistory`
(
    `id`                  bigint(0) NOT NULL AUTO_INCREMENT,
    `createdate`          datetime(0) NULL DEFAULT NULL,
    `sellerusername`      varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `type`                varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `productname`         varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `amount`              decimal(10, 2) NULL DEFAULT NULL,
    `balance`             decimal(10, 2) NULL DEFAULT NULL,
    `price`               decimal(10, 2) NULL DEFAULT NULL,
    `quantity`            int(0) NULL DEFAULT NULL,
    `onlyshiptowarehouse` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `buyersconfirmid`     bigint(0) NULL DEFAULT NULL,
    `buyersid`            bigint(0) NULL DEFAULT NULL,
    `sellerid`            bigint(0) NULL DEFAULT NULL,
    `topaystatus`         varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    index            idx1(createdate,sellerusername,`type`,productname),
    index            idx2(amount,balance,`price`,quantity),
    index            idx3(onlyshiptowarehouse,buyersconfirmid,`buyersid`,sellerid,topaystatus)
) ENGINE = InnoDB AUTO_INCREMENT = 550 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`         bigint(0) NOT NULL AUTO_INCREMENT,
    `username`   varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `password`   varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `status`     int(0) NULL DEFAULT NULL COMMENT '是否删除 0 正常 1 删除',
    `type`       int(0) NULL DEFAULT NULL COMMENT '0 买家 1 卖家',
    `code`       varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '买手添加卖家用',
    `createdate` datetime(0) NULL DEFAULT NULL,
    `lastupdate` datetime(0) NULL DEFAULT NULL,
    `isdelete`   int(0) NULL DEFAULT NULL,
    `name`       varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    index            idx1(username,password,`status`,`type`),
    index            idx2(code,createdate,`lastupdate`,`name`)
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for warehouseconfirmpackages
-- ----------------------------
DROP TABLE IF EXISTS `warehouseconfirmpackages`;
CREATE TABLE `warehouseconfirmpackages`
(
    `id`              bigint(0) NOT NULL AUTO_INCREMENT,
    `excelpackagesid` bigint(0) NULL DEFAULT NULL,
    `buyersconfirmid` bigint(0) NULL DEFAULT NULL,
    `quantity`        int(0) NULL DEFAULT NULL,
    `tracking`        varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `status`          varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `createdate`      datetime(0) NULL DEFAULT NULL,
    `lastupdate`      datetime(0) NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    index            idx1(excelpackagesid,buyersconfirmid,`quantity`,`tracking`),
    index            idx2(`status`,createdate,`lastupdate`)
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for warehouseproduct
-- ----------------------------
DROP TABLE IF EXISTS `warehouseproduct`;
CREATE TABLE `warehouseproduct`
(
    `id`             bigint(0) NOT NULL AUTO_INCREMENT,
    `price`          decimal(10, 2) NULL DEFAULT NULL,
    `conditionvalue` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `asin`           varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `sku`            varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `upc`            varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `checked`        varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `note`           varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `createdate`     datetime(0) NULL DEFAULT NULL,
    `lastupdate`     datetime(0) NULL DEFAULT NULL,
    `isdelete`       int(0) NULL DEFAULT NULL,
    `orgid`          bigint(0) NULL DEFAULT NULL,
    `name`           varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `inbound`        int(0) NULL DEFAULT NULL,
    `status`         varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `mfsku`          varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    index            idx1(price,conditionvalue,`asin`,`sku`),
    index            idx2(upc,checked,`note`,`createdate`),
    index            idx3(lastupdate,orgid,`name`,`inbound`),
    index            idx4(`status`,mfsku)
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for warehousesite
-- ----------------------------
DROP TABLE IF EXISTS `warehousesite`;
CREATE TABLE `warehousesite`
(
    `id`         bigint(0) NOT NULL AUTO_INCREMENT,
    `sitename`   varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `orgid`      bigint(0) NULL DEFAULT NULL,
    `address1`   varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `address2`   varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `state`      varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `city`       varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `zip`        varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `phone`      varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `createdate` datetime(0) NULL DEFAULT NULL,
    `lastupdate` datetime(0) NULL DEFAULT NULL,
    `contact`    varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `selfid`     bigint(0) NULL DEFAULT NULL,
    `appkey`     varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `signature`  varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `mfyccid`    varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    `checked`    varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    index            idx1(sitename,orgid,`address1`,`address2`),
    index            idx2(`state`,city,`zip`,`phone`),
    index            idx3(`createdate`,lastupdate,`contact`,`selfid`,checked)
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

SET
FOREIGN_KEY_CHECKS = 1;
