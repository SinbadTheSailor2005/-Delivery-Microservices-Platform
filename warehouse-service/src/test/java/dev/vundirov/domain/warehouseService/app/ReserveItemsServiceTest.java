package dev.vundirov.domain.warehouseService.app;

import dev.vundirov.common.dto.OrderItemDto;
import dev.vundirov.common.dto.kafka.OrderCreatedEvent;
import dev.vundirov.domain.warehouseService.repositories.IdempotencyKeyRepository;
import dev.vundirov.domain.warehouseService.repositories.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReserveItemsServiceTest {


  @Mock
  IdempotencyKeyRepository idempotencyKeyRepository;
  @Mock
  ProductRepository productRepository;

  @InjectMocks
  ReserveItemsService reserveItemsService;

  @Test
  void givenDublicateEvent_whenProcess_shouldReturnFalse () {
    OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
            1,
            "1",
            1,
            null,
            List.of()
    );
  when(idempotencyKeyRepository.existsById(orderCreatedEvent.messageId())).thenReturn(true);

    boolean result = reserveItemsService.processStockReservation(orderCreatedEvent);
    verify(idempotencyKeyRepository,times(0)).save(any());
    verify(productRepository,times(0)).reserveStock(anyInt(),anyInt());
    Assertions.assertFalse(result);
  }

  @Test
  void givenNewOrder_whenProcess_shouldReserveStockAndReturnTrue () {
    OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
            1,
            "1",
            1,
            null,
            List.of(new OrderItemDto(1, BigDecimal.valueOf(1), 1))
    );
    when(idempotencyKeyRepository.existsById(orderCreatedEvent.messageId())).thenReturn(false);
    when(productRepository.reserveStock(anyInt(), anyInt())).thenReturn(1);
    boolean result = reserveItemsService.processStockReservation(orderCreatedEvent);
    verify(idempotencyKeyRepository,times(1)).save(any());
    verify(productRepository,times(1)).reserveStock(anyInt(),anyInt());
    Assertions.assertTrue(result);
  }

}
