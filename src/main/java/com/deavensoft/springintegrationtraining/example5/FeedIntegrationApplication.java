package com.deavensoft.springintegrationtraining.example5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("/integration/integration.xml")
public class FeedIntegrationApplication {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext ctx = new SpringApplication(FeedIntegrationApplication.class).run(args);
		System.out.println("Hit Enter to terminate");
		System.in.read();
		ctx.close();
	}


}
