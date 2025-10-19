package orderservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import orderservice.data.OperatorOrderAmount;
import orderservice.data.Reservation;
import orderservice.data.Status;
import orderservice.data.StatusHistory;
import com.example.common_module.dto.OperatorDto;
import orderservice.dto.OrderDto;
import orderservice.filter.OrderFilter;
import orderservice.mapper.OrderMapper;
import orderservice.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;
    private final OperatorService operatorService;
    private final StatusService statusService;
    private final FilterService filterService;
    private final AmountService amountService;

    @GetMapping("/order/find-by/{orderId}")
    public Reservation findById(@PathVariable UUID orderId) {
        return orderService.findById(orderId);
    }

    @GetMapping("/order/find-by-userId/{userId}")
    public List<Reservation> findByUserId(@PathVariable UUID userId) {
        return orderService.findByUserId(userId);
    }

    @GetMapping("/order/find-by-operator/{operatorId}")
    public Page<Reservation> findOrderByOperatorId(@PathVariable UUID operatorId,@PageableDefault(size = 20) Pageable pageable) {
        return orderService.findByOperatorId(operatorId, pageable);
    }

    @GetMapping("/order/find-without-operator")
    public Page<Reservation> findOrderWithoutOperatorId(@PageableDefault(size = 20) Pageable pageable) {
        return orderService.findWithoutOperator(pageable);
    }

    @PostMapping("/order/create")
    public void createOrder(@RequestBody OrderDto order) {
        orderService.save(OrderMapper.mapOrderDtoToOrder(order));
    }

    @PutMapping("/order/change-order-status/{orderId}")
    public void changeOrderStatus(@PathVariable UUID orderId, @RequestParam String status) {
        statusService.changeOrderStatus(orderId, status);
    }

    @PutMapping("/order/change-operator-for-order")
    public void changeOperatorForOrder(@RequestParam UUID orderId, @RequestParam UUID operatorId) {
        orderService.changeOperatorId(orderId, operatorId);
        amountService.changeAmount(operatorId);
    }

    @GetMapping("/order/stat/{operatorId}")
    public Long getStatByOperatorId(@PathVariable UUID operatorId) {
        return orderService.getStat(operatorId);
    }

    @GetMapping("/order/stat/all")
    public List<OperatorOrderAmount> getStatAll() {
        return amountService.getOperatorOrderAmounts();
    }

    @PutMapping("/order/comment/{orderId}")
    public void comment(@PathVariable UUID orderId, @RequestParam String comment) {
        orderService.comment(orderId, comment);
    }

    @GetMapping("/order/get-status-history")
    public List<StatusHistory> getStatusHistory(UUID orderId) {
        return statusService.getStatusHistory(orderId);
    }

    @PutMapping("/order/decline")
    public void declineOrder(@RequestParam UUID orderId, @RequestParam String declineReason) {
        statusService.changeOrderStatus(orderId, Status.CANCELED.name());
        orderService.setDeclineReason(orderId, declineReason);
    }

    @GetMapping("/order/get-with-filters")
    public Page<Reservation> getWithFilters(@RequestParam String status, @RequestParam String operatorName,@PageableDefault(size = 20) Pageable pageable) {
        OrderFilter orderFilter = new OrderFilter(operatorName,Status.valueOf(status));
        return filterService.findAllWithFilters(orderFilter, pageable);
    }

    @PostMapping("/order/save-operator")
    public void saveOperator(@RequestBody OperatorDto dto) {
        operatorService.saveOperator(dto);
    }
    @DeleteMapping("/order/delete-operator/{operatorId}")
    public void deleteOperator(@PathVariable UUID operatorId) {
        operatorService.deleteOperator(operatorId);
    }
}
