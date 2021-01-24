package com.zednight.controller.adminPage.nginx;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.zednight.model.Stream;
import com.zednight.service.StreamService;
import com.zednight.utils.BaseController;
import com.zednight.utils.JsonResult;

import cn.craccd.sqlHelper.bean.Sort;
import cn.craccd.sqlHelper.bean.Sort.Direction;
import cn.hutool.core.util.StrUtil;

@Controller
@RequestMapping("/adminPage/stream")
public class StreamController extends BaseController {
	@Autowired
	StreamService streamService;

	@RequestMapping("")
	public ModelAndView index(HttpSession httpSession, ModelAndView modelAndView) {
		List<Stream> streamList = sqlHelper.findAll(new Sort("seq", Direction.ASC), Stream.class);

		modelAndView.addObject("streamList", streamList);
		modelAndView.setViewName("/adminPage/nginxStream/index");
		return modelAndView;
	}

	@RequestMapping("addOver")
	@ResponseBody
	public JsonResult addOver(Stream stream) {
		if (StrUtil.isEmpty(stream.getId())) {
			stream.setSeq(streamService.buildOrder());
		}
		sqlHelper.insertOrUpdate(stream);

		return renderSuccess();
	}

	@RequestMapping("detail")
	@ResponseBody
	public JsonResult detail(String id) {
		return renderSuccess(sqlHelper.findById(id, Stream.class));
	}

	@RequestMapping("del")
	@ResponseBody
	public JsonResult del(String id) {
		sqlHelper.deleteById(id, Stream.class);

		return renderSuccess();
	}

	@RequestMapping("setOrder")
	@ResponseBody
	public JsonResult setOrder(String id, Integer count) {
		streamService.setSeq(id, count);

		return renderSuccess();
	}
}