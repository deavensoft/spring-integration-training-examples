package com.deavensoft.springintegrationtraining.example2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.deavensoft.springintegrationtraining.example2.SoapWsIntegrationApplication.TempConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class SoapWsIntegrationApplicationTest {

  @Autowired
  private TempConverter converter;

  @ParameterizedTest
  @ValueSource(floats = {32.0f, 68.0f, 212.0f})
  void whenSendFarenheit_ShouldConvertToCelsius(float fahrenheit) {
    float celsius = converter.fahrenheitToCelcius(fahrenheit);

    assertThat(celsius, is((fahrenheit - 32) * 5 / 9));

    log.info("{} F = {} C", fahrenheit, celsius);
  }

}
