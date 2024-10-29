package gigedi.dev.infra.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import gigedi.dev.infra.config.jwt.JwtProperties;
import gigedi.dev.infra.config.oauth.GoogleProperties;

@Configuration
@EnableConfigurationProperties({GoogleProperties.class, JwtProperties.class})
public class PropertiesConfig {}