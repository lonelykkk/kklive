package com.kklive.service.impl;

import com.kklive.entity.constants.Constants;
import com.kklive.entity.dto.CountInfoDto;
import com.kklive.entity.dto.SysSettingDto;
import com.kklive.entity.dto.TokenUserInfoDto;
import com.kklive.entity.dto.UserCountInfoDto;
import com.kklive.entity.enums.PageSize;
import com.kklive.entity.enums.ResponseCodeEnum;
import com.kklive.entity.enums.UserSexEnum;
import com.kklive.entity.enums.UserStatusEnum;
import com.kklive.entity.po.UserFocus;
import com.kklive.entity.po.UserInfo;
import com.kklive.entity.po.VideoInfo;
import com.kklive.entity.query.SimplePage;
import com.kklive.entity.query.UserFocusQuery;
import com.kklive.entity.query.UserInfoQuery;
import com.kklive.entity.query.VideoInfoQuery;
import com.kklive.entity.vo.PaginationResultVO;
import com.kklive.exception.BusinessException;
import com.kklive.mappers.UserFocusMapper;
import com.kklive.mappers.UserInfoMapper;
import com.kklive.mappers.VideoInfoMapper;
import com.kklive.service.UserInfoService;
import com.kklive.utils.CopyTools;
import com.kklive.utils.StringTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


/**
 * 用户信息 业务接口实现
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {


    @Override
    public List<UserInfo> findListByParam(UserInfoQuery param) {
        return null;
    }

    @Override
    public Integer findCountByParam(UserInfoQuery param) {
        return null;
    }

    @Override
    public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param) {
        return null;
    }

    @Override
    public Integer add(UserInfo bean) {
        return null;
    }

    @Override
    public Integer addBatch(List<UserInfo> listBean) {
        return null;
    }

    @Override
    public Integer addOrUpdateBatch(List<UserInfo> listBean) {
        return null;
    }

    @Override
    public Integer updateByParam(UserInfo bean, UserInfoQuery param) {
        return null;
    }

    @Override
    public Integer deleteByParam(UserInfoQuery param) {
        return null;
    }

    @Override
    public UserInfo getUserInfoByUserId(String userId) {
        return null;
    }

    @Override
    public Integer updateUserInfoByUserId(UserInfo bean, String userId) {
        return null;
    }

    @Override
    public Integer deleteUserInfoByUserId(String userId) {
        return null;
    }

    @Override
    public UserInfo getUserInfoByEmail(String email) {
        return null;
    }

    @Override
    public Integer updateUserInfoByEmail(UserInfo bean, String email) {
        return null;
    }

    @Override
    public Integer deleteUserInfoByEmail(String email) {
        return null;
    }

    @Override
    public UserInfo getUserInfoByNickName(String nickName) {
        return null;
    }

    @Override
    public Integer updateUserInfoByNickName(UserInfo bean, String nickName) {
        return null;
    }

    @Override
    public Integer deleteUserInfoByNickName(String nickName) {
        return null;
    }

    @Override
    public TokenUserInfoDto login(String email, String password, String ip) {
        return null;
    }

    @Override
    public void register(String email, String nickName, String password) {

    }

    @Override
    public void updateUserInfo(UserInfo userInfo, TokenUserInfoDto tokenUserInfoDto) {

    }

    @Override
    public UserInfo getUserDetailInfo(String currentUserId, String userId) {
        return null;
    }

    @Override
    public UserCountInfoDto getUserCountInfo(String userId) {
        return null;
    }

    @Override
    public Integer updateCoinCountInfo(String userId, Integer changeCount) {
        return null;
    }
}