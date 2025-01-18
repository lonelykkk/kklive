package com.kklive.service.impl;

import com.kklive.component.RedisComponent;
import com.kklive.entity.config.AppConfig;
import com.kklive.entity.constants.Constants;
import com.kklive.entity.dto.SysSettingDto;
import com.kklive.entity.dto.UploadingFileDto;
import com.kklive.entity.enums.*;
import com.kklive.entity.po.VideoInfo;
import com.kklive.entity.po.VideoInfoFile;
import com.kklive.entity.po.VideoInfoFilePost;
import com.kklive.entity.po.VideoInfoPost;
import com.kklive.entity.query.*;
import com.kklive.entity.vo.PaginationResultVO;
import com.kklive.exception.BusinessException;
import com.kklive.mappers.*;
import com.kklive.service.UserMessageService;
import com.kklive.service.VideoInfoPostService;
import com.kklive.utils.CopyTools;
import com.kklive.utils.FFmpegUtils;
import com.kklive.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 视频信息 业务接口实现
 */
@Service("videoInfoPostService")
@Slf4j
public class VideoInfoPostServiceImpl implements VideoInfoPostService {


    @Override
    public List<VideoInfoPost> findListByParam(VideoInfoPostQuery param) {
        return null;
    }

    @Override
    public Integer findCountByParam(VideoInfoPostQuery param) {
        return null;
    }

    @Override
    public PaginationResultVO<VideoInfoPost> findListByPage(VideoInfoPostQuery param) {
        return null;
    }

    @Override
    public Integer add(VideoInfoPost bean) {
        return null;
    }

    @Override
    public Integer addBatch(List<VideoInfoPost> listBean) {
        return null;
    }

    @Override
    public Integer addOrUpdateBatch(List<VideoInfoPost> listBean) {
        return null;
    }

    @Override
    public Integer updateByParam(VideoInfoPost bean, VideoInfoPostQuery param) {
        return null;
    }

    @Override
    public Integer deleteByParam(VideoInfoPostQuery param) {
        return null;
    }

    @Override
    public VideoInfoPost getVideoInfoPostByVideoId(String videoId) {
        return null;
    }

    @Override
    public Integer updateVideoInfoPostByVideoId(VideoInfoPost bean, String videoId) {
        return null;
    }

    @Override
    public Integer deleteVideoInfoPostByVideoId(String videoId) {
        return null;
    }

    @Override
    public void saveVideoInfo(VideoInfoPost videoInfoPost, List<VideoInfoFilePost> uploadFileList) {

    }

    @Override
    public void transferVideoFile(VideoInfoFilePost videoInfoFilePost) throws IOException {

    }

    @Override
    public void auditVideo(String videoId, Integer status, String reason) {

    }

    @Override
    public void recommendVideo(String videoId) {

    }
}