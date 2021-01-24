package com.zednight.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zednight.model.Www;

import cn.craccd.sqlHelper.utils.ConditionAndWrapper;
import cn.craccd.sqlHelper.utils.SqlHelper;

@Service
public class WwwService {

	@Autowired
	SqlHelper sqlHelper;

	public Boolean hasName(String name) {

		return sqlHelper.findCountByQuery(new ConditionAndWrapper().eq("name", name), Www.class) > 0;

	}
}
