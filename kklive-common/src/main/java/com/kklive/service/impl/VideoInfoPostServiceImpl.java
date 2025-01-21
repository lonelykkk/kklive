package com.kklive.service.impl;

import com.kklive.component.RedisComponent;
import com.kklive.entity.config.AppConfig;
import com.kklive.entity.constants.Constants;
import com.kklive.entity.dto.SysSettingDto;
import com.kklive.entity.dto.UploadingFileDto;
import com.kklive.entity.enums.*;
import com.kklive.entity.po.VideoInfo;
import com.kklive.entity.po.VideoInfoFile;
import com.kklive.entity.po.VideoInfoFilePost;
import com.kklive.entity.po.VideoInfoPost;
import com.kklive.entity.query.*;
import com.kklive.entity.vo.PaginationResultVO;
import com.kklive.exception.BusinessException;
import com.kklive.mappers.*;
import com.kklive.service.UserMessageService;
import com.kklive.service.VideoInfoPostService;
import com.kklive.utils.CopyTools;
import com.kklive.utils.FFmpegUtils;
import com.kklive.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 视频信息 业务接口实现
 */
@Service("videoInfoPostService")
@Slf4j
public class VideoInfoPostServiceImpl implements VideoInfoPostService {

    @Resource
    private RedisComponent redisComponent;
    @Resource
    private VideoInfoPostMapper<VideoInfoPost, VideoInfoPostQuery> videoInfoPostMapper;
    @Resource
    private VideoInfoFilePostMapper<VideoInfoFilePost, VideoInfoFilePostQuery> videoInfoFilePostMapper;
    @Resource
    private AppConfig appConfig;
    @Resource
    private FFmpegUtils fFmpegUtils;


    @Override
    public List<VideoInfoPost> findListByParam(VideoInfoPostQuery param) {
        return null;
    }

    @Override
    public Integer findCountByParam(VideoInfoPostQuery param) {
        return this.videoInfoPostMapper.selectCount(param);
    }

