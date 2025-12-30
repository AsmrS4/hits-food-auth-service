package com.example.user_service.api.controller;

import com.example.user_service.api.config.Specification;
import com.example.user_service.api.enums.Role;
import com.example.user_service.api.reponses.auth.AuthClientResponse;
import com.example.user_service.api.reponses.auth.AuthStaffResponse;
import com.example.user_service.api.reponses.auth.TokenPairResponse;
import com.example.user_service.api.reponses.error.ErrorResponse;
import com.example.user_service.api.requests.auth.ClientLoginRequest;
import com.example.user_service.api.requests.auth.RefreshRequest;
import com.example.user_service.api.requests.auth.StaffLoginRequest;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
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
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Authorization API tests")
@SpringBootTest
@ActiveProfiles("test")
public class AuthControllerTest {
    private static final String AUTH_URL = "http://localhost:8910/api/auth";

    @Nested
    @DisplayName("Test suit for success authorization scenario")
    class PositiveAuthTests {
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
        @DisplayName("Must return success response with profile data for Client user")
        public void authWithClientCredentialsTest() {
            AuthClientResponse response = given()
                    .body(clientRequest)
                    .when()
                        .post("/user/sign-in")
                    .then()
                        .log().all()
                    .extract().as(AuthClientResponse.class);

            assertNotNull(response);
            assertEquals(Role.CLIENT, response.getProfile().getRole());
            assertEquals(UUID.fromString("e763309f-b238-485b-8585-fbb78610c713"), response.getProfile().getId());
        }

        @Test
        @DisplayName("Must return success response with profile data for Operator user")
        public void authWithOperatorCredentialsTest() {
            UUID operatorId = UUID.fromString("27160085-2429-4dd7-8619-bcf1d1f387cf");
            String operatorName = "Иван Оператор";
            String operatorUsername = "test@operator1";
            AuthStaffResponse response = given()
                    .body(operatorRequest)
                    .when()
                        .post("/staff/sign-in")
                    .then()
                        .log().all()
                    .extract()
                        .as(AuthStaffResponse.class);
            assertNotNull(response);
            assertEquals(Role.OPERATOR, response.getProfile().getRole());
            assertEquals(operatorId, response.getProfile().getId());
            assertEquals(operatorName, response.getProfile().getFullName());
            assertEquals(operatorUsername, response.getProfile().getUsername());
        }

        @Test
        @DisplayName("Must return success response with profile data for Admin user")
        public void authWithAdminCredentialsTest() {
            UUID adminId = UUID.fromString("d15e48c8-1783-47b7-9051-45f7a5d0f113");
            String adminName = "Админ Админович";
            String adminUsername = "admin@test";
            AuthStaffResponse response = given()
                    .body(adminRequest)
                    .when()
                    .post("/staff/sign-in")
                    .then()
                    .log().all()
                    .extract()
                    .as(AuthStaffResponse.class);
            assertNotNull(response);
            assertEquals(Role.ADMIN, response.getProfile().getRole());
            assertEquals(adminId, response.getProfile().getId());
            assertEquals(adminName, response.getProfile().getFullName());
            assertEquals(adminUsername, response.getProfile().getUsername());
        }

        @Test
        @DisplayName("Must return new pair of access and refresh token")
        public void refreshSessionTest() {
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
            assertNotNull(tokenPairResponse.getAccessToken());
            assertNotNull(tokenPairResponse.getRefreshToken());
        }
    }

    @Nested
    @DisplayName("Test suit for negative authorization scenario")
    class NegativeAuthTests {
        static ClientLoginRequest clientRequestWithWrongPhone = null;
        static ClientLoginRequest clientRequestWithWrongPassword = null;
        static StaffLoginRequest operatorRequestWithWrongUsername = null;
        static StaffLoginRequest operatorRequestWithWrongPassword = null;
        static StaffLoginRequest adminRequestWithWrongPassword = null;
        static StaffLoginRequest withClientUsernameRequest = null;
        static ClientLoginRequest withStaffPhoneRequest = null;
        @BeforeAll
        public static void setUp() {

            Specification.installRequestSpecification(
                    Specification.requestSpecification(AUTH_URL)
            );
            clientRequestWithWrongPhone = new ClientLoginRequest(
                    "88005553500",
                    "password123"
            );
            clientRequestWithWrongPassword = new ClientLoginRequest(
                    "88005553537",
                    "password12345"
            );
            operatorRequestWithWrongUsername = new StaffLoginRequest(
                    "test@operator123",
                    "password123"
            );
            operatorRequestWithWrongPassword = new StaffLoginRequest(
                    "test@operator1",
                    "password12345"
            );
            adminRequestWithWrongPassword = new StaffLoginRequest(
                    "admin@test",
                    "passwosddsdsdsrd123"
            );
            withClientUsernameRequest = new StaffLoginRequest(
                    "test@client2",
                    "password123"
            );
            withStaffPhoneRequest = new ClientLoginRequest(
                    "88005553535",
                    "password123"
            );
        }

