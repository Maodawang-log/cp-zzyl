package com.zzyl.mapper;

import com.github.pagehelper.Page;
import com.zzyl.dto.RoleDto;
import com.zzyl.entity.Role;
import com.zzyl.vo.RoleVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

@Mapper
public interface RoleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Role record);

    int insertSelective(Role record);

    Role selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);

    /**
     * 这是Mybatis Generator拓展插件生成的方法(请勿删除).
     * This method corresponds to the database table sys_role
     *
     * @mbg.generated
     * @author hewei
     */
    int batchInsert(@Param("list") List<Role> list);

    Page<List<Role>> selectPage(RoleDto roleDto);

    @Select("select * from sys_role")
    Set<RoleVo> roleFindAll();
}