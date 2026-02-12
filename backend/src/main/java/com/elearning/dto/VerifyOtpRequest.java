package com.elearning.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VerifyOtpRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 6)
    private String otpCode;

    @NotBlank
    @Size(min = 6, max = 120)
    private String password;

    // Constructors
    public VerifyOtpRequest() {}

    public VerifyOtpRequest(String email, String otpCode, String password) {
        this.email = email;
        this.otpCode = otpCode;
        this.password = password;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
