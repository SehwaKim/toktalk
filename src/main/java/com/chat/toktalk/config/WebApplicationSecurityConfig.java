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
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.Filter;

@Configuration
public class WebApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
    private final Filter filter;


    @Autowired
    TokTalkUserDetailsService tokTalkUserDetailsService;
    public WebApplicationSecurityConfig(Filter googleFilter) {
        this.filter = googleFilter;
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers(new AntPathRequestMatcher("/**.html"))
                .requestMatchers(new AntPathRequestMatcher("/static/**"));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/users/login")
                .and().authorizeRequests()
                .antMatchers("/users/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/api/**").permitAll()
                .anyRequest().fullyAuthenticated()
                .and().headers().frameOptions().disable()
                .and()
                    .formLogin()
                        .loginProcessingUrl("/users/login")
                        .loginPage("/users/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(userAuthenticationSuccessHandler())
                //.and().rememberMe().rememberMeParameter("remember-me")
              //  .failureHandler(new UserAuthenticationFailureHandler())
                .and()
                    .csrf().disable()
                    .addFilterBefore(new AlreadyLoginCheckFilter(), BasicAuthenticationFilter.class)
                    .addFilterBefore(filter, BasicAuthenticationFilter.class);
    }


     @Bean
     public UserAuthenticationSuccessHandler userAuthenticationSuccessHandler(){
         return new UserAuthenticationSuccessHandler();
     }
}
