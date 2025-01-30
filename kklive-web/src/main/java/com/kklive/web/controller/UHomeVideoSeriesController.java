package com.kklive.web.controller;

import com.kklive.entity.dto.TokenUserInfoDto;
import com.kklive.entity.enums.ResponseCodeEnum;
import com.kklive.entity.po.UserVideoSeries;
import com.kklive.entity.po.UserVideoSeriesVideo;
import com.kklive.entity.po.VideoInfo;
import com.kklive.entity.query.UserVideoSeriesQuery;
import com.kklive.entity.query.UserVideoSeriesVideoQuery;
import com.kklive.entity.query.VideoInfoQuery;
import com.kklive.entity.vo.ResponseVO;
import com.kklive.entity.vo.UserVideoSeriesDetailVO;
import com.kklive.exception.BusinessException;
import com.kklive.service.UserVideoSeriesService;
import com.kklive.service.UserVideoSeriesVideoService;
import com.kklive.service.VideoInfoService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
@RequestMapping("/uhome/series")
public class UHomeVideoSeriesController extends ABaseController {

    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private UserVideoSeriesService userVideoSeriesService;

    @Resource
    private UserVideoSeriesVideoService userVideoSeriesVideoService;

    @RequestMapping("/loadVideoSeries")
    public ResponseVO loadVideoSeries(@NotEmpty String userId) {
        List<UserVideoSeries> videoSeries = userVideoSeriesService.getUserAllSeries(userId);
        return getSuccessResponseVO(videoSeries);
    }

    /**
     * 保存系列
     *
     * @param seriesId
     * @param seriesName
     * @param seriesDescription
     * @param videoIds
     * @return
     */
    @RequestMapping("/saveVideoSeries")
    public ResponseVO saveVideoSeries(Integer seriesId,
                                      @NotEmpty @Size(max = 100) String seriesName,
                                      @Size(max = 200) String seriesDescription,
                                      String videoIds) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        UserVideoSeries videoSeries = new UserVideoSeries();
        videoSeries.setUserId(tokenUserInfoDto.getUserId());
        videoSeries.setSeriesId(seriesId);
        videoSeries.setSeriesName(seriesName);
        videoSeries.setSeriesDescription(seriesDescription);
        userVideoSeriesService.saveUserVideoSeries(videoSeries, videoIds);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadAllVideo")
    public ResponseVO loadAllVideo(Integer seriesId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        VideoInfoQuery infoQuery = new VideoInfoQuery();
        if (seriesId != null) {
            UserVideoSeriesVideoQuery videoSeriesVideoQuery = new UserVideoSeriesVideoQuery();
            videoSeriesVideoQuery.setSeriesId(seriesId);
            videoSeriesVideoQuery.setUserId(tokenUserInfoDto.getUserId());
            List<UserVideoSeriesVideo> seriesVideoList = userVideoSeriesVideoService.findListByParam(videoSeriesVideoQuery);
            List<String> videoList = seriesVideoList.stream().map(item -> item.getVideoId()).collect(Collectors.toList());
            infoQuery.setExcludeVideoIdArray(videoList.toArray(new String[videoList.size()]));
        }
        infoQuery.setUserId(tokenUserInfoDto.getUserId());
        List<VideoInfo> videoInfoList = videoInfoService.findListByParam(infoQuery);
        return getSuccessResponseVO(videoInfoList);
    }

    @RequestMapping("/getVideoSeriesDetail")
    public ResponseVO getVideoSeriesDetail(@NotNull Integer seriesId) {
        UserVideoSeries videoSeries = userVideoSeriesService.getUserVideoSeriesBySeriesId(seriesId);
        if (videoSeries == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }

        UserVideoSeriesVideoQuery videoSeriesVideoQuery = new UserVideoSeriesVideoQuery();
        videoSeriesVideoQuery.setOrderBy("sort asc");
        videoSeriesVideoQuery.setQueryVideoInfo(true);
        videoSeriesVideoQuery.setSeriesId(seriesId);
        List<UserVideoSeriesVideo> seriesVideoList = userVideoSeriesVideoService.findListByParam(videoSeriesVideoQuery);
        return getSuccessResponseVO(new UserVideoSeriesDetailVO(videoSeries, seriesVideoList));
    }

    /**
     * 保存系列视频
     *
     * @param seriesId
     * @param videoIds
     * @return
     */
    @RequestMapping("/saveSeriesVideo")
    public ResponseVO saveSeriesVideo(@NotNull Integer seriesId, @NotEmpty String videoIds) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        userVideoSeriesService.saveSeriesVideo(tokenUserInfoDto.getUserId(), seriesId, videoIds);
        return getSuccessResponseVO(null);
    }

    /**
     * 删除视频
     *
     * @param seriesId
     * @param videoId
     * @return
     */
    @RequestMapping("/delSeriesVideo")
    public ResponseVO delSeriesVideo(@NotNull Integer seriesId, @NotEmpty String videoId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        userVideoSeriesService.delSeriesVideo(tokenUserInfoDto.getUserId(), seriesId, videoId);
        return getSuccessResponseVO(null);
    }

    /**
     * 删除系列
     *
     * @param seriesId
     * @return
     */
    @RequestMapping("/delVideoSeries")
    public ResponseVO delVideoSeries(@NotNull Integer seriesId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        userVideoSeriesService.delVideoSeries(tokenUserInfoDto.getUserId(), seriesId);
        return getSuccessResponseVO(null);
    }


    /**
     * 系列排序
     *
     * @param seriesIds
     * @return
     */
    @RequestMapping("/changeVideoSeriesSort")
    public ResponseVO changeVideoSeriesSort(@NotEmpty String seriesIds) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        userVideoSeriesService.changeVideoSeriesSort(tokenUserInfoDto.getUserId(), seriesIds);
        return getSuccessResponseVO(null);
    }


    @RequestMapping("/loadVideoSeriesWithVideo")
    public ResponseVO loadVideoSeriesWithVideo(@NotEmpty String userId) {
        UserVideoSeriesQuery seriesQuery = new UserVideoSeriesQuery();
        seriesQuery.setUserId(userId);
        seriesQuery.setOrderBy("sort asc");
        List<UserVideoSeries> videoSeries = userVideoSeriesService.findListWithVideoList(seriesQuery);
        return getSuccessResponseVO(videoSeries);
    }
}
