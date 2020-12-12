package com.deavensoft.springintegrationtraining.example1;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;

@SpringBootTest
@Slf4j
class HelloWorldIntegrationApplicationTests {

	@Autowired
	private MessageChannel inputChannel;

	@Autowired
	private PollableChannel outputChannel;

	@ParameterizedTest
	@ValueSource(strings = {"World", "enjoy.ing", "deavensoft"})
	void whenSendWorld_ShouldPrintHelloWorld(String input) {
		inputChannel.send(new GenericMessage<>(input));
		Object payload = outputChannel.receive(0).getPayload();

		assertThat(payload.toString(), is("Hello " + input));

		log.info("==> HelloWorldDemo: {}", payload);
	}

}
