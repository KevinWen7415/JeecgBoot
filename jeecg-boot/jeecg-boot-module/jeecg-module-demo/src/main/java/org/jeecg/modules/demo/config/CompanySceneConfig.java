package org.jeecg.modules.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author wenhao
 * @version 1.0
 * @date 2025-06-15 17:24
 */
@Configuration
@ConfigurationProperties(prefix = "custom.company-scene")
@Setter
@Getter
public class CompanySceneConfig {


    private String photoSharedPath;

}
