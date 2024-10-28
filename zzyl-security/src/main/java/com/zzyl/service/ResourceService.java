package com.zzyl.service;

import com.zzyl.dto.ResourceDto;
import com.zzyl.vo.ResourceVo;
import com.zzyl.vo.TreeVo;

import java.util.List;

/**
 * 权限表服务类
 */
public interface ResourceService {


    /**
     * 多条件查询资源列表
     * @param resourceDto
     * @return
     */
    List<ResourceVo> findResourceList(ResourceDto resourceDto);
    /**
     *  资源树形
     * @param resourceDto 查询条件
     * @return: TreeVo
     */
    TreeVo resourceTreeVo(ResourceDto resourceDto);
    /**
     * 添加资源菜单
     * @param resourceDto
     * @return
     */
    void createResource(ResourceDto resourceDto);

    /**
     * 禁用或开启状态
     * @param resourceVo
     */
    void isEnable(ResourceVo resourceVo);

    /**
     * 资源修改
     * @param resourceDto
     */
    void updateResource(ResourceDto resourceDto);

    /**
     * 删除资源
     * @param resourceNo
     */
    void deleteByResourceNo(String resourceNo);
}