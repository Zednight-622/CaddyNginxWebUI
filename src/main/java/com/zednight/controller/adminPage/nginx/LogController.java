package com.zednight.controller.adminPage.nginx;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.zednight.config.ScheduleTask;
import com.zednight.model.Log;
import com.zednight.service.LogService;
import com.zednight.service.SettingService;
import com.zednight.utils.BaseController;
import com.zednight.utils.JsonResult;

import cn.craccd.sqlHelper.bean.Page;

@Controller
@RequestMapping("/adminPage/log")
public class LogController extends BaseController {
	@Autowired
	SettingService settingService;
	@Autowired
	LogService logService;
	@Autowired
	ScheduleTask scheduleTask;
	
	@RequestMapping("")
	public ModelAndView index(HttpSession httpSession, ModelAndView modelAndView, Page page) {
		page = logService.search(page);

		modelAndView.addObject("page", page);
		modelAndView.setViewName("/adminPage/nginxLog/index");
		return modelAndView;
	}
	
	@RequestMapping("del")
	@ResponseBody
	public JsonResult del(String id) {
		sqlHelper.deleteById(id, Log.class);
		return renderSuccess();
	}
	
	@RequestMapping("delAll")
	@ResponseBody
	public JsonResult delAll(String id) {
		sqlHelper.deleteByQuery(null, Log.class); 
		return renderSuccess();
	}
	
	@RequestMapping("detail")
	@ResponseBody
	public JsonResult detail(String id) {
		Log log = sqlHelper.findById(id, Log.class);
		return renderSuccess(log);
		
	}
	
	
	@RequestMapping("analysis")
	@ResponseBody
	public JsonResult analysis() {
		scheduleTask.diviLog();
		return renderSuccess();
		
	}
	

}
