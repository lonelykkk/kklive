package com.kklive.web.controller;

import com.kklive.component.RedisComponent;
import com.kklive.entity.vo.ResponseVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2025/1/18 11:13
 * @Version V1.0
 */
@RestController("sysSettingController")
@RequestMapping("/sysSetting")
@Validated
public class SysSettingController extends ABaseController {

    @Resource
    private RedisComponent redisComponent;

    @RequestMapping(value = "/getSetting")
    public ResponseVO getSetting() {
        return getSuccessResponseVO(redisComponent.getSysSettingDto());
    }
}
