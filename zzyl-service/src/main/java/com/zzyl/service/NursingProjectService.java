package com.zzyl.service;

import com.zzyl.base.PageResponse;

public interface NursingProjectService {

    /**
     * 分页条件查询护理项目
     * @param name
     * @param status
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageResponse selectByPage(String name, Integer status, Integer pageNum, Integer pageSize);
}
