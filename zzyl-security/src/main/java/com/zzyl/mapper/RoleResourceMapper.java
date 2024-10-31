package com.zzyl.mapper;

import com.zzyl.entity.RoleResource;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

@Mapper
public interface RoleResourceMapper {

    int deleteByPrimaryKey(Long id);

    int insert(RoleResource record);

    int insertSelective(RoleResource record);

    RoleResource selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RoleResource record);

    int updateByPrimaryKey(RoleResource record);

    /**
     * 这是Mybatis Generator拓展插件生成的方法(请勿删除).
     * This method corresponds to the database table sys_role_resource
     *
     * @mbg.generated
     * @author hewei
     */
    int batchInsert(@Param("list") List<RoleResource> list);

    @Select("select resource_no from sys_role_resource where role_id = #{roleId}")
    Set<String> selectResourceNoByRoleId(Long roleId);

    @Delete("delete from sys_role_resource where role_id = #{roleId}")
    int deleteRoleResourceByRoleId(Long roleId);
}