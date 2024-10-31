package com.zzyl.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zzyl.base.PageResponse;
import com.zzyl.base.ResponseResult;
import com.zzyl.dto.UserDto;
import com.zzyl.service.UserService;
import com.zzyl.vo.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:
 * @author: 李研
 * @date: 2024/10/28
 */
@Slf4j
@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    /***
     *  多条件查询用户分页列表
     * @param userDto 用户DTO查询条件
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return: Page<UserVo>
     */
    @PostMapping("/page/{pageNum}/{pageSize}")
    @ApiOperation("用户分页")
    public ResponseResult<PageResponse> page(@PathVariable Integer pageNum,
                                             @PathVariable Integer pageSize,
                                             @RequestBody UserDto userDto) {
        PageResponse<UserVo> page = userService.page(pageNum,pageSize,userDto);
        return ResponseResult.success(page);
    }

    /***
     *  多条件查询用户列表
     * @param userDto 用户Vo对象
     * @return List<UserVo>
     */
    @PostMapping("/list")
    @ApiOperation(value = "用户列表", notes = "用户列表")
    @ApiImplicitParam(name = "userDto", value = "用户DTO对象", required = true, dataType = "UserDto")
    public ResponseResult<List<UserVo>> userList(@RequestBody UserDto userDto) {
        List<UserVo> userVoList = userService.findUserList(userDto);
        return ResponseResult.success(userVoList);
    }
    /**
     *  保存用户
     * @param userDto 用户Vo对象
     * @return UserDto
     */
    @PutMapping
    @ApiOperation(value = "用户添加",notes = "用户添加")
    @ApiImplicitParam(name = "userDto",value = "用户DTO对象",required = true,dataType = "UserDto")
    @ApiOperationSupport(includeParameters = {"userDto.email","userDto.dataState","userDto.deptNo","userDto.deptPostUserVoSet","userDto.mobile","userDto.postNo","userDto.realName","userDto.roleVoIds"})
    public ResponseResult createUser(@RequestBody UserDto userDto) {
        boolean userVoResult = userService.createUser(userDto);
        return ResponseResult.success(userVoResult);
    }

    /**
     *  修改用户
     * @param userDto 用户DTO对象
     * @return Boolean 是否修改成功
     */
    @PatchMapping
    @ApiOperation(value = "用户修改",notes = "用户修改")
    @ApiImplicitParam(name = "userDto",value = "用户DTO对象",required = true,dataType = "UserDto")
    @ApiOperationSupport(includeParameters = {"userDto.email","userDto.dataState","userDto.deptNo","userDto.deptPostUserVoSet","userDto.mobile","userDto.postNo","userDto.realName","userDto.roleVoIds"})
    public ResponseResult<Boolean> updateUser(@RequestBody UserDto userDto) {
        Boolean flag = userService.updateUser(userDto);
        return ResponseResult.success(flag);
    }

    /**
     * 返回当前登录用户
     */
    @GetMapping("/current-user")
    @ApiOperation("返回当前登录用户")
    public ResponseResult byUploder(){
        return ResponseResult.success(userService.byUploder());
    }

    @PutMapping("/is-enable/{id}/{status}")
    @ApiOperation("启用或禁用用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "主键"),
            @ApiImplicitParam(name = "status",value = "状态(0:正常,1:禁用)")
    })
    public ResponseResult isEnable(@PathVariable(name = "id") Long id,@PathVariable(name = "status")String status){
        userService.isEnable(id,status);
        return ResponseResult.success();
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/remove/{userId}")
    public ResponseResult remove(@PathVariable Long userId) {
        return ResponseResult.success(userService.deleteUserById(userId));
    }

    @PostMapping("/reset-passwords/{userId}")
    @ApiOperation("修改密码")
    private ResponseResult changePw(@PathVariable Long userId){
        boolean flag =userService.changePw(userId);
        return ResponseResult.success(flag);
    }
}