        @Test
        @DisplayName("Must return new 404 error with message: \"User not found\"")
        public void authUserWithNonExistingPhoneNumberTestFailure() {
            ErrorResponse response = given()
                    .body(clientRequestWithWrongPhone)
                    .when()
                    .post("/user/sign-in")
                    .then()
                    .log().all()
                    .statusCode(404)
                    .extract().as(ErrorResponse.class);

            assertNotNull(response);
            assertEquals(404, response.getStatus());
            assertEquals("User not found", response.getError());
        }

        @Test
        @DisplayName("Must return new 404 error")
        public void authUserWithNonExistingUsernameFailure() {
            ErrorResponse response = given()
                    .body(operatorRequestWithWrongUsername)
                    .when()
                    .post("/staff/sign-in")
                    .then()
                    .log().all()
                    .statusCode(404)
                    .extract().as(ErrorResponse.class);

            assertNotNull(response);
            assertEquals(404, response.getStatus());
            assertEquals("User not found", response.getError());
        }

        @Test
        @DisplayName("Must return new 400 error with message: \"Login failed\"")
        public void authClientWithExistingPhoneNumberAndWrongPasswordTestFailure() {
            ErrorResponse response = given()
                    .body(clientRequestWithWrongPassword)
                    .when()
                    .post("/user/sign-in")
                    .then()
                    .log().all()
                    .statusCode(400)
                    .extract().as(ErrorResponse.class);

            assertNotNull(response);
            assertEquals(400, response.getStatus());
            assertEquals("Login failed", response.getError());
        }

        @Test
        @DisplayName("Must return new 400 error with message: \"Login failed\"")
        public void authOperatorWithUsernameAndWrongPasswordTestFailure() {
            ErrorResponse response = given()
                    .body(operatorRequestWithWrongPassword)
                    .when()
                    .post("/staff/sign-in")
                    .then()
                    .log().all()
                    .statusCode(400)
                    .extract().as(ErrorResponse.class);

            assertNotNull(response);
            assertEquals(400, response.getStatus());
            assertEquals("Login failed", response.getError());
        }

        @Test
        @DisplayName("Must return new 400 error with message: \"Login failed\"")
        public void authAdminWithUsernameAndWrongPasswordTestFailure() {
            ErrorResponse response = given()
                    .body(adminRequestWithWrongPassword)
                    .when()
                    .post("/staff/sign-in")
                    .then()
                    .log().all()
                    .statusCode(400)
                    .extract().as(ErrorResponse.class);

            assertNotNull(response);
            assertEquals(400, response.getStatus());
            assertEquals("Login failed", response.getError());
        }

        @Test
        @DisplayName("Must return new 403 error with message: \"Login with client credentials for staff is forbidden\"")
        public void authOperatorWithPhoneNumberTestFailure() {
            ErrorResponse response = given()
                    .body(withStaffPhoneRequest)
                    .when()
                    .post("/user/sign-in")
                    .then()
                    .log().all()
                    .statusCode(403)
                    .extract().as(ErrorResponse.class);

            assertNotNull(response);
            assertEquals(403, response.getStatus());
            assertEquals("Login with client credentials for staff is forbidden", response.getError());
        }


        @Test
        @DisplayName("Must return new 403 error with message: \"Login with username for client is forbidden\"")
        public void authClientWithUsernameTestFailure() {
            ErrorResponse response = given()
                    .body(withClientUsernameRequest)
                    .when()
                    .post("/staff/sign-in")
                    .then()
                    .log().all()
                    .statusCode(403)
                    .extract().as(ErrorResponse.class);

            assertNotNull(response);
            assertEquals(403, response.getStatus());
            assertEquals("Login with username for client is forbidden", response.getError());
        }

        @Test
        @DisplayName("Must return new 401 error with message: \"Refresh not found\"")
        public void refreshSessionWithWrongRefreshTokenTestFailure() {
            String wrongRefresh = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlNzYzMzA5Zi1iMjM4LTQ4NWItODU4NS1mYmI3ODYxMGM3MTMiLCJpYXQiOjE3NjU1NDMxOTgsImV4cCI6MTc2NjQwNzE5OH0.2bjtORFr_PsMxOTjgNOWhHTzMjfHYrgl4Tst1NPCE-4";
            RefreshRequest request = new RefreshRequest(wrongRefresh);
            ErrorResponse response = given()
                    .body(request)
                    .when()
                    .post("/refresh")
                    .then()
                    .log().all()
                    .statusCode(401)
                    .extract().as(ErrorResponse.class);
            assertEquals(401, response.getStatus());
            assertEquals("Refresh not found", response.getError());
        }
    }


}
