package com.ade.portfolio.main.service;

import com.ade.portfolio.main.mapper.MainMapper;
import com.ade.portfolio.main.model.MainVO;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

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

}
