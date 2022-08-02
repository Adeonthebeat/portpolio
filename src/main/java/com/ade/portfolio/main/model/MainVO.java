package com.ade.portfolio.main.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter @Setter
public class MainVO {

    private String baseDate;
    private BigDecimal totBuyAmt;
    private BigDecimal totestAmt;
    private BigDecimal exrtRate;
    private BigDecimal yield;

}
