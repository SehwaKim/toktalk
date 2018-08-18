package com.chat.toktalk;

import com.chat.toktalk.controller.RegisterController;
import com.chat.toktalk.service.UserService;
import com.chat.toktalk.validator.RegisterValidator;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Log4j2

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerServiceMockTest {

    @MockBean
    private UserService userService;
    @Autowired
    private RegisterValidator registerValidator;

    private MockMvc mockMvc;

    // 테스트 메소드 실행전 셋업 메소드입니다.
    @Before
    public void setup(){
        // 이곳에서 UserController를 MockMvc 객체로 만듭니다.
        this.mockMvc = MockMvcBuilders.standaloneSetup(new RegisterController(userService,registerValidator)).build();
    }

    @Test
    public void 회원가입페이지_로딩테스트() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/identity/register"))
                .andExpect(MockMvcResultMatchers.model().hasNoErrors())
                .andExpect(MockMvcResultMatchers.view().name("users/register"))
                .andExpect(status().isOk());
    }

    @Test
    public void 회원가입테스트() throws Exception {
       this.mockMvc
               .perform(
                       post("/identity/register")
                       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                       .param("nickname","포로리")
                       .param("email","by@naver.com")
                       .param("password","1234")
               )
               .andExpect(redirectedUrl("/"))
               .andExpect(status().is3xxRedirection());

    }
    @Test
    public void 회원가입_존재하는_이메일_테스트() throws Exception {
//        MvcResult result = this.mockMvc
//                .perform(
//                        post("/identity/register")
//                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                        .param("nickname","포로리")
//                        .param("email","noriming2@gmail.com")
//                        .param("password","1234")
//                )
////                .andExpect(model().hasErrors())
////                .andExpect(model().attributeHasErrors("email"))
//                .andReturn();
//        Assert.assertTrue(result.getResponse().getErrorMessage().matches("이미 존재하는 계정 입니다."));

    }

}
