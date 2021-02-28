package com.zednight.service;

import cn.hutool.crypto.digest.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zednight.model.Admin;

import cn.craccd.sqlHelper.bean.Page;
import cn.craccd.sqlHelper.utils.ConditionAndWrapper;
import cn.craccd.sqlHelper.utils.SqlHelper;

@Service
public class AdminService {
	@Autowired
	SqlHelper sqlHelper;

	public void addAdmin(String name, String pass) {
		Admin admin = new Admin();
		admin.setName(name);
		MD5 md5 = MD5.create();
		admin.setPass(md5.digestHex(admin.getPass()));
		admin.setAuth(false);
		sqlHelper.insert(admin);
	}
	public void addAdminForUpdate(Admin admin) {
		admin.setKey("");
		MD5 md5 = MD5.create();
		admin.setPass(md5.digestHex(admin.getPass()));
		sqlHelper.insertOrUpdate(admin);
	}

	public Admin login(String name, String pass) {
		MD5 md5 = MD5.create();
		String password = md5.digestHex(pass);
		return sqlHelper.findOneByQuery(new ConditionAndWrapper().eq(Admin::getName, name).eq(Admin::getPass, password), Admin.class);
	}

	public Page search(Page page) {
		page = sqlHelper.findPage(page, Admin.class);

		return page;
	}

	public Long getCountByName(String name) {
		return sqlHelper.findCountByQuery(new ConditionAndWrapper().eq(Admin::getName, name), Admin.class);
	}

	public Long getCountByNameWithOutId(String name, String id) {
		return sqlHelper.findCountByQuery(new ConditionAndWrapper().eq(Admin::getName, name).ne(Admin::getId, id), Admin.class);
	}

	public Admin getOneByName(String name) {
		return sqlHelper.findOneByQuery(new ConditionAndWrapper().eq(Admin::getName, name), Admin.class);
	}

}
