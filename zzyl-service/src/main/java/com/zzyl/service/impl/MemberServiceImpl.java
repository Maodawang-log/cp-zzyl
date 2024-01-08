package com.zzyl.service.impl;

import com.google.common.collect.Lists;
import com.zzyl.constant.Constants;
import com.zzyl.dto.UserLoginRequestDto;
import com.zzyl.entity.Member;
import com.zzyl.mapper.MemberMapper;
import com.zzyl.properties.JwtTokenManagerProperties;
import com.zzyl.service.MemberService;
import com.zzyl.service.WechatService;
import com.zzyl.utils.JwtUtil;
import com.zzyl.utils.ObjectUtil;
import com.zzyl.utils.StringUtils;
import com.zzyl.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    @Autowired
    private WechatService wechatService;

    @Autowired
    private MemberMapper memberMapper;

    static List<String> DEFAULT_NICKNAME_PREFIX = Lists.newArrayList(
            "生活更美好",
            "大桔大利",
            "日富一日",
            "好柿开花",
            "柿柿如意",
            "一椰暴富",
            "大柚所为",
            "杨梅吐气",
            "天生荔枝"
    );

    @Autowired
    private JwtTokenManagerProperties jwtTokenManagerProperties;

    /**
     * 小程序登录
     * @param userLoginRequestDto
     * @return
     */
    @Override
    public LoginVo login(UserLoginRequestDto userLoginRequestDto) {

        //1.通过前端传递的code发起远程调用，获取用户的openid
        String openid = wechatService.getOpenid(userLoginRequestDto.getCode());

        //2.通过openid查询数据库，查询用户
        Member member = memberMapper.getByOpenid(openid);

        //3.用户为空的话，构建用户数据（openid）
        if(ObjectUtil.isEmpty(member)){
            /*member = new Member();
            member.setOpenId(openid);*/
            //构建者模式给对象赋值
            member = Member
                    .builder()
                    .openId(openid)
                    .build();
        }

        //4.根据detail.code查询手机号（微信官方查询）
        String phone = wechatService.getPhone(userLoginRequestDto.getPhoneCode());

        //5.新增或修改用户
        saveOrUpdate(member,phone);

        //6.生成token返回（包含用户的id和名称）
        Map<String,Object> claims = new HashMap<>();
        claims.put(Constants.JWT_USERID,member.getId());
        claims.put(Constants.JWT_USERNAME,member.getName());
        String token = JwtUtil.createJWT(jwtTokenManagerProperties.getBase64EncodedSecretKey(), jwtTokenManagerProperties.getTtl(), claims);

        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        loginVo.setNickName(member.getName());
        return loginVo;
    }

    /**
     * 保存或修改用户
     * @param member
     * @param phone
     */
    private void saveOrUpdate(Member member, String phone) {

        //判断手机号是否更换
        if(ObjectUtil.notEqual(member.getPhone(),phone)){
            member.setPhone(phone);
        }

        //是否需要更新
        if(ObjectUtil.isNotEmpty(member.getId())){
            memberMapper.updateMember(member);
            return;
        }

        //新增
        //自动生成名称  随机字符串+手机后4位
        String nickName = DEFAULT_NICKNAME_PREFIX.get((int)(Math.random()*DEFAULT_NICKNAME_PREFIX.size())) + StringUtils.substring(phone,7);
        member.setName(nickName);
        memberMapper.insertMember(member);
    }
}
