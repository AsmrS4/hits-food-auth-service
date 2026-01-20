package orderservice.tests;

import jakarta.servlet.UnavailableException;
import orderservice.client.DishClient;
import orderservice.configuration.FeatureToggles;
import orderservice.data.Meal;
import orderservice.data.Reservation;
import orderservice.data.ReservationMeal;
import orderservice.dto.FoodDetailsDto;
import orderservice.dto.FoodDetailsResponse;
import orderservice.repository.MealRepository;
import orderservice.repository.OrderRepository;
import orderservice.repository.ReservationMealRepository;
import orderservice.service.EditOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class EditOrderServiceTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ReservationMealRepository reservationMealRepository;

    @Mock
    private FeatureToggles featureToggles;

    @Mock
    private DishClient dishClient;

    @InjectMocks
    private EditOrderService editOrderService;

    private final UUID ORDER_ID = UUID.randomUUID();
    private final UUID DISH_ID = UUID.randomUUID();
    private final UUID CLIENT_ID = UUID.randomUUID();
    private final UUID RESERVATION_MEAL_ID = UUID.randomUUID();

    private Reservation order;
    private Meal meal;
    private ReservationMeal reservationMeal;
    private FoodDetailsResponse foodDetailsResponse;

    @BeforeEach
    void setUp() {
        order = Reservation.builder()
                .id(ORDER_ID)
                .clientId(CLIENT_ID)
                .price(100.0)
                .build();

        meal = Meal.builder()
                .id(DISH_ID)
                .name("Test Meal")
                .price(25.0)
                .quantity(1)
                .imageUrl(List.of("image1.jpg"))
                .build();

        reservationMeal = ReservationMeal.builder()
                .id(RESERVATION_MEAL_ID)
                .reservationId(ORDER_ID)
                .dishId(DISH_ID)
                .quantity(1)
                .build();

        FoodDetailsDto foodDetailsDto = FoodDetailsDto.builder()
                .id(DISH_ID)
                .name("Test Meal")
                .price(25.0)
                .photos(List.of("image1.jpg"))
                .build();

        foodDetailsResponse = FoodDetailsResponse.builder()
                .foodDetails(foodDetailsDto)
                .couldRate(false)
                .hasRate(false)
                .userRating(0)
                .build();

        // По умолчанию все фичи выключены
        when(featureToggles.isBugNotIncreaseDishAmountAfterAdd()).thenReturn(false);
        when(featureToggles.isBugCantGetDishInformation()).thenReturn(false);
        when(featureToggles.isBugInvalidPriceCountAfterDishAddInOrder()).thenReturn(false);
        when(featureToggles.isBugInvalidPriceCountAfterDishDeleteFromOrder()).thenReturn(false);
        when(featureToggles.isBugMathMistakesInOrderPriceCountingChangeDishAmountMethod()).thenReturn(false);
    }

    // ===============================================
    // АНАЛИЗ ГРАНИЧНЫХ УСЛОВИЙ
    // ===============================================

    @Nested
    @DisplayName("Граничные условия для addDish")
    class AddDishBoundaryTests {

        @Test
        @DisplayName("Добавление блюда в пустой заказ")
        void addDish_toEmptyOrder_shouldCreateNewReservationMeal() throws UnavailableException {
            // Arrange
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(reservationMealRepository.findAllByReservationId(ORDER_ID)).thenReturn(List.of());
            when(dishClient.getFoodDetails(DISH_ID)).thenReturn(ResponseEntity.ok(foodDetailsResponse));
            when(mealRepository.save(any(Meal.class))).thenReturn(meal);
            when(reservationMealRepository.save(any(ReservationMeal.class))).thenReturn(reservationMeal);

            // Act
            editOrderService.addDish(DISH_ID, ORDER_ID);

            // Assert
            verify(reservationMealRepository).save(argThat(rm ->
                    rm.getReservationId().equals(ORDER_ID) &&
                            rm.getDishId().equals(DISH_ID) &&
                            rm.getQuantity() == 1
            ));
            verify(orderRepository).save(argThat(o ->
                    o.getPrice() == 125.0 // 100 + 25
            ));
        }

        @Test
        @DisplayName("Добавление того же блюда во второй раз")
        void addDish_existingDish_shouldIncreaseQuantity() throws UnavailableException {
            // Arrange
            List<ReservationMeal> reservationMeals = List.of(reservationMeal);
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(reservationMealRepository.findAllByReservationId(ORDER_ID)).thenReturn(reservationMeals);
            when(dishClient.getFoodDetails(DISH_ID)).thenReturn(ResponseEntity.ok(foodDetailsResponse));
            when(mealRepository.getReferenceById(DISH_ID)).thenReturn(meal);
            when(mealRepository.save(any(Meal.class))).thenReturn(meal);

            // Act
            editOrderService.addDish(DISH_ID, ORDER_ID);

            // Assert
            verify(reservationMealRepository).save(argThat(rm ->
                    rm.getQuantity() == 2 // увеличилось с 1 до 2
            ));
            verify(orderRepository).save(argThat(o ->
                    o.getPrice() == 125.0
            ));
        }


        @Test
        @DisplayName("Добавление блюда с нулевой ценой")
        void addDish_withZeroPriceMeal() throws UnavailableException {
            // Arrange
            order.setPrice(100.0);
            FoodDetailsDto zeroPriceDto = FoodDetailsDto.builder()
                    .id(DISH_ID)
                    .name("Free Meal")
                    .price(0.0)
                    .photos(List.of())
                    .build();
            FoodDetailsResponse zeroPriceResponse = FoodDetailsResponse.builder()
                    .foodDetails(zeroPriceDto)
                    .build();

            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(reservationMealRepository.findAllByReservationId(ORDER_ID)).thenReturn(List.of());
            when(dishClient.getFoodDetails(DISH_ID)).thenReturn(ResponseEntity.ok(zeroPriceResponse));

            // Act
            editOrderService.addDish(DISH_ID, ORDER_ID);

            // Assert
            verify(orderRepository).save(argThat(o ->
                    o.getPrice() == 100.0 // цена не изменилась
            ));
        }
    }

    // ===============================================
    // КЛАССЫ ЭКВИВАЛЕНТНОСТИ И ПОТОКИ ДАННЫХ
    // ===============================================

    @Nested
    @DisplayName("Классы эквивалентности и потоки данных")
    class EquivalenceClassesAndDataFlowTests {

        // хорошие данных
        static Stream<Arguments> validDishData() {
            return Stream.of(
                    Arguments.of(UUID.randomUUID(), 25.0, 1), // нормальное блюдо
                    Arguments.of(UUID.randomUUID(), 0.01, 1),  // минимальная цена
                    Arguments.of(UUID.randomUUID(), 9999.99, 1), // большая цена
                    Arguments.of(UUID.randomUUID(), 25.0, 100) // большое количество
            );
        }

        // плохие данных
        static Stream<Arguments> invalidDishData() {
            return Stream.of(
                    Arguments.of(null, 25.0, 1), // null dishId
                    Arguments.of(UUID.randomUUID(), -1.0, 1), // отрицательная цена
                    Arguments.of(UUID.randomUUID(), 25.0, 0), // нулевое количество
                    Arguments.of(UUID.randomUUID(), 25.0, -1) // отрицательное количество
            );
        }

        @ParameterizedTest
        @MethodSource("validDishData")
        @DisplayName("Поток данных: успешное добавление блюда (хорошие данные)")
        void addDish_withValidData_shouldSucceed(UUID dishId, Double price, Integer quantity) throws UnavailableException {
            // Arrange
            order.setPrice(100.0);
            FoodDetailsDto dto = FoodDetailsDto.builder()
                    .id(dishId)
                    .name("Meal")
                    .price(price)
                    .build();
            FoodDetailsResponse response = FoodDetailsResponse.builder()
                    .foodDetails(dto)
                    .build();

            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(reservationMealRepository.findAllByReservationId(ORDER_ID)).thenReturn(List.of());
            when(dishClient.getFoodDetails(dishId)).thenReturn(ResponseEntity.ok(response));

            // Act & Assert
            assertDoesNotThrow(() -> editOrderService.addDish(dishId, ORDER_ID));
        }

    }

    // ===============================================
    // СЛОЖНЫЕ ГРАНИЧНЫЕ УСЛОВИЯ
    // ===============================================

    @Nested
    @DisplayName("Сложные граничные условия")
    class ComplexBoundaryTests {

        @Test
        @DisplayName("Добавление 1000 одинаковых блюд последовательно")
        void addSameDishMultipleTimes_stressTest() throws UnavailableException {
            // Arrange
            order.setPrice(0.0);
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

            // Первый вызов - создание нового блюда
            when(reservationMealRepository.findAllByReservationId(ORDER_ID))
                    .thenReturn(List.of()) // первый раз список пустой
                    .thenAnswer(invocation -> {
                        ReservationMeal rm = ReservationMeal.builder()
                                .dishId(DISH_ID)
                                .reservationId(ORDER_ID)
                                .quantity(1) // будет увеличиваться
                                .build();
                        return List.of(rm);
                    });

            when(dishClient.getFoodDetails(DISH_ID)).thenReturn(ResponseEntity.ok(foodDetailsResponse));
            when(mealRepository.getReferenceById(DISH_ID)).thenReturn(meal);

            // Act
            for (int i = 0; i < 1000; i++) {
                editOrderService.addDish(DISH_ID, ORDER_ID);
            }

            // Assert - проверяем последнее сохранение
            verify(reservationMealRepository, atLeast(1000)).save(argThat(rm ->
                    rm.getDishId().equals(DISH_ID)
            ));
        }

    }

    // ===============================================
    // УГАДЫВАНИЕ ОШИБОК
    // ===============================================

    @Nested
    @DisplayName("Угадывание ошибок и edge cases")
    class ErrorGuessingTests {

        @Test
        @DisplayName("Добавление блюда в несуществующий заказ")
        void addDish_toNonExistentOrder_shouldThrowException() {
            // Arrange
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UsernameNotFoundException.class,
                    () -> editOrderService.addDish(DISH_ID, ORDER_ID));
        }


        @Test
        @DisplayName("Сервис блюд недоступен при добавлении")
        void addDish_whenDishServiceUnavailable_shouldThrowUnavailableException() {
            // Arrange
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(reservationMealRepository.findAllByReservationId(ORDER_ID)).thenReturn(List.of());

            // Act & Assert
            assertThrows(UnavailableException.class,
                    () -> editOrderService.addDish(DISH_ID, ORDER_ID));
        }

        @Test
        @DisplayName("Удаление блюда, которого нет в заказе")
        void deleteDish_notInOrder_shouldDoNothing() throws UnavailableException {
            // Arrange
            when(orderRepository.getReferenceById(ORDER_ID)).thenReturn(order);
            when(reservationMealRepository.findAllByReservationIdAndDishId(ORDER_ID, DISH_ID))
                    .thenReturn(null);

            // Act
            editOrderService.deleteDish(DISH_ID, ORDER_ID);

            // Assert - нет исключений, минимальные вызовы
            verify(orderRepository, never()).save(any());
            verify(reservationMealRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Изменение количества блюда на отрицательное значение")
        void changeDishAmount_toNegativeValue_shouldNotBreak() {
            // Arrange
            when(orderRepository.getReferenceById(ORDER_ID)).thenReturn(order);
            when(reservationMealRepository.findAllByReservationIdAndDishId(ORDER_ID, DISH_ID))
                    .thenReturn(reservationMeal);
            when(mealRepository.getReferenceById(DISH_ID)).thenReturn(meal);

            // Act
            editOrderService.changeDishAmount(DISH_ID, ORDER_ID, -5);

            // Assert - система не падает, но логически это ошибка
            // В реальности нужно добавить валидацию
            verify(reservationMealRepository).save(argThat(rm ->
                    rm.getQuantity() == -5
            ));
        }
    }

    // ===============================================
    // ТЕСТИРОВАНИЕ FEATURE TOGGLES
    // ===============================================

    @Nested
    @DisplayName("Тестирование фич-тогглов")
    class FeatureToggleTests {

        @Test
        @DisplayName("Баг: не увеличивать количество блюда при добавлении")
        void addDish_withBugNotIncreaseDishAmountFeature() throws UnavailableException {
            // Arrange
            when(featureToggles.isBugNotIncreaseDishAmountAfterAdd()).thenReturn(true);
            List<ReservationMeal> reservationMeals = List.of(reservationMeal);

            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(reservationMealRepository.findAllByReservationId(ORDER_ID)).thenReturn(reservationMeals);
            when(dishClient.getFoodDetails(DISH_ID)).thenReturn(ResponseEntity.ok(foodDetailsResponse));
            when(mealRepository.getReferenceById(DISH_ID)).thenReturn(meal);

            // Act
            editOrderService.addDish(DISH_ID, ORDER_ID);

            // Assert - количество не должно измениться
            verify(reservationMealRepository).save(argThat(rm ->
                    rm.getQuantity() == 1
            ));
        }

        @Test
        @DisplayName("Баг: неправильный подсчет цены при изменении количества")
        void changeDishAmount_withMathBugFeature() {
            // Arrange
            when(featureToggles.isBugMathMistakesInOrderPriceCountingChangeDishAmountMethod()).thenReturn(true);
            when(orderRepository.getReferenceById(ORDER_ID)).thenReturn(order);
            when(reservationMealRepository.findAllByReservationIdAndDishId(ORDER_ID, DISH_ID))
                    .thenReturn(reservationMeal);
            when(mealRepository.getReferenceById(DISH_ID)).thenReturn(meal);

            // Act
            editOrderService.changeDishAmount(DISH_ID, ORDER_ID, 3);

            verify(orderRepository).save(argThat(o -> {
                return o.getPrice() == 103.0;
            }));
        }

        @Test
        @DisplayName("Баг: неправильный dishId при получении информации о блюде")
        void addDish_withBugCantGetDishInformation() throws UnavailableException {
            // Arrange
            when(featureToggles.isBugCantGetDishInformation()).thenReturn(true);
            List<ReservationMeal> reservationMeals = List.of(reservationMeal);

            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(reservationMealRepository.findAllByReservationId(ORDER_ID)).thenReturn(reservationMeals);
            // Здесь баг: передается orderId вместо dishId
            when(dishClient.getFoodDetails(ORDER_ID)).thenReturn(ResponseEntity.ok(foodDetailsResponse));
            when(mealRepository.getReferenceById(DISH_ID)).thenReturn(meal);

            // Act
            editOrderService.addDish(DISH_ID, ORDER_ID);

            // Assert - проверяем, что вызывается с неправильным ID
            verify(dishClient).getFoodDetails(ORDER_ID); // баг!
        }

        @Test
        @DisplayName("Комбинация нескольких багов")
        void addDish_withMultipleBugs() throws UnavailableException {
            // Arrange
            when(featureToggles.isBugNotIncreaseDishAmountAfterAdd()).thenReturn(true);
            when(featureToggles.isBugInvalidPriceCountAfterDishAddInOrder()).thenReturn(true);

            List<ReservationMeal> reservationMeals = List.of(reservationMeal);

            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(reservationMealRepository.findAllByReservationId(ORDER_ID)).thenReturn(reservationMeals);
            when(dishClient.getFoodDetails(DISH_ID)).thenReturn(ResponseEntity.ok(foodDetailsResponse));
            when(mealRepository.getReferenceById(DISH_ID)).thenReturn(meal);

            // Act
            editOrderService.addDish(DISH_ID, ORDER_ID);

            // Assert - цена не должна измениться из-за бага
            verify(orderRepository).save(argThat(o ->
                    o.getPrice() == 100.0 // не изменилась
            ));
        }
    }

    // ===============================================
    // СТРУКТУРИРОВАННОЕ БАЗИСНОЕ ТЕСТИРОВАНИЕ
    // ===============================================

    @Nested
    @DisplayName("Структурированное базисное тестирование")
    class StructuralBasisTesting {

        @Test
        @DisplayName("Все ветви метода addDish")
        void allBranchesOfAddDish() throws UnavailableException {
            // Ветвь 1: Заказ не найден
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());
            assertThrows(UsernameNotFoundException.class,
                    () -> editOrderService.addDish(DISH_ID, ORDER_ID));

            // Ветвь 2: Блюдо уже есть в заказе (increase = true)
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            when(reservationMealRepository.findAllByReservationId(ORDER_ID))
                    .thenReturn(List.of(reservationMeal));
            when(dishClient.getFoodDetails(DISH_ID)).thenReturn(ResponseEntity.ok(foodDetailsResponse));
            when(mealRepository.getReferenceById(DISH_ID)).thenReturn(meal);

            editOrderService.addDish(DISH_ID, ORDER_ID);
            verify(reservationMealRepository).save(any());

            // Ветвь 3: Блюда нет в заказе (increase = false)
            UUID newDishId = UUID.randomUUID();
            when(reservationMealRepository.findAllByReservationId(ORDER_ID))
                    .thenReturn(List.of());
            when(dishClient.getFoodDetails(newDishId)).thenReturn(ResponseEntity.ok(foodDetailsResponse));

            editOrderService.addDish(newDishId, ORDER_ID);
            verify(reservationMealRepository, times(2)).save(any());

        }
    }

    // ===============================================
    // ТЕСТЫ ДЛЯ ДЕЙСТВИТЕЛЬНО СЛОЖНЫХ СЦЕНАРИЕВ
    // ===============================================


    @Test
    @DisplayName("Циклическая зависимость: Meal ссылается на себя через getReferenceById")
    void circularDependencyInMealReference() {
        // Arrange
        when(orderRepository.getReferenceById(ORDER_ID)).thenReturn(order);
        when(reservationMealRepository.findAllByReservationIdAndDishId(ORDER_ID, DISH_ID))
                .thenReturn(reservationMeal);

        when(mealRepository.getReferenceById(DISH_ID)).thenAnswer(invocation -> {
            Meal m = new Meal();
            m.setId(DISH_ID);
            m.setPrice(25.0);
            m.setQuantity(1);
            return m;
        });

        // Act & Assert - не должно быть StackOverflowError
        assertDoesNotThrow(() ->
                editOrderService.changeDishAmount(DISH_ID, ORDER_ID, 5)
        );
    }
}