    @Override
    public PaginationResultVO<VideoInfoPost> findListByPage(VideoInfoPostQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<VideoInfoPost> list = this.findListByParam(param);
        PaginationResultVO<VideoInfoPost> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    @Override
    public Integer add(VideoInfoPost bean) {
        return null;
    }

    @Override
    public Integer addBatch(List<VideoInfoPost> listBean) {
        return null;
    }

    @Override
    public Integer addOrUpdateBatch(List<VideoInfoPost> listBean) {
        return null;
    }

    @Override
    public Integer updateByParam(VideoInfoPost bean, VideoInfoPostQuery param) {
        return null;
    }

    @Override
    public Integer deleteByParam(VideoInfoPostQuery param) {
        return null;
    }

    @Override
    public VideoInfoPost getVideoInfoPostByVideoId(String videoId) {
        return null;
    }

    @Override
    public Integer updateVideoInfoPostByVideoId(VideoInfoPost bean, String videoId) {
        return null;
    }

    @Override
    public Integer deleteVideoInfoPostByVideoId(String videoId) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveVideoInfo(VideoInfoPost videoInfoPost, List<VideoInfoFilePost> uploadFileList) {
        // 如果修改的视频文件大小超出系统内存限制，抛出异常
        if (uploadFileList.size() > redisComponent.getSysSettingDto().getVideoPCount()) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 如果视频信息的主键id为不为空
        if (!StringTools.isEmpty(videoInfoPost.getVideoId())) {
            VideoInfoPost videoInfoPostDb = this.videoInfoPostMapper.selectByVideoId(videoInfoPost.getVideoId());
            // 如果数据库没有该视频信息，无法修改
            if (videoInfoPostDb == null) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            // 如果视频处于转码或者待审核状态，无法进行修改
            if (ArrayUtils.contains(new Integer[]{VideoStatusEnum.STATUS0.getStatus(), VideoStatusEnum.STATUS2.getStatus()}, videoInfoPostDb.getStatus())) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
        }

        Date curDate = new Date();
        String videoId = videoInfoPost.getVideoId();
        List<VideoInfoFilePost> deleteFileList = new ArrayList();
        List<VideoInfoFilePost> addFileList = uploadFileList;

        // 如果视频信息的主键id为为空
        if (StringTools.isEmpty(videoId)) {
            // 生成视频id
            videoId = StringTools.getRandomString(Constants.LENGTH_10);
            videoInfoPost.setVideoId(videoId);
            videoInfoPost.setCreateTime(curDate);
            videoInfoPost.setLastUpdateTime(curDate);
            videoInfoPost.setStatus(VideoStatusEnum.STATUS0.getStatus());
            // 将该视频信息插入数据库
            this.videoInfoPostMapper.insert(videoInfoPost);
        } else {
            VideoInfoFilePostQuery fileQuery = new VideoInfoFilePostQuery();
            fileQuery.setVideoId(videoId);
            fileQuery.setUserId(videoInfoPost.getUserId());
            List<VideoInfoFilePost> dbInfoFileList = this.videoInfoFilePostMapper.selectList(fileQuery);
            Map<String, VideoInfoFilePost> uploadFileMap = uploadFileList.stream().
                    collect(Collectors.toMap(item -> item.getUploadId(), Function.identity(), (data1, data2) -> data2));

            //删除的文件 -> 数据库中有，uploadFileList没有
            Boolean updateFileName = false;
            for (VideoInfoFilePost fileInfo : dbInfoFileList) {
                VideoInfoFilePost updateFile = uploadFileMap.get(fileInfo.getUploadId());
                if (updateFile == null) {
                    deleteFileList.add(fileInfo);
                } else if (!updateFile.getFileName().equals(fileInfo.getFileName())) {
                    updateFileName = true;
                }
            }
            //新增的文件  没有fileId就是新增的文件
            addFileList = uploadFileList.stream().filter(item -> item.getFileId() == null).collect(Collectors.toList());
            videoInfoPost.setLastUpdateTime(curDate);

            //判断视频信息是否有更改
            Boolean changeVideoInfo = this.changeVideoInfo(videoInfoPost);
            if (!addFileList.isEmpty()) {  //如果有新增的文件，将其转码
                videoInfoPost.setStatus(VideoStatusEnum.STATUS0.getStatus());
            } else if (changeVideoInfo || updateFileName) { //如果没有新增的文件但是文件信息有修改，将视频信息改为待审核
                videoInfoPost.setStatus(VideoStatusEnum.STATUS2.getStatus());
            }
            this.videoInfoPostMapper.updateByVideoId(videoInfoPost, videoInfoPost.getVideoId());
        }

        //清除已经删除的数据
        if (!deleteFileList.isEmpty()) {
            List<String> delFileIdList = deleteFileList.stream().map(item -> item.getFileId()).collect(Collectors.toList());
            this.videoInfoFilePostMapper.deleteBatchByFileId(delFileIdList, videoInfoPost.getUserId());
            //将要删除的视频加入消息队列
            List<String> delFilePathList = deleteFileList.stream().map(item -> item.getFilePath()).collect(Collectors.toList());
            redisComponent.addFile2DelQueue(videoId, delFilePathList);
        }

        //更新视频信息
        Integer index = 1;
        for (VideoInfoFilePost videoInfoFile : uploadFileList) {
            videoInfoFile.setFileIndex(index++);
            videoInfoFile.setVideoId(videoId);
            videoInfoFile.setUserId(videoInfoPost.getUserId());
            if (videoInfoFile.getFileId() == null) {
                videoInfoFile.setFileId(StringTools.getRandomString(Constants.LENGTH_20));
                videoInfoFile.setUpdateType(VideoFileUpdateTypeEnum.UPDATE.getStatus());
                videoInfoFile.setTransferResult(VideoFileTransferResultEnum.TRANSFER.getStatus());
            }
        }
        this.videoInfoFilePostMapper.insertOrUpdateBatch(uploadFileList);

        //将需要转码的视频加入队列
        if (!addFileList.isEmpty()) {
            for (VideoInfoFilePost file : addFileList) {
                file.setUserId(videoInfoPost.getUserId());
                file.setVideoId(videoId);
            }
            redisComponent.addFile2TransferQueue(addFileList);
        }
    }

    private boolean changeVideoInfo(VideoInfoPost videoInfoPost) {
        VideoInfoPost dbInfo = this.videoInfoPostMapper.selectByVideoId(videoInfoPost.getVideoId());
        //标题，封面，标签，简介
        if (!videoInfoPost.getVideoCover().equals(dbInfo.getVideoCover()) || !videoInfoPost.getVideoName().equals(dbInfo.getVideoName()) || !videoInfoPost.getTags().equals(dbInfo.getTags()) || !videoInfoPost.getIntroduction().equals(
                dbInfo.getIntroduction())) {
            return true;
        }
        return false;
    }

    @Override
    public void transferVideoFile(VideoInfoFilePost videoInfoFile) throws IOException {
        VideoInfoFilePost updateFilePost = new VideoInfoFilePost();
        try {
            // 从redis中获取需要转码的文件信息
            UploadingFileDto fileDto = redisComponent.getUploadingVideoFile(videoInfoFile.getUserId(), videoInfoFile.getUploadId());
            // 获取需要转码的文件的临时目录
            String tempFIlePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_FOLDER_TEMP + fileDto.getFilePath();
            File tempFile = new File(tempFIlePath);

            // 将转码后的视频放入正式目录
            String targetFilePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_VIDEO + fileDto.getFilePath();
            File targetFile = new File(targetFilePath);
            if (!targetFile.exists()) {
                targetFile.mkdirs();
            }
            FileUtils.copyDirectory(tempFile, targetFile);

            // 删除临时目录
            FileUtils.forceDelete(tempFile);

            // 删除缓存
            redisComponent.delVideoFileInfo(videoInfoFile.getUserId(), videoInfoFile.getUploadId());

            //合并文件 文件名：completeVideo
            String completeVideo = targetFilePath + Constants.TEMP_VIDEO_NAME;
            // 将分片合并转为mp4视频
            this.union(targetFilePath, completeVideo, true);

            // 获取播放时长--通过ffmpeg
            Integer duration = fFmpegUtils.getVideoInfoDuration(completeVideo);
            updateFilePost.setDuration(duration);
            updateFilePost.setFileSize(new File(completeVideo).length());
            updateFilePost.setFilePath(Constants.FILE_VIDEO + fileDto.getFilePath());
            updateFilePost.setTransferResult(VideoFileTransferResultEnum.SUCCESS.getStatus());

            /**
             * ffmpeg切割文件
             */
            this.convertVideo2Ts(completeVideo);
        } catch (Exception e) {
            log.error("文件转码失败");
            updateFilePost.setTransferResult(VideoFileTransferResultEnum.FAIL.getStatus());
        }finally {
            //更新文件状态
            videoInfoFilePostMapper.updateByUploadIdAndUserId(updateFilePost, videoInfoFile.getUploadId(), videoInfoFile.getUserId());
            //更新视频信息
            VideoInfoFilePostQuery fileQuery = new VideoInfoFilePostQuery();
            fileQuery.setVideoId(videoInfoFile.getVideoId());
            fileQuery.setTransferResult(VideoFileTransferResultEnum.FAIL.getStatus());
            Integer failCount = videoInfoFilePostMapper.selectCount(fileQuery);
            if (failCount > 0) {
                VideoInfoPost videoUpdate = new VideoInfoPost();
                videoUpdate.setStatus(VideoStatusEnum.STATUS1.getStatus());
                videoInfoPostMapper.updateByVideoId(videoUpdate, videoInfoFile.getVideoId());
                return;
            }
            fileQuery.setTransferResult(VideoFileTransferResultEnum.TRANSFER.getStatus());
            Integer transferCount = videoInfoFilePostMapper.selectCount(fileQuery);
            if (transferCount == 0) {
                Integer duration = videoInfoFilePostMapper.sumDuration(videoInfoFile.getVideoId());
                VideoInfoPost videoUpdate = new VideoInfoPost();
                videoUpdate.setStatus(VideoStatusEnum.STATUS2.getStatus());
                videoUpdate.setDuration(duration);
                videoInfoPostMapper.updateByVideoId(videoUpdate, videoInfoFile.getVideoId());
            }
        }
    }
    private void convertVideo2Ts(String videoFilePath) {
        File videoFile = new File(videoFilePath);
        //创建同名切片目录
        File tsFolder = videoFile.getParentFile();
        String codec = fFmpegUtils.getVideoCodec(videoFilePath);
        //转码
        if (Constants.VIDEO_CODE_HEVC.equals(codec)) {
            String tempFileName = videoFilePath + Constants.VIDEO_CODE_TEMP_FILE_SUFFIX;
            new File(videoFilePath).renameTo(new File(tempFileName));
            fFmpegUtils.convertHevc2Mp4(tempFileName, videoFilePath);
            new File(tempFileName).delete();
        }

        //视频转为ts
        fFmpegUtils.convertVideo2Ts(tsFolder, videoFilePath);

        //删除视频文件
        videoFile.delete();
    }
    /**
     * 将视频分片文件合并
     * @param dirPath
     * @param toFilePath
     * @param delSource
     */
    private void union(String dirPath, String toFilePath, Boolean delSource) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            throw new BusinessException("目录不存在");
        }
        File fileList[] = dir.listFiles();
        File targetFile = new File(toFilePath);
        try (RandomAccessFile writeFile = new RandomAccessFile(targetFile, "rw")) {
            byte[] b = new byte[1024 * 10];
            for (int i = 0; i < fileList.length; i++) {
                int len = -1;
                //创建读块文件的对象
                File chunkFile = new File(dirPath + File.separator + i);
                RandomAccessFile readFile = null;
                try {
                    readFile = new RandomAccessFile(chunkFile, "r");
                    while ((len = readFile.read(b)) != -1) {
                        writeFile.write(b, 0, len);
                    }
                } catch (Exception e) {
                    log.error("合并分片失败", e);
                    throw new BusinessException("合并文件失败");
                } finally {
                    readFile.close();
                }
            }
        } catch (Exception e) {
            throw new BusinessException("合并文件" + dirPath + "出错了");
        } finally {
            if (delSource) {
                for (int i = 0; i < fileList.length; i++) {
                    fileList[i].delete();
                }
            }
        }
    }
    @Override
    public void auditVideo(String videoId, Integer status, String reason) {

    }

    @Override
    public void recommendVideo(String videoId) {

    }
}