package com.ade.portfolio.main.service;

import com.ade.portfolio.main.mapper.MainMapper;
import com.ade.portfolio.main.model.MainVO;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class MainService {

    @Autowired
    private MainMapper mainMapper;

    public MainVO selectEstm(MainVO vo) {

        Map<String, Object> resultMap = Maps.newHashMap();

        MainVO main = mainMapper.selectEstm(vo);

        log.info("@ADFASFDS :"+main.getBaseDate());


        return main;
    }

}
