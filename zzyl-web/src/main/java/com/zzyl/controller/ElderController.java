
package com.zzyl.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.zzyl.base.ResponseResult;
import com.zzyl.dto.NursingElderDto;
import com.zzyl.service.ElderService;
import com.zzyl.vo.ElderVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 客户老人关联 Controller
 */
@RestController
@RequestMapping("/elder")
@Api(tags = "老人")
public class ElderController extends BaseController {

    @Resource
    private ElderService elderService;

    /**
     * 列表
     */
    @GetMapping("/selectList")
    @ApiOperation(value = "列表")
    public ResponseResult selectList() {
        List<ElderVo> elderVos = elderService.selectList();
        return success(elderVos);
    }


    @GetMapping("/selectByIdCard")
    @ApiOperation(value = "身份证号", notes = "身份证号")
    public ResponseResult selectByIdCard(@RequestParam String  idCard) {
        return success(elderService.selectByIdCard(idCard));
    }
}


