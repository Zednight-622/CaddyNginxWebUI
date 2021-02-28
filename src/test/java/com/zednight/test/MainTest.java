package com.zednight.test;

import cn.hutool.crypto.digest.MD5;
import com.github.odiszapc.nginxparser.NgxBlock;
import com.github.odiszapc.nginxparser.NgxParam;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import com.zednight.NginxWebUI;
import com.zednight.service.MonitorService;
import com.zednight.service.SettingService;
import com.zednight.utils.MessageUtils;
import com.zednight.utils.SendMailUtils;

import cn.craccd.sqlHelper.utils.SqlHelper;

@SpringBootTest(classes = NginxWebUI.class)
public class MainTest {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	SqlHelper sqlHelper;
	@Value("${project.version}")
	String version;
	@Autowired
	MonitorService monitorService;
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	SendMailUtils sendMailUtils;
	@Autowired
	SettingService settingService;
	@Autowired
	MessageUtils m;
	
	@BeforeAll
	static void before() {
		System.out.println("--------------测试开始----------");
	}

	@Test
	public void encryption() {
		MD5 md5 = MD5.create();
		String password1 = md5.digestHex("pass");
		String password2 = md5.digestHex("pass");
		System.out.println(password1);
		System.out.println(password2);
	}

	@Test
	public void testStartUp() throws InterruptedException {
		NgxBlock blank = new NgxBlock();
		NgxParam ngxParam = new NgxParam();
		String http3_on = "http3 on";
		ngxParam.addValue(http3_on);
		blank.addEntry(ngxParam);
		System.out.println(blank.toString());

	}

	@AfterAll
	static void after() {
		System.out.println("--------------测试结束----------");
	}


	public static void main(String[] args) {
		
	}
}
