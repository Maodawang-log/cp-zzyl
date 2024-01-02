package com.zzyl.mapper;

import com.github.pagehelper.Page;
import com.zzyl.entity.NursingProject;
import com.zzyl.vo.NursingProjectVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NursingProjectMapper {
    int insert(NursingProject nursingProject);
    
    int update(NursingProject nursingProject);
    
    int deleteById(long id);
    
    NursingProject findById(long id);
    
    Page<NursingProjectVo> selectByPage(String name, Integer status);
}