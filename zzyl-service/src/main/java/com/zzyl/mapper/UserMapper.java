package com.zzyl.mapper;

import com.zzyl.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select * from sys_user where username = #{username}")
    UserVo selectByName(String username);
}
