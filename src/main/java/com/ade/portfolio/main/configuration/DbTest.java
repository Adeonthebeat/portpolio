package com.ade.portfolio.main.configuration;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;

@Slf4j
public class DbTest {

    private final static String dbUrl = "jdbc:oracle:thin:@orcl_high?TNS_ADMIN=./src/main/resources/Wallet_orcl";
    private final static String user = "admin";
    private final static String password = "Dkepqk780200";

    public static void main(String[] args) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void Connection() {
        try {
            Connection conn = DriverManager.getConnection(dbUrl, user, password);
            log.info("# Connection : " +conn);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
