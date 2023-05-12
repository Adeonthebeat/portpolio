package com.ade.portfolio.main.service;

import com.ade.portfolio.main.configuration.JasyptConfig;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import java.sql.Connection;
import java.sql.DriverManager;

@Slf4j
public class Practice {

    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testConnection() {
        try (Connection con =
                     DriverManager.getConnection(
                             "jdbc:oracle:thin:@여기에디비이름?TNS_ADMIN=여기에지갑경로",
                             "계정이름",
                             "계정비밀번호"
                     )) {
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static void main(String[] args) {
//        String password = "jdbc:oracle:thin:@orcl_high?TNS_ADMIN=/Users/leehyeok/codingTest/Wallet_orcl";
//
//        StandardPBEStringEncryptor jasypt = new StandardPBEStringEncryptor();
//        jasypt.setPassword(password);
//        jasypt.setAlgorithm("PBEWithMD5AndDES"); // 암호화 알고리즘
//
//        String encryptedText = jasypt.encrypt(password);
//        String decryptedText = jasypt.decrypt(encryptedText);
//
//        System.out.println("encryptedText = " + encryptedText);
//        System.out.println("decryptedText = " + decryptedText);
    }
}
