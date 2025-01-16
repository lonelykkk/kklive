package com.kklive.web.controller;

import com.kklive.component.RedisComponent;
import com.kklive.entity.constants.Constants;
import com.kklive.entity.dto.TokenUserInfoDto;
import com.kklive.entity.dto.UserCountInfoDto;
import com.kklive.entity.vo.ResponseVO;
import com.kklive.exception.BusinessException;
import com.kklive.redis.RedisUtils;
import com.kklive.service.UserInfoService;
import com.kklive.utils.StringTools;
import com.wf.captcha.ArithmeticCaptcha;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息
 */
@RestController("accountController")
@RequestMapping("/account")
@Validated
public class AccountController extends ABaseController {

    @Resource
    private RedisComponent redisComponent;
    @Resource
    private UserInfoService userInfoService;

    /**
     * 验证码
     * @return
     */
    @RequestMapping("/checkCode")
    public ResponseVO checkCode() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 42);
        String code = captcha.text();
        String checkCodeKey = redisComponent.saveCheckCode(code);
        String checkCodeBase64 = captcha.toBase64();
        Map<String, String> result = new HashMap<>();
        result.put("checkCode", checkCodeBase64);
        result.put("checkCodeKey", checkCodeKey);
        return getSuccessResponseVO(result);
    }

    /**
     * 注册
     * @param email
     * @param nickName
     * @param registerPassword
     * @param checkCodeKey
     * @param checkCode
     * @return
     */
    @RequestMapping("/register")
    public ResponseVO register(@NotEmpty @Email @Size(max = 150) String email, @NotEmpty @Size(max = 20) String nickName, @NotEmpty @Pattern(regexp =
            Constants.REGEX_PASSWORD) String registerPassword, @NotEmpty String checkCodeKey, @NotEmpty String checkCode) {
        try {
            if (!checkCode.equalsIgnoreCase(redisComponent.getCheckCode(checkCodeKey))) {
                throw new BusinessException("图片验证码不正确");
            }
            userInfoService.register(email, nickName, registerPassword);
            return getSuccessResponseVO(null);
        }finally {
            redisComponent.cleanCheckCode(checkCodeKey);
        }

    }
}