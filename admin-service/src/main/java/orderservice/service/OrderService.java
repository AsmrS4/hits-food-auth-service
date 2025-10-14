package orderservice.service;

import lombok.RequiredArgsConstructor;
import orderservice.data.Reservation;
import orderservice.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Reservation findById(UUID id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Page<Reservation> findByOperatorId(UUID id, Pageable pageable) {
        return orderRepository.findByOperatorId(id, pageable);
    }

    public Page<Reservation> findWithoutOperator(Pageable pageable) {
        return orderRepository.findByOperatorId(null, pageable);
    }

    public void save(Reservation order) {
        orderRepository.save(order);
    }

    public void changeOperatorId(UUID orderId, UUID operatorId) {
        Reservation order = findById(orderId);
        order.setOperatorId(operatorId);
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
}
