package com.deavensoft.springintegrationtraining.example3;

import java.io.File;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.channel.interceptor.WireTap;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.transformer.FileToStringTransformer;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.mail.dsl.Mail;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@SpringBootApplication
@IntegrationComponentScan
@Slf4j
public class FileEmailIntegrationApplication {

	private static final String XML_INBOUND_PATH = "test/xml";

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(FileEmailIntegrationApplication.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		application.run(args);
	}

//
//	private File getDirectory() {
//		try {
//			URL pathUrl = getClass().getClassLoader().getResource(XML_INBOUND_PATH);
//			return new File(pathUrl.toURI());
//		} catch (Exception e) {
//			log.error("Problem reading inbound directory!", e);
//			throw new RuntimeException(e);
//		}
//	}

	private File getDirectory() {
		return new File("c:\\tmp\\si\\xml");
	}

	@Bean(name = PollerMetadata.DEFAULT_POLLER)
	public PollerMetadata poller() {
		return Pollers.fixedRate(500).get();
	}

	@Bean
	public MessageChannel logChannel() {
		return new PublishSubscribeChannel();
	}

	@Bean
	public MessageChannel fileToStringChannel() {
		return MessageChannels.publishSubscribe()
				.interceptor(new WireTap(logChannel()))
				.get();
	}

	@Bean
	@ServiceActivator(inputChannel = "logChannel")
	public MessageHandler logger() {
		LoggingHandler loggingHandler =  new LoggingHandler(LoggingHandler.Level.INFO.name());
		loggingHandler.setLoggerName("CHANNEL_LOGGER");
		return loggingHandler;
	}

	@Bean
	@InboundChannelAdapter(value = "fileInputChannel", poller = @Poller(fixedDelay = "100"))
	public MessageSource<File> xmlFileReadingMessageSource() {
		FileReadingMessageSource source = new FileReadingMessageSource();
		source.setDirectory(getDirectory());
		CompositeFileListFilter<File> compositeFileListFilter= new CompositeFileListFilter<>();
		compositeFileListFilter.addFilter(new AcceptOnceFileListFilter<>());
		compositeFileListFilter.addFilter(new SimplePatternFileListFilter("*.xml"));
		source.setFilter(compositeFileListFilter);
		return source;
	}


	@Bean
	@Transformer(inputChannel = "fileInputChannel", outputChannel = "fileToStringChannel")
	public FileToStringTransformer fileToStringTransformer() {
		return new FileToStringTransformer();
	}

	@Bean
	public IntegrationFlow sendEmailFlow() {
		return IntegrationFlows.from("fileToStringChannel")
				.enrichHeaders(Mail.headers()
						.subject("Hello from Spring Integration")
						.from("foo@bar.baz")
						.toFunction(m -> new String[] { "si.training@spring.com" }))
				.handle(Mail.outboundAdapter("localhost")
								.port(25)
								.protocol("smtp"),
						e -> e.id("sendMailEndpoint"))
				.get();
	}

}
