package com.kklive.admin.controller;

import com.kklive.annotation.RecordUserMessage;
import com.kklive.entity.enums.MessageTypeEnum;
import com.kklive.entity.po.VideoInfoFilePost;
import com.kklive.entity.query.VideoInfoFilePostQuery;
import com.kklive.entity.query.VideoInfoPostQuery;
import com.kklive.entity.vo.PaginationResultVO;
import com.kklive.entity.vo.ResponseVO;
import com.kklive.service.VideoInfoFilePostService;
import com.kklive.service.VideoInfoPostService;
import com.kklive.service.VideoInfoService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2025/1/21 21:01
 * @Version V1.0
 */
@RestController
@Validated
@RequestMapping("/videoInfo")
public class VideoInfoController extends ABaseController{

    @Resource
    private VideoInfoPostService videoInfoPostService;

    @Resource
    private VideoInfoFilePostService videoInfoFilePostService;

    @Resource
    private VideoInfoService videoInfoService;

    @RequestMapping("/loadVideoList")
    public ResponseVO loadVideoList(VideoInfoPostQuery videoInfoPostQuery) {
        videoInfoPostQuery.setOrderBy("last_update_time desc");
        videoInfoPostQuery.setQueryCountInfo(true);
        videoInfoPostQuery.setQueryUserInfo(true);
        PaginationResultVO resultVO = videoInfoPostService.findListByPage(videoInfoPostQuery);
        return getSuccessResponseVO(resultVO);
    }

    @RequestMapping("/auditVideo")
    @RecordUserMessage(messageType = MessageTypeEnum.SYS)
    public ResponseVO auditVideo(@NotEmpty String videoId, @NotNull Integer status, String reason) {
        videoInfoPostService.auditVideo(videoId, status, reason);
        return getSuccessResponseVO(null);
    }

    /**
     * 推荐视频
     * @param videoId
     * @return
     */
    @RequestMapping("/recommendVideo")
    public ResponseVO recommendVideo(@NotEmpty String videoId) {
        videoInfoPostService.recommendVideo(videoId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/deleteVideo")
    public ResponseVO deleteVideo(@NotEmpty String videoId) {
        videoInfoService.deleteVideo(videoId, null);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadVideoPList")
    public ResponseVO loadVideoPList(@NotEmpty String videoId) {
        VideoInfoFilePostQuery postQuery = new VideoInfoFilePostQuery();
        postQuery.setOrderBy("file_index asc");
        postQuery.setVideoId(videoId);
        List<VideoInfoFilePost> videoInfoFilePostsList = videoInfoFilePostService.findListByParam(postQuery);
        return getSuccessResponseVO(videoInfoFilePostsList);
    }
}
