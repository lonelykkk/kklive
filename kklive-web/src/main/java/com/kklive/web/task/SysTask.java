package com.kklive.web.task;

import com.kklive.entity.config.AppConfig;
import com.kklive.entity.constants.Constants;
import com.kklive.entity.enums.DateTimePatternEnum;
import com.kklive.service.StatisticsInfoService;
import com.kklive.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

@Component
@Slf4j
public class SysTask {

    @Resource
    private StatisticsInfoService statisticsInfoService;

    @Resource
    private AppConfig appConfig;

    @Scheduled(cron = "0 0 0 * * ?")
    public void statisticsData() {
        statisticsInfoService.statisticsData();
    }

    @Scheduled(cron = "0 */1 * * * ?")//0 0 3 * * ?
    public void delTempFile() {
        String tempFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_FOLDER_TEMP;
        File folder = new File(tempFolderName);
        File[] listFile = folder.listFiles();
	if (listFile == null) {
            return;
        }
        String twodaysAgo = DateUtil.format(DateUtil.getDayAgo(2), DateTimePatternEnum.YYYYMMDD.getPattern()).toLowerCase();
        Integer dayInt = Integer.parseInt(twodaysAgo);
        for (File file : listFile) {
            Integer fileDate = Integer.parseInt(file.getName());
            if (fileDate <= dayInt) {
                try {
                    FileUtils.deleteDirectory(file);
                } catch (IOException e) {
                    log.info("删除临时文件失败", e);
                }
            }
        }
    }
}
