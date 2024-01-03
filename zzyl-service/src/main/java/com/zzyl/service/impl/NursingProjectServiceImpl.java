package com.zzyl.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zzyl.base.PageResponse;
import com.zzyl.dto.NursingProjectDto;
import com.zzyl.entity.NursingProject;
import com.zzyl.mapper.NursingProjectMapper;
import com.zzyl.service.NursingProjectService;
import com.zzyl.vo.NursingProjectVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NursingProjectServiceImpl implements NursingProjectService {


    @Autowired
    private NursingProjectMapper nursingProjectMapper;

    /**
     * 分页条件查询护理项目
     * @param name
     * @param status
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResponse selectByPage(String name, Integer status, Integer pageNum, Integer pageSize) {

        //设置分页
        PageHelper.startPage(pageNum,pageSize);
        Page<NursingProjectVo> nursingProjects = nursingProjectMapper.selectByPage(name, status);
        return PageResponse.of(nursingProjects,NursingProjectVo.class);
    }

    /**
     * 新增护理项目
     * @param nursingProjectDto
     */
    @Override
    public void add(NursingProjectDto nursingProjectDto) {
        NursingProject nursingProject = BeanUtil.toBean(nursingProjectDto, NursingProject.class);
        nursingProjectMapper.insert(nursingProject);

    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Override
    public NursingProjectVo findById(Long id) {
        NursingProject nursingProject = nursingProjectMapper.findById(id);
        return BeanUtil.toBean(nursingProject,NursingProjectVo.class);
    }

    /**
     * 修改护理项目
     * @param nursingProjectDto
     */
    @Override
    public void update(NursingProjectDto nursingProjectDto) {
        NursingProject nursingProject = BeanUtil.toBean(nursingProjectDto, NursingProject.class);
        nursingProjectMapper.update(nursingProject);
    }
}
