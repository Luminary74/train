package com.koko.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.koko.train.common.context.LoginMemberContext;
import com.koko.train.common.resp.PageResp;
import com.koko.train.common.util.SnowUtil;
import com.koko.train.member.domain.Passenger;
import com.koko.train.member.domain.PassengerExample;
import com.koko.train.member.mapper.PassengerMapper;
import com.koko.train.member.req.PassengerQueryReq;
import com.koko.train.member.req.PassengerSaveReq;
import com.koko.train.member.resp.PassengerQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassengerService {

	private static final Logger LOG = LoggerFactory.getLogger(PassengerService.class);

	@Resource
	private PassengerMapper passengerMapper;

	public void save(PassengerSaveReq req) {
		DateTime now = DateTime.now();
		Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);
		if (!ObjectUtil.isNotNull(req.getId())) {
			passenger.setMemberId(LoginMemberContext.getId());
			passenger.setId(SnowUtil.getSnowflakeNextId());
			passenger.setCreateTime(now);
			passenger.setUpdateTime(now);
			passengerMapper.insert(passenger);
		} else {
			passenger.setUpdateTime(now);
			passengerMapper.updateByPrimaryKey(passenger);
		}

	}

	public PageResp<PassengerQueryResp> queryList(PassengerQueryReq  req) {
		PassengerExample passengerExample = new PassengerExample();
		passengerExample.setOrderByClause("id desc");
		PassengerExample.Criteria criteria = passengerExample.createCriteria();
		if (ObjectUtil.isNotNull(req.getMemberId())) {
			criteria.andMemberIdEqualTo(LoginMemberContext.getId());
		}
		LOG.info("查询页码：{}", req.getPage());
		LOG.info("每页条数：{}", req.getSize());
		PageHelper.startPage(req.getPage(),req.getSize());
		List<Passenger> passengerList = passengerMapper.selectByExample(passengerExample);

		PageInfo<Passenger> pageInfo = new PageInfo<>(passengerList);
		LOG.info("总行数：{}", pageInfo.getTotal());
		LOG.info("总页数：{}", pageInfo.getPages());

		List <PassengerQueryResp> list = BeanUtil.copyToList(passengerList, PassengerQueryResp.class);

		PageResp<PassengerQueryResp> pageResp = new PageResp<>();
		pageResp.setTotal(pageInfo.getTotal());
		pageResp.setList(list);
		return pageResp;

	}

	public void delete(Long id) {
		passengerMapper.deleteByPrimaryKey(id);
	}
}