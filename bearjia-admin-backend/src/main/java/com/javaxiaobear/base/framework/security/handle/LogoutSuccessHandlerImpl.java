package com.javaxiaobear.base.framework.security.handle;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import com.alibaba.fastjson2.JSON;
import com.javaxiaobear.base.common.constant.Constants;
import com.javaxiaobear.base.common.utils.ServletUtils;
import com.javaxiaobear.base.common.utils.StringUtils;
import com.javaxiaobear.base.framework.manager.AsyncManager;
import com.javaxiaobear.base.framework.manager.factory.AsyncFactory;
import com.javaxiaobear.base.framework.security.LoginUser;
import com.javaxiaobear.base.framework.security.service.TokenService;
import com.javaxiaobear.base.framework.web.domain.AjaxResult;

/**
 * 自定义退出处理类 返回成功
 * 
 * @author javaxiaobear
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler
{
    @Autowired
    private TokenService tokenService;

    /**
     * 退出处理
     * 
     * @return
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser))
        {
            String userName = loginUser.getUsername();
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
            // 记录用户退出日志
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(userName, Constants.LOGOUT, "退出成功"));
        }
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.success("退出成功")));
    }
}
