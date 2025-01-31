package com.kklive.component;

import com.kklive.entity.config.AppConfig;
import com.kklive.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2025/1/31 19:09
 * @Version V1.0
 */
@Component("EsSearchComponent")
@Slf4j
public class EsSearchComponent {
    @Resource
    private AppConfig appConfig;
    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * 创建索引前判断是否已经有该索引
     * @return
     */
    private Boolean isExistIndex() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(appConfig.getEsIndexVideoName());
        return restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
    }
    /**
     * 项目启动时创建es索引
     */
    public void createIndex() {
        try {
            // 如果已经有该索引，直接返回无需创建
            if (isExistIndex()) {
                return;
            }
            // 否则开始创建索引
            CreateIndexRequest request = new CreateIndexRequest(appConfig.getEsIndexVideoName());
            request.settings(
                    "{\"analysis\": {\n" +
                            "      \"analyzer\": {\n" +
                            "        \"comma\": {\n" +
                            "          \"type\": \"pattern\",\n" +
                            "          \"pattern\": \",\"\n" +
                            "        }\n" +
                            "      }\n" +
                            "    }}", XContentType.JSON);

            request.mapping(
                    "{\"properties\": {\n" +
                            "      \"videoId\":{\n" +
                            "        \"type\": \"text\",\n" +
                            "        \"index\": false\n" +
                            "      },\n" +
                            "      \"userId\":{\n" +
                            "        \"type\": \"text\",\n" +
                            "        \"index\": false\n" +
                            "      },\n" +
                            "      \"videoCover\":{\n" +
                            "        \"type\": \"text\",\n" +
                            "        \"index\": false\n" +
                            "      },\n" +
                            "      \"videoName\":{\n" +
                            "        \"type\": \"text\",\n" +
                            "        \"analyzer\": \"ik_max_word\"\n" +
                            "      },\n" +
                            "      \"tags\":{\n" +
                            "        \"type\": \"text\",\n" +
                            "        \"analyzer\": \"comma\"\n" +
                            "      },\n" +
                            "      \"playCount\":{\n" +
                            "        \"type\":\"integer\",\n" +
                            "        \"index\":false\n" +
                            "      },\n" +
                            "      \"danmuCount\":{\n" +
                            "        \"type\":\"integer\",\n" +
                            "        \"index\":false\n" +
                            "      },\n" +
                            "      \"collectCount\":{\n" +
                            "        \"type\":\"integer\",\n" +
                            "        \"index\":false\n" +
                            "      },\n" +
                            "      \"createTime\":{\n" +
                            "        \"type\":\"date\",\n" +
                            "        \"format\": \"yyyy-MM-dd HH:mm:ss\",\n" +
                            "        \"index\": false\n" +
                            "      }\n" +
                            " }}", XContentType.JSON);
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            boolean acknowledged = createIndexResponse.isAcknowledged();
            if (!acknowledged) {
                throw new BusinessException("初始化es失败");
            }
        }catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("初始化es失败", e);
            throw new BusinessException("初始化es失败");
        }
    }
}
