package com.example.user_service.api.controller;

import com.example.user_service.api.config.Specification;
import com.example.user_service.api.enums.Role;
import com.example.user_service.api.reponses.auth.AuthClientResponse;
import com.example.user_service.api.reponses.auth.TokenPairResponse;
import com.example.user_service.api.requests.auth.ClientLoginRequest;
import com.example.user_service.api.requests.auth.RefreshRequest;
import com.example.user_service.api.requests.auth.StaffLoginRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@DisplayName("Authorization bugs API tests")
@SpringBootTest
@ActiveProfiles("test")
public class AuthControllerBugsTest {
    private static final String AUTH_URL = "http://localhost:8910/api/auth";

    @Nested
    @DisplayName("Test suit for checking feature flags")
    class AuthBugTests {
        static ClientLoginRequest clientRequest = null;
        static StaffLoginRequest operatorRequest = null;
        static StaffLoginRequest adminRequest = null;

        @BeforeAll
        public static void setUp() {
            Specification.installSpecifications(
                    Specification.requestSpecification(AUTH_URL),
                    Specification.responseSpecificationOk()
            );
            clientRequest = new ClientLoginRequest(
                    "88005553537",
                    "password123"
            );
            operatorRequest = new StaffLoginRequest(
                    "test@operator1",
                    "password123"
            );
            adminRequest = new StaffLoginRequest(
                    "admin@test",
                    "password123"
            );
        }

        @Test
        @DisplayName("Must return success response with access && refresh tokens")
        public void authEmptyResponseBugTest() {
            AuthClientResponse response = given()
                    .body(clientRequest)
                    .when()
                    .post("/user/sign-in")
                    .then()
                    .log().all()
                    .extract().as(AuthClientResponse.class);

            assertNotNull(response);
            assertNotNull(response.getAccessToken());
            assertNotNull(response.getRefreshToken());
        }
        @Test
        @DisplayName("Must return new pair of access and refresh token in correct order")
        public void tokenPairOrderBugTest() {
            AuthClientResponse response = given()
                    .body(clientRequest)
                    .when()
                    .post("/user/sign-in")
                    .then()
                    .log().all()
                    .extract().as(AuthClientResponse.class);

            String refreshTokenAfterAuth = response.getRefreshToken();
            RefreshRequest request = new RefreshRequest(refreshTokenAfterAuth);

            TokenPairResponse tokenPairResponse = given()
                    .body(request)
                    .when()
                    .post("/refresh")
                    .then()
                    .log().all()
                    .statusCode(200)
                    .extract().as(TokenPairResponse.class);

            assertNotNull(tokenPairResponse);
        }

        @Test
        @DisplayName("Must return not expired refresh token")
        public void refreshTokenExpirationBugTest() {
            AuthClientResponse response = given()
                    .body(clientRequest)
                    .when()
                    .post("/user/sign-in")
                    .then()
                    .log().all()
                    .extract().as(AuthClientResponse.class);

            String refreshTokenAfterAuth = response.getRefreshToken();
            RefreshRequest request = new RefreshRequest(refreshTokenAfterAuth);

            TokenPairResponse tokenPairResponse = given()
                    .body(request)
                    .when()
                    .post("/refresh")
                    .then()
                    .log().all()
                    .statusCode(200)
                    .extract().as(TokenPairResponse.class);

            assertNotNull(tokenPairResponse);
        }
    }
}
