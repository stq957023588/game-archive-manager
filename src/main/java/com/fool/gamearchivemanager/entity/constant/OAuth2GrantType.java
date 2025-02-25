package com.fool.gamearchivemanager.entity.constant;

public interface OAuth2GrantType {

    String PASSWORD = "password";


    /**
     * 短信验证码模式（自定义）
     */
    String GRANT_TYPE_MOBILE = "authorization_mobile";

    /**
     * 短信验证码
     */
    String SMS_CODE = "sms_code";

    /**
     * 短信验证码默认值
     */
    String SMS_CODE_VALUE = "8888";

}
