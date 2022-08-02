package com.ade.portfolio;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;

@SpringBootApplication
@ComponentScan("com.ade.portfolio.*")
@MapperScan("com.ade.portfolio.*.mapper")
public class PortpolioApplication extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return super.configure(builder);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		servletContext.setSessionTimeout(30);
		SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
		super.onStartup(servletContext);
	}

	public static void main(String[] args) {
		SpringApplication.run(PortpolioApplication.class, args);
	}

}
