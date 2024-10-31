package com.zzyl.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.zzyl.base.PageResponse;
import com.zzyl.constant.SuperConstant;
import com.zzyl.dto.UserDto;
import com.zzyl.entity.User;
import com.zzyl.entity.UserRole;
import com.zzyl.enums.BasicEnum;
import com.zzyl.exception.BaseException;
import com.zzyl.mapper.DeptMapper;
import com.zzyl.mapper.RoleMapper;
import com.zzyl.mapper.SUserMapper;
import com.zzyl.mapper.UserRoleMapper;
import com.zzyl.service.UserService;
import com.zzyl.utils.NoProcessing;
import com.zzyl.utils.UserThreadLocal;
import com.zzyl.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:
 * @author: 李研
 * @date: 2024/10/28
 */
@Service
public class UserServiceimpl implements UserService {
    @Autowired
    private SUserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public PageResponse<UserVo> page(Integer pageNum, Integer pageSize, UserDto userDto) {
        //去除部门多余的0
        userDto.setDeptNo(NoProcessing.processString(userDto.getDeptNo()));
        // 使用 PageHelper 开始分页
        PageHelper.startPage(pageNum, pageSize);

        // 调用分页查询
        Page<UserVo> page = userMapper.selectPage(userDto);

        // 返回分页结果，封装为 PageResponse
        return PageResponse.of(page, UserVo.class);
    }


    @Override
    public List<UserVo> findUserList(UserDto userDto) {
        List<User> userList = userMapper.selectList();
        return BeanUtil.copyToList(userList, UserVo.class);
    }

    @Override
    @Transactional
    public boolean createUser(UserDto userDto) {
        //根据部门编号查询，是否是最底层的部门，如果不是，不允许添加用户
        if (isLowestDept(userDto.getDeptNo())) {
            throw new BaseException(BasicEnum.USER_LOCATED_BOTTOMED_DEPT);
        }
        //转换UserVo为User
        User user = BeanUtil.toBean(userDto, User.class);
        user.setUsername(user.getEmail());
        user.setNickName(user.getRealName());
        user.setDataState(SuperConstant.DATA_STATE_0);
        //设置默认密码
        String hashpw = BCrypt.hashpw("1234", BCrypt.gensalt());
        user.setPassword(hashpw);
        int flag = userMapper.insert(user);
        if (flag == 0) {
            throw new RuntimeException("保存用户信息出错");
        }
        //保存用户角色中间表
        List<UserRole> userRoles = Lists.newArrayList();
        userDto.getRoleVoIds().forEach(r -> {
            userRoles.add(UserRole.builder()
                    .userId(user.getId())
                    .roleId(Long.valueOf(r))
                    .dataState(SuperConstant.DATA_STATE_0)
                    .build());
        });
        flag = userRoleMapper.batchInsert(userRoles);
        if (flag <= 0) {
            throw new RuntimeException("保存用户角色中间表出错");
        }

        return true;
    }

    /**
     * 判断是否是最底层部门
     *
     * @return
     */
    private boolean isLowestDept(String deptNo) {
        int count = deptMapper.isLowestDept(deptNo);
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Boolean updateUser(UserDto userDto) {

        //根据部门编号查询，是否是最底层的部门，如果不是，不允许添加用户
        if (isLowestDept(userDto.getDeptNo())) {
            throw new BaseException(BasicEnum.USER_LOCATED_BOTTOMED_DEPT);
        }

        //转换UserVo为User
        User user = BeanUtil.toBean(userDto, User.class);
        user.setUsername(userDto.getEmail());
        int flag = userMapper.updateByPrimaryKeySelective(user);
        if (flag == 0) {
            throw new RuntimeException("修改用户信息出错");
        }


        if (CollUtil.isNotEmpty(userDto.getRoleVoIds())) {
            //删除角色中间表
            boolean flagDel = userRoleMapper.deleteUserRoleByUserId(user.getId());
            if (!flagDel) {
                throw new RuntimeException("删除角色中间表出错");
            }

            //重新保存角色中间表
            List<UserRole> userRoles = Lists.newArrayList();
            userDto.getRoleVoIds().forEach(r -> {
                userRoles.add(UserRole.builder()
                        .userId(user.getId())
                        .roleId(Long.valueOf(r))
                        .dataState(SuperConstant.DATA_STATE_0)
                        .build());
            });
            flag = userRoleMapper.batchInsert(userRoles);
            if (flag == 0) {
                throw new RuntimeException("保存角色中间表出错");
            }
        }

        return true;
    }

    @Override
    public UserRole byUploder() {
        User user = userMapper.selectByPrimaryKey(UserThreadLocal.get());
        return BeanUtil.toBean(user, UserRole.class);
    }

    @Override
    public void isEnable(Long id, String status) {
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null) {
            throw new BaseException(BasicEnum.USER_EMPTY_EXCEPTION);
        }
        user.setDataState(status);
        userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    @Transactional
    public Object deleteUserById(Long userId) {
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 删除用户与岗位关联
        return userMapper.deleteByPrimaryKey(userId);
    }

    @Override
    public boolean changePw(Long userId) {
        return false;
    }
}
