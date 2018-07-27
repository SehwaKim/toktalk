package com.chat.toktalk.config;

import com.chat.toktalk.filter.AlreadyLoginCheckFilter;
import com.chat.toktalk.security.TokTalkUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.Filter;
import javax.sql.DataSource;

@Configuration
public class WebApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
    private final Filter filter;

    public WebApplicationSecurityConfig(Filter googleFilter) {
        this.filter = googleFilter;
    }

    @Autowired
    DataSource dataSource;

    @Autowired
    TokTalkUserDetailsService tokTalkUserDetailsService;


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers(new AntPathRequestMatcher("/**.html"))
                .requestMatchers(new AntPathRequestMatcher("/static/**"))
                .requestMatchers(new AntPathRequestMatcher("/public/**"));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/users/login")
                .and().authorizeRequests()
                .antMatchers("/identity/**").permitAll()
                .antMatchers("/users/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/api/**").permitAll()
                .anyRequest().hasAnyRole("ADMIN", "USER")
                .and().headers().frameOptions().disable()
                .and().formLogin()
                .loginProcessingUrl("/users/login")
                .loginPage("/users/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .and().rememberMe()
                .rememberMeCookieName("remember-me")
                .userDetailsService(tokTalkUserDetailsService)
                .tokenValiditySeconds(24 * 60 * 60) //1day
                .tokenRepository(persistentTokenRepository())
                .and().csrf()
                .disable()
                .addFilterBefore(new AlreadyLoginCheckFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(filter, BasicAuthenticationFilter.class);
    }


    @Bean
    public UserAuthenticationSuccessHandler userAuthenticationSuccessHandler() {
        return new UserAuthenticationSuccessHandler();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}