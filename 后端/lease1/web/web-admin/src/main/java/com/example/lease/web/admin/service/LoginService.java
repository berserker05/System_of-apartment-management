package com.example.lease.web.admin.service;

import com.example.lease.web.admin.vo.login.CaptchaVo;
import com.example.lease.web.admin.vo.login.LoginVo;
import com.example.lease.web.admin.vo.system.user.SystemUserInfoVo;

public interface LoginService {

    CaptchaVo getCaptcha();

    String login(LoginVo loginVo);

    SystemUserInfoVo getLoginUserInfo(Long userId);
}
