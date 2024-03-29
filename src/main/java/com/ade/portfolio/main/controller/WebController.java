package com.ade.portfolio.main.controller;

import com.ade.portfolio.main.service.MainService;
import com.ade.portfolio.main.service.ScheduleService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class WebController {

    @Autowired
    private MainService mainService;
    private ScheduleService scheduleService;

    @GetMapping("/")
    public ModelAndView home() {
        Map<String, Object> param = Maps.newHashMap();

        Map<String, Object> TopInfo = mainService.selectTopInfo(param);
        List<Map<String, Object>> itemChart = mainService.selectItemChart(param);
        List<Map<String, Object>> baseChart = mainService.selectBaseChart(param);
        List<Map<String, Object>> ratioChart = mainService.selectRatioChart(param);
        List<Map<String, Object>> fundRatioChart = mainService.selectFundRatioChart(param);
        List<Map<String, Object>> itemList = mainService.selectItemList(param);
        List<Map<String, Object>> stndItemCList = mainService.selectStndItemCList(param);
        List<Map<String, Object>> fundCList = mainService.selectFundCList(param);

        ModelAndView mv = new ModelAndView();
        mv.addObject("TopInfo", TopInfo);
        mv.addObject("itemChart", itemChart);
        mv.addObject("baseChart", baseChart);
        mv.addObject("ratioChart", ratioChart);
        mv.addObject("fundRatioChart", fundRatioChart);
        mv.addObject("itemList", itemList);
        mv.addObject("stndItemCList", stndItemCList);
        mv.addObject("fundCList", fundCList);
        mv.setViewName("index");
        return mv;
    }

    @PostMapping("/tranSaction")
    public RedirectView procTrisToTris(@RequestBody Map<String, Object> params) {

        mainService.PROC_TRIS_TO_TRIS(params);
        mainService.PROC_TRIS_TO_BASE(params);

        return new RedirectView("/");
    }

    @PostMapping("/fund")
    public RedirectView procFundToFund(@RequestBody Map<String, Object> params) {

        mainService.PROC_FUND_TO_FUND(params);

        return new RedirectView("/");
    }

    @PostMapping("/getPrice")
    public RedirectView getPrice(@RequestBody Map<String, Object> params) throws Exception{

        mainService.getPrice();
        mainService.getExchRate();

        return new RedirectView("/");
    }

    @PostMapping("/getBase")
    public RedirectView getBase(@RequestBody Map<String, Object> params) throws Exception{

        mainService.PROC_BASE_TO_ONE();
        mainService.PROC_BASE_TO_ESTM();

        return new RedirectView("/");
    }

}
