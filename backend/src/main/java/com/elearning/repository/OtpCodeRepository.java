package com.elearning.repository;

import com.elearning.model.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findByEmailAndOtpCodeAndIsUsedFalse(String email, String otpCode);
    
    void deleteByEmail(String email);
}
