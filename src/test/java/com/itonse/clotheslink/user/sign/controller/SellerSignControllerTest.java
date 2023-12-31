package com.itonse.clotheslink.user.sign.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itonse.clotheslink.user.dto.SignInForm;
import com.itonse.clotheslink.user.dto.SignUpForm;
import com.itonse.clotheslink.user.repository.SellerRepository;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureMockMvc
@SpringBootTest
class SellerSignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SellerRepository sellerRepository;

    @BeforeEach
    void resetDataBase() {
        sellerRepository.deleteAll();
    }

    @Test
    void signUpSuccess() throws Exception {
        // given
        SignUpForm signUpForm = SignUpForm.builder()
                .email("aaa@naver.com")
                .password("11223344")
                .phone("010-1111-2222")
                .build();

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.post("/signup/seller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpForm)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email")
                        .value("aaa@naver.com"));
    }

    @Test
    void signUpFailCustomException() throws Exception {
        // given
        SignUpForm fistForm = SignUpForm.builder()
                .email("aaa@naver.com")
                .password("11223344")
                .phone("010-3333-4444")
                .build();

        SignUpForm secondForm = SignUpForm.builder()
                .email("aaa@naver.com")
                .password("77778888")
                .phone("010-5555-6666")
                .build();

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.post("/signup/seller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fistForm)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email")
                        .value("aaa@naver.com"));

        mockMvc.perform(MockMvcRequestBuilders.post("/signup/seller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondForm)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("ALREADY_REGISTERED_SELLER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("이미 가입된 이메일 입니다."));

    }

    @Test
    void signUpFailValidException() throws Exception {
        // given
        SignUpForm form = SignUpForm.builder()
                .email("aaa@naver.com")
                .password("11223344")
                .build();

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.post("/signup/seller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(form)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()")
                        .value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]")
                        .value("연락처는 필수 항목 입니다."));
    }

    @Test
    void signInSuccess() throws Exception {
        // given
        SignUpForm signUpForm = SignUpForm.builder()
                .email("aaa@naver.com")
                .password("11223344")
                .phone("010-2222-4444")
                .build();

        SignInForm signInForm = SignInForm.builder()
                .email("aaa@naver.com")
                .password("11223344")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/signup/seller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpForm)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.post("/signin/seller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInForm)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.TOKEN")
                        .value(Matchers.notNullValue()));
    }

    @Test
    void signInFailCustomException() throws Exception {
        // given
        SignInForm signInForm = SignInForm.builder()
                .email("bbb@naver.com")
                .password("11223344")
                .build();

        // when
        // then
        mockMvc.perform(MockMvcRequestBuilders.post("/signin/seller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInForm)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("LOGIN_FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("일치하는 회원정보가 없습니다."));

    }

}