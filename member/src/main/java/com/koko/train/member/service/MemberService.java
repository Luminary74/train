package com.koko.train.member.service;

import cn.hutool.core.collection.CollUtil;
import com.koko.train.common.exception.BusinessException;
import com.koko.train.common.exception.BusinessExceptionEnum;
import com.koko.train.common.util.SnowUtil;
import com.koko.train.member.domain.Member;
import com.koko.train.member.domain.MemberExample;
import com.koko.train.member.mapper.MemberMapper;
import com.koko.train.member.req.MemberRegisterReq;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    @Resource
    private MemberMapper memberMapper;

    public int count() {
        return Math.toIntExact(memberMapper.countByExample(null));
    }

    //注册服务
    public long register(MemberRegisterReq req) {
        String mobile = req.getMobile();
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> list = memberMapper.selectByExample(memberExample);

        if (CollUtil.isNotEmpty(list)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }


        Member member = new Member();
        member.setId(SnowUtil.getSnowflakeNextId());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }



}
