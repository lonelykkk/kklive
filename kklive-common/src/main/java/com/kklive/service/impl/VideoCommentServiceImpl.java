package com.kklive.service.impl;

import com.kklive.entity.constants.Constants;
import com.kklive.entity.enums.CommentTopTypeEnum;
import com.kklive.entity.enums.PageSize;
import com.kklive.entity.enums.ResponseCodeEnum;
import com.kklive.entity.enums.UserActionTypeEnum;
import com.kklive.entity.po.UserInfo;
import com.kklive.entity.po.VideoComment;
import com.kklive.entity.po.VideoInfo;
import com.kklive.entity.query.SimplePage;
import com.kklive.entity.query.UserInfoQuery;
import com.kklive.entity.query.VideoCommentQuery;
import com.kklive.entity.query.VideoInfoQuery;
import com.kklive.entity.vo.PaginationResultVO;
import com.kklive.exception.BusinessException;
import com.kklive.mappers.UserInfoMapper;
import com.kklive.mappers.VideoCommentMapper;
import com.kklive.mappers.VideoInfoMapper;
import com.kklive.service.UserMessageService;
import com.kklive.service.VideoCommentService;
import com.kklive.utils.StringTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


/**
 * 评论 业务接口实现
 */
@Service("videoCommentService")
public class VideoCommentServiceImpl implements VideoCommentService {


    @Override
    public List<VideoComment> findListByParam(VideoCommentQuery param) {
        return null;
    }

    @Override
    public Integer findCountByParam(VideoCommentQuery param) {
        return null;
    }

    @Override
    public PaginationResultVO<VideoComment> findListByPage(VideoCommentQuery param) {
        return null;
    }

    @Override
    public Integer add(VideoComment bean) {
        return null;
    }

    @Override
    public Integer addBatch(List<VideoComment> listBean) {
        return null;
    }

    @Override
    public Integer addOrUpdateBatch(List<VideoComment> listBean) {
        return null;
    }

    @Override
    public Integer updateByParam(VideoComment bean, VideoCommentQuery param) {
        return null;
    }

    @Override
    public Integer deleteByParam(VideoCommentQuery param) {
        return null;
    }

    @Override
    public VideoComment getVideoCommentByCommentId(Integer commentId) {
        return null;
    }

    @Override
    public Integer updateVideoCommentByCommentId(VideoComment bean, Integer commentId) {
        return null;
    }

    @Override
    public Integer deleteVideoCommentByCommentId(Integer commentId) {
        return null;
    }

    @Override
    public void postComment(VideoComment comment, Integer replyCommentId) {

    }

    @Override
    public void deleteComment(Integer commentId, String userId) {

    }

    @Override
    public void topComment(Integer commentId, String userId) {

    }

    @Override
    public void cancelTopComment(Integer commentId, String userId) {

    }
}