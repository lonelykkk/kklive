package com.kklive.aspect;

import com.kklive.annotation.RecordUserMessage;
import com.kklive.entity.constants.Constants;
import com.kklive.entity.dto.TokenUserInfoDto;
import com.kklive.entity.enums.MessageTypeEnum;
import com.kklive.entity.enums.ResponseCodeEnum;
import com.kklive.entity.enums.UserActionTypeEnum;
import com.kklive.entity.vo.ResponseVO;
import com.kklive.exception.BusinessException;
import com.kklive.redis.RedisUtils;
import com.kklive.service.UserMessageService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Component("userMessageOperationAspect")
@Aspect
@Slf4j
public class UserMessageOperationAspect {

    private static final String PARAMETERS_VIDEO_ID = "videoId";

    private static final String PARAMETERS_ACTION_TYPE = "actionType";

    private static final String PARAMETERS_REPLY_COMMENTID = "replyCommentId";

    private static final String PARAMETERS_AUDIT_REJECT_REASON = "reason";

    private static final String PARAMETERS_CONTENT = "content";

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private UserMessageService userMessageService;


    @Around("@annotation(com.kklive.annotation.RecordUserMessage)")
    public ResponseVO interceptorDo(ProceedingJoinPoint point) throws Exception {
        try {
            ResponseVO result = (ResponseVO) point.proceed();
            Method method = ((MethodSignature) point.getSignature()).getMethod();
            RecordUserMessage recordUserMessage = method.getAnnotation(RecordUserMessage.class);
            if (recordUserMessage != null) {
                saveUserMessage(recordUserMessage, point.getArgs(), method.getParameters());
            }
            return result;
        } catch (BusinessException e) {
            log.error("全局拦截器异常", e);
            throw e;
        } catch (Exception e) {
            log.error("全局拦截器异常", e);
            throw e;
        } catch (Throwable e) {
            log.error("全局拦截器异常", e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }
    }


    private void saveUserMessage(RecordUserMessage recordUserMessage, Object[] arguments, Parameter[] parameters) {
        String videoId = null;
        Integer actionType = null;
        Integer replyCommentId = null;
        String content = null;
        for (int i = 0; i < parameters.length; i++) {
            if (PARAMETERS_VIDEO_ID.equals(parameters[i].getName())) {
                videoId = (String) arguments[i];
            } else if (PARAMETERS_ACTION_TYPE.equals(parameters[i].getName())) {
                actionType = (Integer) arguments[i];
            } else if (PARAMETERS_REPLY_COMMENTID.equals(parameters[i].getName())) {
                replyCommentId = (Integer) arguments[i];
            } else if (PARAMETERS_AUDIT_REJECT_REASON.equals(parameters[i].getName())) {
                content = (String) arguments[i];
            } else if (PARAMETERS_CONTENT.equals(parameters[i].getName())) {
                content = (String) arguments[i];
            }
        }
        MessageTypeEnum messageTypeEnum = recordUserMessage.messageType();
        if (UserActionTypeEnum.VIDEO_COLLECT.getType().equals(actionType)) {
            messageTypeEnum = MessageTypeEnum.COLLECTION;
        }

        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        //管理端获取不到用户信息，系统发送，不需要用户id
        userMessageService.saveUserMessage(videoId, tokenUserInfoDto == null ? null : tokenUserInfoDto.getUserId(), messageTypeEnum, content, replyCommentId);
    }

    private TokenUserInfoDto getTokenUserInfoDto() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader(Constants.TOKEN_WEB);
        return (TokenUserInfoDto) redisUtils.get(Constants.REDIS_KEY_TOKEN_WEB + token);
    }
}