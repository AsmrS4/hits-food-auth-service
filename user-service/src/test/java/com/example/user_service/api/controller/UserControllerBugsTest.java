package com.example.user_service.api.controller;

import com.example.user_service.api.config.Specification;
import com.example.user_service.api.dto.ClientDto;
import com.example.user_service.api.enums.Role;
import com.example.user_service.api.reponses.auth.AuthClientResponse;
import com.example.user_service.api.reponses.auth.AuthStaffResponse;
import com.example.user_service.api.requests.auth.ClientLoginRequest;
import com.example.user_service.api.requests.auth.StaffLoginRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@DisplayName("User API tests")
public class UserControllerBugsTest {
    private static final String BASE_URL = "http://localhost:8910/api";;

    @Nested
    @DisplayName("Test suit for positive scenarios")
    class PositiveUserTests {
        static ClientLoginRequest clientRequest = null;
        static StaffLoginRequest operatorRequest = null;
        static StaffLoginRequest adminRequest = null;

        @BeforeAll
        public static void setUp() {
            Specification.installSpecifications(
                    Specification.requestSpecification(BASE_URL),
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
        @DisplayName("Must return success response with client user profile")
        public void getClientProfileTest() {
            String accessToken = getAccessTokenAfterAuthorization(Role.CLIENT);
            ClientDto response = given()
                    .when()
                    .header("Authorization", "Bearer " + accessToken)
                    .get("/users/me")
                    .then()
                    .log().all()
                    .statusCode(200)
                    .extract().as(ClientDto.class);

            assertNotNull(response);

        }
        private String getAccessTokenAfterAuthorization(Role userRole) {
            Specification.installSpecifications(
                    Specification.requestSpecification(BASE_URL),
                    Specification.responseSpecificationOk()
            );
            switch (userRole) {
                case Role.CLIENT ->  {
                    AuthClientResponse response = given()
                            .body(clientRequest)
                            .when()
                            .post("/auth/user/sign-in")
                            .then()
                            .extract().as(AuthClientResponse.class);
                    return response.getAccessToken();
                }
                case Role.OPERATOR -> {
                    AuthStaffResponse response = given()
                            .body(operatorRequest)
                            .when()
                            .post("/auth/staff/sign-in")
                            .then()
                            .extract().as(AuthStaffResponse.class);
                    return response.getAccessToken();
                }
                case Role.ADMIN -> {
                    AuthStaffResponse response = given()
                            .body(adminRequest)
                            .when()
                            .post("/auth/staff/sign-in")
                            .then()
                            .extract().as(AuthStaffResponse.class);
                    return response.getAccessToken();
                }
                default -> throw new IllegalArgumentException("Unknown user role");
            }
        }
    }
}
