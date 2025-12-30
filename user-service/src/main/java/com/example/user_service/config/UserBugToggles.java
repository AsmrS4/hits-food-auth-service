package com.example.user_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "feature")
@RefreshScope
@Getter
@Setter
public class UserBugToggles {
    private boolean enableSaveEditedUser;
    private boolean enableBadStatusInsteadSuccess;
    private boolean enableChangePassword;
    private boolean enableStaffAuthViaPhoneNumber;
    private boolean enableRefreshSession;

    private boolean enableInternalServerError;
    private boolean enableSaveNullableProperties;

    private boolean enableReturnEmptyResult;
    private boolean enableMixedUpTokens;
    private boolean enableExpiredAccessToken;
    private boolean enableOperatorsWithClientsBug;
}

