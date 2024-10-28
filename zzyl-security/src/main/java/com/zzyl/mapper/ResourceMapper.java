package com.zzyl.mapper;

import com.zzyl.dto.ResourceDto;
import com.zzyl.entity.Resource;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ResourceMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Resource record);

    int insertSelective(Resource record);

    Resource selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Resource record);

    int updateByPrimaryKey(Resource record);

    /**
     * 这是Mybatis Generator拓展插件生成的方法(请勿删除).
     * This method corresponds to the database table sys_resource
     *
     * @mbg.generated
     * @author hewei
     */
    int batchInsert(@Param("list") List<Resource> list);

    List<Resource> selectList(ResourceDto resourceDto);

    @Select("select * from sys_resource where resource_no = #{resourceNo}")
    Resource selectByResourceNo(String parentResourceNo);

    @Update("update sys_resource set data_state = #{dataState} where resource_no = #{resourceNo} ")
    void updateByResourceNo( String resourceNo,  String dataState);

    @Update("update sys_resource set data_state = #{dataState} where parent_resource_no like concat(#{resourceNo},'%')")
    void updateByParentResourceNo(String resourceNo, String dataState);

    @Select("select count(1) from sys_resource where parent_resource_no = #{resourceNo}")
    int hasChildByMenuId(String resourceNo);

    @Delete("delete from sys_resource where resource_no = #{resourceNo}")
    void deleteByResourceNo(String resourceNo);
}