package com.zednight.test;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.github.odiszapc.nginxparser.NgxBlock;
import com.github.odiszapc.nginxparser.NgxParam;
import com.github.woostju.ssh.SshClientConfig;
import com.github.woostju.ssh.SshResponse;
import com.github.woostju.ssh.pool.SshClientWrapper;
import com.github.woostju.ssh.pool.SshClientsPool;
import net.schmizz.sshj.SSHClient;
import org.apache.http.client.utils.HttpClientUtils;
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

import java.io.*;
import java.util.List;

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
	@Autowired
	private SshClientsPool pool;
	
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

	@Test
	public void testAdminApi() {
		File file = new File("D:\\Enviroment\\caddy_2.2.1_linux_amd64\\Caddyfile");
		try {
			FileInputStream stream = new FileInputStream(file);
			BufferedInputStream inputStream = new BufferedInputStream(stream);
			try {
				byte[] data = new byte[inputStream.available()];
				int read = inputStream.read(data);
				String body = HttpRequest.post("http://localhost:2019/load")
						.header(Header.CONTENT_TYPE, "text/caddyfile")
						.body(data).execute().body();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println(HttpRequest.get("http://localhost").execute().body());
	}
	@Test
	public void testConfigTransport(){
		SshClientConfig clientConfig = new SshClientConfig("192.168.0.146", 22, "root", "Iamxujiangyi622", null);
		SshClientWrapper client = pool.client(clientConfig);
		SshResponse response = client.executeCommand("curl -X GET 'http://localhost:2019/config/...'", 100);
		StringBuilder s = new StringBuilder();
		List<String> stdout = response.getStdout();
		stdout.forEach(s::append);
		String body = s.toString();
		String res = HttpRequest.post("http://localhost:2019/load")
				.body(body)
				.header(Header.CONTENT_TYPE, "application/json")
				.execute().body();
//		System.out.println(HttpRequest.get("http://localhost:2019/config/...").execute().body());
//		System.out.println(HttpRequest.get("http://localhost").execute().body());
	}
}
