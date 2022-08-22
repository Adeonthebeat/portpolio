package com.ade.portfolio.main.service;

import com.ade.portfolio.main.mapper.MainMapper;
import com.ade.portfolio.main.model.MainVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.fx.FxQuote;

import javax.jws.Oneway;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ScheduleService {

    @Autowired
    private MainMapper mainMapper;

    @Autowired
    private MainService mainService;

    @Scheduled(cron = "* 0 10 * * MON-FRI")
    public void getExchRate() throws Exception {

        int result = 0;
        Map<String, Object> param = Maps.newHashMap();
        FxQuote usdkrw = YahooFinance.getFx("USDKRW=X");
        FxQuote eurkrw = YahooFinance.getFx("EURKRW=X");

        MainVO vo = new MainVO();

        // 통화리스트
        List<MainVO> curCList = mainMapper.selectCurCList();

        // 기준일자
        String baseDate = MapUtils.getString(mainService.selectBusiDay(param), "BASE_DATE");

        for(int i = 0; i < curCList.size(); i++){

            // 환율
            if(StringUtils.equals(curCList.get(i).getCurC(), "USD")){
                vo.setBaseDate(baseDate);
                vo.setCurC(curCList.get(i).getCurC());
                vo.setExchRate(usdkrw.getPrice());
            } else if(StringUtils.equals(curCList.get(i).getCurC(), "EUR")){
                vo.setBaseDate(baseDate);
                vo.setCurC(curCList.get(i).getCurC());
                vo.setExchRate(eurkrw.getPrice());
            }
            MainVO list = mainService.selectExchRateList(vo);

            log.info("========================================");
            log.info("# 기준일자 : " + vo.getBaseDate());
            log.info("# 통화코드 : " + vo.getCurC());
            log.info("# 횐율    : " + vo.getExchRate());
            log.info("========================================");

            if(list == null){
                log.info("# insertADEXRT ");
                result = mainMapper.insertADEXRT(vo);

                param.put("BATCH_ID", "EX");
                param.put("BATCH_STATUS", "01");
                mainService.insertBatchInfo(param);

            }
        }

    }

    @Scheduled(cron = "* 5 10 * * TUE-SAT")
    public void getPrice() throws Exception {

        int result = 0;
        Map<String, Object> param = Maps.newHashMap();

        List<MainVO> itemList = mainService.selectGetPriceItemList();
        String baseDate = MapUtils.getString(mainService.selectBeforeBusiDay(param), "BASE_DATE");

        for(MainVO vo : itemList){
            vo.setBaseDate(baseDate);
            vo.setPrice(YahooFinance.get(vo.getStndItemC()).getQuote().getPrice());
            vo.setCurC(YahooFinance.get(vo.getStndItemC()).getCurrency());

            log.info("------------------------------------------");
            log.info("# 기준일자 : " + vo.getBaseDate());
            log.info("# 종목코드 : " + vo.getStndItemC());
            log.info("# 기준가격 : " + vo.getPrice());
            log.info("# 통화코드 : " + vo.getCurC());
            log.info("------------------------------------------");

            MainVO list = mainMapper.selectADPRIF(vo);
            if(list == null){
                log.info("# insertADPRIF ");
                result = mainMapper.insertADPRIF(vo);

                param.put("BATCH_ID", "PR");
                param.put("BATCH_STATUS", "01");
                mainService.insertBatchInfo(param);
            }
        }
    }

    @Scheduled(cron = "* 15 10 * * TUE-SAT")
    public void PROC_BASE_TO_ONE() {

        int result = 0;
        Map<String, Object> param = Maps.newHashMap();

        List<MainVO> itemList = mainService.selectGetPriceItemList();

        String BASE_DATE = MapUtils.getString(mainMapper.selectBeforeBusiDay(param), "BASE_DATE");

        for(int i = 0; i < itemList.size(); i++){

            // SETTING
            param.put("BASE_DATE", BASE_DATE);
            param.put("STND_ITEM_C", itemList.get(i).getStndItemC());

            log.info("# param : {} ", param);

            // CHECK
            List<Map<String, Object>> adbaseCheck = mainMapper.selectADBASECheck(param);
            List<Map<String, Object>> commonCheck = mainMapper.selectCommonCheck(param);

            if(adbaseCheck.size() == 0 && commonCheck.size() == itemList.size()){

                // INSERT
                log.info("# PROC_BASE_TO_ONE ");
                result = mainMapper.PROC_BASE_TO_ONE(param);

                param.put("BATCH_ID", "BO");
                param.put("BATCH_STATUS", "01");
                mainService.insertBatchInfo(param);
            }
        }
    }

    @Scheduled(cron = "* 20 10 * * TUE-SAT")
    public void PROC_BASE_TO_ESTM() {

        int result = 0;
        Map<String, Object> param = Maps.newHashMap();

        param.put("BASE_DATE", MapUtils.getString(mainService.selectBeforeBusiDay(param), "BASE_DATE"));

        // CHECK
        List<Map<String, Object>> adbaseCheck = mainMapper.selectADBASECheck(param);
        List<Map<String, Object>> adestmCheck = mainMapper.selectADESTMCheck(param);

        if(adbaseCheck.size() > 0 && adestmCheck.size() == 0){
            // INSERT
            log.info("# PROC_BASE_TO_ESTM ");
            result = mainMapper.PROC_BASE_TO_ESTM(param);

            param.put("BATCH_ID", "BE");
            param.put("BATCH_STATUS", "01");
            mainService.insertBatchInfo(param);
        }
    }

    @Scheduled(cron = "* 30 10 * * MON-FRI")
    public void PROC_BASE_TO_RATIO() {

        int result = 0;
        Map<String, Object> param = Maps.newHashMap();

        String BASE_DATE = MapUtils.getString(mainService.selectBeforeBusiDay(param), "BASE_DATE");

        param.put("BASE_DATE", BASE_DATE);

        log.info("param : {} ", param);

        // CHECK
        List<Map<String, Object>> adbaseCheck = mainMapper.selectADBASECheck(param);

        if(adbaseCheck.size() > 0){
            // INSERT
            log.info("# PROC_BASE_TO_RATIO ");
            result = mainMapper.PROC_BASE_TO_RATIO(param);

            param.put("BATCH_ID", "BR");
            param.put("BATCH_STATUS", "01");
            mainService.insertBatchInfo(param);
        }

    }

    @Scheduled(cron = "* 35 10 * * MON-FRI")
    public void PROC_FUND_TO_RATIO() {

        int result = 0;
        Map<String, Object> param = Maps.newHashMap();

        String BASE_DATE = MapUtils.getString(mainService.selectBeforeBusiDay(param), "BASE_DATE");

        param.put("BASE_DATE", BASE_DATE);

        log.info("param : {} ", param);
        // CHECK
        List<Map<String, Object>> fundCheck = mainMapper.selectFundCheck(param);

        if(fundCheck.size() > 0){
            // INSERT
            log.info("# PROC_FUND_TO_RATIO ");
            result = mainMapper.PROC_FUND_TO_RATIO(param);

            param.put("BATCH_ID", "FR");
            param.put("BATCH_STATUS", "01");
            mainService.insertBatchInfo(param);
        }
    }

    @Scheduled(cron = "* 10 10 * * MON-FRI")
    public void PROC_DELETE_ALL() {

        int result = 0;
        Map<String, Object> param = Maps.newHashMap();

        String BASE_DATE = MapUtils.getString(mainService.selectBeforeBusiDay(param), "BASE_DATE");

        param.put("BASE_DATE", BASE_DATE);

        log.info("param : {} ", param);

        // INSERT
        log.info("# PROC_DELETE_ALL ");
        result = mainMapper.PROC_DELETE_ALL(param);

        param.put("BATCH_ID", "DA");
        param.put("BATCH_STATUS", "01");
        mainService.insertBatchInfo(param);
    }

}
