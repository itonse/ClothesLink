package com.itonse.clotheslink.seller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itonse.clotheslink.seller.dto.SignUpForm;
import com.itonse.clotheslink.seller.repository.SellerRepository;

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
class SellerControllerTest {

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
        mockMvc.perform(MockMvcRequestBuilders.post("/seller/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpForm)))
                .andExpect(MockMvcResultMatchers.content()
                        .string("회원가입이 완료되었습니다."));
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
        mockMvc.perform(MockMvcRequestBuilders.post("/seller/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fistForm)))
                .andExpect(MockMvcResultMatchers.content()
                        .string("회원가입이 완료되었습니다."));

        mockMvc.perform(MockMvcRequestBuilders.post("/seller/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondForm)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value("ALREADY_REGISTERED_SELLER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이미 가입된 이메일 입니다."));

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
        mockMvc.perform(MockMvcRequestBuilders.post("/seller/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(form)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").value("연락처는 필수 항목 입니다."));
    }

}