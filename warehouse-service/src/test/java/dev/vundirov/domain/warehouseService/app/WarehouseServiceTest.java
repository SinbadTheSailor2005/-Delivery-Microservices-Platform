package dev.vundirov.domain.warehouseService.app;

import dev.vundirov.app.KafkaConfiguration;
import dev.vundirov.common.dto.kafka.OrderCreatedEvent;
import dev.vundirov.common.dto.kafka.StockProcessedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WarehouseServiceTest {
  @Mock
  KafkaTemplate<String, Object> kafkaTemplate;

  @Mock
  ReserveItemsService reserveItemsService;

  @InjectMocks
  WarehouseService warehouseService;


  @Test
  void givenOrderCreatedEvent_whenSuccessfullyReserveProducts_thenSendSuccessToKafka() {

    OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
            1,
            "1",
            1,
            null,
            null
    );
    when(reserveItemsService.processStockReservation(orderCreatedEvent)).thenReturn(true);
    warehouseService.reserveProducts(orderCreatedEvent);

    StockProcessedEvent stockProcessedEvent = new StockProcessedEvent(
            orderCreatedEvent.orderId(),
            orderCreatedEvent.userId(),
            orderCreatedEvent.messageId(),
            true,
            null,
            "Stock reserved successfully",
            null
    );
    verify(kafkaTemplate, times(1)).send(
            eq(KafkaConfiguration.STOCK_PROCESSED_TOPIC),
            eq (stockProcessedEvent.orderId().toString()),
            eq(stockProcessedEvent)

    );
  }


  @Test
  void givenOrderCreatedEvent_whenFailedReserveProducts_thenSendFailToKafka() {

    OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
            1,
            "1",
            1,
            null,
            null
    );
    when(reserveItemsService.processStockReservation(orderCreatedEvent)).thenThrow(
            OutOfStockException.class);
    warehouseService.reserveProducts(orderCreatedEvent);

    StockProcessedEvent stockProcessedEvent = new StockProcessedEvent(
            orderCreatedEvent.orderId(),
            orderCreatedEvent.userId(),
            orderCreatedEvent.messageId(),
            false,
            null,
            "Failed to reserve stock: null",
            null
    );
    verify(kafkaTemplate, times(1)).send(
            eq(KafkaConfiguration.STOCK_PROCESSED_TOPIC),
            eq (stockProcessedEvent.orderId().toString()),
            eq(stockProcessedEvent)

    );
  }

}
