package com.deavensoft.springintegrationtraining.example4;

import java.util.Collections;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Filter;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.annotation.Splitter;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.integration.ws.SimpleWebServiceOutboundGateway;
import org.springframework.integration.ws.WebServiceHeaders;
import org.springframework.integration.xml.transformer.XPathTransformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;

@Configuration
@SpringBootApplication
@IntegrationComponentScan
public class FiltersRoutersIntegrationApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(FiltersRoutersIntegrationApplication.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		application.run(args);
	}

	@Bean(name = PollerMetadata.DEFAULT_POLLER)
	public PollerMetadata poller() {
		return Pollers.fixedRate(100).get();
	}

	@Bean
	public MessageChannel inputChannel() {
		return new DirectChannel();
	}

	@Bean
	public PollableChannel filterChannel() {
		return new QueueChannel(10);
	}

	@Bean
	public PollableChannel employeeChannel() {
		return new QueueChannel(10);
	}

	@Bean
	public PollableChannel itDeptOutputChannel() {
		return new QueueChannel(10);
	}

	@Bean
	public PollableChannel financeDeptOutputChannel() {
		return new QueueChannel(10);
	}

	@Bean
	public PollableChannel deadLetterOutputChannel() {
		return new QueueChannel(10);
	}

	@Splitter(inputChannel = "inputChannel", outputChannel = "employeeChannel")
	public List<Employee> split(EmployeeList list) {
		return list.getEmployees();
	}

	@Filter(inputChannel = "employeeChannel", outputChannel = "filterChannel")
	public boolean filter(Employee employee) {
		return !employee.getDepartment().equals(Department.ADMINISTRATION);
	}

	@Router(inputChannel = "filterChannel")
	public List<String> route(Employee employee) {
		if (employee.getDepartment().equals(Department.IT)) {
			return Collections.singletonList("itDeptOutputChannel");
		} else if (employee.getDepartment().equals(Department.FINANCE)) {
			return Collections.singletonList("financeDeptOutputChannel");
		} else {
			return Collections.singletonList("deadLetterOutputChannel");
		}
	}

}
