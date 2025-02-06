package com.kklive.admin.controller;

import com.kklive.entity.enums.StatisticsTypeEnum;
import com.kklive.entity.po.StatisticsInfo;
import com.kklive.entity.query.StatisticsInfoQuery;
import com.kklive.entity.query.UserInfoQuery;
import com.kklive.entity.vo.ResponseVO;
import com.kklive.service.StatisticsInfoService;
import com.kklive.service.UserInfoService;
import com.kklive.utils.DateUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/index")
@Validated
public class IndexController extends ABaseController {
    @Resource
    private StatisticsInfoService statisticsInfoService;

    @Resource
    private UserInfoService userInfoService;

    @RequestMapping("/getActualTimeStatisticsInfo")
    public ResponseVO getActualTimeStatisticsInfo() {
        String preDate = DateUtil.getBeforeDayDate(1);
        StatisticsInfoQuery param = new StatisticsInfoQuery();
        param.setStatisticsDate(preDate);
        List<StatisticsInfo> preDayData = statisticsInfoService.findListTotalInfoByParam(param);
        //查询用户总数，替换类型为粉丝的数量
        Integer userCount = userInfoService.findCountByParam(new UserInfoQuery());
        preDayData.forEach(item -> {
            if (StatisticsTypeEnum.FANS.getType().equals(item.getDataType())) {
                item.setStatisticsCount(userCount);
            }
        });
        Map<Integer, Integer> preDayDataMap = preDayData.stream().collect(Collectors.toMap(StatisticsInfo::getDataType, StatisticsInfo::getStatisticsCount, (item1,
                                                                                                                                                             item2) -> item2));
        Map<String, Integer> totalCountInfo = statisticsInfoService.getStatisticsInfoActualTime(null);
        Map<String, Object> result = new HashMap<>();
        result.put("preDayData", preDayDataMap);
        result.put("totalCountInfo", totalCountInfo);
        return getSuccessResponseVO(result);
    }

    @RequestMapping("/getWeekStatisticsInfo")
    public ResponseVO getWeekStatisticsInfo(Integer dataType) {
        List<String> dateList = DateUtil.getBeforeDates(7);

        List<StatisticsInfo> statisticsInfoList = new ArrayList<>();
        StatisticsInfoQuery param = new StatisticsInfoQuery();
        param.setDataType(dataType);
        param.setStatisticsDateStart(dateList.get(0));
        param.setStatisticsDateEnd(dateList.get(dateList.size() - 1));
        param.setOrderBy("statistics_date asc");

        if (!StatisticsTypeEnum.FANS.getType().equals(dataType)) {
            statisticsInfoList = statisticsInfoService.findListTotalInfoByParam(param);
        } else {
            statisticsInfoList = statisticsInfoService.findUserCountTotalInfoByParam(param);
        }

        Map<String, StatisticsInfo> dataMap = statisticsInfoList.stream().collect(Collectors.toMap(item -> item.getStatisticsDate(), Function.identity(), (data1,
                                                                                                                                                           data2) -> data2));
        List<StatisticsInfo> resultDataList = new ArrayList<>();
        for (String date : dateList) {
            StatisticsInfo dataItem = dataMap.get(date);
            if (dataItem == null) {
                dataItem = new StatisticsInfo();
                dataItem.setStatisticsCount(0);
                dataItem.setStatisticsDate(date);
            }
            resultDataList.add(dataItem);
        }
        return getSuccessResponseVO(resultDataList);
    }
}
