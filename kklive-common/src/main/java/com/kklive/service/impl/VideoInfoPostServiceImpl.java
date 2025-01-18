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

    @Resource
    private RedisComponent redisComponent;
    @Resource
    private VideoInfoPostMapper<VideoInfoPost, VideoInfoPostQuery> videoInfoPostMapper;
    @Resource
    private VideoInfoFilePostMapper<VideoInfoFilePost, VideoInfoFilePostQuery> videoInfoFilePostMapper;


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
    @Transactional(rollbackFor = Exception.class)
    public void saveVideoInfo(VideoInfoPost videoInfoPost, List<VideoInfoFilePost> uploadFileList) {
        if (uploadFileList.size() > redisComponent.getSysSettingDto().getVideoPCount()) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (!StringTools.isEmpty(videoInfoPost.getVideoId())) {
            VideoInfoPost videoInfoPostDb = this.videoInfoPostMapper.selectByVideoId(videoInfoPost.getVideoId());
            if (videoInfoPostDb == null) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            if (ArrayUtils.contains(new Integer[]{VideoStatusEnum.STATUS0.getStatus(), VideoStatusEnum.STATUS2.getStatus()}, videoInfoPostDb.getStatus())) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
        }

        Date curDate = new Date();
        String videoId = videoInfoPost.getVideoId();
        List<VideoInfoFilePost> deleteFileList = new ArrayList();
        List<VideoInfoFilePost> addFileList = uploadFileList;

        if (StringTools.isEmpty(videoId)) {
            videoId = StringTools.getRandomString(Constants.LENGTH_10);
            videoInfoPost.setVideoId(videoId);
            videoInfoPost.setCreateTime(curDate);
            videoInfoPost.setLastUpdateTime(curDate);
            videoInfoPost.setStatus(VideoStatusEnum.STATUS0.getStatus());
            this.videoInfoPostMapper.insert(videoInfoPost);
        } else {
            VideoInfoFilePostQuery fileQuery = new VideoInfoFilePostQuery();
            fileQuery.setVideoId(videoId);
            fileQuery.setUserId(videoInfoPost.getUserId());
            List<VideoInfoFilePost> dbInfoFileList = this.videoInfoFilePostMapper.selectList(fileQuery);
            Map<String, VideoInfoFilePost> uploadFileMap = uploadFileList.stream().
                    collect(Collectors.toMap(item -> item.getUploadId(), Function.identity(), (data1, data2) -> data2));

            //删除的文件 -> 数据库中有，uploadFileList没有
            Boolean updateFileName = false;
            for (VideoInfoFilePost fileInfo : dbInfoFileList) {
                VideoInfoFilePost updateFile = uploadFileMap.get(fileInfo.getUploadId());
                if (updateFile == null) {
                    deleteFileList.add(fileInfo);
                } else if (!updateFile.getFileName().equals(fileInfo.getFileName())) {
                    updateFileName = true;
                }
            }
            //新增的文件  没有fileId就是新增的文件
            addFileList = uploadFileList.stream().filter(item -> item.getFileId() == null).collect(Collectors.toList());
            videoInfoPost.setLastUpdateTime(curDate);

            //判断视频信息是否有更改
            Boolean changeVideoInfo = this.changeVideoInfo(videoInfoPost);
            if (!addFileList.isEmpty()) {
                videoInfoPost.setStatus(VideoStatusEnum.STATUS0.getStatus());
            } else if (changeVideoInfo || updateFileName) {
                videoInfoPost.setStatus(VideoStatusEnum.STATUS2.getStatus());
            }
            this.videoInfoPostMapper.updateByVideoId(videoInfoPost, videoInfoPost.getVideoId());
        }
    }

    private boolean changeVideoInfo(VideoInfoPost videoInfoPost) {
        VideoInfoPost dbInfo = this.videoInfoPostMapper.selectByVideoId(videoInfoPost.getVideoId());
        //标题，封面，标签，简介
        if (!videoInfoPost.getVideoCover().equals(dbInfo.getVideoCover()) || !videoInfoPost.getVideoName().equals(dbInfo.getVideoName()) || !videoInfoPost.getTags().equals(dbInfo.getTags()) || !videoInfoPost.getIntroduction().equals(
                dbInfo.getIntroduction())) {
            return true;
        }
        return false;
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