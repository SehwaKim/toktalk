package com.chat.toktalk;

import com.chat.toktalk.interceptor.RefererSaveInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableRedisHttpSession
@SpringBootApplication
public class ToktalkApplication implements WebMvcConfigurer {


	public static void main(String[] args) {
		SpringApplication.run(ToktalkApplication.class, args);
	}

	@Bean
	public RefererSaveInterceptor loginCheckInterceptor(){
		RefererSaveInterceptor refererSaveInterceptor = new RefererSaveInterceptor();
		return refererSaveInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginCheckInterceptor());
	}
}
