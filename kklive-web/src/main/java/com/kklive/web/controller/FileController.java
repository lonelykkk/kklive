package com.kklive.web.controller;

import com.kklive.entity.config.AppConfig;
import com.kklive.entity.constants.Constants;
import com.kklive.entity.enums.DateTimePatternEnum;
import com.kklive.entity.enums.ResponseCodeEnum;
import com.kklive.entity.vo.ResponseVO;
import com.kklive.exception.BusinessException;
import com.kklive.utils.DateUtil;
import com.kklive.utils.FFmpegUtils;
import com.kklive.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import static com.kklive.web.controller.ABaseController.STATUC_SUCCESS;


/**
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2025/1/17 20:57
 * @Version V1.0
 */
@Validated
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {
    @Resource
    private AppConfig appConfig;
    @Resource
    private FFmpegUtils fFmpegUtils;

    @RequestMapping("/getResource")
    public void getResource(HttpServletResponse response, @NotEmpty String sourceName) {
        if (!StringTools.pathIsOk(sourceName)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        String suffix = StringTools.getFileSuffix(sourceName);
        response.setContentType("image/" + suffix.replace(".", ""));
        response.setHeader("Cache-Control", "max-age=2592000");
        readFile(response, sourceName);
    }


    protected void readFile(HttpServletResponse response, String filePath) {
        File file = new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER + filePath);
        if (!file.exists()) {
            return;
        }
        try (OutputStream out = response.getOutputStream(); FileInputStream in = new FileInputStream(file)) {
            byte[] byteData = new byte[1024];
            int len = 0;
            while ((len = in.read(byteData)) != -1) {
                out.write(byteData, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            log.error("读取文件异常", e);
        }
    }
}
