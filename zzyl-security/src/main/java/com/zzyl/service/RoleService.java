package com.zzyl.service;

import com.zzyl.base.PageResponse;
import com.zzyl.dto.RoleDto;
import com.zzyl.vo.RoleVo;

import java.util.List;
import java.util.Set;

/**
 * 角色表服务类
 */
public interface RoleService {

    /**
     *  多条件查询角色表分页列表
     * @param roleDto 查询条件
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return Page<ResourceVo>
     */
    PageResponse<RoleVo> findRolePage(RoleDto roleDto, int pageNum, int pageSize);

    /**
     * 插入角色
     * @param roleDto
     */
    void createRole(RoleDto roleDto);

    Set<String> findCheckedResources(Long roleId);

    Boolean updateRole(RoleDto roleDto);

    Object deleteRoleById(Long roleId);

    Set<RoleVo> roleInit();
}