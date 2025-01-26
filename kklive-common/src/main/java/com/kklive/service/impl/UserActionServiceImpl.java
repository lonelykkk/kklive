package com.kklive.service.impl;

import com.kklive.entity.enums.PageSize;
import com.kklive.entity.enums.ResponseCodeEnum;
import com.kklive.entity.enums.SearchOrderTypeEnum;
import com.kklive.entity.enums.UserActionTypeEnum;
import com.kklive.entity.po.UserAction;
import com.kklive.entity.po.VideoComment;
import com.kklive.entity.po.VideoInfo;
import com.kklive.entity.query.SimplePage;
import com.kklive.entity.query.UserActionQuery;
import com.kklive.entity.query.VideoCommentQuery;
import com.kklive.entity.query.VideoInfoQuery;
import com.kklive.entity.vo.PaginationResultVO;
import com.kklive.exception.BusinessException;
import com.kklive.mappers.UserActionMapper;
import com.kklive.mappers.UserInfoMapper;
import com.kklive.mappers.VideoCommentMapper;
import com.kklive.mappers.VideoInfoMapper;
import com.kklive.service.UserActionService;
import com.kklive.service.UserMessageService;
import com.kklive.utils.StringTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


/**
 * 用户行为 点赞、评论 业务接口实现
 */
@Service("userActionService")
public class UserActionServiceImpl implements UserActionService {
    @Resource
    private UserActionMapper<UserAction, UserActionQuery> userActionMapper;

    @Resource
    private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;

    @Resource
    private VideoCommentMapper<VideoComment, VideoCommentQuery> videoCommentMapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public List<UserAction> findListByParam(UserActionQuery param) {
        return this.userActionMapper.selectList(param);
    }

    @Override
    public Integer findCountByParam(UserActionQuery param) {
        return null;
    }

    @Override
    public PaginationResultVO<UserAction> findListByPage(UserActionQuery param) {
        return null;
    }

    @Override
    public Integer add(UserAction bean) {
        return null;
    }

    @Override
    public Integer addBatch(List<UserAction> listBean) {
        return null;
    }

    @Override
    public Integer addOrUpdateBatch(List<UserAction> listBean) {
        return null;
    }

    @Override
    public Integer updateByParam(UserAction bean, UserActionQuery param) {
        return null;
    }

    @Override
    public Integer deleteByParam(UserActionQuery param) {
        return null;
    }

    @Override
    public UserAction getUserActionByActionId(Integer actionId) {
        return null;
    }

    @Override
    public Integer updateUserActionByActionId(UserAction bean, Integer actionId) {
        return null;
    }

    @Override
    public Integer deleteUserActionByActionId(Integer actionId) {
        return null;
    }

    @Override
    public UserAction getUserActionByVideoIdAndCommentIdAndActionTypeAndUserId(String videoId, Integer commentId, Integer actionType, String userId) {
        return null;
    }

    @Override
    public Integer updateUserActionByVideoIdAndCommentIdAndActionTypeAndUserId(UserAction bean, String videoId, Integer commentId, Integer actionType, String userId) {
        return null;
    }

    @Override
    public Integer deleteUserActionByVideoIdAndCommentIdAndActionTypeAndUserId(String videoId, Integer commentId, Integer actionType, String userId) {
        return null;
    }

    @Override
    public void saveAction(UserAction bean) {
        VideoInfo videoInfo = videoInfoMapper.selectByVideoId(bean.getVideoId());
        if (videoInfo == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        bean.setVideoUserId(videoInfo.getUserId());
        UserActionTypeEnum actionTypeEnum = UserActionTypeEnum.getByType(bean.getActionType());
        if (actionTypeEnum == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        UserAction dbAction = userActionMapper.selectByVideoIdAndCommentIdAndActionTypeAndUserId(bean.getVideoId(), bean.getCommentId(), bean.getActionType(),
                bean.getUserId());

        bean.setActionTime(new Date());
        switch (actionTypeEnum) {
            //点赞,收藏
            case VIDEO_LIKE:
            case VIDEO_COLLECT:
                if (dbAction != null) {
                    userActionMapper.deleteByActionId(dbAction.getActionId());
                } else {
                    userActionMapper.insert(bean);
                }
                Integer changeCount = dbAction == null ? 1 : -1;
                videoInfoMapper.updateCountInfo(bean.getVideoId(), actionTypeEnum.getField(), changeCount);

                if (actionTypeEnum == UserActionTypeEnum.VIDEO_COLLECT) {
                    // TODO 更新es收藏数量
                   // esSearchComponent.updateDocCount(videoInfo.getVideoId(), SearchOrderTypeEnum.VIDEO_COLLECT.getField(), changeCount);
                }
                break;
            // 投币
            case VIDEO_COIN:
                if (videoInfo.getUserId().equals(bean.getUserId())) {
                    throw new BusinessException("UP主不能给自己投币");
                }
                if (dbAction != null) {
                    throw new BusinessException("对本稿件的投币枚数已用完");
                }
                //减少自己的硬币
                Integer updateCount = userInfoMapper.updateCoinCountInfo(bean.getUserId(), -bean.getActionCount());
                if (updateCount == 0) {
                    throw new BusinessException("币不够");
                }
                // 给up主加币
                updateCount = userInfoMapper.updateCoinCountInfo(videoInfo.getUserId(), bean.getActionCount());
                if (updateCount == 0) {
                    throw new BusinessException("投币失败");
                }
                userActionMapper.insert(bean);
                videoInfoMapper.updateCountInfo(bean.getVideoId(), actionTypeEnum.getField(), bean.getActionCount());
                break;
            //评论
            case COMMENT_LIKE:
            case COMMENT_HATE:
                UserActionTypeEnum opposeTypeEnum = UserActionTypeEnum.COMMENT_LIKE == actionTypeEnum ? UserActionTypeEnum.COMMENT_HATE : UserActionTypeEnum.COMMENT_LIKE;
                UserAction opposeAction = userActionMapper.selectByVideoIdAndCommentIdAndActionTypeAndUserId(bean.getVideoId(), bean.getCommentId(),
                        opposeTypeEnum.getType(), bean.getUserId());
                if (opposeAction != null) {
                    userActionMapper.deleteByActionId(opposeAction.getActionId());
                }

                if (dbAction != null) {
                    userActionMapper.deleteByActionId(dbAction.getActionId());
                } else {
                    userActionMapper.insert(bean);
                }
                changeCount = dbAction == null ? 1 : -1;
                Integer opposeChangeCount = changeCount * -1;
                videoCommentMapper.updateCountInfo(bean.getCommentId(),
                        actionTypeEnum.getField(),
                        changeCount,
                        opposeAction == null ? null : opposeTypeEnum.getField(),
                        opposeChangeCount);
                break;
        }
    }
}