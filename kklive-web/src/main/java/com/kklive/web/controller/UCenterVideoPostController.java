package com.kklive.web.controller;

import com.kklive.entity.dto.TokenUserInfoDto;
import com.kklive.entity.enums.VideoStatusEnum;
import com.kklive.entity.po.VideoInfoFilePost;
import com.kklive.entity.po.VideoInfoPost;
import com.kklive.entity.query.VideoInfoPostQuery;
import com.kklive.entity.vo.PaginationResultVO;
import com.kklive.entity.vo.ResponseVO;
import com.kklive.entity.vo.VideoStatusCountInfoVO;
import com.kklive.service.VideoInfoFilePostService;
import com.kklive.service.VideoInfoPostService;
import com.kklive.service.VideoInfoService;
import com.kklive.utils.JsonUtils;
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
}
