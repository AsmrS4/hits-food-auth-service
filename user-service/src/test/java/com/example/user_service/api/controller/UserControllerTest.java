package com.example.user_service.api.controller;


import com.example.user_service.api.config.Specification;
import com.example.user_service.api.dto.ClientDto;
import com.example.user_service.api.dto.Response;
import com.example.user_service.api.dto.StaffDto;
import com.example.user_service.api.enums.Role;
import com.example.user_service.api.reponses.auth.AuthClientResponse;
import com.example.user_service.api.reponses.auth.AuthStaffResponse;
import com.example.user_service.api.reponses.error.ErrorResponse;
import com.example.user_service.api.reponses.error.ExtendedErrorResponse;
import com.example.user_service.api.requests.auth.ClientLoginRequest;
import com.example.user_service.api.requests.auth.StaffLoginRequest;
import com.example.user_service.api.requests.users.ChangePasswordRequest;
import com.example.user_service.api.requests.users.EditProfileDto;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User API tests")
public class UserControllerTest {
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
        public void getClientProfileWithAuthTest() {
            String accessToken = getAccessTokenAfterAuthorization(Role.CLIENT);
            ClientDto response = given()
                    .when()
                    .header("Authorization", "Bearer " + accessToken)
                    .get("/users/me")
                    .then()
                    .log().all()
                    .extract().as(ClientDto.class);

            assertNotNull(response);
            assertEquals("Babanov1", response.getFullName());
            assertEquals("e763309f-b238-485b-8585-fbb78610c713", response.getId().toString());
            assertEquals("88005553537", response.getPhone());
            assertEquals(Role.CLIENT, response.getRole());
        }

        @Test
        @DisplayName("Must return success response with edited user profile")
        public void updateUserProfileTest() {
            String accessToken = getAccessTokenAfterAuthorization(Role.CLIENT);
            EditProfileDto resetProfile = new EditProfileDto("88005553537", "Babanov1");
            EditProfileDto editProfileDto = new EditProfileDto("88005553539", "BabanovPrime");
            ClientDto response = given()
                    .when()
                    .header("Authorization", "Bearer " + accessToken)
                    .body(editProfileDto)
                    .put("/users/me")
                    .then()
                    .log().all()
                    .extract().as(ClientDto.class);

            assertNotNull(response);
            assertEquals("e763309f-b238-485b-8585-fbb78610c713", response.getId().toString());
            assertEquals(Role.CLIENT, response.getRole());
            assertNotEquals("88005553537", response.getPhone());
            assertNotEquals("Babanov1", response.getFullName());

            given().when()
                    .header("Authorization", "Bearer " + accessToken)
                    .body(resetProfile)
                    .put("/users/me");
        }

        @Test
        @DisplayName("Must return success response status 200 and message")
        public void changePasswordTest() {
            String accessToken = getAccessTokenAfterAuthorization(Role.CLIENT);
            ChangePasswordRequest request = new ChangePasswordRequest("password123", "password12345");
            ChangePasswordRequest reset = new ChangePasswordRequest("password12345", "password123");
            Response response = given()
                    .when()
                        .header("Authorization", "Bearer " + accessToken)
                        .body(request)
                        .put("/users/password/change")
                    .then()
                            .log().all()
                    .extract().as(Response.class);

            assertNotNull(response);
            assertEquals("Password was changed successfully", response.getMessage());

            given()
                    .when()
                    .header("Authorization", "Bearer " + accessToken)
                    .body(reset)
                    .put("/users/password/change");
        }

