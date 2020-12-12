package com.deavensoft.springintegrationtraining.example2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.ws.SimpleWebServiceOutboundGateway;
import org.springframework.integration.ws.WebServiceHeaders;
import org.springframework.integration.xml.transformer.XPathTransformer;

@Configuration
@SpringBootApplication
@IntegrationComponentScan
public class SoapWsIntegrationApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(SoapWsIntegrationApplication.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		application.run(args);
	}

	@MessagingGateway
	public interface TempConverter {

		@Gateway(requestChannel = "convert.input")
		float fahrenheitToCelcius(float fahren);

	}

	@Bean
	public IntegrationFlow convert() {
		return f -> f
				.transform(payload ->
						"<FahrenheitToCelsius xmlns=\"https://www.w3schools.com/xml/\">"
								+     "<Fahrenheit>" + payload + "</Fahrenheit>"
								+ "</FahrenheitToCelsius>")
				.enrichHeaders(h -> h
						.header(WebServiceHeaders.SOAP_ACTION,
								"https://www.w3schools.com/xml/FahrenheitToCelsius"))
				.handle(new SimpleWebServiceOutboundGateway(
						"https://www.w3schools.com/xml/tempconvert.asmx"))
				.transform(new XPathTransformer("/*[local-name()=\"FahrenheitToCelsiusResponse\"]"
						+ "/*[local-name()=\"FahrenheitToCelsiusResult\"]"));
	}

}
