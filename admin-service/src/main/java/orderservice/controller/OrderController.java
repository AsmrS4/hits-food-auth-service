package orderservice.controller;

import com.example.common_module.dto.OperatorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.UnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orderservice.data.*;
import orderservice.dto.AmountDto;
import orderservice.dto.OrderDto;
import orderservice.dto.OrderResponseDto;
import orderservice.filter.OrderFilter;
import orderservice.mapper.OrderAmountMapper;
import orderservice.mapper.OrderMapper;
import orderservice.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final EditOrderService editOrderService;
    private final OrderService orderService;
    private final OperatorService operatorService;
    private final StatusService statusService;
    private final FilterService filterService;
    private final AmountService amountService;
    private final MealService mealService;
    private final ReservationMealService reservationMealService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> request) {
        try {
            log.info("ORDER CONTROLLER - Creating new order");

            log.info("ORDER CONTROLLER - PaymentMethod: '{}'", request.get("paymentMethod"));
            log.info("ORDER CONTROLLER - UserId: '{}'", request.get("userId"));
            log.info("ORDER CONTROLLER - Total: '{}'", request.get("total"));

            if (!request.containsKey("userId")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "UserId is required"
                ));
            }

            if (!request.containsKey("paymentMethod")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "PaymentMethod is required"
                ));
            }

            String paymentMethod = (String) request.get("paymentMethod");
            try {
                PayWay payWay = PayWay.valueOf(paymentMethod.toUpperCase());
                log.info("ORDER CONTROLLER - Valid payment method: {}", payWay);
            } catch (IllegalArgumentException e) {
                log.error("ORDER CONTROLLER - Invalid payment method: '{}'", paymentMethod);
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Некорректный способ оплаты: " + paymentMethod,
                        "validMethods", List.of("CARD_COURIER", "CARD_ONLINE", "CASH_COURIER")
                ));
            }

            if (!request.containsKey("items") || ((List<?>) request.get("items")).isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Cart is empty"
                ));
            }
            UUID orderId = UUID.randomUUID();
            OrderDto orderDto = convertToOrderDto(request);
            orderService.save(OrderMapper.mapOrderDtoToOrder(orderDto, orderId));
            for (Meal meal : orderDto.getItems()) {
                if (mealService.getById(meal.getId()).isEmpty()) {
                    mealService.addMeal(meal);
                }
                reservationMealService.create(orderId, meal.getId(), meal.getQuantity());
            }
            return ResponseEntity.ok(orderDto);

        } catch (Exception e) {
            log.error("ORDER CONTROLLER - Error creating order: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "error", "Internal server error: " + e.getMessage()
                    ));
        }
    }

    private OrderDto convertToOrderDto(Map<String, Object> request) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            OrderDto dto = mapper.convertValue(request, OrderDto.class);

            if (dto.getUserId() == null) {
                throw new IllegalArgumentException("UserId is required");
            }
            if (dto.getPaymentMethod() == null || dto.getPaymentMethod().trim().isEmpty()) {
                throw new IllegalArgumentException("PaymentMethod is required");
            }

            return dto;

        } catch (Exception e) {
            log.error("Error converting request to OrderDto: {}", e.getMessage());
            throw new RuntimeException("Invalid order data: " + e.getMessage());
        }
    }

    @GetMapping("/find-by/{orderId}")
    public ResponseEntity<?> findById(@PathVariable UUID orderId) {
        try {
            log.info("ORDER CONTROLLER - Finding order by ID: {}", orderId);
            OrderResponseDto order = orderService.findByIdForController(orderId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("ORDER CONTROLLER - Error finding order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Order not found: " + e.getMessage()));
        }
    }

    @GetMapping("/find-by-userId/{userId}")
    public ResponseEntity<?> findByUserId(@PathVariable UUID userId) {
        try {
            log.info("ORDER CONTROLLER - Finding orders by user ID: {}", userId);
            List<OrderResponseDto> orders = orderService.findByUserId(userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("ORDER CONTROLLER - Error finding user orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error finding orders: " + e.getMessage()));
        }
    }

    @GetMapping("/find-by-operator/{operatorId}")
    public ResponseEntity<?> findOrderByOperatorId(@PathVariable UUID operatorId,
                                                   @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("ORDER CONTROLLER - Finding orders by operator ID: {}", operatorId);
            List<OrderResponseDto> orders = orderService.findByOperatorId(operatorId, pageable);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("ORDER CONTROLLER - Error finding operator orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error finding orders: " + e.getMessage()));
        }
    }

    @GetMapping("/find-without-operator")
    public ResponseEntity<?> findOrderWithoutOperatorId(@PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("ORDER CONTROLLER - Finding orders without operator");
            List<OrderResponseDto> orders = orderService.findWithoutOperator(pageable);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("ORDER CONTROLLER - Error finding orders without operator: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error finding orders: " + e.getMessage()));
        }
    }

    @PutMapping("/change-order-status/{orderId}")
    public ResponseEntity<?> changeOrderStatus(@PathVariable UUID orderId, @RequestParam Status status) {
        try {
            log.info("ORDER CONTROLLER - Changing order status. Order: {}, Status: {}", orderId, status);
            statusService.changeOrderStatus(orderId, status);
            return ResponseEntity.ok(Map.of("message", "Order status updated successfully"));
        } catch (Exception e) {
            log.error("ORDER CONTROLLER - Error changing order status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error changing status: " + e.getMessage()));
        }
    }

    @PutMapping("/change-operator-for-order")
    public ResponseEntity<?> changeOperatorForOrder(@RequestParam UUID orderId, @RequestParam UUID operatorId) {
        try {
            log.info("ORDER CONTROLLER - Changing operator for order. Order: {}, Operator: {}", orderId, operatorId);
            orderService.changeOperatorId(orderId, operatorId);
            amountService.changeAmount(operatorId);
            return ResponseEntity.ok(Map.of("message", "Operator changed successfully"));
        } catch (Exception e) {
            log.error("ORDER CONTROLLER - Error changing operator: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error changing operator: " + e.getMessage()));
        }
    }

    @GetMapping("/stat/{operatorId}")
    public ResponseEntity<?> getStatByOperatorId(@PathVariable UUID operatorId) {
        try {
            log.info("ORDER CONTROLLER - Getting stats for operator: {}", operatorId);
            Long stat = orderService.getStat(operatorId);
            return ResponseEntity.ok(stat);
        } catch (Exception e) {
            log.error("ORDER CONTROLLER - Error getting stats: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error getting stats: " + e.getMessage()));
        }
    }

    @GetMapping("/stat/all")
    public ResponseEntity<?> getStatAll() {
        try {
            log.info("ORDER CONTROLLER - Getting all stats");
            List<OperatorOrderAmount> stats = amountService.getOperatorOrderAmounts();
            List<OperatorOrderAmountDto> responseStats = new java.util.ArrayList<>(List.of());
            for(OperatorOrderAmount stat : stats){
                OperatorDto operator = operatorService.getOperatorDetails(stat.getOperatorId());
                responseStats.add(OrderAmountMapper.mapOrderAmountToDto(operator, stat));
            }
            return ResponseEntity.ok(responseStats);
        } catch (Exception e) {
            log.error("ORDER CONTROLLER - Error getting all stats: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error getting stats: " + e.getMessage()));
        }
    }

    @GetMapping("/get-order-amount-by-user")
    public AmountDto getOrderAmountByUser(UUID userId) {
        return orderService.getOrderAmountByUser(userId);
    }

    @PutMapping("/comment/{orderId}")
    public ResponseEntity<?> comment(@PathVariable UUID orderId, @RequestParam String comment) {
        try {
            log.info("ORDER CONTROLLER - Adding comment to order: {}", orderId);
            orderService.comment(orderId, comment);
            return ResponseEntity.ok(Map.of("message", "Comment added successfully"));
        } catch (Exception e) {
            log.error("ORDER CONTROLLER - Error adding comment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error adding comment: " + e.getMessage()));
        }
    }

    @GetMapping("/get-status-history")
    public ResponseEntity<?> getStatusHistory(@RequestParam UUID orderId) {
        try {
            log.info("ORDER CONTROLLER - Getting status history for order: {}", orderId);
            List<StatusHistory> history = statusService.getStatusHistory(orderId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("ORDER CONTROLLER - Error getting status history: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error getting history: " + e.getMessage()));
        }
    }

    @PutMapping("/decline")
    public ResponseEntity<?> declineOrder(@RequestParam UUID orderId, @RequestParam String declineReason) {
        try {
            log.info("ORDER CONTROLLER - Declining order: {}, Reason: {}", orderId, declineReason);
            statusService.changeOrderStatus(orderId, Status.CANCELED);
            orderService.setDeclineReason(orderId, declineReason);
            return ResponseEntity.ok(Map.of("message", "Order declined successfully"));
        } catch (Exception e) {
            log.error("ORDER CONTROLLER - Error declining order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error declining order: " + e.getMessage()));
        }
    }

    @PutMapping("/add-dish/{orderId}/{dishId}")
    public ResponseEntity<?> addDishToOrder(@PathVariable UUID orderId, @PathVariable UUID dishId) throws UnavailableException {
        editOrderService.addDish(dishId, orderId);
        return ResponseEntity.ok(Map.of("message", "Dish added successfully"));
    }

    @DeleteMapping("/delete-dish/{orderId}/{dishId}")
    public ResponseEntity<?> deleteDishFromOrder(@PathVariable UUID orderId, @PathVariable UUID dishId) throws UnavailableException {
        editOrderService.deleteDish(dishId, orderId);
        return ResponseEntity.ok(Map.of("message", "Dish deleted successfully"));
    }

    @PutMapping("/change/quantity/{orderId}/{dishId}")
    public ResponseEntity<?> changeDishQuantity(@PathVariable UUID orderId, @PathVariable UUID dishId,
                                                @RequestParam Integer amount) {
        try {
            log.info("ORDER CONTROLLER - Changing dish quantity. Order: {}, Dish: {}, Amount: {}",
                    orderId, dishId, amount);
            editOrderService.changeDishAmount(dishId, orderId, amount);
            return ResponseEntity.ok(Map.of("message", "Dish quantity updated successfully"));
        } catch (Exception e) {
            log.error("ORDER CONTROLLER - Error changing dish quantity: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error changing quantity: " + e.getMessage()));
        }
    }

    @GetMapping("/get-with-filters")
    public ResponseEntity<?> getWithFilters(@RequestParam(required = false) Status status,
                                            @RequestParam(required = false) String operatorName,
                                            @PageableDefault(size = 20) Pageable pageable) {
        try {
            log.info("ORDER CONTROLLER - Getting orders with filters. Status: {}, Operator: {}", status, operatorName);
            OrderFilter orderFilter = new OrderFilter(operatorName, status);
            List<OrderResponseDto> orders = filterService.findAllWithFilters(orderFilter, pageable);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("ORDER CONTROLLER - Error filtering orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error filtering orders: " + e.getMessage()));
        }
    }

    @PostMapping("/save-operator")
    @Deprecated
    public ResponseEntity<?> saveOperator(@RequestBody OperatorDto dto) {
        try {
            log.info("ORDER CONTROLLER - Saving operator: {}", dto.getFullName());
            operatorService.saveOperator(dto);
            return ResponseEntity.ok(Map.of("message", "Operator saved successfully"));
        } catch (Exception e) {
            log.error("ORDER CONTROLLER - Error saving operator: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error saving operator: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete-operator/{operatorId}")
    @Deprecated
    public ResponseEntity<?> deleteOperator(@PathVariable UUID operatorId) {
        try {
            log.info("ORDER CONTROLLER - Deleting operator: {}", operatorId);
            operatorService.deleteOperator(operatorId);
            return ResponseEntity.ok(Map.of("message", "Operator deleted successfully"));
        } catch (Exception e) {
            log.error("ORDER CONTROLLER - Error deleting operator: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error deleting operator: " + e.getMessage()));
        }
    }

    @GetMapping("/check-has-ordered/{foodId}")
    public ResponseEntity<?> checkHasOrderedFood(@PathVariable UUID foodId) {
        try {
            log.info("ORDER CONTROLLER - Checking if food was ordered: {}", foodId);
            boolean hasOrdered = orderService.hasOrdered(foodId);
            return ResponseEntity.ok(hasOrdered);
        } catch (Exception e) {
            log.error("ORDER CONTROLLER - Error checking ordered food: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error checking ordered food: " + e.getMessage()));
        }
    }

    @GetMapping("/test/get-operator/{operatorId}")
    @Operation(
            description = "Fetch operator details from user-service",
            summary = "Тестовый метод для провреки взаимодействия сервисов друг с другом. Не использовать в проде"
    )
    @Deprecated
    public ResponseEntity<?> getOperatorDetailsById(@PathVariable UUID operatorId) {
        try {
            log.info("ORDER CONTROLLER - Getting operator details: {}", operatorId);
            OperatorDto operator = operatorService.getOperatorDetails(operatorId);
            return ResponseEntity.ok(operator);
        } catch (UnavailableException e) {
            log.error("ORDER CONTROLLER - Service unavailable: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Service unavailable: " + e.getMessage()));
        } catch (Exception e) {
            log.error("ORDER CONTROLLER - Error getting operator details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error getting operator: " + e.getMessage()));
        }
    }

}