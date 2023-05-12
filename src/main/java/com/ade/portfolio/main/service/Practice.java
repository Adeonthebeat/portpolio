package com.ade.portfolio.main.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.util.Calendar;

@Service
@Slf4j
public class Practice {

    @Autowired
    private MainService mainService;


    public void solution() throws Exception{
        mainService.getPrice();
    }

    public static void main(String[] args) throws Exception {
        Practice main = new Practice();
        main.solution();
    }

}
