package com.ade.portfolio.main.mapper;

import com.ade.portfolio.main.model.MainVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface MainMapper {

    /**
     * 총매수금액, 총평가금액, 환율, 수익률
     * @param param
     * @return
     */
    public Map<String, Object> selectTopInfo(Map<String, Object> param);

    /**
     * 종목별 매수금액(차트)
     * @param param
     * @return
     */
    public List<Map<String, Object>> selectItemChart(Map<String, Object> param);

    /**
     * 총 잔고금액(차트)
     * @param param
     * @return
     */
    public List<Map<String, Object>> selectBaseChart(Map<String, Object> param);

    /**
     * 종목비율(차트)
     * @param param
     * @return
     */
    public List<Map<String, Object>> selectRatioChart(Map<String, Object> param);

    /**
     * 펀드비율(차트)
     * @param param
     * @return
     */
    public List<Map<String, Object>> selectFundRatioChart(Map<String, Object> param);

    /**
     * 종목리스트
     * @param param
     * @return
     */
    public List<Map<String, Object>> selectItemList(Map<String, Object> param);

    /**
     * 거래내역 등록
     * @param param
     * @return
     */
    int PROC_TRIS_TO_TRIS(Map<String, Object> param);

    /**
     * 펀드거래내역 입력
     * @param param
     * @return
     */
    int PROC_FUND_TO_FUND(Map<String, Object> param);

    /**
     * 거래정보 잔고화
     * @param param
     * @return
     */
    int PROC_TRIS_TO_BASE(Map<String, Object> param);

    /**
     * 거래없는 잔고화
     * @param param
     * @return
     */
    int PROC_BASE_TO_ONE(Map<String, Object> param);

    /**
     * 잔고를 비율화
     * @param param
     * @return
     */
    int PROC_BASE_TO_RATIO(Map<String, Object> param);

    /**
     * 펀드잔고 비율화
     * @param param
     * @return
     */
    int PROC_FUND_TO_RATIO(Map<String, Object> param);

    /**
     * 잔고를 평가
     * @param param
     * @return
     */
    int PROC_BASE_TO_ESTM(Map<String, Object> param);

    public Map<String, Object> selectBusiDay(Map<String, Object> param);
    public Map<String, Object> selectBeforeBusiDay(Map<String, Object> param);
    public List<Map<String, Object>> selectPriceList(Map<String, Object> param);
    public List<Map<String, Object>> selectTrisList(Map<String, Object> param);
    public List<Map<String, Object>> selectStndItemCList(Map<String, Object> param);
    public List<Map<String, Object>> selectFundCList(Map<String, Object> param);
    public MainVO selectExchRateList(MainVO vo);
    public MainVO selectADPRIF(MainVO vo);
    public List<MainVO> selectCurCList();
    public List<MainVO> selectGetPriceItemList();
    public List<Map<String, Object>> selectADBASECheck(Map<String, Object> param);
    public List<Map<String, Object>> selectADESTMCheck(Map<String, Object> param);
    public List<Map<String, Object>> selectCommonCheck(Map<String, Object> param);
    public List<Map<String, Object>> selectADBSRTCheck(Map<String, Object> param);
    public List<Map<String, Object>> selectFundCheck(Map<String, Object> param);
    public Map<String, Object> selectGetBatchId(Map<String, Object> param);

    int insertADEXRT(MainVO vo);

    int insertADPRIF(MainVO vo);

    int insertADBAIF(Map<String, Object> param);

}
