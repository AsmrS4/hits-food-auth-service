package orderservice.service;

import lombok.RequiredArgsConstructor;
import orderservice.data.Reservation;
import orderservice.data.Status;
import orderservice.data.StatusHistory;
import orderservice.repository.OrderRepository;
import orderservice.repository.StatusHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatusService {
    private final OrderRepository orderRepository;
    private final StatusHistoryRepository statusHistoryRepository;

    public List<StatusHistory> getStatusHistory(UUID orderId) {
        return statusHistoryRepository.findByOrderId(orderId);
    }

    public void changeOrderStatus(UUID id, String status) {
        Reservation order = orderRepository.findById(id).orElse(null);
        assert order != null;
        order.setStatus(Status.valueOf(status));
        StatusHistory statusHistory = new StatusHistory();
        statusHistory.setOrderId(id);
        statusHistory.setStatus(Status.valueOf(status));
        statusHistory.setDate(LocalDateTime.now());
        statusHistoryRepository.save(statusHistory);
        orderRepository.save(order);
    }
}
