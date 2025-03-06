# kklive


## 在线播放人数思路

### 1. 通过轮询‘’/reportVideoPlayOnline‘’ 接口实时查询在线人数 

```java
@RequestMapping("/reportVideoPlayOnline")
    public ResponseVO reportVideoPlayOnline(@NotEmpty String fileId, String deviceId) {
        Integer count = redisComponent.reportVideoPlayOnline(fileId, deviceId);
        return getSuccessResponseVO(count);
    }
```

###  2.设置两个key，如果有用户在线观看，总数+1，延长key的过期时间，此方法对于用户在线观看人数只增不减

```java
public Integer reportVideoPlayOnline(String fileId, String deviceId) {
        // 用户播放键 (userPlayOnlineKey)
        // 在线总数键 (playOnlineCountKey)
        String userPlayOnlineKey = String.format(Constants.REDIS_KEY_VIDEO_PLAY_COUNT_USER, fileId, deviceId);
        String playOnlineCountKey = String.format(Constants.REDIS_KEY_VIDEO_PLAY_COUNT_ONLINE, fileId);

        if (!redisUtils.keyExists(userPlayOnlineKey)) {
            redisUtils.setex(userPlayOnlineKey, fileId, Constants.REDIS_KEY_EXPIRES_ONE_SECONDS * 8);
            return redisUtils.incrementex(playOnlineCountKey, Constants.REDIS_KEY_EXPIRES_ONE_SECONDS * 10).intValue();
        }
        //给视频在线总数量续期
        redisUtils.expire(playOnlineCountKey, Constants.REDIS_KEY_EXPIRES_ONE_SECONDS * 10);
        //给播放用户续期
        redisUtils.expire(userPlayOnlineKey, Constants.REDIS_KEY_EXPIRES_ONE_SECONDS * 8);
        Integer count = (Integer) redisUtils.get(playOnlineCountKey);
        return count == null ? 1 : count;
    }
```

#### **核心逻辑解析**

##### **1. 数据结构设计**

- **用户播放键 (userPlayOnlineKey)**
	格式：`Constants.REDIS_KEY_VIDEO_PLAY_COUNT_USER` + `fileId` + `deviceId`
	作用：标记某设备（`deviceId`）正在播放某视频（`fileId`），避免重复计数。
	过期时间：**8秒**（通过`setex`设置）。
- **在线总数键 (playOnlineCountKey)**
	格式：`Constants.REDIS_KEY_VIDEO_PLAY_COUNT_ONLINE` + `fileId`
	作用：记录当前在线播放该视频的总次数。
	过期时间：**10秒**（通过`incrementex`或`expire`设置）。

------

##### **2. 执行流程**

1. **首次播放**
	- 当用户首次播放时（`userPlayOnlineKey`不存在）：
		- 设置 `userPlayOnlineKey`，过期时间8秒。
		- 对 `playOnlineCountKey` 的值递增1，并设置其过期时间10秒。
		- 返回递增后的总次数。
2. **非首次播放（用户持续播放）**
	- 当用户已存在播放记录（`userPlayOnlineKey`存在）：
		- 续期 `userPlayOnlineKey` 和 `playOnlineCountKey`，分别重置为8秒和10秒。
		- 直接返回当前 `playOnlineCountKey` 的值（不递增）。



### 3. 监听key过期状态

```java
@Component
@Slf4j
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
    @Resource
    private RedisComponent redisComponent;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = message.toString();
        if (!key.startsWith(Constants.REDIS_KEY_VIDEO_PLAY_COUNT_ONLINE_PREIFX + Constants.REDIS_KEY_VIDEO_PLAY_COUNT_USER_PREFIX)) {
            return;
        }
        //监听 在线用户过期的key
        Integer userKeyIndex = key.indexOf(Constants.REDIS_KEY_VIDEO_PLAY_COUNT_USER_PREFIX) + Constants.REDIS_KEY_VIDEO_PLAY_COUNT_USER_PREFIX.length();
        String fileId = key.substring(userKeyIndex, userKeyIndex + Constants.LENGTH_20);
        redisComponent.decrementPlayOnlineCount(String.format(Constants.REDIS_KEY_VIDEO_PLAY_COUNT_ONLINE, fileId));
    }
    
    public void decrementPlayOnlineCount(String key) {
        redisUtils.decrement(key);
    }
}
```

#### **核心逻辑解析**

##### **1. 功能描述**

- **监听Redis键过期事件**
	当Redis中的某个键过期时，会触发 `onMessage` 方法。
- **过滤特定前缀的键**
	只处理以 `Constants.REDIS_KEY_VIDEO_PLAY_COUNT_ONLINE_PREIFX + Constants.REDIS_KEY_VIDEO_PLAY_COUNT_USER_PREFIX` 开头的键。
- **解析键并减少在线播放数**
	从过期的键中提取 `fileId`，并调用 `redisComponent.decrementPlayOnlineCount` 方法减少对应视频的在线播放数。

