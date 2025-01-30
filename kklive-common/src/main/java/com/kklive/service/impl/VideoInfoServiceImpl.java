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
    private AppConfig appConfig;
    @Resource
    private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;
    @Resource
    private VideoInfoFileMapper<VideoInfoFile, VideoInfoFileQuery> videoInfoFileMapper;
    @Resource
    private VideoInfoFilePostMapper<VideoInfoFilePost, VideoInfoFilePostQuery> videoInfoFilePostMapper;
    @Resource
    private VideoDanmuMapper<VideoDanmu, VideoDanmuQuery> videoDanmuMapper;
    @Resource
    private VideoCommentMapper<VideoComment, VideoCommentQuery> videoCommentMapper;
    @Resource
    private VideoInfoPostMapper<VideoInfoPost, VideoInfoPostQuery> videoInfoPostMapper;
    @Resource
    private UserInfoService userInfoService;

    @Resource
    private RedisComponent redisComponent;

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
        return this.videoInfoMapper.selectByVideoId(videoId);
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
    @Transactional(rollbackFor = Exception.class)
    public void changeInteraction(String videoId, String userId, String interaction) {
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setInteraction(interaction);
        VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
        videoInfoQuery.setVideoId(videoId);
        videoInfoQuery.setUserId(userId);
        videoInfoMapper.updateByParam(videoInfo, videoInfoQuery);


        VideoInfoPost videoInfoPost = new VideoInfoPost();
        videoInfoPost.setInteraction(interaction);
        VideoInfoPostQuery videoInfoPostQuery = new VideoInfoPostQuery();
        videoInfoPostQuery.setVideoId(videoId);
        videoInfoPostQuery.setUserId(userId);
        videoInfoPostMapper.updateByParam(videoInfoPost, videoInfoPostQuery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVideo(String videoId, String userId) {
        VideoInfoPost videoInfoPost = this.videoInfoPostMapper.selectByVideoId(videoId);
        if (videoInfoPost == null || userId != null && !userId.equals(videoInfoPost.getUserId())) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }

        this.videoInfoMapper.deleteByVideoId(videoId);

        this.videoInfoPostMapper.deleteByVideoId(videoId);

        /**
         * 删除用户硬币
         */
        SysSettingDto sysSettingDto = redisComponent.getSysSettingDto();
        // TODO 减去用户硬币
        // userInfoService.updateCoinCountInfo(videoInfoPost.getUserId(), -sysSettingDto.getPostVideoCoinCount());
        // TODO 删除es信息

        // 通过异步线程池删除数据库中视频信息
        executorService.execute(()->{
            VideoInfoFileQuery videoInfoFileQuery = new VideoInfoFileQuery();
            videoInfoFileQuery.setVideoId(videoId);
            //查询分P
            List<VideoInfoFile> videoInfoFileList = this.videoInfoFileMapper.selectList(videoInfoFileQuery);

            //删除分P
            videoInfoFileMapper.deleteByParam(videoInfoFileQuery);

            VideoInfoFilePostQuery videoInfoFilePostQuery = new VideoInfoFilePostQuery();
            videoInfoFilePostQuery.setVideoId(videoId);
            videoInfoFilePostMapper.deleteByParam(videoInfoFilePostQuery);

            //删除弹幕
            VideoDanmuQuery videoDanmuQuery = new VideoDanmuQuery();
            videoDanmuQuery.setVideoId(videoId);
            videoDanmuMapper.deleteByParam(videoDanmuQuery);

            //删除评论
            VideoCommentQuery videoCommentQuery = new VideoCommentQuery();
            videoCommentQuery.setVideoId(videoId);
            videoCommentMapper.deleteByParam(videoCommentQuery);

            //删除文件
            for (VideoInfoFile item : videoInfoFileList) {
                try {
                    FileUtils.deleteDirectory(new File(appConfig.getProjectFolder() + item.getFilePath()));
                } catch (IOException e) {
                    log.error("删除文件失败，文件路径:{}", item.getFilePath());
                }
            }

        });
    }
}