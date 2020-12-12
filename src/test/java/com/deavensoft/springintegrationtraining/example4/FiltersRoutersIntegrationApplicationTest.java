package com.deavensoft.springintegrationtraining.example4;

import static com.deavensoft.springintegrationtraining.example4.Department.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.MessageBuilder;

@SpringBootTest
@Slf4j
class FiltersRoutersIntegrationApplicationTest {

  @Autowired
  private MessageChannel inputChannel;

  @Autowired
  private PollableChannel itDeptOutputChannel;

  @Autowired
  private PollableChannel financeDeptOutputChannel;

  @Autowired
  private PollableChannel deadLetterOutputChannel;


  @Test
  void testProcessing() throws Exception {

    // given
    Employee employee1 = Employee.builder().department(IT).name("John").build();
    Employee employee2 = Employee.builder().department(IT).name("Merry").build();
    Employee employee3 = Employee.builder().department(FINANCE).name("Peter").build();
    Employee employee4 = Employee.builder().department(ADMINISTRATION).name("Diego").build();
    Employee employee5 = Employee.builder().department(NOT_ASSIGNED).name("Arnold").build();

    EmployeeList list = EmployeeList.builder()
        .employees(Arrays.asList(employee1, employee2, employee3, employee4, employee5))
        .build();


    // when
    inputChannel.send(MessageBuilder.withPayload(list).build());
    Thread.sleep(1000L);


    // then
    assertThat(getOne(itDeptOutputChannel), is(employee1));
    assertThat(getOne(itDeptOutputChannel), is(employee2));
    assertThat(getOne(financeDeptOutputChannel), is(employee3));
    assertThat(getOne(deadLetterOutputChannel), is(employee5));
  }

  private Employee getOne(PollableChannel channel) {
    return (Employee) channel.receive(0).getPayload();
  }
}
