package com.javaxiaobear;

import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import org.junit.jupiter.api.Test;

/**
 * 验证码测试类
 * 
 * @author javaxiaobear
 */
public class CaptchaTest {

    @Test
    public void testArithmeticCaptcha() {
        // 测试算术验证码
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(160, 60);
        captcha.setLen(2); // 设置位数
        
        String code = captcha.text(); // 获取运算的结果
        String base64 = captcha.toBase64(); // 获取base64格式的图片
        
        System.out.println("算术验证码结果: " + code);
        System.out.println("Base64长度: " + base64.length());
        System.out.println("Base64前缀: " + base64.substring(0, Math.min(50, base64.length())));
        
        // 验证结果不为空
        assert code != null && !code.trim().isEmpty();
        assert base64 != null && base64.startsWith("data:image/");
    }

    @Test
    public void testSpecCaptcha() {
        // 测试字符验证码
        SpecCaptcha captcha = new SpecCaptcha(160, 60, 4);
        captcha.setCharType(Captcha.TYPE_DEFAULT);
        
        String code = captcha.text(); // 获取验证码的字符
        String base64 = captcha.toBase64(); // 获取base64格式的图片
        
        System.out.println("字符验证码结果: " + code);
        System.out.println("Base64长度: " + base64.length());
        System.out.println("Base64前缀: " + base64.substring(0, Math.min(50, base64.length())));
        
        // 验证结果不为空且长度为4
        assert code != null && code.length() == 4;
        assert base64 != null && base64.startsWith("data:image/");
    }
}
