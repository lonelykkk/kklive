package com.kklive.service.impl;

import com.kklive.entity.constants.Constants;
import com.kklive.entity.enums.PageSize;
import com.kklive.entity.enums.ResponseCodeEnum;
import com.kklive.entity.enums.SearchOrderTypeEnum;
import com.kklive.entity.enums.UserActionTypeEnum;
import com.kklive.entity.po.VideoDanmu;
import com.kklive.entity.po.VideoInfo;
import com.kklive.entity.query.SimplePage;
import com.kklive.entity.query.VideoDanmuQuery;
import com.kklive.entity.query.VideoInfoQuery;
import com.kklive.entity.vo.PaginationResultVO;
import com.kklive.exception.BusinessException;
import com.kklive.mappers.VideoDanmuMapper;
import com.kklive.mappers.VideoInfoMapper;
import com.kklive.service.VideoDanmuService;
import com.kklive.utils.StringTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


/**
 * 视频弹幕 业务接口实现
 */
@Service("videoDanmuService")
public class VideoDanmuServiceImpl implements VideoDanmuService {

    @Resource
    private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;
    @Resource
    private VideoDanmuMapper<VideoDanmu, VideoDanmuQuery> videoDanmuMapper;


    @Override
    public List<VideoDanmu> findListByParam(VideoDanmuQuery param) {
        return this.videoDanmuMapper.selectList(param);
    }

    @Override
    public Integer findCountByParam(VideoDanmuQuery param) {
        return this.videoDanmuMapper.selectCount(param);
    }

    @Override
    public PaginationResultVO<VideoDanmu> findListByPage(VideoDanmuQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<VideoDanmu> list = this.findListByParam(param);
        PaginationResultVO<VideoDanmu> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    @Override
    public Integer add(VideoDanmu bean) {
        return null;
    }

    @Override
    public Integer addBatch(List<VideoDanmu> listBean) {
        return null;
    }

    @Override
    public Integer addOrUpdateBatch(List<VideoDanmu> listBean) {
        return null;
    }

    @Override
    public Integer updateByParam(VideoDanmu bean, VideoDanmuQuery param) {
        return null;
    }

    @Override
    public Integer deleteByParam(VideoDanmuQuery param) {
        return null;
    }

    @Override
    public VideoDanmu getVideoDanmuByDanmuId(Integer danmuId) {
        return null;
    }

    @Override
    public Integer updateVideoDanmuByDanmuId(VideoDanmu bean, Integer danmuId) {
        return null;
    }

    @Override
    public Integer deleteVideoDanmuByDanmuId(Integer danmuId) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveVideoDanmu(VideoDanmu bean) {
        VideoInfo videoInfo = videoInfoMapper.selectByVideoId(bean.getVideoId());
        if (videoInfo == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        //是否关闭弹幕
        if (videoInfo.getInteraction() != null && videoInfo.getInteraction().contains(Constants.ONE.toString())) {
            throw new BusinessException("UP主已关闭弹幕");
        }
        this.videoDanmuMapper.insert(bean);
        this.videoInfoMapper.updateCountInfo(bean.getVideoId(), UserActionTypeEnum.VIDEO_DANMU.getField(), 1);

        // TODO 跟新es 弹幕数量
    }

    /**
     * 删除弹幕
     * @param userId
     * @param danmuId
     */
    @Override
    public void deleteDanmu(String userId, Integer danmuId) {
        VideoDanmu danmu = videoDanmuMapper.selectByDanmuId(danmuId);
        if (null == danmu) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        VideoInfo videoInfo = videoInfoMapper.selectByVideoId(danmu.getVideoId());
        if (null == videoInfo) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        if (userId != null && !videoInfo.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        videoDanmuMapper.deleteByDanmuId(danmuId);
    }
}