        @Test
        @DisplayName("Must return success response status 200 and message")
        public void getOperatorListTest() {
            String accessToken = getAccessTokenAfterAuthorization(Role.ADMIN);
            List<StaffDto> response = given()
                    .when()
                        .header("Authorization", "Bearer " + accessToken)
                        .get("/users/operators")
                    .then()
                        .log().all()
                    .extract().as(new TypeRef<List<StaffDto>>() {});
            assertNotNull(response);
            assertTrue(!response.isEmpty());
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
    @Nested
    @DisplayName("Test suit for negative scenarios")
    class NegativeUserTests {
        static ClientLoginRequest clientRequest = null;
        static StaffLoginRequest operatorRequest = null;
        static StaffLoginRequest adminRequest = null;
        @BeforeAll
        public static void setUp() {
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
        @DisplayName("Must return 401 status code with message \"Authentication is required\"")
        public void getProfileWithoutAuthorizationTest() {
            Specification.installSpecifications(
                    Specification.requestSpecification(BASE_URL),
                    Specification.responseSpecificationError401()
            );
            ExtendedErrorResponse response = given()
                    .when()
                    .get("/users/me")
                    .then()
                    .log().all()
                    .statusCode(401)
                    .extract().as(ExtendedErrorResponse.class);

            assertEquals("Unauthorized", response.getError());
            assertEquals("Authentication is required", response.getMessage());
            assertEquals(401, response.getStatus());
        }
        @Test
        @DisplayName("Must return 401 status code with message \"Authentication is required\"")
        public void editProfileWithoutAuthorizationTest() {
            Specification.installSpecifications(
                    Specification.requestSpecification(BASE_URL),
                    Specification.responseSpecificationError401()
            );
            EditProfileDto editProfileDto = new EditProfileDto("88005553539", "BabanovPrime");
            ExtendedErrorResponse response = given()
                    .when()
                    .body(editProfileDto)
                    .put("/users/me")
                    .then()
                    .log().all()
                    .statusCode(401)
                    .extract().as(ExtendedErrorResponse.class);

            assertEquals("Unauthorized", response.getError());
            assertEquals("Authentication is required", response.getMessage());
            assertEquals(401, response.getStatus());
        }
        @Test
        @DisplayName("Must return 400 status code with message \"Authentication is required\"")
        public void editProfileWithBadDataTest() {
            String accessToken = getAccessTokenAfterAuthorization(Role.CLIENT);
            Specification.installSpecifications(
                    Specification.requestSpecification(BASE_URL),
                    Specification.responseSpecificationError400()
            );

            EditProfileDto editProfileDto = new EditProfileDto("880055sddssdds539", "");
            given()
                    .when()
                    .header("Authorization", "Bearer " + accessToken)
                    .body(editProfileDto)
                    .put("/users/me")
                        .then()
                        .log().all()
                    .statusCode(400);
        }

        @Test
        @DisplayName("Must return error response status 400 and message \"Incorrect password\"")
        public void changePasswordInvalidPrevTest() {
            String accessToken = getAccessTokenAfterAuthorization(Role.CLIENT);
            Specification.installSpecifications(
                    Specification.requestSpecification(BASE_URL),
                    Specification.responseSpecificationError400()
            );
            ChangePasswordRequest request = new ChangePasswordRequest("password123sdsdds", "password12345");
            ErrorResponse response = given()
                    .when()
                    .header("Authorization", "Bearer " + accessToken)
                    .body(request)
                    .put("/users/password/change")
                    .then()
                    .log().all().statusCode(400)
                    .extract().as(ErrorResponse.class);

            assertNotNull(response);
            assertEquals(400, response.getStatus());
            assertEquals("Incorrect password", response.getError());
        }

        @Test
        @DisplayName("Must return error response status 400 and message \"Previous password and new password mustn't be equals\"")
        public void changePasswordWithEqualPasswordsTest() {
            String accessToken = getAccessTokenAfterAuthorization(Role.CLIENT);
            Specification.installSpecifications(
                    Specification.requestSpecification(BASE_URL),
                    Specification.responseSpecificationError400()
            );
            ChangePasswordRequest request = new ChangePasswordRequest("password123", "password123");
            ErrorResponse response = given()
                    .when()
                    .header("Authorization", "Bearer " + accessToken)
                    .body(request)
                    .put("/users/password/change")
                    .then()
                    .log().all().statusCode(400)
                    .extract().as(ErrorResponse.class);

            assertNotNull(response);
            assertEquals(400, response.getStatus());
            assertEquals("Previous password and new password mustn't be equals", response.getError());
        }

        @Test
        @DisplayName("Must return success response status 403 and message \"You do not have the necessary permissions to access this resource.\"")
        public void getOperatorListWithClientRoleTest() {
            String accessToken = getAccessTokenAfterAuthorization(Role.CLIENT);
            Specification.installSpecifications(
                    Specification.requestSpecification(BASE_URL),
                    Specification.responseSpecificationError403()
            );
            ExtendedErrorResponse response = given()
                    .when()
                    .header("Authorization", "Bearer " + accessToken)
                    .get("/users/operators")
                    .then()
                    .log().all()
                    .statusCode(403)
                    .extract().as(ExtendedErrorResponse.class);
            assertNotNull(response);
            assertEquals(403, response.getStatus());
            assertEquals("You do not have the necessary permissions to access this resource.", response.getMessage());
            assertEquals("Forbidden", response.getError());
        }

        @Test
        @DisplayName("Must return success response status 403 and message \"You do not have the necessary permissions to access this resource.\"")
        public void getOperatorListWithOperatorRoleTest() {
            String accessToken = getAccessTokenAfterAuthorization(Role.OPERATOR);
            Specification.installSpecifications(
                    Specification.requestSpecification(BASE_URL),
                    Specification.responseSpecificationError403()
            );

            ExtendedErrorResponse response = given()
                    .when()
                    .header("Authorization", "Bearer " + accessToken)
                    .get("/users/operators")
                    .then()
                    .log().all()
                    .statusCode(403)
                    .extract().as(ExtendedErrorResponse.class);
            assertNotNull(response);
            assertEquals(403, response.getStatus());
            assertEquals("You do not have the necessary permissions to access this resource.", response.getMessage());
            assertEquals("Forbidden", response.getError());
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
