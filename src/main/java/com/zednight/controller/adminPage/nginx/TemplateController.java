package com.zednight.controller.adminPage.nginx;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.zednight.ext.TemplateExt;
import com.zednight.model.Param;
import com.zednight.model.Template;
import com.zednight.service.TemplateService;
import com.zednight.utils.BaseController;
import com.zednight.utils.JsonResult;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

@Controller
@RequestMapping("/adminPage/template")
public class TemplateController extends BaseController {
	@Autowired
	TemplateService templateService;
	
	@RequestMapping("")
	public ModelAndView index(HttpSession httpSession, ModelAndView modelAndView) {
		List<Template> templateList = sqlHelper.findAll(Template.class);

		List<TemplateExt> extList = new ArrayList<>();
		for(Template template:templateList) {
			TemplateExt templateExt = new TemplateExt();
			templateExt.setTemplate(template);
			
			templateExt.setParamList(templateService.getParamList(template.getId()));
			templateExt.setCount(templateExt.getParamList().size()); 
			
			extList.add(templateExt);
		}
		
		modelAndView.addObject("templateList", extList);
		modelAndView.setViewName("/adminPage/nginxTemplate/index");
		return modelAndView;
	}

	@RequestMapping("addOver")
	@ResponseBody
	public JsonResult addOver(Template template,String paramJson) {
		
		if (StrUtil.isEmpty(template.getId())) {
			Long count = templateService.getCountByName(template.getName());
			if (count > 0) {
				return renderError(m.get("templateStr.sameName"));
			}
		} else {
			Long count = templateService.getCountByNameWithOutId(template.getName(), template.getId());
			if (count > 0) {
				return renderError(m.get("templateStr.sameName"));
			}
		}
		
		List<Param> params = JSONUtil.toList(JSONUtil.parseArray(paramJson), Param.class);
		
		templateService.addOver(template, params);

		return renderSuccess();
	}

	@RequestMapping("detail")
	@ResponseBody
	public JsonResult detail(String id) {
		Template template = sqlHelper.findById(id, Template.class);
		TemplateExt templateExt = new TemplateExt();
		templateExt.setTemplate(template);
		
		templateExt.setParamList(templateService.getParamList(template.getId()));
		templateExt.setCount(templateExt.getParamList().size()); 
		
		return renderSuccess(templateExt);
	}

	@RequestMapping("del")
	@ResponseBody
	public JsonResult del(String id) {

		templateService.del(id);
		return renderSuccess();
	}
	
	@RequestMapping("getTemplate")
	@ResponseBody
	public JsonResult getTemplate() {

		return renderSuccess(sqlHelper.findAll(Template.class));
	}
	

}
