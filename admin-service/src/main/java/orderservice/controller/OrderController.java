package orderservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.ws.rs.DefaultValue;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import orderservice.data.*;
import com.example.common_module.dto.OperatorDto;
import orderservice.dto.OrderDto;
import orderservice.filter.OrderFilter;
import orderservice.mapper.OrderMapper;
import orderservice.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.sql.In;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @GetMapping("/find-by/{orderId}")
    public Reservation findById(@PathVariable UUID orderId) {
        return orderService.findById(orderId);
    }

    @GetMapping("/find-by-userId/{userId}")
    public List<Reservation> findByUserId(@PathVariable UUID userId) {
        return orderService.findByUserId(userId);
    }

    @GetMapping("/find-by-operator/{operatorId}")
    public List<Reservation> findOrderByOperatorId(@PathVariable UUID operatorId, @PageableDefault(size = 20) Pageable pageable) {
        return orderService.findByOperatorId(operatorId, pageable);
    }

    @GetMapping("/find-without-operator")
    public List<Reservation> findOrderWithoutOperatorId(@PageableDefault(size = 20) Pageable pageable) {
        return orderService.findWithoutOperator(pageable);
    }

    @PostMapping("/create")
    public void createOrder(@RequestBody OrderDto order) {
        orderService.save(OrderMapper.mapOrderDtoToOrder(order));
    }

    @PutMapping("/change-order-status/{orderId}")
    public void changeOrderStatus(@PathVariable UUID orderId, @RequestParam String status) {
        statusService.changeOrderStatus(orderId, status);
    }

    @PutMapping("/change-operator-for-order")
    public void changeOperatorForOrder(@RequestParam UUID orderId, @RequestParam UUID operatorId) {
        orderService.changeOperatorId(orderId, operatorId);
        amountService.changeAmount(operatorId);
    }

    @GetMapping("/stat/{operatorId}")
    public Long getStatByOperatorId(@PathVariable UUID operatorId) {
        return orderService.getStat(operatorId);
    }

    @GetMapping("/stat/all")
    public List<OperatorOrderAmountDto> getStatAll() {
        return amountService.getOperatorOrderAmounts();
    }

    @PutMapping("/comment/{orderId}")
    public void comment(@PathVariable UUID orderId, @RequestParam String comment) {
        orderService.comment(orderId, comment);
    }

    @GetMapping("/get-status-history")
    public List<StatusHistory> getStatusHistory(UUID orderId) {
        return statusService.getStatusHistory(orderId);
    }

    @PutMapping("/decline")
    public void declineOrder(@RequestParam UUID orderId, @RequestParam String declineReason) {
        statusService.changeOrderStatus(orderId, Status.CANCELED.name());
        orderService.setDeclineReason(orderId, declineReason);
    }

    @PutMapping("/add-dish/{orderId}/{dishId}")
    public void addDishToOrder(@PathVariable UUID orderId, @PathVariable UUID dishId) {
        editOrderService.addDish(dishId, orderId);
    }

    @DeleteMapping("/delete-dish/{orderId}/{dishId}")
    public void deleteDishFromOrderOrder(@PathVariable UUID orderId, @PathVariable UUID dishId) {
        editOrderService.deleteDish(dishId, orderId);
    }

    @PutMapping("/change/quantity/{orderId}/{dishId}")
    public void changeDishQuantity(@PathVariable UUID orderId, @PathVariable UUID dishId, @RequestParam Integer amount) {
        editOrderService.changeDishAmount(dishId, orderId, amount);
    }

    @GetMapping("/get-with-filters")
    public Page<Reservation> getWithFilters(@RequestParam(required = false) String status, @RequestParam(required = false) String operatorName, @PageableDefault(size = 20) Pageable pageable) {
        OrderFilter orderFilter = new OrderFilter(operatorName, Status.valueOf(status));
        return filterService.findAllWithFilters(orderFilter, pageable);
    }

    @PostMapping("/save-operator")
    public void saveOperator(@RequestBody OperatorDto dto) {
        operatorService.saveOperator(dto);
    }

    @DeleteMapping("/delete-operator/{operatorId}")
    public void deleteOperator(@PathVariable UUID operatorId) {
        operatorService.deleteOperator(operatorId);
    }

    @GetMapping("/check-has-ordered/{foodId}")
    public ResponseEntity<?> checkHasOrderedFood(@PathVariable UUID foodId) {
        return ResponseEntity.ok(orderService.hasOrdered(foodId));
    }
}
