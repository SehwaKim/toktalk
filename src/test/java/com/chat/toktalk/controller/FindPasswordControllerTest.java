package com.chat.toktalk.controller;

import com.chat.toktalk.dto.EmailForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FindPasswordControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void processForgotPassword() throws Exception {
        EmailForm emailForm = new EmailForm();
        emailForm.setEmail("noriming@naver.com");
        this.mockMvc.perform(post("/identity/password/forgot")
                .param("email","noriming2@naver.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/identity/password/sent"));

    }

    @Test
    public void displayResetPasswordPage() {
    }

    @Test
    public void processPasswordReset() {
    }

    @Test
    public void displaySendSuccessPage() {
    }
}