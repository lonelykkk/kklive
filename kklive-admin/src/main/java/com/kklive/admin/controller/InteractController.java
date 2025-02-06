package com.kklive.admin.controller;

import com.kklive.entity.query.VideoCommentQuery;
import com.kklive.entity.query.VideoDanmuQuery;
import com.kklive.entity.vo.PaginationResultVO;
import com.kklive.entity.vo.ResponseVO;
import com.kklive.service.VideoCommentService;
import com.kklive.service.VideoDanmuService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

/**
 * 互动管理
 */
@RestController
@RequestMapping("/interact")
@Validated
public class InteractController extends ABaseController {
    @Resource
    private VideoCommentService videoCommentService;

    @Resource
    private VideoDanmuService videoDanmuService;


    @RequestMapping("/loadDanmu")
    public ResponseVO loadDanmu(Integer pageNo, String videoNameFuzzy) {
        VideoDanmuQuery danmuQuery = new VideoDanmuQuery();
        danmuQuery.setOrderBy("danmu_id desc");
        danmuQuery.setPageNo(pageNo);
        danmuQuery.setQueryVideoInfo(true);
        danmuQuery.setVideoNameFuzzy(videoNameFuzzy);
        PaginationResultVO resultVO = videoDanmuService.findListByPage(danmuQuery);
        return getSuccessResponseVO(resultVO);
    }


    @RequestMapping("/delDanmu")
    public ResponseVO delDanmu(@NotNull Integer danmuId) {
        videoDanmuService.deleteDanmu(null, danmuId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadComment")
    public ResponseVO loadComment(Integer pageNo, String videoNameFuzzy) {
        VideoCommentQuery commentQuery = new VideoCommentQuery();
        commentQuery.setOrderBy("comment_id desc");
        commentQuery.setPageNo(pageNo);
        commentQuery.setQueryVideoInfo(true);
        commentQuery.setVideoNameFuzzy(videoNameFuzzy);
        PaginationResultVO resultVO = videoCommentService.findListByPage(commentQuery);
        return getSuccessResponseVO(resultVO);
    }

    @RequestMapping("/delComment")
    public ResponseVO delComment(@NotNull Integer commentId) {
        videoCommentService.deleteComment(commentId, null);
        return getSuccessResponseVO(null);
    }
}
