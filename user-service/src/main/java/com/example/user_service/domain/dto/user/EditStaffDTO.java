package com.example.user_service.domain.dto.user;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditStaffDTO {
    @Nullable
    @Size(min = 3, max = 255, message = "The fullName length must be between 3 and 255 characters.")
    private String fullName;
    @Nullable
    @Pattern(regexp = "^(?:\\+?7|8)\\d{10}$", message = "Invalid phone number format")
    private String phone;
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "The username length must be between 3 and 100 characters.")
    private String username;
}
