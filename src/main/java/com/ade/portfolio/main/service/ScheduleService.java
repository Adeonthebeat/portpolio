package com.ade.portfolio.main.service;

import com.ade.portfolio.main.mapper.MainMapper;
import com.ade.portfolio.main.model.MainVO;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.query2v8.HistQuotesQuery2V8Request;
import yahoofinance.quotes.fx.FxQuote;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ScheduleService {

    @Autowired
    private MainMapper mainMapper;

    @Autowired
    private MainService mainService;

    @Scheduled(cron = "* 50 9 * * MON-FRI")
    public void PROC_DELETE_ALL() {

        int result = 0;
        Map<String, Object> param = Maps.newHashMap();

        String BASE_DATE = MapUtils.getString(mainService.selectBeforeBusiDay(param), "BASE_DATE");

        param.put("BASE_DATE", BASE_DATE);

        // INSERT
        log.info("# PROC_DELETE_ALL ");
        result = mainMapper.PROC_DELETE_ALL(param);

        param.put("BATCH_ID", "DA");
        param.put("BATCH_STATUS", "01");
        mainService.insertBatchInfo(param);
    }

//    @Scheduled(cron = "* 0 10 * * MON-FRI")
    public void getExchRate() throws Exception {

        int result = 0;
        Map<String, Object> param = Maps.newHashMap();

        FxQuote USDKRW = new FxQuote("USDKRW=X");

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
                vo.setExchRate(USDKRW.getPrice());
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

    @Scheduled(cron = "* 5 10 * * MON-FRI")
    public void getPrice() throws Exception {

        int result = 0;
        Map<String, Object> param = Maps.newHashMap();

        List<MainVO> itemList = mainService.selectGetPriceItemList();
        String baseDate = MapUtils.getString(mainService.selectBeforeBusiDay(param), "BASE_DATE");

        for(MainVO vo : itemList){
            vo.setBaseDate(baseDate);

            HistQuotesQuery2V8Request stockInfo = new HistQuotesQuery2V8Request(vo.getStndItemC());

            JSONObject jsonObject = new JSONObject(stockInfo.getJson());
            jsonObject = new JSONObject(jsonObject.get("chart").toString());
            String resultJson = jsonObject.get("result").toString();
            JSONArray jsonArray = new JSONArray(resultJson);

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = (JSONObject) jsonArray.get(i);
                JSONObject parseJson = (JSONObject) json.get("meta");

                // 가격
                Double price = (Double) parseJson.get("regularMarketPrice");
                vo.setPrice(BigDecimal.valueOf(price));
            }
            log.info("------------------------------------------");
            log.info("# 기준일자 : " + vo.getBaseDate());
            log.info("# 종목코드 : " + vo.getStndItemC());
            log.info("# 기준가격 : " + vo.getPrice());
            log.info("# 통화코드 : " + vo.getCurC());
            log.info("------------------------------------------");

            result = mainMapper.insertADPRIF(vo);

            param.put("BATCH_ID", "PR");
            param.put("BATCH_STATUS", "01");
            mainService.insertBatchInfo(param);
        }
    }

    @Scheduled(cron = "* 10 10 * * MON-FRI")
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

//    @Scheduled(cron = "* 15 10 * * MON-FRI")
    public void PROC_PRICE_TO_FUND() {

        int result = 0;
        Map<String, Object> param = Maps.newHashMap();

        String BASE_DATE = MapUtils.getString(mainMapper.selectBeforeBusiDay(param), "BASE_DATE");

        param.put("BASE_DATE", BASE_DATE);
        
        log.info("# PROC_PRICE_TO_FUND ");
        result = mainMapper.PROC_PRICE_TO_FUND(param);

        param.put("BATCH_ID", "FO");
        param.put("BATCH_STATUS", "01");
        mainService.insertBatchInfo(param);

    }

    @Scheduled(cron = "* 20 10 * * MON-FRI")
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

//    @Scheduled(cron = "* 25 10 * * MON-FRI")
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
    
//    @Scheduled(cron = "* 30 10 * * MON-FRI")
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

//    @Scheduled(cron = "* 30 15 * * MON-FRI")
    public void getPriceFund() throws Exception {

        int result = 0;
        Map<String, Object> param = Maps.newHashMap();

        List<MainVO> itemList = mainService.selectGetPriceFundList();
        String baseDate = MapUtils.getString(mainService.selectBusiDay(param), "BASE_DATE");

        for(MainVO vo : itemList){
            vo.setBaseDate(baseDate);
            vo.setPrice(YahooFinance.get(vo.getStndItemC()).getQuote().getPrice());

            log.info("------------------------------------------");
            log.info("# 기준일자 : " + vo.getBaseDate());
            log.info("# 종목코드 : " + vo.getStndItemC());
            log.info("# 기준가격 : " + vo.getPrice());
            log.info("# 통화코드 : " + vo.getCurC());
            log.info("------------------------------------------");

            log.info("# insertADPRIF ");
            result = mainMapper.insertADPRIF(vo);

            param.put("BATCH_ID", "PR");
            param.put("BATCH_STATUS", "01");
            mainService.insertBatchInfo(param);
        }
    }

    //@Scheduled(cron = "* 40 20 * * *")
    public void insertBM() throws Exception {

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.DATE, -365);

        String[] symbols = new String[] { "^KS11", "VTI"};
        Map<String, Stock> stocks = YahooFinance.get(symbols, true);

        for(Map.Entry<String, Stock> ele : stocks.entrySet()) {
            List<HistoricalQuote> stockHis = ele.getValue().getHistory(from, to, Interval.WEEKLY);

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

            for(HistoricalQuote historicalQuote : stockHis) {

                Map<String, Object> param = Maps.newHashMap();

                String baseDate = df.format(historicalQuote.getDate().getTime());
                String BM_CODE = historicalQuote.getSymbol().replace("^", "");
                String BM_NAME = "";

                param.put("BM_CODE", BM_CODE);
                param.put("BASE_WEEK", baseDate);
                if(StringUtils.equalsIgnoreCase(BM_CODE, "KS11")) {
                    BM_NAME = "코스피";
                } else if(StringUtils.equalsIgnoreCase(BM_CODE, "VTI")) {
                    BM_NAME = "미국";
                }
                param.put("BM_NM", BM_NAME);
                param.put("CLOSE_PRICE", historicalQuote.getAdjClose());
                param.put("VOLUME", historicalQuote.getVolume());

                mainMapper.insertBM(param);

            }
        }
    }

//    @Scheduled(cron = "* 23 22 * * *")
    public void insertIndustryPrice() throws Exception {

        Map<String, Object> param = Maps.newHashMap();

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.DATE, -365);

        List<Map<String, Object>> industryInfo = mainMapper.selectIndustryInfo(param);

        for(Map<String, Object> info : industryInfo) {

            String STND_ITEM_C = MapUtils.getString(info, "STND_ITEM_C");
            String ITEM_NAME = MapUtils.getString(info, "ITEM_NAME");

            Stock stock = YahooFinance.get(STND_ITEM_C);

            List<HistoricalQuote> stockHis = stock.getHistory(from, to, Interval.WEEKLY);

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

            for(HistoricalQuote historicalQuote : stockHis) {

                Map<String, Object> result = Maps.newHashMap();

                String baseDate = df.format(historicalQuote.getDate().getTime());

                result.put("BASE_WEEK", baseDate);
                result.put("STND_ITEM_C", STND_ITEM_C);
                result.put("ITEM_NAME", ITEM_NAME);
                result.put("CLOSE_PRICE", historicalQuote.getAdjClose());

                mainMapper.insertIndustryPrice(result);

            }

        }

    }


}
