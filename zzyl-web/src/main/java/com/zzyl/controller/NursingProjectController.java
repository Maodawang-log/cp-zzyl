package com.zzyl.controller;


import com.zzyl.base.PageResponse;
import com.zzyl.base.ResponseResult;
import com.zzyl.service.NursingProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/nursing_project")
@Api(tags = "护理项目相关接口")
public class NursingProjectController extends BaseController {

    @Autowired
    private NursingProjectService nursingProjectService;

    @GetMapping
    @ApiOperation("分页条件查询护理项目")
    public ResponseResult selectByPage(@ApiParam(value = "护理项目名称") String name, Integer status, Integer pageNum, Integer pageSize){
        PageResponse pageResponse = nursingProjectService.selectByPage(name, status, pageNum, pageSize);
        return success(pageResponse);
    }
}
