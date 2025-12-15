package orderservice.tests;

import orderservice.controller.OrderController;
import orderservice.service.EditOrderService;
import orderservice.service.OperatorService;
import orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.UnavailableException;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OperatorService operatorService;

    @Mock
    private OrderService orderService;

    @Mock
    private EditOrderService editOrderService;

    @InjectMocks
    private OrderController orderController;

    private UUID testOperatorId;
    private UUID testOrderId;
    private UUID testDishId;
    private Integer testAmount;

    @BeforeEach
    void setUp() {
        testOperatorId = UUID.randomUUID();
        testOrderId = UUID.randomUUID();
        testDishId = UUID.randomUUID();
        testAmount = 5;
    }

    @Test
    void getOperatorDetailsById_UnavailableException() throws jakarta.servlet.UnavailableException {
        String errorMessage = "User service unavailable";
        when(operatorService.getOperatorDetails(testOperatorId))
                .thenThrow(new UnavailableException(errorMessage));

        ResponseEntity<?> response = orderController.getOperatorDetailsById(testOperatorId);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(((String) body.get("error")).contains(errorMessage));

        verify(operatorService, times(1)).getOperatorDetails(testOperatorId);
    }

    @Test
    void getOperatorDetailsById_GeneralException() throws jakarta.servlet.UnavailableException {
        String errorMessage = "Internal server error";
        when(operatorService.getOperatorDetails(testOperatorId))
                .thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> response = orderController.getOperatorDetailsById(testOperatorId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(((String) body.get("error")).contains("Error getting operator: " + errorMessage));

        verify(operatorService, times(1)).getOperatorDetails(testOperatorId);
    }

    @Test
    void changeDishQuantity_Success() {
        doNothing().when(editOrderService).changeDishAmount(testDishId, testOrderId, testAmount);

        ResponseEntity<?> response = orderController.changeDishQuantity(testOrderId, testDishId, testAmount);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Dish quantity updated successfully", body.get("message"));

        verify(editOrderService, times(1)).changeDishAmount(testDishId, testOrderId, testAmount);
    }

    @Test
    void changeDishQuantity_WithNegativeAmount() {
        // Arrange
        Integer negativeAmount = -2;
        String errorMessage = "Amount cannot be negative";
        doThrow(new IllegalArgumentException(errorMessage))
                .when(editOrderService).changeDishAmount(testDishId, testOrderId, negativeAmount);

        ResponseEntity<?> response = orderController.changeDishQuantity(testOrderId, testDishId, negativeAmount);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(((String) body.get("error")).contains(errorMessage));

        verify(editOrderService, times(1)).changeDishAmount(testDishId, testOrderId, negativeAmount);
    }

    @Test
    void changeDishQuantity_OrderNotFound() {
        // Arrange
        String errorMessage = "Order not found";
        doThrow(new RuntimeException(errorMessage))
                .when(editOrderService).changeDishAmount(testDishId, testOrderId, testAmount);

        ResponseEntity<?> response = orderController.changeDishQuantity(testOrderId, testDishId, testAmount);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(((String) body.get("error")).contains(errorMessage));

        verify(editOrderService, times(1)).changeDishAmount(testDishId, testOrderId, testAmount);
    }
}