------

##### **2. 执行流程**

1. **获取过期键**
	- 从 `message` 中提取过期的键（`key`）。
	- 检查键是否以特定前缀开头（`Constants.REDIS_KEY_VIDEO_PLAY_COUNT_ONLINE_PREIFX + Constants.REDIS_KEY_VIDEO_PLAY_COUNT_USER_PREFIX`）。
		- 如果不匹配，直接返回，不处理。
2. **解析 `fileId`**
	- 计算 `Constants.REDIS_KEY_VIDEO_PLAY_COUNT_USER_PREFIX` 在键中的起始位置（`userKeyIndex`）。
	- 从键中提取 `fileId`，长度为 `Constants.LENGTH_20`。
3. **减少在线播放数**
	- 调用 `redisComponent.decrementPlayOnlineCount` 方法，减少对应视频的在线播放数。
	- 参数为格式化后的键：`String.format(Constants.REDIS_KEY_VIDEO_PLAY_COUNT_ONLINE, fileId)`。

------

#### **代码细节分析**

##### **1. 键的格式**

- **过期键的格式**
	假设 `Constants.REDIS_KEY_VIDEO_PLAY_COUNT_USER_PREFIX` 为 `user:play:`，`Constants.REDIS_KEY_VIDEO_PLAY_COUNT_ONLINE_PREIFX` 为 `video:online:`，则过期键的格式为：
	`video:online:user:play:{fileId}{deviceId}`
	- `{fileId}`：视频ID，长度为20。
	- `{deviceId}`：设备ID，长度不固定。
- **解析逻辑**
	- `userKeyIndex` 是 `user:play:` 的结束位置。
	- 从 `userKeyIndex` 开始，提取20个字符作为 `fileId`。

##### **2. 减少在线播放数**

- **`decrementPlayOnlineCount` 方法**
	该方法应实现以下功能：
	- 对 `video:online:{fileId}` 键的值递减1。
	- 如果键不存在或值为0，则不操作。

## 统计视频播放数

```java
    @RequestMapping("/videoResource/{fileId}")
    public void getVideoResource(HttpServletResponse response, @PathVariable @NotEmpty String fileId) {
        VideoInfoFile videoInfoFile = videoInfoFileService.getVideoInfoFileByFileId(fileId);
        String filePath = videoInfoFile.getFilePath();
        readFile(response, filePath + "/" + Constants.M3U8_NAME);

        // 更新视频的阅读信息
        VideoPlayInfoDto videoPlayInfoDto = new VideoPlayInfoDto();
        videoPlayInfoDto.setVideoId(videoInfoFile.getVideoId());
        videoPlayInfoDto.setFileIndex(videoInfoFile.getFileIndex());

        TokenUserInfoDto tokenUserInfoDto = getTokenInfoFromCookie();
        if (tokenUserInfoDto != null) {
            videoPlayInfoDto.setUserId(tokenUserInfoDto.getUserId());
        }
        // 加入消息队列
        redisComponent.addVideoPlay(videoPlayInfoDto);
    }

```

### 核心思想

**每次播放视频后不是立马更新视频播放数，而是将该视频信息添加进消息队列，通过异步线程的方式进行消费从而提高性能**

1. 将视频信息存入消息队列

```java
public void addVideoPlay(VideoPlayInfoDto videoPlayInfoDto) {
       redisUtils.lpush(Constants.REDIS_KEY_QUEUE_VIDEO_PLAY, videoPlayInfoDto, null);
}
```

2. 通过异步线程去消费该队列

```java
	@PostConstruct
    public void consumeVideoPlayQueue() {
        executorService.execute(() -> {
            while (true) {
                try {
                    VideoPlayInfoDto videoPlayInfoDto = (VideoPlayInfoDto) 			redisUtils.rpop(Constants.REDIS_KEY_QUEUE_VIDEO_PLAY);
                    if (videoPlayInfoDto == null) {
                        Thread.sleep(1500);
                        continue;
                    }
                    //更新播放数
                    videoInfoService.addReadCount(videoPlayInfoDto.getVideoId());
                    if (!StringTools.isEmpty(videoPlayInfoDto.getUserId())) {
                        // 记录历史
                        videoPlayHistoryService.saveHistory(videoPlayInfoDto.getUserId(), videoPlayInfoDto.getVideoId(), videoPlayInfoDto.getFileIndex());
                    }
                    //按天记录播放数
                    redisComponent.recordVideoPlayCount(videoPlayInfoDto.getVideoId());

                    //更新es播放数量
                    esSearchComponent.updateDocCount(videoPlayInfoDto.getVideoId(), SearchOrderTypeEnum.VIDEO_PLAY.getField(), 1);

                } catch (Exception e) {
                    log.error("获取视频播放文件队列信息失败", e);
                }
            }
        });
    }
```

