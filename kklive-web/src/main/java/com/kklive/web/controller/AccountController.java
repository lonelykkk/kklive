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
import com.kklive.web.annotation.GlobalInterceptor;
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

    @RequestMapping(value = "/login")
    public ResponseVO login(HttpServletRequest request, HttpServletResponse response, @NotEmpty @Email String email, @NotEmpty String password,
                            @NotEmpty String checkCodeKey, @NotEmpty String checkCode) {
        try {
            if (!checkCode.equalsIgnoreCase(redisComponent.getCheckCode(checkCodeKey))) {
                throw new BusinessException("图片验证码不正确");
            }
            String ip = getIpAddr();
            TokenUserInfoDto tokenUserInfoDto = userInfoService.login(email, password, ip);
            saveToken2Cookie(response, tokenUserInfoDto.getToken());
            return getSuccessResponseVO(tokenUserInfoDto);
        }finally {
            redisComponent.cleanCheckCode(checkCodeKey);
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                String token = null;
                for (Cookie cookie : cookies) {
                    if (Constants.TOKEN_WEB.equals(cookie.getName())) {
                        token = cookie.getValue();
                    }
                }
                if (!StringTools.isEmpty(token)) {
                    redisComponent.cleanToken(token);
                }
            }
        }
    }

    @RequestMapping(value = "/autoLogin")
    public ResponseVO autoLogin(HttpServletResponse response) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        if (tokenUserInfoDto == null) {
            return getSuccessResponseVO(null);
        }
        if (tokenUserInfoDto.getExpireAt() - System.currentTimeMillis() < Constants.REDIS_KEY_EXPIRES_DAY) {
            redisComponent.saveTokenInfo(tokenUserInfoDto);
            saveToken2Cookie(response, tokenUserInfoDto.getToken());
        }
        return getSuccessResponseVO(tokenUserInfoDto);
    }

    @RequestMapping(value = "/logout")
    public ResponseVO logout(HttpServletResponse response) {
        cleanCookie(response);
        return getSuccessResponseVO(null);
    }

    @RequestMapping(value = "/getUserCountInfo")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO getUserCountInfo() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        UserCountInfoDto userCountInfoDto = userInfoService.getUserCountInfo(tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(userCountInfoDto);
    }
}