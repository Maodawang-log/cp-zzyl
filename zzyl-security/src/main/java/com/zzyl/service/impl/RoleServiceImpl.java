package com.zzyl.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.zzyl.base.PageResponse;
import com.zzyl.constant.SuperConstant;
import com.zzyl.dto.RoleDto;
import com.zzyl.entity.Role;
import com.zzyl.entity.RoleResource;
import com.zzyl.mapper.RoleMapper;
import com.zzyl.mapper.RoleResourceMapper;
import com.zzyl.service.RoleService;
import com.zzyl.utils.EmptyUtil;
import com.zzyl.vo.RoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @description:
 * @author: 李研
 * @date: 2024/10/28
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleResourceMapper roleResourceMapper;

    @Override
    public PageResponse<RoleVo> findRolePage(RoleDto roleDto, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<List<Role>> page = roleMapper.selectPage(roleDto);
        return PageResponse.of(page, RoleVo.class);
    }

    @Override
    public void createRole(RoleDto roleDto) {
        Role role = BeanUtil.toBean(roleDto, Role.class);
        roleMapper.insert(role);
    }

    @Override
    public Set<String> findCheckedResources(Long roleId) {
        return roleResourceMapper.selectResourceNoByRoleId(roleId);
    }

    @Override
    @Transactional
    public Boolean updateRole(RoleDto roleDto) {
        //转换RoleVo为Role
        Role role = BeanUtil.toBean(roleDto, Role.class);

        //TODO 该角色已分配用户,不能禁用

        //修改角色
        roleMapper.updateByPrimaryKeySelective(role);
        //判断是否修改角色对应的资源数据
        if (ObjectUtil.isEmpty(roleDto.getCheckedResourceNos())) {
            return true;
        }

        //删除原有角色资源中间信息
        roleResourceMapper.deleteRoleResourceByRoleId(role.getId());
        //保存角色资源中间信息
        List<RoleResource> roleResourceList = Lists.newArrayList();
        Arrays.asList(roleDto.getCheckedResourceNos()).forEach(n -> {
            RoleResource roleResource = RoleResource.builder()
                    .roleId(role.getId())
                    .resourceNo(n)
                    .dataState(SuperConstant.DATA_STATE_0)
                    .build();
            roleResourceList.add(roleResource);
        });
        //如果集合为空，则结束请求
        if (EmptyUtil.isNullOrEmpty(roleResourceList)) {
            return true;
        }
        //批量保存角色和资源的关系数据
        roleResourceMapper.batchInsert(roleResourceList);
        return true;
    }

    @Override
    @Transactional
    public Object deleteRoleById(Long roleId) {
        // 删除角色与菜单关联
        roleResourceMapper.deleteRoleResourceByRoleId(roleId);
        return roleMapper.deleteByPrimaryKey(roleId);
    }

    @Override
    public Set<RoleVo> roleInit() {
        return roleMapper.roleFindAll();
    }
}
