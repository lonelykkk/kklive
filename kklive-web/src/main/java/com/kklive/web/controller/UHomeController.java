package com.kklive.web.controller;

import com.kklive.entity.constants.Constants;
import com.kklive.entity.dto.TokenUserInfoDto;
import com.kklive.entity.enums.PageSize;
import com.kklive.entity.enums.UserActionTypeEnum;
import com.kklive.entity.enums.VideoOrderTypeEnum;
import com.kklive.entity.po.UserInfo;
import com.kklive.entity.query.UserActionQuery;
import com.kklive.entity.query.UserFocusQuery;
import com.kklive.entity.query.VideoInfoQuery;
import com.kklive.entity.vo.PaginationResultVO;
import com.kklive.entity.vo.ResponseVO;
import com.kklive.entity.vo.UserInfoVO;
import com.kklive.service.UserActionService;
import com.kklive.service.UserFocusService;
import com.kklive.service.UserInfoService;
import com.kklive.service.VideoInfoService;
import com.kklive.utils.CopyTools;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 用户个人主页
 */
@RestController
@Validated
@RequestMapping("/uhome")
public class UHomeController extends ABaseController {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private UserFocusService userFocusService;

    @Resource
    private UserActionService userActionService;

    @RequestMapping("/getUserInfo")
    public ResponseVO getUserInfo(@NotEmpty String userId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        UserInfo userInfo = userInfoService.getUserDetailInfo(null == tokenUserInfoDto ? null : tokenUserInfoDto.getUserId(), userId);
        UserInfoVO userInfoVO = CopyTools.copy(userInfo, UserInfoVO.class);
        return getSuccessResponseVO(userInfoVO);
    }


    @RequestMapping("/updateUserInfo")
    public ResponseVO updateUserInfo(@NotEmpty @Size(max = 20) String nickName,
                                     @NotEmpty @Size(max = 100) String avatar,
                                     @NotNull Integer sex, String birthday,
                                      @Size(max = 150) String school,
                                     @Size(max = 80) String personIntroduction,
                                     @Size(max = 300) String noticeInfo) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(tokenUserInfoDto.getUserId());
        userInfo.setNickName(nickName);
        userInfo.setAvatar(avatar);
        userInfo.setSex(sex);
        userInfo.setBirthday(birthday);
        userInfo.setSchool(school);
        userInfo.setPersonIntroduction(personIntroduction);
        userInfo.setNoticeInfo(noticeInfo);
        userInfoService.updateUserInfo(userInfo, tokenUserInfoDto);

        return getSuccessResponseVO(null);
    }

    @RequestMapping("/saveTheme")
    public ResponseVO saveTheme(Integer theme) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        UserInfo userInfo = new UserInfo();
        userInfo.setTheme(theme);
        userInfoService.updateUserInfoByUserId(userInfo, tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/focus")
    public ResponseVO focus(@NotEmpty String focusUserId) {
        userFocusService.focusUser(getTokenUserInfoDto().getUserId(), focusUserId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/cancelFocus")
    public ResponseVO cancelFocus(@NotEmpty String focusUserId) {
        userFocusService.cancelFocus(getTokenUserInfoDto().getUserId(), focusUserId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadFocusList")
    public ResponseVO loadFocusList(Integer pageNo) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        UserFocusQuery focusQuery = new UserFocusQuery();
        focusQuery.setUserId(tokenUserInfoDto.getUserId());
        focusQuery.setQueryType(Constants.ZERO);
        focusQuery.setPageNo(pageNo);
        focusQuery.setOrderBy("focus_time desc");
        PaginationResultVO resultVO = userFocusService.findListByPage(focusQuery);
        return getSuccessResponseVO(resultVO);
    }

    @RequestMapping("/loadFansList")
    public ResponseVO loadFansList(Integer pageNo) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        UserFocusQuery focusQuery = new UserFocusQuery();
        focusQuery.setFocusUserId(tokenUserInfoDto.getUserId());
        focusQuery.setQueryType(Constants.ONE);
        focusQuery.setPageNo(pageNo);
        focusQuery.setOrderBy("focus_time desc");
        PaginationResultVO resultVO = userFocusService.findListByPage(focusQuery);
        return getSuccessResponseVO(resultVO);
    }

    @RequestMapping("/loadVideoList")
    public ResponseVO loadVideoList(@NotEmpty String userId, Integer type, Integer pageNo, String videoName, Integer orderType) {
        VideoInfoQuery infoQuery = new VideoInfoQuery();
        if (type != null) {
            infoQuery.setPageSize(PageSize.SIZE10.getSize());
        }
        VideoOrderTypeEnum videoOrderTypeEnum = VideoOrderTypeEnum.getByType(orderType);
        if (videoOrderTypeEnum == null) {
            videoOrderTypeEnum = VideoOrderTypeEnum.CREATE_TIME;
        }
        infoQuery.setOrderBy(videoOrderTypeEnum.getField() + " desc");
        infoQuery.setVideoNameFuzzy(videoName);
        infoQuery.setPageNo(pageNo);
        infoQuery.setUserId(userId);
        PaginationResultVO resultVO = videoInfoService.findListByPage(infoQuery);
        return getSuccessResponseVO(resultVO);
    }


    /**
     * 获取收藏视频信息
     * @param userId
     * @param pageNo
     * @return
     */
    @RequestMapping("/loadUserCollection")
    public ResponseVO loadUserCollection(@NotEmpty String userId, Integer pageNo) {
        UserActionQuery actionQuery = new UserActionQuery();
        actionQuery.setActionType(UserActionTypeEnum.VIDEO_COLLECT.getType());
        actionQuery.setUserId(userId);
        actionQuery.setPageNo(pageNo);
        actionQuery.setQueryVideoInfo(true);
        actionQuery.setOrderBy("action_time desc");
        PaginationResultVO resultVO = userActionService.findListByPage(actionQuery);
        return getSuccessResponseVO(resultVO);
    }

}
