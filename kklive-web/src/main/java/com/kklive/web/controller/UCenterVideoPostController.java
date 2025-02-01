package com.kklive.web.controller;

import com.kklive.entity.dto.TokenUserInfoDto;
import com.kklive.entity.enums.ResponseCodeEnum;
import com.kklive.entity.enums.VideoStatusEnum;
import com.kklive.entity.po.VideoInfoFilePost;
import com.kklive.entity.po.VideoInfoPost;
import com.kklive.entity.query.VideoInfoFilePostQuery;
import com.kklive.entity.query.VideoInfoPostQuery;
import com.kklive.entity.vo.PaginationResultVO;
import com.kklive.entity.vo.ResponseVO;
import com.kklive.entity.vo.VideoPostEditInfoVo;
import com.kklive.entity.vo.VideoStatusCountInfoVO;
import com.kklive.exception.BusinessException;
import com.kklive.service.VideoInfoFilePostService;
import com.kklive.service.VideoInfoPostService;
import com.kklive.service.VideoInfoService;
import com.kklive.utils.JsonUtils;
import com.kklive.web.annotation.GlobalInterceptor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2025/1/18 11:26
 * @Version V1.0
 */
@RestController
@Validated
@RequestMapping("/ucenter")
public class UCenterVideoPostController extends ABaseController {
    @Resource
    private VideoInfoPostService videoInfoPostService;
    @Resource
    private VideoInfoFilePostService videoInfoFilePostService;

    @Resource
    private VideoInfoService videoInfoService;

    @RequestMapping("/postVideo")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO postVideo(String videoId, @NotEmpty String videoCover, @NotEmpty @Size(max = 100) String videoName, @NotNull Integer pCategoryId,
                                Integer categoryId, @NotNull Integer postType, @NotEmpty @Size(max = 300) String tags, @Size(max = 2000) String introduction,
                                @Size(max = 3) String interaction, @NotEmpty String uploadFileList) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        List<VideoInfoFilePost> fileInfoList = JsonUtils.convertJsonArray2List(uploadFileList, VideoInfoFilePost.class);

        VideoInfoPost videoInfo = new VideoInfoPost();
        videoInfo.setVideoId(videoId);
        videoInfo.setVideoName(videoName);
        videoInfo.setVideoCover(videoCover);
        videoInfo.setpCategoryId(pCategoryId);
        videoInfo.setCategoryId(categoryId);
        videoInfo.setPostType(postType);
        videoInfo.setTags(tags);
        videoInfo.setIntroduction(introduction);
        videoInfo.setInteraction(interaction);

        videoInfo.setUserId(tokenUserInfoDto.getUserId());

        videoInfoPostService.saveVideoInfo(videoInfo, fileInfoList);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadVideoList")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO loadVideoList(Integer status, Integer pageNo, String videoNameFuzzy) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        VideoInfoPostQuery videoInfoQuery = new VideoInfoPostQuery();
        videoInfoQuery.setUserId(tokenUserInfoDto.getUserId());
        videoInfoQuery.setOrderBy("v.create_time desc");
        videoInfoQuery.setPageNo(pageNo);
        if (status != null) {
            if (status == -1) {
                videoInfoQuery.setExcludeStatusArray(new Integer[]{VideoStatusEnum.STATUS3.getStatus(), VideoStatusEnum.STATUS4.getStatus()});
            } else {
                videoInfoQuery.setStatus(status);
            }
        }
        videoInfoQuery.setVideoNameFuzzy(videoNameFuzzy);
        videoInfoQuery.setQueryCountInfo(true);
        PaginationResultVO resultVO = videoInfoPostService.findListByPage(videoInfoQuery);
        return getSuccessResponseVO(resultVO);
    }

    @RequestMapping("/getVideoCountInfo")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO getVideoCountInfo() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        VideoInfoPostQuery videoInfoQuery = new VideoInfoPostQuery();
        videoInfoQuery.setUserId(tokenUserInfoDto.getUserId());
        videoInfoQuery.setStatus(VideoStatusEnum.STATUS3.getStatus());

        Integer auditPassCount = videoInfoPostService.findCountByParam(videoInfoQuery);
        videoInfoQuery.setStatus(VideoStatusEnum.STATUS4.getStatus());
        Integer auditFailCount = videoInfoPostService.findCountByParam(videoInfoQuery);

        videoInfoQuery.setStatus(null);
        videoInfoQuery.setExcludeStatusArray(new Integer[]{VideoStatusEnum.STATUS3.getStatus(), VideoStatusEnum.STATUS4.getStatus()});
        Integer inProgress = videoInfoPostService.findCountByParam(videoInfoQuery);

        VideoStatusCountInfoVO countInfo = new VideoStatusCountInfoVO();
        countInfo.setAuditPassCount(auditPassCount);
        countInfo.setAuditFailCount(auditFailCount);
        countInfo.setInProgress(inProgress);
        return getSuccessResponseVO(countInfo);
    }

    @RequestMapping("/getVideoByVideoId")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO getVideoByVideoId(@NotEmpty String videoId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        VideoInfoPost videoInfoPost = this.videoInfoPostService.getVideoInfoPostByVideoId(videoId);
        if (videoInfoPost == null || !videoInfoPost.getUserId().equals(tokenUserInfoDto.getUserId())) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
        VideoInfoFilePostQuery videoInfoFilePostQuery = new VideoInfoFilePostQuery();
        videoInfoFilePostQuery.setVideoId(videoId);
        videoInfoFilePostQuery.setOrderBy("file_index asc");
        List<VideoInfoFilePost> videoInfoFilePostList = this.videoInfoFilePostService.findListByParam(videoInfoFilePostQuery);
        VideoPostEditInfoVo vo = new VideoPostEditInfoVo();
        vo.setVideoInfo(videoInfoPost);
        vo.setVideoInfoFileList(videoInfoFilePostList);
        return getSuccessResponseVO(vo);
    }

    @RequestMapping("/saveVideoInteraction")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO saveVideoInteraction(@NotEmpty String videoId, String interaction) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        videoInfoService.changeInteraction(videoId, tokenUserInfoDto.getUserId(), interaction);
        return getSuccessResponseVO(null);
    }

    /**
     * 删除视频
     * @param videoId
     * @return
     */
    @RequestMapping("/deleteVideo")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO deleteVideo(@NotEmpty String videoId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        videoInfoService.deleteVideo(videoId, tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(null);
    }
}
