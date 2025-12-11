package com.javaxiaobear.module.common;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.base.Captcha;
import com.javaxiaobear.base.common.constant.CacheConstants;
import com.javaxiaobear.base.common.constant.Constants;
import com.javaxiaobear.base.common.utils.uuid.IdUtils;
import com.javaxiaobear.base.framework.redis.RedisCache;
import com.javaxiaobear.base.framework.web.domain.AjaxResult;
import com.javaxiaobear.module.system.service.ISysConfigService;

/**
 * 验证码操作处理
 *
 * @author javaxiaobear
 */
@RestController
public class CaptchaController
{
    @Autowired
    private RedisCache redisCache;

    // 验证码类型
    @Value("${project.captchaType}")
    private String captchaType;

    @Autowired
    private ISysConfigService configService;

    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    public AjaxResult getCode(HttpServletResponse response) throws IOException
    {
        AjaxResult ajax = AjaxResult.success();
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        ajax.put("captchaEnabled", captchaEnabled);
        if (!captchaEnabled)
        {
            return ajax;
        }

        // 保存验证码信息
        String uuid = IdUtils.simpleUUID();
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;

        Captcha captcha = null;
        String code = null;

        // 生成验证码
        if ("math".equals(captchaType))
        {
            // 算术验证码
            captcha = new ArithmeticCaptcha(130, 48);
            captcha.setLen(2); // 设置位数
            code = captcha.text(); // 获取运算的结果：5
        }
        else if ("char".equals(captchaType))
        {
            // 字符验证码
            captcha = new SpecCaptcha(130, 48, 4);
            captcha.setCharType(Captcha.TYPE_DEFAULT); // 设置字符类型，纯数字、纯字母、字母数字混合
            code = captcha.text(); // 获取验证码的字符
        }

        if (captcha == null || code == null)
        {
            return AjaxResult.error("验证码生成失败");
        }

        // 缓存验证码
        redisCache.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);

        ajax.put("uuid", uuid);
        ajax.put("img", captcha.toBase64()); // 获取base64格式的图片
        return ajax;
    }
}
