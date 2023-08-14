package com.backtrader.emailconfig;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailService {
	private final Logger logger = LoggerFactory.getLogger(EmailService.class);

	@Bean
	public JavaMailSender javaMailSender() {
		logger.info("mail sender bean started");
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		try {
			Properties properties = mailSender.getJavaMailProperties();
			mailSender.setHost("smtp.gmail.com");
			mailSender.setPort(587);
			mailSender.setUsername("hugaranilkumar37@gmail.com");
			mailSender.setPassword("nlkmbcjgwbugdfnh");
			properties.put("mail.transport.protocol", "smtp");
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.debug", "true");
		} catch (Exception e) {
			logger.error("Exception occurred while creating Mail sender bean", e);
		}
		return mailSender;
	}
	@Bean
	public JavaMailSender mailSender() {
		return new JavaMailSenderImpl();
	}
}
