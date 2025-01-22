package com.kklive.web.controller;

import com.kklive.component.RedisComponent;
import com.kklive.entity.config.AppConfig;
import com.kklive.entity.constants.Constants;
import com.kklive.entity.dto.SysSettingDto;
import com.kklive.entity.dto.TokenUserInfoDto;
import com.kklive.entity.dto.UploadingFileDto;
import com.kklive.entity.dto.VideoPlayInfoDto;
import com.kklive.entity.enums.DateTimePatternEnum;
import com.kklive.entity.enums.ResponseCodeEnum;
import com.kklive.entity.po.VideoInfoFile;
import com.kklive.entity.vo.ResponseVO;
import com.kklive.exception.BusinessException;
import com.kklive.service.VideoInfoFileService;
import com.kklive.utils.DateUtil;
import com.kklive.utils.FFmpegUtils;
import com.kklive.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
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
public class FileController extends ABaseController {

    @Resource
    private VideoInfoFileService videoInfoFileService;
    @Resource
    private AppConfig appConfig;
    @Resource
    private FFmpegUtils fFmpegUtils;
    @Resource
    private RedisComponent redisComponent;

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

    /**
     * 预上传
     *
     * @param fileName
     * @param chunks   分片号
     * @return 返回文件id
     */
    @RequestMapping("/preUploadVideo")
    public ResponseVO preUploadVideo(@NotEmpty String fileName, @NotNull Integer chunks) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        String uploadId = redisComponent.savePreVideoFileInfo(tokenUserInfoDto.getUserId(), fileName, chunks);
        return getSuccessResponseVO(uploadId);
    }

    /**
     * 上传视频
     * @param chunkFile
     * @param chunkIndex
     * @param uploadId
     * @return
     * @throws IOException
     */
    @RequestMapping("/uploadVideo")
    public ResponseVO uploadVideo(@NotNull MultipartFile chunkFile, @NotNull Integer chunkIndex, @NotEmpty String uploadId) throws IOException {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        UploadingFileDto fileDto = redisComponent.getUploadingVideoFile(tokenUserInfoDto.getUserId(), uploadId);
        if (fileDto == null) {
            throw new BusinessException("文件不存在，请重新上传");
        }
        SysSettingDto sysSettingDto = redisComponent.getSysSettingDto();
        if (fileDto.getFileSize() > sysSettingDto.getVideoSize() * Constants.MB_SIZE) {
            throw new BusinessException("文件超过大小限制");
        }
        // 判断分片
        if ((chunkIndex - 1) > fileDto.getChunkIndex() || chunkIndex > fileDto.getChunks() - 1) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        // 放置分片的目录
        String folder = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_FOLDER_TEMP + fileDto.getFilePath();
        File targetFile = new File(folder + "/" + chunkIndex); //创建文件（分片）
        chunkFile.transferTo(targetFile);
        //记录文件上传的分片数
        fileDto.setChunkIndex(chunkIndex);
        fileDto.setFileSize(fileDto.getFileSize() + chunkFile.getSize());
        redisComponent.updateVideoFileInfo(tokenUserInfoDto.getUserId(), fileDto);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/delUploadVideo")
    public ResponseVO delUploadVideo(@NotEmpty String uploadId) throws IOException {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
        UploadingFileDto fileDto = redisComponent.getUploadingVideoFile(tokenUserInfoDto.getUserId(), uploadId);
        if (fileDto == null) {
            throw new BusinessException("文件不存在");
        }
        redisComponent.delVideoFileInfo(tokenUserInfoDto.getUserId(),uploadId);
        FileUtils.deleteDirectory(new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_FOLDER_TEMP + fileDto.getFilePath()));
        return getSuccessResponseVO(uploadId);
    }

    /**
     * 上传视频封面
     * @param file
     * @param createThumbnail
     * @return
     * @throws IOException
     */
    @RequestMapping("/uploadImage")
    public ResponseVO uploadImage(@NotNull MultipartFile file,@NotNull Boolean createThumbnail) throws IOException {
        String day = DateUtil.format(new Date(), DateTimePatternEnum.YYYYMMDD.getPattern());
        String folder = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_COVER + day;
        File folderFile = new File(folder);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        String realFileName = StringTools.getRandomString(Constants.LENGTH_30) + fileSuffix;
        String filePath = folder + "/" + realFileName;
        file.transferTo(new File(filePath));
        if (createThumbnail) {
            //生成缩略图
            fFmpegUtils.createImageThumbnail(filePath);
        }
        return getSuccessResponseVO(Constants.FILE_COVER + day + "/" + realFileName);
    }

    @RequestMapping("/videoResource/{fileId}")
    public void getVideoResource(HttpServletResponse response, @PathVariable @NotEmpty String fileId) {
        VideoInfoFile videoInfoFile = videoInfoFileService.getVideoInfoFileByFileId(fileId);
        String filePath = videoInfoFile.getFilePath();
        readFile(response, filePath + "/" + Constants.M3U8_NAME);

        // 更新视频阅读信息
        VideoPlayInfoDto videoPlayInfoDto = new VideoPlayInfoDto();
        videoPlayInfoDto.setVideoId(videoInfoFile.getVideoId());
        videoPlayInfoDto.setFileIndex(videoInfoFile.getFileIndex());

        TokenUserInfoDto tokenUserInfoDto = getTokenInfoFromCookie();
        if (tokenUserInfoDto != null) {
            videoPlayInfoDto.setUserId(tokenUserInfoDto.getUserId());
        }
        redisComponent.addVideoPlay(videoPlayInfoDto);
    }

    @RequestMapping("/videoResource/{fileId}/{ts}")
    public void getVideoResourceTs(HttpServletResponse response, @PathVariable @NotEmpty String fileId, @PathVariable @NotNull String ts) {
        VideoInfoFile videoInfoFile = videoInfoFileService.getVideoInfoFileByFileId(fileId);
        String filePath = videoInfoFile.getFilePath();
        readFile(response, filePath + "/" + ts);
    }
}
