package com.example.user_service.domain.dto.about;

import lombok.Data;

import java.util.UUID;

@Data
public class AboutDTO {
    private UUID id;
    private String companyName;
    private String mailAddress;
    private String contactEmail;
    private String managerPhone;
    private String operatorPhone;
}
