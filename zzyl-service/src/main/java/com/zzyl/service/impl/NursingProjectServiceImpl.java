package com.zzyl.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zzyl.base.PageResponse;
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
}
