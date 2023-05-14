package com.ade.portfolio;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@Slf4j
public class DBTest {

    final static String DB_URL= "jdbc:oracle:thin:@orcl_high?TNS_ADMIN=./src/main/resources/Wallet_orcl";
    final static String DB_USER = "admin";
    final static String DB_PASSWORD = "Dkepqk780200";

    @Test
    public static void main(String[] args) throws Exception{
        Connection conn;
        Statement stmt;
        ResultSet rs;

        try {
            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("DB 접속 " + conn);
            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
