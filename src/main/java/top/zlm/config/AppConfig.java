package top.zlm.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 项目配置类
 */
@Configuration
@ComponentScan(basePackages = {"top.zlm.config","top.zlm.service"})
public class AppConfig {
    // ...
}

