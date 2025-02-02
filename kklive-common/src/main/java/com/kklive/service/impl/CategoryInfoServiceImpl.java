package com.kklive.service.impl;

import com.kklive.component.RedisComponent;
import com.kklive.entity.constants.Constants;
import com.kklive.entity.enums.PageSize;
import com.kklive.entity.po.CategoryInfo;
import com.kklive.entity.query.CategoryInfoQuery;
import com.kklive.entity.query.SimplePage;
import com.kklive.entity.query.VideoInfoQuery;
import com.kklive.entity.vo.PaginationResultVO;
import com.kklive.exception.BusinessException;
import com.kklive.mappers.CategoryInfoMapper;
import com.kklive.service.CategoryInfoService;
import com.kklive.service.VideoInfoService;
import com.kklive.utils.StringTools;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * 分类信息 业务接口实现
 */
@Service("categoryInfoService")
public class CategoryInfoServiceImpl implements CategoryInfoService {


    @Resource
    private CategoryInfoMapper<CategoryInfo, CategoryInfoQuery> categoryInfoMapper;
    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private RedisComponent redisComponent;


    @Override
    public List<CategoryInfo> findListByParam(CategoryInfoQuery param) {
        List<CategoryInfo> categoryInfoList = this.categoryInfoMapper.selectList(param);
        if (param.getConvert2Tree() != null && param.getConvert2Tree()) {
            categoryInfoList = convertLine2Tree(categoryInfoList, Constants.ZERO);
        }
        return categoryInfoList;
    }

    @Override
    public Integer findCountByParam(CategoryInfoQuery param) {
        return null;
    }

    @Override
    public PaginationResultVO<CategoryInfo> findListByPage(CategoryInfoQuery param) {
        return null;
    }

    @Override
    public Integer add(CategoryInfo bean) {
        return null;
    }

    @Override
    public Integer addBatch(List<CategoryInfo> listBean) {
        return null;
    }

    @Override
    public Integer addOrUpdateBatch(List<CategoryInfo> listBean) {
        return null;
    }

    @Override
    public Integer updateByParam(CategoryInfo bean, CategoryInfoQuery param) {
        return null;
    }

    @Override
    public Integer deleteByParam(CategoryInfoQuery param) {
        return null;
    }

    @Override
    public CategoryInfo getCategoryInfoByCategoryId(Integer categoryId) {
        return null;
    }

    @Override
    public Integer updateCategoryInfoByCategoryId(CategoryInfo bean, Integer categoryId) {
        return null;
    }

    @Override
    public Integer deleteCategoryInfoByCategoryId(Integer categoryId) {
        return null;
    }

    @Override
    public CategoryInfo getCategoryInfoByCategoryCode(String categoryCode) {
        return null;
    }

    @Override
    public Integer updateCategoryInfoByCategoryCode(CategoryInfo bean, String categoryCode) {
        return null;
    }

    @Override
    public Integer deleteCategoryInfoByCategoryCode(String categoryCode) {
        return null;
    }

    @Override
    public void saveCategoryInfo(CategoryInfo bean) {
        CategoryInfo dbBean = this.categoryInfoMapper.selectByCategoryCode(bean.getCategoryCode());
        if (bean.getCategoryId() == null && dbBean != null || bean.getCategoryId() != null && dbBean != null && !bean.getCategoryId().equals(dbBean.getCategoryId())) {
            throw new BusinessException("分类编号已经存在");
        }
        if (bean.getCategoryId() == null) {
            Integer maxSort = this.categoryInfoMapper.selectMaxSort(bean.getpCategoryId());
            bean.setSort(maxSort + 1);
            this.categoryInfoMapper.insert(bean);
        } else {
            this.categoryInfoMapper.updateByCategoryId(bean, bean.getCategoryId());
        }
        //刷新缓存
        save2Redis();
    }

    private void save2Redis() {
        CategoryInfoQuery categoryInfoQuery = new CategoryInfoQuery();
        categoryInfoQuery.setOrderBy("sort asc");
        List<CategoryInfo> sourceCategoryInfoList = this.categoryInfoMapper.selectList(categoryInfoQuery);
        List<CategoryInfo> categoryInfoList = convertLine2Tree(sourceCategoryInfoList, 0);
        redisComponent.saveCategoryList(categoryInfoList);
    }


    @Override
    public void delCategory(Integer categoryId) {
        // 查询分类下是否有视频
        VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
        videoInfoQuery.setCategoryIdOrPCategoryId(categoryId);
        Integer count = videoInfoService.findCountByParam(videoInfoQuery);
        if (count > 0) {
            throw new BusinessException("分类下有视频信息，无法删除");
        }

        CategoryInfoQuery categoryInfoQuery = new CategoryInfoQuery();
        categoryInfoQuery.setCategoryIdOrPCategoryId(categoryId);
        categoryInfoMapper.deleteByParam(categoryInfoQuery);

        //刷新缓存
        save2Redis();
    }

    @Override
    public void changeSort(Integer pCategoryId, String categoryIds) {
        String[] categoryIdArray = categoryIds.split(",");
        List<CategoryInfo> categoryInfoList = new ArrayList<>();
        Integer sort = 0;
        for (String categoryId : categoryIdArray) {
            CategoryInfo categoryInfo = new CategoryInfo();
            categoryInfo.setCategoryId(Integer.parseInt(categoryId));
            categoryInfo.setpCategoryId(pCategoryId);
            categoryInfo.setSort(++sort);
            categoryInfoList.add(categoryInfo);
        }
        this.categoryInfoMapper.updateSortBatch(categoryInfoList);

        save2Redis();
    }

    @Override
    public List<CategoryInfo> getAllCategoryList() {
        List<CategoryInfo> categoryList = redisComponent.getCategoryList();
        if (categoryList.isEmpty()) {
            save2Redis();
            return redisComponent.getCategoryList();
        }
        return categoryList;
    }

    private List<CategoryInfo> convertLine2Tree(List<CategoryInfo> dataList, Integer pid) {
        List<CategoryInfo> children = new ArrayList();
        for (CategoryInfo m : dataList) {
            if (m.getCategoryId() != null && m.getpCategoryId() != null && m.getpCategoryId().equals(pid)) {
                m.setChildren(convertLine2Tree(dataList, m.getCategoryId()));
                children.add(m);
            }
        }
        return children;
    }
}