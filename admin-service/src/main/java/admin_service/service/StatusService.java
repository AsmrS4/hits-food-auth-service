package admin_service.service;

import lombok.RequiredArgsConstructor;
import admin_service.data.Reservation;
import admin_service.data.Status;
import admin_service.data.StatusHistory;
import admin_service.repository.OrderRepository;
import admin_service.repository.StatusHistoryRepository;
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

    public void changeOrderStatus(UUID id, Status status) {
        Reservation order = orderRepository.findById(id).orElse(null);
        assert order != null;
        order.setStatus(status);
        StatusHistory statusHistory = new StatusHistory();
        statusHistory.setOrderId(id);
        statusHistory.setStatus(status);
        statusHistory.setDate(LocalDateTime.now());
        statusHistoryRepository.save(statusHistory);
        orderRepository.save(order);
    }
}
