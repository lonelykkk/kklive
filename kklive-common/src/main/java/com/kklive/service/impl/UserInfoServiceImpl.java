package com.kklive.service.impl;

import com.kklive.component.RedisComponent;
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


    @Resource
    private RedisComponent redisComponent;

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Resource
    private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;

    @Resource
    private UserFocusMapper<UserFocus, UserFocusQuery> userFocusMapper;
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
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
        if (null == userInfo || !userInfo.getPassword().equals(password)) {
            throw new BusinessException("账号或者密码错误");
        }
        if (UserStatusEnum.DISABLE.getStatus().equals(userInfo.getStatus())) {
            throw new BusinessException("账号已禁用");
        }
        UserInfo updateInfo = new UserInfo();
        updateInfo.setLastLoginTime(new Date());
        updateInfo.setLastLoginIp(ip);
        this.userInfoMapper.updateByUserId(updateInfo, userInfo.getUserId());

        TokenUserInfoDto tokenUserInfoDto = CopyTools.copy(userInfo, TokenUserInfoDto.class);
        redisComponent.saveTokenInfo(tokenUserInfoDto);
        return tokenUserInfoDto;
    }

    @Override
    public void register(String email, String nickName, String password) {
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
        if (null != userInfo) {
            throw new BusinessException("邮箱账号已经存在");
        }
        UserInfo nickNameUser = this.userInfoMapper.selectByNickName(nickName);
        if (null != nickNameUser) {
            throw new BusinessException("昵称已经存在");
        }
        String userId = StringTools.getRandomNumber(Constants.LENGTH_10);
        userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setNickName(nickName);
        userInfo.setEmail(email);
        userInfo.setPassword(StringTools.encodeByMD5(password));
        userInfo.setJoinTime(new Date());
        userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
        userInfo.setSex(UserSexEnum.SECRECY.getType());
        userInfo.setTheme(Constants.ONE);
        // TODO 初始化用户硬币
        SysSettingDto sysSettingDto = redisComponent.getSysSettingDto();
        userInfo.setTotalCoinCount(sysSettingDto.getRegisterCoinCount());
        userInfo.setCurrentCoinCount(sysSettingDto.getRegisterCoinCount());

        this.userInfoMapper.insert(userInfo);
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