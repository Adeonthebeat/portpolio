package com.ade.portfolio.main.controller;

import com.ade.portfolio.main.model.MainVO;
import com.ade.portfolio.main.service.MainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@Slf4j
public class WebController {

    @Autowired
    private MainService mainService;

    @GetMapping("/index")
    public ModelAndView index() {
        MainVO mainVO = new MainVO();
        MainVO vo = mainService.selectEstm(mainVO);

        log.info("# vadfoajdf " + vo.getBaseDate());
        ModelAndView mv = new ModelAndView();
        mv.setViewName("index");

        return mv;
    }



}
