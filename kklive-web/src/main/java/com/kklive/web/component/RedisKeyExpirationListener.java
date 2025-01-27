package com.kklive.web.component;

import com.kklive.component.RedisComponent;
import com.kklive.entity.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 监听redis的key
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2025/1/26 19:21
 * @Version V1.0
 */
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
        Integer userKeyIndex = key.indexOf(Constants.REDIS_KEY_VIDEO_PLAY_COUNT_USER_PREFIX) + Constants.REDIS_KEY_VIDEO_PLAY_COUNT_USER_PREFIX.length();
        String fileId = key.substring(userKeyIndex, userKeyIndex + Constants.LENGTH_20);
        redisComponent.decrementPlayOnlineCount(String.format(Constants.REDIS_KEY_VIDEO_PLAY_COUNT_ONLINE, fileId));
    }
}
