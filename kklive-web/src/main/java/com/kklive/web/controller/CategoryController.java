package com.kklive.web.controller;

import com.kklive.entity.po.CategoryInfo;
import com.kklive.entity.query.CategoryInfoQuery;
import com.kklive.entity.vo.ResponseVO;
import com.kklive.service.CategoryInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/category")
@Validated
public class CategoryController extends ABaseController {
    @Resource
    private CategoryInfoService categoryInfoService;

    @RequestMapping("/loadAllCategory")
    public ResponseVO loadAllCategory() {
        List<CategoryInfo> categoryInfoList = categoryInfoService.getAllCategoryList();
        return getSuccessResponseVO(categoryInfoList);
    }
}

