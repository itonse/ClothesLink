package com.itonse.clotheslink.admin.repository;

import com.itonse.clotheslink.admin.domain.Mail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MailRepository extends JpaRepository<Mail, Long> {

    Optional<Mail> findByEmail(String email);
}
