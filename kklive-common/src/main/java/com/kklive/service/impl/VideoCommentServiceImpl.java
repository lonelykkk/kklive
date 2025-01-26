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

    @Resource
    private VideoCommentMapper<VideoComment, VideoCommentQuery> videoCommentMapper;

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Resource
    private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;

    @Override
    public List<VideoComment> findListByParam(VideoCommentQuery param) {
        if (param.getLoadChildren() != null && param.getLoadChildren()) {
            return this.videoCommentMapper.selectListWithChildren(param);
        }
        return this.videoCommentMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(VideoCommentQuery param) {
        return this.videoCommentMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<VideoComment> findListByPage(VideoCommentQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<VideoComment> list = this.findListByParam(param);
        PaginationResultVO<VideoComment> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
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
        VideoInfo videoInfo = videoInfoMapper.selectByVideoId(comment.getVideoId());
        if (videoInfo == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        //是否关闭评论
        if (videoInfo.getInteraction() != null && videoInfo.getInteraction().contains(Constants.ZERO.toString())) {
            throw new BusinessException("UP主已关闭评论区");
        }
        if (replyCommentId != null) {
            VideoComment replyComment = getVideoCommentByCommentId(replyCommentId);
            if (replyComment == null || !replyComment.getVideoId().equals(comment.getVideoId())) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            if (replyComment.getpCommentId() == 0) {
                comment.setpCommentId(replyComment.getCommentId());
            } else {
                comment.setpCommentId(replyComment.getpCommentId());
                comment.setReplyUserId(replyComment.getUserId());
            }
            UserInfo userInfo = userInfoMapper.selectByUserId(replyComment.getUserId());
            comment.setReplyNickName(userInfo.getNickName());
            comment.setReplyAvatar(userInfo.getAvatar());
        } else {
            comment.setpCommentId(0);
        }
        comment.setPostTime(new Date());
        comment.setVideoUserId(videoInfo.getUserId());
        this.videoCommentMapper.insert(comment);
        //增加评论数
        if (comment.getpCommentId() == 0) {
            this.videoInfoMapper.updateCountInfo(comment.getVideoId(), UserActionTypeEnum.VIDEO_COMMENT.getField(), 1);
        }
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