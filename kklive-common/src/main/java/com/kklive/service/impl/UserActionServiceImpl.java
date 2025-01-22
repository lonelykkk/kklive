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

    @Override
    public List<UserAction> findListByParam(UserActionQuery param) {
        return null;
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

    }
}