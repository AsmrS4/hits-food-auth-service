package orderservice.service;

import com.example.common_module.dto.OperatorDto;
import jakarta.servlet.UnavailableException;
import lombok.RequiredArgsConstructor;
import orderservice.client.UserClient;
import orderservice.data.Operator;
import orderservice.data.Reservation;
import orderservice.data.Status;
import orderservice.dto.AmountDto;
import orderservice.dto.OrderDto;
import orderservice.repository.OperatorRepository;
import orderservice.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OperatorService operatorService;
    private final OperatorRepository operatorRepository;

    public Reservation findById(UUID id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Page<Reservation> findByOperatorId(UUID id, Pageable pageable) {
        return orderRepository.findByOperatorId(id, pageable);
    }

    public Page<Reservation> findWithoutOperator(Pageable pageable) {
        return orderRepository.findByOperatorId(null, pageable);
    }

    public void save(Reservation order) throws UnavailableException {
        if(order.getOperatorId() != null){
            OperatorDto op = operatorService.getOperatorDetails(order.getOperatorId());
            order.setOperatorName(op.getFullName());
        }
        orderRepository.save(order);
    }

    public void changeOperatorId(UUID orderId, UUID operatorId) {
        Reservation order = findById(orderId);
        order.setOperatorId(operatorId);
        if(operatorRepository.findById(operatorId).isPresent()){
            order.setOperatorName(operatorRepository.findById(operatorId).get().getFullName());
        }
        orderRepository.save(order);
    }

    public void comment(UUID orderId, String comment) {
        Reservation order = findById(orderId);
        order.setComment(comment);
        orderRepository.save(order);
    }

    public Long getStat(UUID operatorId) {
        return orderRepository.countOrdersByOperatorId(operatorId);
    }

    public void setDeclineReason(UUID orderId, String reason) {
        Reservation order = findById(orderId);
        order.setDeclineReason(reason);
        orderRepository.save(order);
    }

    public List<Reservation> findByUserId(UUID userId) {
        return orderRepository.findByClientId(userId);
    }

    public boolean hasOrdered(UUID foodId) {
        UUID userId = getUserIdFromContext();
        if(userId == null) {
            return false;
        }
        return orderRepository.hasOrderedFood(foodId, userId);
    }

    private UUID getUserIdFromContext() {
        var id = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        try {
            return UUID.fromString(id);
        } catch (Exception ex) {
            return null;
        }
    }



    public AmountDto getOrderAmountByUser(UUID userId) {
        return new AmountDto(orderRepository.countByClientIdAndStatus(userId, Status.CONFIRMED),orderRepository.countByClientIdAndStatusNot(userId, Status.CONFIRMED));
    }
}
