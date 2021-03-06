package com.zednight.controller.adminPage;

import cn.craccd.sqlHelper.utils.ConditionAndWrapper;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.util.StrUtil;
import com.zednight.config.VersionConfig;
import com.zednight.model.Admin;
import com.zednight.model.Remote;
import com.zednight.service.AdminService;
import com.zednight.service.CreditService;
import com.zednight.service.SettingService;
import com.zednight.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录页
 *
 * @author Administrator
 */
@RequestMapping("/adminPage/login")
@Controller
public class LoginController extends BaseController {
    @Autowired
    AdminService adminService;
    @Autowired
    CreditService creditService;
    @Autowired
    VersionConfig versionConfig;
    @Autowired
    AuthUtils authUtils;
    @Value("${project.version}")
    String currentVersion;

    @Autowired
    SettingService settingService;

    @RequestMapping("map")
    public ModelAndView map(ModelAndView modelAndView) {

        modelAndView.setViewName("/adminPage/login/map");
        return modelAndView;
    }

    @RequestMapping("")
    public ModelAndView admin(ModelAndView modelAndView) {

        modelAndView.addObject("adminCount", sqlHelper.findCountByQuery(new ConditionAndWrapper(), Admin.class));
        modelAndView.setViewName("/adminPage/login/index");
        return modelAndView;
    }

    @RequestMapping("loginOut")
    public String loginOut(HttpSession httpSession) {
        httpSession.removeAttribute("isLogin");
        return "redirect:/adminPage/login";
    }

    @RequestMapping("noServer")
    public ModelAndView noServer(ModelAndView modelAndView) {
        modelAndView.setViewName("/adminPage/login/noServer");
        return modelAndView;
    }

    @RequestMapping("login")
    @ResponseBody
    public JsonResult submitLogin(String name, String pass, String code, String authCode, HttpSession httpSession) {

        // 验证码
        String imgCode = (String) httpSession.getAttribute("imgCode");
        if (StrUtil.isEmpty(imgCode) || StrUtil.isNotEmpty(imgCode) && !imgCode.equalsIgnoreCase(code)) {
            return renderError(m.get("loginStr.backError1")); // 验证码不正确
        }

        // 用户名密码
        Admin admin = adminService.login(name, pass);
        if (admin == null) {
            return renderError(m.get("loginStr.backError2")); // 用户名密码错误
        }

        // 登录成功
        httpSession.setAttribute("localType", "local");
        httpSession.setAttribute("isLogin", true);
        httpSession.removeAttribute("imgCode"); // 立刻销毁验证码

        // 检查更新
        versionConfig.getNewVersion();

        return renderSuccess();
    }


    @ResponseBody
    @RequestMapping("getAuth")
    public JsonResult getAuth(String name, String pass, String code, Integer remote, HttpSession httpSession) {
        // 验证码
        if (remote == null) {
            String imgCode = (String) httpSession.getAttribute("imgCode");
            if (StrUtil.isEmpty(imgCode) || StrUtil.isNotEmpty(imgCode) && !imgCode.equalsIgnoreCase(code)) {
                return renderError(m.get("loginStr.backError1")); // 验证码不正确
            }
        }

        // 用户名密码
        Admin admin = adminService.login(name, pass);
        if (admin == null) {
            return renderError(m.get("loginStr.backError2")); // 用户名密码错误
        }

        Admin ad = new Admin();
        ad.setAuth(admin.getAuth());
        ad.setKey(admin.getKey());

        return renderSuccess(ad);
    }

    @ResponseBody
    @RequestMapping("getCredit")
    public JsonResult getCredit(String name, String pass, String code, String auth) {
        // 用户名密码
        Admin admin = adminService.login(name, pass);
        if (admin == null) {
            return renderError(m.get("loginStr.backError2"));  // 用户名密码错误
        }

        if (!admin.getAuth()) {
            String imgCode = settingService.get("remoteCode");
            if (StrUtil.isEmpty(imgCode) || StrUtil.isNotEmpty(imgCode) && !imgCode.equalsIgnoreCase(code)) {
                return renderError(m.get("loginStr.backError1")); // 验证码不正确
            }
        } else {
            if (!authUtils.testKey(admin.getKey(), auth)) {
                return renderError(m.get("loginStr.backError6")); // 身份码不正确
            }
        }

        settingService.remove("remoteCode"); // 立刻销毁验证码

        Map<String, String> map = new HashMap<String, String>();
        map.put("creditKey", creditService.make());
        map.put("system", SystemTool.getSystem());
        return renderSuccess(map);

    }

    @ResponseBody
    @RequestMapping("getLocalType")
    public JsonResult getLocalType(HttpSession httpSession) {
        String localType = (String) httpSession.getAttribute("localType");
        if (StrUtil.isNotEmpty(localType)) {
            if ("local".equals(localType)) {
                return renderSuccess(m.get("remoteStr.local"));
            } else {
                Remote remote = (Remote) httpSession.getAttribute("remote");
                if (StrUtil.isNotEmpty(remote.getDescr())) {
                    return renderSuccess(remote.getDescr());
                }

                return renderSuccess(remote.getIp() + ":" + remote.getPort());
            }
        }

        return renderSuccess("");
    }

    @ResponseBody
    @RequestMapping("changeLang")
    public JsonResult changeLang() {
        if (settingService.get("lang") != null && settingService.get("lang").equals("en_US")) {
            settingService.set("lang", "");
        } else {
            settingService.set("lang", "en_US");
        }

        return renderSuccess();
    }

    @RequestMapping("addAdmin")
    @ResponseBody
    public JsonResult addAdmin(String name, String pass) {

        Long adminCount = sqlHelper.findCountByQuery(new ConditionAndWrapper(), Admin.class);
        if (adminCount > 0) {
            return renderError(m.get("loginStr.backError4"));
        }

        if (!(PwdCheckUtil.checkContainUpperCase(pass) && PwdCheckUtil.checkContainLowerCase(pass) && PwdCheckUtil.checkContainDigit(pass) && PwdCheckUtil.checkPasswordLength(pass, "8", "100"))) {
            return renderError(m.get("loginStr.tips"));
        }

        adminService.addAdmin(name, pass);
        return renderSuccess();
    }

    @RequestMapping("/getCode")
    public void getCode(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(100, 40);
        captcha.setGenerator(new RandomGenerator("0123456789", 4));

        String createText = captcha.getCode();
        httpServletRequest.getSession().setAttribute("imgCode", createText);

        captcha.write(httpServletResponse.getOutputStream());
    }

    @RequestMapping("/getRemoteCode")
    public void getRemoteCode(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(100, 40);
        captcha.setGenerator(new RandomGenerator("0123456789", 4));

        String createText = captcha.getCode();
        settingService.set("remoteCode", createText);

        captcha.write(httpServletResponse.getOutputStream());
    }
}
