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

    //@Scheduled(fixedDelay = 10000)
    public void getExchRate() throws Exception {

        Map<String, Object> param = Maps.newHashMap();
        FxQuote usdkrw = YahooFinance.getFx("USDKRW=X");
        FxQuote eurkrw = YahooFinance.getFx("EURKRW=X");

        MainVO vo = new MainVO();
        List<MainVO> curCList = mainMapper.selectCurCList();

        vo.setBaseDate(MapUtils.getString(mainService.selectBusiDay(param), "BASE_DATE")); // 기준일자

        for(int i = 0; i < curCList.size(); i++){

            vo.setCurC(curCList.get(i).getCurC());  // 통화코드
            // 환율
            if(StringUtils.equals(curCList.get(i).getCurC(), "USD")){
                vo.setExchRate(usdkrw.getPrice());
            } else if(StringUtils.equals(curCList.get(i).getCurC(), "EUR")){
                vo.setExchRate(eurkrw.getPrice());
            }
            MainVO list = mainService.selectExchRateList(vo);

            log.info("# 기준일자 : " + vo.getBaseDate());
            log.info("# 통화코드 : " + vo.getCurC());
            log.info("# 횐율    : " + vo.getExchRate());

            if(list == null){
                log.info("# INSERT ");
                mainMapper.insertADEXRT(vo);
            }
        }

    }

}
