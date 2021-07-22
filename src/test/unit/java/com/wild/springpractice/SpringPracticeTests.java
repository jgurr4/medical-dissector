package com.wild.springpractice;

import com.wild.springpractice.student.StudentController;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Source: https://stackoverflow.com/questions/52056680/unit-test-java-api-http-requests
@SpringBootTest
@RunWith(SpringRunner.class)
@WebMvcTest(value = StudentController.class)
class SpringPracticeTests {
//  @MockBean
//  private OfferRepository offerRepository;
  // These are used when you have a couchbase or mariadb test you need to run.
  // mock beans are just like fake verticles.

  @Autowired
  private WebApplicationContext webApplicationContext;

  protected MockMvc mockMvc;

  @Before
  public void setup() throws Exception
  {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  void contextLoads() {

  }

  @Test
  public void getAllOffers() throws Exception {

    this.mockMvc.perform(get("/api/student"))
      .andExpect(status().is2xxSuccessful())
      .andDo(print());
  }

}
