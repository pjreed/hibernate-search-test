package com.example.test;

import com.example.config.WebAppConfigurationAware;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MvcResult;

import javax.inject.Inject;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class TestControllerTest extends WebAppConfigurationAware {
    @Inject
    private TestController testController;

    @Inject
    private FilterChainProxy springSecurityFilterChain;

    private Logger logger = LoggerFactory.getLogger(TestControllerTest.class);

    @Test
    public void searchUsers() throws Exception {
        mockMvc.perform(get("/test/addusers")).andExpect(view().name("test/addusers"));
        mockMvc.perform(get("/test/search?number=1")).andExpect(content().json(
                "[{\"id\":2,\"email\":\"user1\",\"role\":\"ROLE_USER\",\"phoneNumbers\":[{\"id\":4,\"number\":\"1-555-1111\"},{\"id\":5,\"number\":\"1-555-1112\"},{\"id\":6,\"number\":\"1-555-1113\"}]}]"));
    }
}
