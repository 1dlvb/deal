package com.fintech.deal.config;

import com.fintech.deal.auditor.AuditorAwareImpl;
import com.fintech.deal.filter.JwtFilter;
import com.fintech.deal.service.TokenService;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Objects;

/**
 * Configuration file for spring boot application
 * @author Matushkin Anton
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class DealConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Deal API")
                        .description("An API for serving operations with deals.")
                        .version("0.0.1-SNAPSHOT")
                );
    }

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtTokenFilter(TokenService tokenService) {
        FilterRegistrationBean<JwtFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtFilter(tokenService));
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

}
