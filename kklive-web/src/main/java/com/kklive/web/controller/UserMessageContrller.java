package com.kklive.web.controller;

import com.kklive.entity.dto.TokenUserInfoDto;
import com.kklive.entity.dto.UserMessageCountDto;
import com.kklive.entity.enums.MessageReadTypeEnum;
import com.kklive.entity.po.UserMessage;
import com.kklive.entity.query.UserMessageQuery;
import com.kklive.entity.vo.PaginationResultVO;
import com.kklive.entity.vo.ResponseVO;
import com.kklive.service.UserMessageService;
import com.kklive.web.annotation.GlobalInterceptor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@Validated
@RequestMapping("/message")
public class UserMessageContrller extends ABaseController {

    @Resource
    private UserMessageService userMessageService;

    /**
     * 获取未读消息
     * @return
     */
    @RequestMapping("/getNoReadCount")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO getNoReadCount() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getSuccessResponseVO(0);
        }
        UserMessageQuery messageQuery = new UserMessageQuery();
        messageQuery.setUserId(tokenUserInfoDto.getUserId());
        messageQuery.setReadType(MessageReadTypeEnum.NO_READ.getType());
        Integer count = userMessageService.findCountByParam(messageQuery);
        return getSuccessResponseVO(count);
    }

    /**
     * 获取分组消息
     * @return
     */
    @RequestMapping("/getNoReadCountGroup")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO getNoReadCountGroup() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        List<UserMessageCountDto> dataList = userMessageService.getMessageTypeNoReadCount(tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(dataList);
    }

    /**
     * 消息全部已读
     * @param messageType
     * @return
     */
    @RequestMapping("/readAll")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO readAll(Integer messageType) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();

        UserMessageQuery userMessageQuery = new UserMessageQuery();
        userMessageQuery.setUserId(tokenUserInfoDto.getUserId());
        userMessageQuery.setMessageType(messageType);

        UserMessage userMessage = new UserMessage();
        userMessage.setReadType(MessageReadTypeEnum.READ.getType());
        userMessageService.updateByParam(userMessage, userMessageQuery);
        return getSuccessResponseVO(null);
    }

    /**
     * 加载消息
     * @param messageType
     * @param pageNo
     * @return
     */
    @RequestMapping("/loadMessage")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO loadMessage(@NotNull Integer messageType, Integer pageNo) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        UserMessageQuery messageQuery = new UserMessageQuery();
        messageQuery.setMessageType(messageType);
        messageQuery.setPageNo(pageNo);
        messageQuery.setUserId(tokenUserInfoDto.getUserId());
        messageQuery.setOrderBy("message_id desc");
        PaginationResultVO resultVO = userMessageService.findListByPage(messageQuery);
        return getSuccessResponseVO(resultVO);
    }

    /**
     * 删除消息
     * @param messageId
     * @return
     */
    @RequestMapping("/delMessage")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO delMessage(@NotNull Integer messageId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        UserMessageQuery messageQuery = new UserMessageQuery();
        messageQuery.setUserId(tokenUserInfoDto.getUserId());
        messageQuery.setMessageId(messageId);
        userMessageService.deleteByParam(messageQuery);
        return getSuccessResponseVO(null);
    }
}
