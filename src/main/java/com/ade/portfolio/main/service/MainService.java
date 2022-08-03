package com.ade.portfolio.main.service;

import com.ade.portfolio.main.mapper.MainMapper;
import com.ade.portfolio.main.model.MainVO;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
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

    public Map<String, Object> selectRatioChart(Map<String, Object> param) {
        return mainMapper.selectRatioChart(param);
    }

    public Map<String, Object> selectFundRatioChart(Map<String, Object> param) {
        return mainMapper.selectFundRatioChart(param);
    }

    public List<Map<String, Object>> selectItemList(Map<String, Object> param) {
        return mainMapper.selectItemList(param);
    }

}
