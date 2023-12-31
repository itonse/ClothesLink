package com.itonse.clotheslink.user.service.Impl;

import com.itonse.clotheslink.common.UserType;
import com.itonse.clotheslink.common.UserVo;
import com.itonse.clotheslink.config.security.JwtTokenProvider;
import com.itonse.clotheslink.user.domain.Customer;
import com.itonse.clotheslink.user.dto.SignInDto;
import com.itonse.clotheslink.user.dto.SignUpDto;
import com.itonse.clotheslink.user.dto.UserInfoResponse;
import com.itonse.clotheslink.user.repository.CustomerRepository;
import com.itonse.clotheslink.user.service.CustomerAuthService;
import com.itonse.clotheslink.exception.CustomException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.itonse.clotheslink.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class CustomerAuthServiceImpl implements CustomerAuthService {

    private final CustomerRepository customerRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public UserInfoResponse signUp(SignUpDto dto) {

        customerRepository.findByEmail(dto.getEmail()).ifPresent(e -> {
            throw new CustomException(ALREADY_REGISTERED_CUSTOMER);
        });

        Customer customer = SignUpDto.toCustomerEntity(dto);
        customerRepository.save(customer);

        return UserInfoResponse.builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .build();
    }

    @Override
    public String signIn(SignInDto dto) {
        Customer customer = customerRepository.findByEmailAndPassword(dto.getEmail(), dto.getPassword())
                        .orElseThrow(() -> new CustomException(LOGIN_FAIL));

        return jwtTokenProvider.createToken(
                customer.getEmail(), customer.getId(), UserType.CUSTOMER);
    }

    @Override
    public Customer findCustomerByToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new CustomException(INVALID_TOKEN);
        }

        UserVo vo = jwtTokenProvider.getUserInfo(token);

        if (!vo.getUserType().equals(UserType.CUSTOMER)) {
            throw new CustomException(USER_TYPE_MISMATCH);
        }

        return customerRepository.findByIdAndEmail(vo.getId(), vo.getEmail())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }

    @Override
    public Customer validateCustomer(String token) {
        Customer customer = findCustomerByToken(token);

        if (!customer.isAuthenticated()) {
            throw new CustomException(UNAUTHORIZED_CUSTOMER);
        }

        return customer;
    }
}
