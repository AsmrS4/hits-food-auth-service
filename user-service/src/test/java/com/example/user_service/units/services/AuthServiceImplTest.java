package com.example.user_service.units.services;

import com.example.common_module.jwt.TokenService;
import com.example.user_service.domain.dto.auth.LoginRequest;
import com.example.user_service.domain.dto.auth.StaffLoginRequest;
import com.example.user_service.domain.dto.user.ClientUserDTO;
import com.example.user_service.domain.dto.user.StaffUserDTO;
import com.example.user_service.domain.entities.User;
import com.example.common_module.enums.Role;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.services.impl.AuthServiceImpl;
import com.example.user_service.utils.UserMapper;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Authorization service impl tests")
public class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthServiceImpl authService;
    @Mock
    private TokenService tokenService;

    @Nested
    @DisplayName("Test suit for client login check")
    class ClientLoginTests {
        private final LoginRequest request = new LoginRequest();
        private static User user;
        private static ClientUserDTO dto;
        private static final UUID userId = UUID.fromString("5bf82501-0753-43b2-9568-e7d6fb7a25ed");
        private static final String defaultUserPassword = "password123";
        private static final String defaultUserPhone = "88005553535";
        private static final String accessToken = "accessToken";
        @BeforeAll
        public static void setUp() {
            user = new User();
            user.setId(userId);
            user.setFullName("Test user");
            user.setPhone(defaultUserPhone);
            user.setPassword(defaultUserPassword);
            user.setRole(Role.CLIENT);

            dto = new ClientUserDTO();
            dto.setId(userId);
            dto.setRole(Role.CLIENT);
            dto.setPhone(defaultUserPhone);
            dto.setFullName("Test user");
        }
        @Test
        @DisplayName("Should return user with existing account and valid phone number")
        public void testExistingUser(){
            request.setPhone("88005553535");

            when(userRepository.findUserByPhone(request.getPhone()))
                    .thenReturn(Optional.of(user));

            Optional<User> userEntity = userRepository.findUserByPhone(request.getPhone());

            assertNotNull(userEntity);
        }
        @Test
        @DisplayName("Should not found user with not registered phone number")
        public void testNotExistingUser(){
            request.setPhone("88005553536");

            when(userRepository.findUserByPhone(request.getPhone()))
                    .thenReturn(Optional.empty());

            Optional<User> userEntity = userRepository.findUserByPhone(request.getPhone());

            assertTrue(userEntity.isEmpty());
        }
        @Test
        @DisplayName("Should test successful login with registered phone number")
        public void testSuccessLogin(){
            request.setPhone("88005553535");
            request.setPassword("password123");

            when(userRepository.findUserByPhone(request.getPhone()))
                    .thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getPassword(), user.getPassword()))
                    .thenReturn(true);
            when(tokenService.getAccessToken(user))
                    .thenReturn(accessToken);
            when(userMapper.mapClient(user))
                    .thenReturn(dto);

            Optional<User> currentUser = userRepository.findUserByPhone(request.getPhone());
            boolean isEqual = passwordEncoder.matches(request.getPassword(), user.getPassword());
            String generatedAccessToken = tokenService.getAccessToken(currentUser.get());
            ClientUserDTO userDTO = userMapper.mapClient(currentUser.get());

            assertNotNull(currentUser);
            assertTrue(isEqual);
            assertEquals(currentUser.get().getRole(), Role.CLIENT);
            assertEquals(generatedAccessToken, accessToken);
            assertEquals(userDTO, dto);
        }

        @Test
        @DisplayName("Should throws BadRequest exception with incorrect password")
        public void testLoginWithIncorrectPassword(){
            request.setPhone("88005553535");
            request.setPassword("password1234");

            when(userRepository.findUserByPhone(request.getPhone()))
                    .thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getPassword(), user.getPassword()))
                    .thenReturn(false);
            Optional<User> currentUser = userRepository.findUserByPhone(request.getPhone());

            final BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    ()-> authService.loginClientUser(request)
            );
            assertNotNull(currentUser);
            assertEquals("Login failed", exception.getMessage());
        }
        @Test
        @DisplayName("Should throws UsernameNotFoundException exception with not registered phone number")
        public void testLoginWithIncorrectPhone(){
            request.setPhone("88005553536");
            request.setPassword("password123");

            when(userRepository.findUserByPhone(request.getPhone()))
                    .thenThrow(new UsernameNotFoundException("User not found"));

            final UsernameNotFoundException exception = assertThrows(
                    UsernameNotFoundException.class,
                    ()-> authService.loginClientUser(request)
            );
            assertEquals("User not found", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Test suit for staff login check")
    class StaffLoginTests {
        private static User user;
        private static StaffUserDTO dto;
        private final StaffLoginRequest request = new StaffLoginRequest();
        private static final UUID userId = UUID.fromString("5bf82501-0753-43b2-9568-e7d6fb7a25ed");
        private static final String defaultUserPassword = "password123";
        private static final String defaultUsername = "testOperator";
        private static final String accessToken = "accessToken";
        @BeforeAll
        public static void setUp() {
            user = new User();
            user.setId(userId);
            user.setUsername(defaultUsername);
            user.setPassword(defaultUserPassword);
            user.setRole(Role.OPERATOR);

            dto = new StaffUserDTO();
            dto.setUsername(user.getLoginName());
            dto.setRole(Role.OPERATOR);
            dto.setId(user.getId());
        }
        @Test
        @DisplayName("Should return user with existing account and valid username")
        public void testExistingUser(){
            request.setUsername("testOperator");

            when(userRepository.findUserByUsername(request.getUsername()))
                    .thenReturn(Optional.of(user));

            Optional<User> userEntity = userRepository.findUserByUsername(request.getUsername());

            assertNotNull(userEntity);
            assertEquals(request.getUsername(), userEntity.get().getLoginName());
        }
        @Test
        @DisplayName("Should not found user with unregistered phone number")
        public void testNotExistingUser(){
            request.setUsername("88005553536");

            when(userRepository.findUserByPhone(request.getUsername()))
                    .thenReturn(Optional.empty());

            Optional<User> userEntity = userRepository.findUserByPhone(request.getUsername());

            assertTrue(userEntity.isEmpty());
        }
        @Test
        @DisplayName("Should test successful login with registered username and valid password")
        public void testSuccessLogin(){
            request.setUsername("testOperator");
            request.setPassword("password123");

            when(userRepository.findUserByUsername(request.getUsername()))
                    .thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getPassword(), user.getPassword()))
                    .thenReturn(true);
            when(tokenService.getAccessToken(user))
                    .thenReturn(accessToken);
            when(userMapper.mapClient(user))
                    .thenReturn(dto);

            Optional<User> currentUser = userRepository.findUserByUsername(request.getUsername());
            boolean isEqual = passwordEncoder.matches(request.getPassword(), user.getPassword());
            String generatedAccessToken = tokenService.getAccessToken(currentUser.get());
            ClientUserDTO userDTO = userMapper.mapClient(currentUser.get());

            assertNotNull(currentUser);
            assertEquals(currentUser.get().getRole(), Role.OPERATOR);
            assertTrue(isEqual);
            assertEquals(generatedAccessToken, accessToken);
            assertEquals(userDTO, dto);
        }

        @Test
        @DisplayName("Should throws BadRequest exception with incorrect password")
        public void testLoginStaffWithIncorrectPassword(){
            request.setUsername("testOperator");
            request.setPassword("password1234");

            when(userRepository.findUserByUsername(request.getUsername()))
                    .thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getPassword(), user.getPassword()))
                    .thenReturn(false);
            Optional<User> currentUser = userRepository.findUserByUsername(request.getUsername());

            final BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    ()-> authService.loginStaffUser(request)
            );
            assertNotNull(currentUser);
            assertEquals("Login failed", exception.getMessage());
        }
        @Test
        @DisplayName("Should throws UsernameNotFoundException exception with not registered username")
        public void testLoginStaffWithIncorrectUsername(){
            request.setUsername("testOperator123");
            request.setPassword("password123");

            when(userRepository.findUserByUsername(request.getUsername()))
                    .thenThrow(new UsernameNotFoundException("User not found"));

            final UsernameNotFoundException exception = assertThrows(
                    UsernameNotFoundException.class,
                    ()-> authService.loginStaffUser(request)
            );
            assertEquals("User not found", exception.getMessage());
        }
    }
}

