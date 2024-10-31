package com.zzyl.service;

import com.zzyl.base.PageResponse;
import com.zzyl.dto.UserDto;
import com.zzyl.entity.UserRole;
import com.zzyl.vo.UserVo;

import java.util.List;

/**
 * @description:
 * @author: 李研
 * @date: 2024/10/28
 */

public interface UserService {
    PageResponse<UserVo> page(Integer pageNum, Integer pageSize, UserDto userDto);

    List<UserVo> findUserList(UserDto userDto);

    boolean createUser(UserDto userDto);

    Boolean updateUser(UserDto userDto);

    UserRole byUploder();

    void isEnable(Long id, String status);

    Object deleteUserById(Long userId);

    boolean changePw(Long userId);
}
