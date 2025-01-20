package com.kklive.web.task;

import com.kklive.component.RedisComponent;
import com.kklive.entity.constants.Constants;
import com.kklive.entity.po.VideoInfoFilePost;
import com.kklive.redis.RedisUtils;
import com.kklive.service.VideoInfoPostService;
import com.kklive.service.VideoInfoService;
import com.kklive.service.VideoPlayHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.kklive.entity.constants.Constants.REDIS_KEY_QUEUE_TRANSFER;

/**
 * 处理消息队列
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2025/1/20 16:56
 * @Version V1.0
 */
@Component
@Slf4j
public class ExecuteQueueTask {
    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    @Resource
    private VideoInfoPostService videoInfoPostService;
    @Resource
    private RedisComponent redisComponent;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private VideoPlayHistoryService videoPlayHistoryService;

    @PostConstruct
    public void consumeTransferFileQueue() {
        executorService.execute(()->{
            while (true) {
                try {
                    // 从消息队列取出需要转码的视频信息
                    VideoInfoFilePost videoInfoFile = (VideoInfoFilePost) redisUtils.rpop(REDIS_KEY_QUEUE_TRANSFER);
                    //如果消息队列没有则休眠
                    if (videoInfoFile == null) {
                        Thread.sleep(1500);
                        continue;
                    }
                    // 开始转码
                    videoInfoPostService.transferVideoFile(videoInfoFile);
                } catch (Exception e) {
                    log.error("获取转码文件队列信息失败", e);
                }
            }
        });
    }

    /*@PostConstruct
    public void consumeVideoPlayQueue() {
        executorService.execute(()->{
            while (true) {
                try {
                    // 从消息队列取出需要转码的视频信息
                    VideoInfoFilePost videoInfoFile = (VideoInfoFilePost) redisUtils.rpop(REDIS_KEY_QUEUE_TRANSFER);
                    //如果消息队列没有则休眠
                    if (videoInfoFile == null) {
                        Thread.sleep(1500);
                        continue;
                    }
                    // 开始转码
                    videoInfoPostService.transferVideoFile(videoInfoFile);
                } catch (Exception e) {
                    log.error("获取转码文件队列信息失败", e);
                }
            }
        });
    }*/
}
