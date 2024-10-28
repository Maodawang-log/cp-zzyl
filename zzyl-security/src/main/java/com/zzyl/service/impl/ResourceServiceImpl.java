package com.zzyl.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.zzyl.constant.SuperConstant;
import com.zzyl.dto.ResourceDto;
import com.zzyl.entity.Resource;
import com.zzyl.enums.BasicEnum;
import com.zzyl.exception.BaseException;
import com.zzyl.mapper.ResourceMapper;
import com.zzyl.service.ResourceService;
import com.zzyl.utils.EmptyUtil;
import com.zzyl.utils.NoProcessing;
import com.zzyl.vo.ResourceVo;
import com.zzyl.vo.TreeItemVo;
import com.zzyl.vo.TreeVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 权限表服务实现类
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;

    /**
     * 多条件查询资源列表
     *
     * @param resourceDto
     * @return
     */
    @Override
    public List<ResourceVo> findResourceList(ResourceDto resourceDto) {
        List<Resource> resourceList = resourceMapper.selectList(resourceDto);
        return BeanUtil.copyToList(resourceList, ResourceVo.class);
    }

    @Override
    public TreeVo resourceTreeVo(ResourceDto resourceDto) {
        //构建查询条件
        ResourceDto dto = ResourceDto.builder().dataState(SuperConstant.DATA_STATE_0)
                .parentResourceNo(NoProcessing.processString(SuperConstant.ROOT_PARENT_ID))
                .resourceType(SuperConstant.MENU)
                .build();
        //查询所有资源数据
        List<Resource> resourceList = resourceMapper.selectList(dto);
        if (EmptyUtil.isNullOrEmpty(resourceList)) {
            throw new RuntimeException("资源信息未定义");
        }
        //数据库无根节点，构建根节点
        Resource rootResource = new Resource();
        rootResource.setResourceNo(SuperConstant.ROOT_PARENT_ID);
        rootResource.setResourceName("智慧养老院");

        //返回的树型集合
        ArrayList<TreeItemVo> itemVos = new ArrayList<>();

        //使用递归构建树形结构
        iterativeTreeItem(itemVos, rootResource, resourceList);

        return TreeVo.builder().items(itemVos).build();
    }

    /*private void recursionTreeItem(List<TreeItemVo> itemVos, Resource rootResource, List<Resource> resourceList) {
        //构建每个资源的属性
        TreeItemVo treeItemVo  = TreeItemVo.builder()
                .id(rootResource.getResourceNo())
                .label(rootResource.getResourceName())
                .build();
        //获取当前资源下子资源
        List<Resource> childrenResourceList  = resourceList.stream()
                .filter(n -> n.getParentResourceNo().equals(rootResource.getResourceNo()))
                .collect(Collectors.toList());
        //判断子资源是否为空
        if(!EmptyUtil.isNullOrEmpty(childrenResourceList)){
            List<TreeItemVo> listChildren = new ArrayList<>();
            childrenResourceList.forEach(resource -> {
                recursionTreeItem(listChildren,resource,resourceList);
            });
            treeItemVo.setChildren(listChildren);
        }
        itemVos.add(treeItemVo);
    }*/
    private void iterativeTreeItem(List<TreeItemVo> itemVos, Resource rootResource, List<Resource> resourceList) {
        // 使用一个栈来模拟递归
        ArrayDeque<Resource> stack = new ArrayDeque<>();
        ArrayDeque<TreeItemVo> itemVoStack = new ArrayDeque<>();

        // 构建根节点并入栈
        TreeItemVo rootItemVo = TreeItemVo.builder()
                .id(rootResource.getResourceNo())
                .label(rootResource.getResourceName())
                .build();
        stack.push(rootResource);
        itemVoStack.push(rootItemVo);

        // 处理栈中的元素
        while (!stack.isEmpty()) {
            // 从栈中弹出当前处理的节点
            Resource currentResource = stack.pop();
            TreeItemVo currentItemVo = itemVoStack.pop();

            // 查找当前节点的子节点
            List<Resource> childrenResourceList = resourceList.stream()
                    .filter(n -> n.getParentResourceNo().equals(currentResource.getResourceNo()))
                    .collect(Collectors.toList());

            // 如果有子节点，构建子节点结构
            if (!EmptyUtil.isNullOrEmpty(childrenResourceList)) {
                List<TreeItemVo> childrenVos = new ArrayList<>();
                for (Resource childResource : childrenResourceList) {
                    // 构建子节点的 TreeItemVo
                    TreeItemVo childItemVo = TreeItemVo.builder()
                            .id(childResource.getResourceNo())
                            .label(childResource.getResourceName())
                            .build();

                    // 将子节点及其 TreeItemVo 入栈，等待后续处理
                    stack.push(childResource);
                    itemVoStack.push(childItemVo);

                    // 将构建好的子节点添加到当前节点的子节点集合中
                    childrenVos.add(childItemVo);
                }

                // 设置当前节点的子节点列表
                currentItemVo.setChildren(childrenVos);
            }

            // 如果当前节点是根节点，则将其加入到itemVos中（仅根节点应该在itemVos中）
            if (currentResource.getResourceNo().equals(SuperConstant.ROOT_PARENT_ID)) {
                itemVos.add(currentItemVo);
            }
        }
    }

    @Override
    public void createResource(ResourceDto resourceDto) {
        //转换为数据库类型
        Resource resource = BeanUtil.toBean(resourceDto, Resource.class);
        //和父资源状态保持一致
        Resource parentResource = resourceMapper.selectByResourceNo(resourceDto.getParentResourceNo());
        resource.setDataState(parentResource.getDataState());

        //判断是否是按钮，如果是按钮，则不限制层级
        boolean isIgnore = true;
        if (StringUtils.isNotEmpty(resourceDto.getResourceType())
                && resourceDto.getResourceType().equals(SuperConstant.BUTTON)) {
            isIgnore = false;
        }
        //创建当前资源的编号
        String resourceNo = createResourceNo(resourceDto.getParentResourceNo(), isIgnore);
        resource.setResourceNo(resourceNo);
        //进行插入操作
        resourceMapper.insert(resource);
    }

    private String createResourceNo(String parentResourceNo, boolean isIgnore) {
        //判断资源编号是否大于三级
        //100 001 000 000 000
        //100 001 001 000 000
        //100 001 001 001 000
        //100 001 001 001 001 001
        //判断父资源的长度，父资源的等级大于三级不允许生成
        if (isIgnore && NoProcessing.processString(parentResourceNo).length() == 15) {
            throw new BaseException(BasicEnum.RESOURCE_DEPTH_UPPER_LIMIT);
        }
        //生成查询方法需要的参数
        ResourceDto dto = ResourceDto.builder().parentResourceNo(parentResourceNo).build();
        //子资源集合
        List<Resource> resources = resourceMapper.selectList(dto);
        if (EmptyUtil.isNullOrEmpty(resources)) {
            //无下属节点，创建新的节点编号  100 001 001 001 000--->100 001 001 001 001
            return NoProcessing.createNo(parentResourceNo, false);
        } else {
            //有下属节点，在已有的节点上追加
            //先获取已有节点的最大值--100001003000000
            Long maxNo = resources.stream()
                    .map(resource -> {
                        return Long.valueOf(resource.getResourceNo());
                    })
                    .max(Long::compareTo)
                    .orElseThrow(() -> new IllegalArgumentException("没有找到值！"));
            return NoProcessing.createNo(String.valueOf(maxNo), true);
        }
    }

    @Override
    public void isEnable(ResourceVo resourceVo) {
        // 判断是要禁用还是启用
        if (Objects.nonNull(resourceVo.getDataState()) && "0".equals(resourceVo.getDataState())) {
            //获取父节点
            Resource parentResource = resourceMapper.selectByResourceNo(resourceVo.getParentResourceNo());
            // 检查父节点的状态：如果父节点为空或处于启用状态，则允许启用当前节点和子节点
            if (parentResource != null && "1".equals(parentResource.getDataState())) {
                throw new BaseException(BasicEnum.PARENT_MENU_DISABLE);
            }
        }
        String resourceNo = resourceVo.getResourceNo();
        resourceMapper.updateByResourceNo(resourceNo, resourceVo.getDataState());
        resourceMapper.updateByParentResourceNo(NoProcessing.processString(resourceNo), resourceVo.getDataState());
    }

    /**
     * 修改资源
     * @param resourceDto
     */
    @Override
    public void updateResource(ResourceDto resourceDto) {
        //转换
        Resource resource = BeanUtil.toBean(resourceDto, Resource.class);
        int flag = resourceMapper.updateByPrimaryKeySelective(resource);
        if (flag == 0) {
            throw new RuntimeException("修改资源信息出错");
        }
    }
    /**
     * 删除菜单
     * @param resourceNo
     */
    @Override
    public void deleteByResourceNo(String resourceNo) {
        if (hasChildByMenuId(resourceNo)) {
            throw new RuntimeException("存在子菜单,不允许删除");
        }

        Resource resource = resourceMapper.selectByResourceNo(resourceNo);
        if(resource == null || resource.getDataState().equals(SuperConstant.DATA_STATE_0)){
            throw new RuntimeException("菜单启用状态,不能删除");
        }

        resourceMapper.deleteByResourceNo(resourceNo);
    }

    /**
     * 判断是否有子菜单
     * @param resourceNo
     * @return
     */
    private boolean hasChildByMenuId(String resourceNo) {
        int result = resourceMapper.hasChildByMenuId(resourceNo);
        return result > 0;
    }
}