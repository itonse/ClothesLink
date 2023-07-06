package com.itonse.clotheslink.admin.service.Impl;

import com.itonse.clotheslink.admin.domain.Mail;
import com.itonse.clotheslink.admin.dto.UserInfo;
import com.itonse.clotheslink.admin.repository.MailRepository;
import com.itonse.clotheslink.admin.service.MailAuthService;
import com.itonse.clotheslink.admin.service.TokenService;
import com.itonse.clotheslink.common.*;
import com.itonse.clotheslink.common.strategy.MailAuthContext;
import com.itonse.clotheslink.common.strategy.CustomerStrategy;
import com.itonse.clotheslink.common.strategy.SellerStrategy;
import com.itonse.clotheslink.config.security.JwtTokenProvider;
import com.itonse.clotheslink.exception.CustomException;
import com.itonse.clotheslink.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.itonse.clotheslink.exception.ErrorCode.ALREADY_VERIFIED;

@RequiredArgsConstructor
@Service
public class MailAuthServiceImpl implements MailAuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MailRepository mailRepository;
    private final TokenService tokenService;
    private final JavaMailSender javaMailSender;

    private final SellerStrategy sellerStrategy;
    private final CustomerStrategy customerStrategy;
    private final MailAuthContext mailAuthContext;

    @Override
    public boolean verifyUserAuth(String token) {
        UserType userType = jwtTokenProvider.getUserInfo(token).getUserType();

        if (userType.equals(UserType.SELLER)) {
            mailAuthContext.setUserTypeStrategy(sellerStrategy);
            return mailAuthContext.isAuthenticated(token);
        }

        if (userType.equals(UserType.CUSTOMER)) {
            mailAuthContext.setUserTypeStrategy(customerStrategy);
            return mailAuthContext.isAuthenticated(token);
        }

        throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    @Override
    @Transactional
    public UserInfo sendMail(String token) {
        tokenService.validateToken(token);

        if (verifyUserAuth(token)) {
            throw new CustomException(ALREADY_VERIFIED);
        }

        String randomKey = RandomStringUtils.randomAlphanumeric(5);

        UserVo vo = jwtTokenProvider.getUserInfo(token);


        Optional<Mail> optionalMail
                = mailRepository.findByEmailAndUserType(vo.getEmail(), vo.getUserType());

        if (optionalMail.isPresent()) {
            Mail existMail = optionalMail.get();
            existMail.setAuthCode(randomKey);
            existMail.setExpiredAt(LocalDateTime.now().plusMinutes(30));
        } else {
            Mail newMail = Mail.builder()
                    .userType(vo.getUserType())
                    .authCode(randomKey)
                    .email(vo.getEmail())
                    .expiredAt(LocalDateTime.now().plusMinutes(30))
                    .build();
            mailRepository.save(newMail);
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(vo.getEmail());
        mailMessage.setSubject("[From Clotheslink] 이메일 인증을 위한 인증코드가 발급되었습니다.");
        mailMessage.setText("아래의 인증코드를 복사하여 이메일 인증을 완료해주세요.\n"
                + "인증코드: " + randomKey + "\n"
                + "개인정보 보호를 위해 이메일 인증코드는 30분간 유효합니다.");

        javaMailSender.send(mailMessage);

        return UserInfo.builder()
                .userType(vo.getUserType())
                .id(vo.getId())
                .email(vo.getEmail())
                .build();
    }
}