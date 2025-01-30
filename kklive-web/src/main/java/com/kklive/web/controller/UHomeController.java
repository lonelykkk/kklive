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

@RestController
@Validated
@RequestMapping("/uhome")
public class UHomeController extends ABaseController {


}
