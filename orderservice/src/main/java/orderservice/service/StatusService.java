package orderservice.service;

import lombok.RequiredArgsConstructor;
import orderservice.configuration.FeatureToggles;
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
    private final FeatureToggles featureToggles;

    public List<StatusHistory> getStatusHistory(UUID orderId) {
        return statusHistoryRepository.findByOrderId(orderId);
    }

    public void changeOrderStatus(UUID id, Status status) {
        Reservation order = orderRepository.findById(id).orElse(null);
        assert order != null;
        if(!featureToggles.isBugWrongStatusChange()){
            order.setStatus(status);
        }
        StatusHistory statusHistory = new StatusHistory();
        statusHistory.setOrderId(id);
        statusHistory.setStatus(status);
        statusHistory.setDate(LocalDateTime.now());
        if(!featureToggles.isBugStatusHistoryNotChanges()){
            statusHistoryRepository.save(statusHistory);
        }
        orderRepository.save(order);
    }
}