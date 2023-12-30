
package com.zzyl.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.zzyl.dto.ElderDto;
import com.zzyl.entity.Elder;
import com.zzyl.mapper.ElderMapper;
import com.zzyl.service.ElderService;
import com.zzyl.utils.ObjectUtil;
import com.zzyl.vo.ElderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ElderServiceImpl是ElderService的实现类
 */
@Service
public class ElderServiceImpl implements ElderService {

    @Autowired
    private ElderMapper elderMapper;

    /**
     * 根据id删除老人信息
     */
    @Override
    public int deleteByPrimaryKey(Long id) {
        return elderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 插入老人信息
     * @return
     */
    @Override
    public Elder insert(ElderDto elderDto) {
        Elder elder = BeanUtil.toBean(elderDto, Elder.class);
        elder.setStatus(4);
        elder.setRemark("0");
        ElderVo elderVo = selectByIdCardAndName(elderDto.getIdCardNo(), elderDto.getName());
        if (ObjectUtil.isNotEmpty(elderVo)) {
            int i = Integer.parseInt(elderVo.getRemark()) + 1;
            elder.setName(elder.getName() + i);
            elderVo.setRemark(i + "");
            Elder elder1 = BeanUtil.toBean(elderVo, Elder.class);
            elderMapper.updateByPrimaryKeySelective(elder1);
        }
        elderMapper.insert(elder);
        return elder;
    }

    /**
     * 选择性插入老人信息
     */
    @Override
    public int insertSelective(ElderDto elderDto) {
        Elder elder = BeanUtil.toBean(elderDto, Elder.class);
        return elderMapper.insertSelective(elder);
    }

    /**
     * 根据id选择老人信息
     */
    @Override
    public ElderVo selectByPrimaryKey(Long id) {
        Elder elder = elderMapper.selectByPrimaryKey(id);
        return BeanUtil.toBean(elder, ElderVo.class);
    }

    /**
     * 选择性更新老人信息
     */
    @Override
    public Elder updateByPrimaryKeySelective(ElderDto elderDto, boolean b) {
        Elder elder = BeanUtil.toBean(elderDto, Elder.class);
        if (b) {
            elder.setRemark("0");
            ElderVo elderVo = selectByIdCardAndName(elderDto.getIdCardNo(), elderDto.getName());
            if (ObjectUtil.isNotEmpty(elderVo)) {
                int i = Integer.parseInt(elderVo.getRemark()) + 1;
                elder.setName(elder.getName() + i);
                elderVo.setRemark(i + "");
                Elder elder1 = BeanUtil.toBean(elderVo, Elder.class);
                elderMapper.updateByPrimaryKeySelective(elder1);
                return elder1;
            }
        }
        elderMapper.updateByPrimaryKeySelective(elder);
        return elder;
    }

    /**
     * 更新老人信息
     */
    @Override
    public int updateByPrimaryKey(ElderDto elderDto) {
        Elder elder = BeanUtil.toBean(elderDto, Elder.class);
        return elderMapper.updateByPrimaryKey(elder);
    }


    /**
     * 根据身份证号和姓名查询老人信息
     */
    @Override
    public ElderVo selectByIdCardAndName(String idCard, String name) {
        Elder elder = elderMapper.selectByIdCardAndName(idCard, name);
        return BeanUtil.toBean(elder, ElderVo.class);
    }


    /**
     * 根据身份证号和姓名查询老人信息
     */
    @Override
    public List<ElderVo> selectList() {
        List<Elder> elder = elderMapper.selectList();
        return BeanUtil.copyToList(elder, ElderVo.class);
    }

    @Override
    public List<Elder> selectByIds(List<Long> ids) {
        return elderMapper.selectByIds(ids);
    }


    @Override
    public ElderVo selectByIdCard(String idCard) {
        Elder elder = elderMapper.selectByIdCard(idCard);
        return BeanUtil.toBean(elder, ElderVo.class);
    }

    /**
     * 清除老人床位编号
     * @param elderId
     */
    @Override
    public void clearBedNum(Long elderId) {
        elderMapper.clearBedNum(elderId);
    }
}


