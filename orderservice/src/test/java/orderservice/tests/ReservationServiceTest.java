package orderservice.tests;

import jakarta.servlet.UnavailableException;
import orderservice.data.Operator;
import orderservice.data.Reservation;
import orderservice.dto.OrderResponseDto;
import orderservice.repository.OperatorRepository;
import orderservice.repository.OrderRepository;
import orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OperatorRepository operatorRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testFindById_WhenReservationExists_ShouldReturnReservation() {

        UUID reservationId = UUID.randomUUID();
        Reservation expectedReservation = new Reservation();
        expectedReservation.setId(reservationId);

        when(orderRepository.findById(reservationId)).thenReturn(Optional.of(expectedReservation));


        Reservation result = orderService.findById(reservationId);


        assertNotNull(result);
        assertEquals(reservationId, result.getId());
        verify(orderRepository, times(1)).findById(reservationId);
    }

    @Test
    void testFindById_WhenReservationNotExists_ShouldReturnNull() {
        UUID nonExistentId = UUID.randomUUID();

        when(orderRepository.findById(nonExistentId)).thenReturn(Optional.empty());


        Reservation result = orderService.findById(nonExistentId);

        assertNull(result);
        verify(orderRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void testFindByOperatorId_ShouldReturnPagedReservations() {
        UUID operatorId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        List<Reservation> reservations = Arrays.asList(
                new Reservation(),
                new Reservation()
        );
        Page<Reservation> expectedPage = new PageImpl<>(reservations, pageable, reservations.size());

        when(orderRepository.findByOperatorId(operatorId, pageable)).thenReturn(expectedPage);

        List<OrderResponseDto> result = orderService.findByOperatorId(operatorId, pageable);

        assertNotNull(result);
        assertTrue(!result.isEmpty());
        verify(orderRepository, times(1)).findByOperatorId(operatorId, pageable);
    }

    @Test
    void testFindWithoutOperator_ShouldReturnReservationsWithoutOperator() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Reservation> reservations = Arrays.asList(
                new Reservation(),
                new Reservation()
        );
        Page<Reservation> expectedPage = new PageImpl<>(reservations, pageable, reservations.size());

        when(orderRepository.findByOperatorId(null, pageable)).thenReturn(expectedPage);

        List<OrderResponseDto> result = orderService.findWithoutOperator(pageable);

        assertNotNull(result);
        assertTrue(!result.isEmpty());
        verify(orderRepository, times(1)).findByOperatorId(null, pageable);
    }

    @Test
    void testSave_WhenOperatorIdExists_ShouldSetOperatorNameAndSave() throws UnavailableException {
        UUID operatorId = UUID.randomUUID();
        Reservation reservation = new Reservation();
        reservation.setOperatorId(operatorId);

        Operator operator = new Operator();
        operator.setId(operatorId);
        operator.setFullName("John Doe");

        when(operatorRepository.findById(operatorId)).thenReturn(Optional.of(operator));

        orderService.save(reservation);

        assertEquals("John Doe", reservation.getOperatorName());
        verify(operatorRepository, times(1)).findById(operatorId);
        verify(orderRepository, times(1)).save(reservation);
    }
}