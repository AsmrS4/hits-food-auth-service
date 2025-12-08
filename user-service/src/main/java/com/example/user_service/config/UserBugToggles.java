package com.example.user_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "features")
@Getter
@Setter
public class UserBugToggles {
    private boolean enableSaveEditedUser;
    private boolean enableBadStatusInsteadSuccess;
    private boolean enableChangePassword;
    private boolean enableStaffAuthViaPhoneNumber;
    private boolean enableRefreshSession;
}

