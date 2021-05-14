package cn.lttc.configdemo.component;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


/**
 * SourceValue自定义配置组件，用于测试 @PropertySource
 *
 * @author：谢 鑫
 * @create：2021-04-28
 */
@Component
@PropertySource(value = "classpath:application-source.properties")
@ConfigurationProperties(prefix = "com.example")
public class SourceValue {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String username) {
        this.userName = username;
    }

    public String getPassWord() {
        return password;
    }

    public void setPassWord(String password) {
        this.password = password;
    }
}
