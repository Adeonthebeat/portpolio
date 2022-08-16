package com.ade.portfolio.main.service;

import com.ade.portfolio.main.mapper.MainMapper;
import com.ade.portfolio.main.model.MainVO;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import yahoofinance.YahooFinance;
import yahoofinance.quotes.fx.FxQuote;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MainService {

    @Autowired
    private MainMapper mainMapper;

    /**
     * 평가정보 - 총매수금액, 총평가금액, 환율, 수익률
     * @param param
     * @return
     */
    public Map<String, Object> selectTopInfo(Map<String, Object> param) {
        return mainMapper.selectTopInfo(param);
    }

    /**
     * 종목별 매수금액(차트)
     * @param param
     * @return
     */
    public List<Map<String, Object>> selectItemChart(Map<String, Object> param) {
        return mainMapper.selectItemChart(param);
    }

    public List<Map<String, Object>> selectBaseChart(Map<String, Object> param) {
        return mainMapper.selectBaseChart(param);
    }

    public List<Map<String, Object>> selectRatioChart(Map<String, Object> param) {
        return mainMapper.selectRatioChart(param);
    }

    public List<Map<String, Object>> selectFundRatioChart(Map<String, Object> param) {
        return mainMapper.selectFundRatioChart(param);
    }

    public List<Map<String, Object>> selectItemList(Map<String, Object> param) {
        return mainMapper.selectItemList(param);
    }

    /**
     * 거래정보 입력
     * @param param
     */
    public void PROC_TRIS_TO_TRIS(Map<String, Object> param) {

        int result = 0;
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("TR_DATE", MapUtils.getString(this.selectBusiDay(param), "BASE_DATE"));
        resultMap.put("STND_ITEM_C", MapUtils.getString(param, "STND_ITEM_C"));
        resultMap.put("TR_PRICE", param.get("TR_PRICE"));
        resultMap.put("TR_QUANTITY", param.get("TR_QUANTITY"));

        // 오늘자 거래정보
        List<Map<String, Object>> trisList = mainMapper.selectTrisList(resultMap);

        // 거래정보가 없다면.
        if(trisList.size() == 0) {
            result = mainMapper.PROC_TRIS_TO_TRIS(resultMap);
            param.put("BATCH_ID", "TR");
            if(result > 0){
                param.put("BATCH_STATUS", "01");
            } else {
                param.put("BATCH_STATUS", "02");
            }
            this.insertBatchInfo(param);
        }
    }

    public void PROC_FUND_TO_FUND(Map<String, Object> param) {

        int result = 0;
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("BASE_DATE", MapUtils.getString(this.selectBusiDay(param), "BASE_DATE"));
        resultMap.put("STND_ITEM_C", MapUtils.getString(param, "STND_ITEM_C"));
        resultMap.put("TOT_BUY_AMT", param.get("TOT_BUY_AMT"));
        resultMap.put("TOT_EST_AMT", param.get("TOT_EST_AMT"));

        log.info("resultMap : " + resultMap);

        result = mainMapper.PROC_FUND_TO_FUND(resultMap);

        param.put("BATCH_ID", "FU");
        param.put("BATCH_STATUS", "01");
        this.insertBatchInfo(param);
    }

    /**
     * 거래정보를 잔고화
     * @param param
     */
    public void PROC_TRIS_TO_BASE(Map<String, Object> param) {

        int result = 0;
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("BASE_DATE", MapUtils.getString(this.selectBusiDay(param), "BASE_DATE"));
        resultMap.put("STND_ITEM_C", MapUtils.getString(param, "STND_ITEM_C"));

        // 시세정보
        List<Map<String, Object>> getPriceList = mainMapper.selectPriceList(resultMap);

        // 시세정보가 있다면.
        if(getPriceList.size() > 0){
            // 거래정보를 잔고화
            result = mainMapper.PROC_TRIS_TO_BASE(resultMap);
        }
        param.put("BATCH_ID", "TB");
        param.put("BATCH_STATUS", "01");
        this.insertBatchInfo(param);
    }

    public Map<String, Object> selectBusiDay(Map<String, Object> param) {
        return mainMapper.selectBusiDay(param);
    }

    public Map<String, Object> selectBeforeBusiDay(Map<String, Object> param) {
        return mainMapper.selectBeforeBusiDay(param);
    }

    public List<Map<String, Object>> selectStndItemCList(Map<String, Object> param) {
        return mainMapper.selectStndItemCList(param);
    }

    public List<Map<String, Object>> selectFundCList(Map<String, Object> param) {
        return mainMapper.selectFundCList(param);
    }
    public MainVO selectExchRateList(MainVO vo) {
        return mainMapper.selectExchRateList(vo);
    }

    public List<MainVO> selectGetPriceItemList() {
        return mainMapper.selectGetPriceItemList();
    }


    public void insertBatchInfo(Map<String, Object> param){

        String BATCH_ID = MapUtils.getString(param, "BATCH_ID");
        String BATCH_STATUS = MapUtils.getString(param, "BATCH_STATUS");

        log.info("BATCH_ID      : " + BATCH_ID);
        log.info("BATCH_STATUS  : " + BATCH_STATUS);

        if(StringUtils.isNotBlank(BATCH_ID) && StringUtils.isNotBlank(BATCH_STATUS)){
            mainMapper.insertADBAIF(param);
        }

    }

    public void getPrice() throws Exception{
        int result = 0;
        Map<String, Object> param = Maps.newHashMap();

        List<MainVO> itemList = this.selectGetPriceItemList();
        String baseDate = MapUtils.getString(this.selectBusiDay(param), "BASE_DATE");

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
                this.insertBatchInfo(param);
            }
        }
    }

    public void getExchRate() throws Exception {

        int result = 0;
        Map<String, Object> param = Maps.newHashMap();
        FxQuote usdkrw = YahooFinance.getFx("USDKRW=X");
        FxQuote eurkrw = YahooFinance.getFx("EURKRW=X");

        MainVO vo = new MainVO();

        // 통화리스트
        List<MainVO> curCList = mainMapper.selectCurCList();

        // 기준일자
        String baseDate = MapUtils.getString(this.selectBusiDay(param), "BASE_DATE");

        for(int i = 0; i < curCList.size(); i++){

            // 환율
            if(StringUtils.equals(curCList.get(i).getCurC(), "USD")){
                vo.setBaseDate(baseDate);
                vo.setCurC(curCList.get(i).getCurC());  // 통화코드
                vo.setExchRate(usdkrw.getPrice());
            } else if(StringUtils.equals(curCList.get(i).getCurC(), "EUR")){
                vo.setBaseDate(baseDate);
                vo.setCurC(curCList.get(i).getCurC());  // 통화코드
                vo.setExchRate(eurkrw.getPrice());
            }
            MainVO list = this.selectExchRateList(vo);

            log.info("# 기준일자 : " + vo.getBaseDate());
            log.info("# 통화코드 : " + vo.getCurC());
            log.info("# 횐율    : " + vo.getExchRate());

            if(list == null){
                log.info("# insertADEXRT ");
                result = mainMapper.insertADEXRT(vo);

                param.put("BATCH_ID", "EX");
                param.put("BATCH_STATUS", "01");
                this.insertBatchInfo(param);
            }
        }

    }

    public void PROC_BASE_TO_ONE() {

        int result = 0;
        Map<String, Object> param = Maps.newHashMap();

        List<MainVO> itemList = this.selectGetPriceItemList();

        String BASE_DATE = MapUtils.getString(mainMapper.selectBusiDay(param), "BASE_DATE");

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
                this.insertBatchInfo(param);
            }
        }
    }

    public void PROC_BASE_TO_ESTM() {

        int result = 0;
        Map<String, Object> param = Maps.newHashMap();

        param.put("BASE_DATE", MapUtils.getString(this.selectBusiDay(param), "BASE_DATE"));

        // CHECK
        List<Map<String, Object>> adbaseCheck = mainMapper.selectADBASECheck(param);
        List<Map<String, Object>> adestmCheck = mainMapper.selectADESTMCheck(param);

        if(adbaseCheck.size() > 0 && adestmCheck.size() == 0){
            // INSERT
            log.info("# PROC_BASE_TO_ESTM ");
            result = mainMapper.PROC_BASE_TO_ESTM(param);

            param.put("BATCH_ID", "BE");
            param.put("BATCH_STATUS", "01");
            this.insertBatchInfo(param);
        }
    }

}
