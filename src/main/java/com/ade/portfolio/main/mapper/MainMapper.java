package com.ade.portfolio.main.mapper;

import com.ade.portfolio.main.model.MainVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface MainMapper {

    public MainVO selectEstm(MainVO vo);

}
