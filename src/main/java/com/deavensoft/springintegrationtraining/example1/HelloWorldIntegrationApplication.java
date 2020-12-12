package com.deavensoft.springintegrationtraining.example1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;

@Configuration
@SpringBootApplication
@IntegrationComponentScan
public class HelloWorldIntegrationApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(HelloWorldIntegrationApplication.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		application.run(args);
	}

	@Bean
	public MessageChannel inputChannel() {
		return new DirectChannel();
	}

	@Bean
	public PollableChannel outputChannel() {
		return new QueueChannel(10);
	}

	@Bean
	@ServiceActivator(inputChannel = "inputChannel", outputChannel = "outputChannel")
	public HelloService helloService() {
		return new HelloService();
	}

}
