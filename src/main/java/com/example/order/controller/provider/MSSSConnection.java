package com.example.order.controller.provider;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
@Data
@Configuration
public class MSSSConnection {
    static String SqlServerdbUrl;
    @Value("${msssdburl}")
    private void setSqlServerdbUrl(String url){
        SqlServerdbUrl = url;
    }

    static String SqlServeruserName;
    @Value("${msssusername}")
    private void setSqlServeruserName(String userName){
        SqlServeruserName = userName;
    }

    static String SqlServeruserPsw;
    @Value("${msssuserpsw}")
    private void setSqlServeruserPsw(String psw){
        SqlServeruserPsw = psw;
    }

    public static Connection getConn() {
        String dirverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String dbUrl = SqlServerdbUrl;
        String userName = SqlServeruserName;
        String userPsw = SqlServeruserPsw;

        Connection conn = null;
        try {
            Class.forName(dirverName); //classLoader,加载对应驱动
            conn = (Connection) DriverManager.getConnection(dbUrl, userName, userPsw);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
