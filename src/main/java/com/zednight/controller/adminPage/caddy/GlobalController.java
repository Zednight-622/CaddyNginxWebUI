package com.zednight.controller.adminPage.caddy;

import cn.craccd.sqlHelper.bean.Sort;
import cn.craccd.sqlHelper.bean.Sort.Direction;
import cn.hutool.core.util.StrUtil;
import com.zednight.model.Global;
import com.zednight.service.GlobalService;
import com.zednight.utils.BaseController;
import com.zednight.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/adminPage/global")
public class GlobalController extends BaseController {
	@Autowired
	GlobalService globalService;
	
	@RequestMapping("")
	public ModelAndView index(ModelAndView modelAndView) {
		List<Global> globalList = sqlHelper.findAll(new Sort().add(Global::getSeq, Direction.ASC), Global.class);

		modelAndView.addObject("globalList", globalList);
		modelAndView.setViewName("/adminPage/caddyGlobal/index");
		return modelAndView;
	}

	@RequestMapping("addOver")
	@ResponseBody
	public JsonResult addOver(Global base) {
		if (StrUtil.isEmpty(base.getId())) {
			base.setSeq(globalService.buildOrder());
		}
		sqlHelper.insertOrUpdate(base);

		return renderSuccess();
	}

	@RequestMapping("setOrder")
	@ResponseBody
	public JsonResult setOrder(String id, Integer count) {
		globalService.setSeq(id, count);

		return renderSuccess();
	}
	
	@RequestMapping("detail")
	@ResponseBody
	public JsonResult detail(String id) {
		return renderSuccess(sqlHelper.findById(id, Global.class));
	}

	@RequestMapping("del")
	@ResponseBody
	public JsonResult del(String id) {
		sqlHelper.deleteById(id, Global.class);

		return renderSuccess();
	}

}
