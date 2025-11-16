package com.example.user_service.domain.dto.about;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditAbout {
    @NotBlank(message = "Company name is required")
    @Size(min = 3, max = 255, message = "The company name length must be between 3 and 255 characters.")
    private String companyName;
    @NotBlank(message = "Mail address is required")
    @Size(min = 3, max = 255, message = "The mail address length must be between 3 and 255 characters.")
    private String mailAddress;
    @NotBlank(message = "Contact email is required")
    @Email(message = "Invalid email format")
    private String contactEmail;
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(?:\\+?7|8)\\d{10}$", message = "Invalid phone number format")
    private String managerPhone;
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(?:\\+?7|8)\\d{10}$", message = "Invalid phone number format")
    private String operatorPhone;
}
