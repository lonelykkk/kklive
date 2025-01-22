package com.kklive.service.impl;
import com.kklive.component.RedisComponent;
import com.kklive.entity.config.AppConfig;
import com.kklive.entity.dto.SysSettingDto;
import com.kklive.entity.enums.PageSize;
import com.kklive.entity.enums.ResponseCodeEnum;
import com.kklive.entity.enums.UserActionTypeEnum;
import com.kklive.entity.po.*;
import com.kklive.entity.query.*;
import com.kklive.entity.vo.PaginationResultVO;
import com.kklive.exception.BusinessException;
import com.kklive.mappers.*;
import com.kklive.service.UserInfoService;
import com.kklive.service.VideoInfoService;
import com.kklive.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 视频信息 业务接口实现
 */
@Service("videoInfoService")
@Slf4j
public class VideoInfoServiceImpl implements VideoInfoService {

    private static ExecutorService executorService = Executors.newFixedThreadPool(10);
    @Resource
    private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;

    @Override
    public List<VideoInfo> findListByParam(VideoInfoQuery param) {
        return this.videoInfoMapper.selectList(param);
    }

    @Override
    public Integer findCountByParam(VideoInfoQuery param) {
        return this.videoInfoMapper.selectCount(param);
    }

    @Override
    public PaginationResultVO<VideoInfo> findListByPage(VideoInfoQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<VideoInfo> list = this.findListByParam(param);
        PaginationResultVO<VideoInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    @Override
    public Integer add(VideoInfo bean) {
        return null;
    }

    @Override
    public Integer addBatch(List<VideoInfo> listBean) {
        return null;
    }

    @Override
    public Integer addOrUpdateBatch(List<VideoInfo> listBean) {
        return null;
    }

    @Override
    public Integer updateByParam(VideoInfo bean, VideoInfoQuery param) {
        return null;
    }

    @Override
    public Integer deleteByParam(VideoInfoQuery param) {
        return null;
    }

    @Override
    public VideoInfo getVideoInfoByVideoId(String videoId) {
        return null;
    }

    @Override
    public Integer updateVideoInfoByVideoId(VideoInfo bean, String videoId) {
        return null;
    }

    @Override
    public Integer deleteVideoInfoByVideoId(String videoId) {
        return null;
    }

    @Override
    public void addReadCount(String videoId) {

    }

    @Override
    public void changeInteraction(String videoId, String userId, String interaction) {

    }

    @Override
    public void deleteVideo(String videoId, String userId) {

    }
}