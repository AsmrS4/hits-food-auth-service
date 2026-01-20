package com.example.user_service.api.controller;

import com.example.user_service.api.config.Specification;
import com.example.user_service.api.dto.ClientDto;
import com.example.user_service.api.dto.StaffDto;
import com.example.user_service.api.enums.Role;
import com.example.user_service.api.reponses.auth.AuthClientResponse;
import com.example.user_service.api.reponses.auth.AuthStaffResponse;
import com.example.user_service.api.reponses.error.ErrorResponse;
import com.example.user_service.api.requests.auth.ClientLoginRequest;
import com.example.user_service.api.requests.auth.StaffLoginRequest;
import com.example.user_service.api.requests.users.EditProfileDto;
import com.example.user_service.domain.dto.Response;
import com.example.user_service.domain.dto.user.EditClientDTO;
import com.example.user_service.domain.dto.user.EditStaffDTO;
import com.example.user_service.domain.dto.user.ExchangePasswordRequest;
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
            List<StaffDto> clientUser = response.stream().filter(user ->
                user.getRole().equals(Role.CLIENT)
            ).toList();
            assertTrue(clientUser.isEmpty());
        }

        @Test
        @DisplayName("Should throw UsernameNotFoundException with not existing user")
        public void findNotExistingUserByPhoneShouldThrowException() {
            String accessToken = getAccessTokenAfterAuthorization(Role.CLIENT);
            Specification.installSpecifications(
                    Specification.requestSpecification(BASE_URL),
                    Specification.responseSpecificationError404()
            );

            ErrorResponse errorResponse = given()
                    .header("Authorization", "Bearer " + accessToken)
                    .queryParam("phone","88000003538")
                    .get("/users/find-by-phone")
                    .then()
                    .log().all()
                    .statusCode(404).extract().as(ErrorResponse.class);

            assertEquals(404, errorResponse.getStatus());
            assertEquals("User not found", errorResponse.getError());
        }

        @Test
        @DisplayName("Should return correct client role after update")
        public void updateClientProfileShouldReturnClientDtoWithClientRole() {
            String accessToken = getAccessTokenAfterAuthorization(Role.CLIENT);
            EditClientDTO editProfile = new EditClientDTO("Babanov1","88005553537");
            EditClientDTO resetProfile = new EditClientDTO("Babanov1", "88005553537");
            ClientDto clientDto = given()
                    .when()
                    .header("Authorization", "Bearer " + accessToken)
                    .body(editProfile)
                    .put("/users/me")
                    .then()
                    .statusCode(200)
                    .extract().as(ClientDto.class);
            given().when()
                    .header("Authorization", "Bearer " + accessToken)
                    .body(resetProfile)
                    .put("/users/me");

            assertNotNull(clientDto);
            assertEquals(Role.CLIENT, clientDto.getRole());
        }

        @Test
        @DisplayName("Should return correct client role after retrieve request")
        public void getClientProfileShouldReturnClientDtoWithClientRole() {
            String accessToken = getAccessTokenAfterAuthorization(Role.CLIENT);
            ClientDto clientDto = given()
                    .when()
                    .header("Authorization", "Bearer " + accessToken)
                    .get("/users/me")
                    .then()
                    .statusCode(200)
                    .extract().as(ClientDto.class);

            assertEquals(Role.CLIENT, clientDto.getRole());
        }

        @Test
        @DisplayName("Should return correct operator role after update")
        public void updateStaffProfileShouldReturnOperatorRole() {
            String accessToken = getAccessTokenAfterAuthorization(Role.OPERATOR);
            EditStaffDTO resetProfile = new EditStaffDTO("Иван Оператор", "88005553536", "test@operator1");
            EditStaffDTO editProfileDto = new EditStaffDTO("Иван Оператор", "88005553536", "test@operator1");
            StaffDto response = given()
                    .when()
                    .header("Authorization", "Bearer " + accessToken)
                    .body(editProfileDto)
                    .put("/users/me/staff")
                    .then()
                    .statusCode(200)
                    .extract().as(StaffDto.class);

            given().when()
                    .header("Authorization", "Bearer " + accessToken)
                    .body(resetProfile)
                    .put("/users/me/staff");

            assertNotNull(response);
            assertEquals("27160085-2429-4dd7-8619-bcf1d1f387cf", response.getId().toString());
            assertEquals(Role.OPERATOR, response.getRole());
        }

        @Test
        @DisplayName("Should return correct operator role retrieve request")
        public void getStaffProfileShouldReturnStaffDtoWithOperatorOrAdminRole() {
            String accessToken = getAccessTokenAfterAuthorization(Role.OPERATOR);
            StaffDto staffDto = given()
                    .when()
                    .header("Authorization", "Bearer " + accessToken)
                    .get("/users/me")
                    .then()
                    .statusCode(200)
                    .extract().as(StaffDto.class);

            assertEquals(Role.OPERATOR, staffDto.getRole());
        }

        private Response changePasswordRequest(ExchangePasswordRequest request, String accessToken) {
            return given()
                    .when()
                    .header("Authorization", "Bearer " + accessToken)
                    .body(request)
                    .put("/users/password/change")
                    .then()
                    .log().all()
                    .statusCode(200)
                    .extract().as(Response.class);
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
