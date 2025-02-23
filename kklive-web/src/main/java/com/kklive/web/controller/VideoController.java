package com.kklive.web.controller;

import com.kklive.component.EsSearchComponent;
import com.kklive.component.RedisComponent;
import com.kklive.entity.constants.Constants;
import com.kklive.entity.dto.TokenUserInfoDto;
import com.kklive.entity.enums.*;
import com.kklive.entity.po.UserAction;
import com.kklive.entity.po.VideoInfo;
import com.kklive.entity.po.VideoInfoFile;
import com.kklive.entity.query.UserActionQuery;
import com.kklive.entity.query.VideoInfoFileQuery;
import com.kklive.entity.query.VideoInfoQuery;
import com.kklive.entity.vo.PaginationResultVO;
import com.kklive.entity.vo.ResponseVO;
import com.kklive.entity.vo.VideoInfoResultVo;
import com.kklive.entity.vo.VideoInfoVo;
import com.kklive.exception.BusinessException;
import com.kklive.service.UserActionService;
import com.kklive.service.VideoInfoFileService;
import com.kklive.service.VideoInfoService;
import com.kklive.utils.CopyTools;
import com.kklive.web.annotation.GlobalInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2025/1/22 16:05
 * @Version V1.0
 */
@RestController
@Validated
@RequestMapping("/video")
@Slf4j
public class VideoController extends ABaseController {
    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private VideoInfoFileService videoInfoFileService;
    @Resource
    private UserActionService userActionService;

    @Resource
    private RedisComponent redisComponent;
    @Resource
    private EsSearchComponent esSearchComponent;

    @RequestMapping("/loadRecommendVideo")
    @GlobalInterceptor
    public ResponseVO loadRecommendVideo() {
        VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
        videoInfoQuery.setQueryUserInfo(true);
        videoInfoQuery.setOrderBy("create_time desc");
        videoInfoQuery.setRecommendType(VideoRecommendTypeEnum.RECOMMEND.getType());
        List<VideoInfo> recommendVideoList = videoInfoService.findListByParam(videoInfoQuery);
        return getSuccessResponseVO(recommendVideoList);
    }

    /**
     * 加载视频
     * @param pCategoryId
     * @param categoryId
     * @param pageNo
     * @return
     */
    @RequestMapping("/loadVideo")
    @GlobalInterceptor
    public ResponseVO postVideo(Integer pCategoryId, Integer categoryId, Integer pageNo) {
        VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
        videoInfoQuery.setCategoryId(categoryId);
        videoInfoQuery.setpCategoryId(pCategoryId);
        videoInfoQuery.setPageNo(pageNo);
        videoInfoQuery.setQueryUserInfo(true);
        videoInfoQuery.setOrderBy("create_time desc");
        if (categoryId == null && pCategoryId == null) {
            videoInfoQuery.setRecommendType(VideoRecommendTypeEnum.NO_RECOMMEND.getType());
        }
        PaginationResultVO resultVO = videoInfoService.findListByPage(videoInfoQuery);
        return getSuccessResponseVO(resultVO);
    }

    /**
     * 获取视频分级列表
     * @param videoId
     * @return
     */
    @RequestMapping("/loadVideoPList")
    @GlobalInterceptor
    public ResponseVO loadVideoPList(@NotEmpty String videoId) {
        VideoInfoFileQuery videoInfoQuery = new VideoInfoFileQuery();
        videoInfoQuery.setVideoId(videoId);
        videoInfoQuery.setOrderBy("file_index asc");
        List<VideoInfoFile> fileList = videoInfoFileService.findListByParam(videoInfoQuery);
        return getSuccessResponseVO(fileList);
    }
    @RequestMapping("/getVideoInfo")
    @GlobalInterceptor
    public ResponseVO getVideoInfo(@NotEmpty String videoId) {
        VideoInfo videoInfo = videoInfoService.getVideoInfoByVideoId(videoId);
        if (null == videoInfo) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
        TokenUserInfoDto userInfoDto = getTokenUserInfoDto();

        List<UserAction> userActionList = new ArrayList<>();
        if (userInfoDto != null) {
            UserActionQuery actionQuery = new UserActionQuery();
            actionQuery.setVideoId(videoId);
            actionQuery.setUserId(userInfoDto.getUserId());
            actionQuery.setActionTypeArray(new Integer[]{UserActionTypeEnum.VIDEO_LIKE.getType(), UserActionTypeEnum.VIDEO_COLLECT.getType(),
                    UserActionTypeEnum.VIDEO_COIN.getType(),});
            userActionList = userActionService.findListByParam(actionQuery);
        }

        // 获取用户行为、点赞、投币、收藏
        VideoInfoResultVo resultVo = new VideoInfoResultVo();
        resultVo.setVideoInfo(CopyTools.copy(videoInfo, VideoInfoVo.class));
        resultVo.setUserActionList(userActionList);
        return getSuccessResponseVO(resultVo);
    }

    @RequestMapping("/reportVideoPlayOnline")
    @GlobalInterceptor
    public ResponseVO reportVideoPlayOnline(@NotEmpty String fileId, String deviceId) {
        Integer count = redisComponent.reportVideoPlayOnline(fileId, deviceId);
        return getSuccessResponseVO(count);
    }

    @RequestMapping("/search")
    @GlobalInterceptor
    public ResponseVO search(@NotEmpty String keyword, Integer orderType, Integer pageNo) {
        // 记录搜索热词
        redisComponent.addKeywordCount(keyword);
        PaginationResultVO resultVO = esSearchComponent.search(true, keyword, orderType, pageNo, PageSize.SIZE30.getSize());
        return getSuccessResponseVO(resultVO);
    }

    @RequestMapping("/getVideoRecommend")
    @GlobalInterceptor
    public ResponseVO getVideoRecommend(@NotEmpty String keyword, @NotEmpty String videoId) {
        List<VideoInfo> videoInfoList = esSearchComponent.search(false, keyword, SearchOrderTypeEnum.VIDEO_PLAY.getType(), 1, PageSize.SIZE10.getSize()).getList();
        videoInfoList = videoInfoList.stream().filter(item -> !item.getVideoId().equals(videoId)).collect(Collectors.toList());
        return getSuccessResponseVO(videoInfoList);
    }
    @RequestMapping("/getSearchKeywordTop")
    @GlobalInterceptor
    public ResponseVO getSearchKeywordTop() {
        List<String> keywordList = redisComponent.getKeywordTop(Constants.LENGTH_10);
        return getSuccessResponseVO(keywordList);
    }

    @RequestMapping("/loadHotVideoList")
    public ResponseVO loadHotVideoList(Integer pageNo) {
        VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
        videoInfoQuery.setPageNo(pageNo);
        videoInfoQuery.setQueryUserInfo(true);
        videoInfoQuery.setOrderBy("play_count desc");
        videoInfoQuery.setLastPlayHour(Constants.HOUR_24);
        PaginationResultVO resultVO = videoInfoService.findListByPage(videoInfoQuery);
        return getSuccessResponseVO(resultVO);
    }
}
