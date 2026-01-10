package dev.vundirov.domain.warehouseService.app;


import dev.vundirov.app.KafkaConfiguration;
import dev.vundirov.common.dto.kafka.OrderCreatedEvent;
import dev.vundirov.common.dto.kafka.StockProcessedEvent;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WarehouseService {
  private static final Logger logger =
          LoggerFactory.getLogger(WarehouseService.class);

  private StockService stockService;
  private KafkaTemplate<String, Object> kafkaTemplate;


  @KafkaListener(
          topics = KafkaConfiguration.ORDER_CREATED_TOPIC,
          groupId = "warehouse-service-group",
          containerFactory = "objectListenerFactory"


  )
  public void reserveProducts(OrderCreatedEvent event) {
    logger.info("Received OrderCreatedEvent: {}", event);
    try {
      stockService.processStockReservation(event);
      logger.info("Stock reservation succeed for event: {}", event.messageId());
              sendEvent(event, true, "Stock reserved successfully");

    } catch (Exception e) {
      logger.warn(
              "Reservation failed for order {}: {}", event.orderId(),
              e.getMessage()
      );
      sendEvent(event, false, "Failed to reserve stock: " + e.getMessage());
    }
  }

  private void sendEvent(
          OrderCreatedEvent event, boolean stockAvailable, String message) {
    StockProcessedEvent stockProcessedEvent = new StockProcessedEvent(
            event.orderId(),
            event.messageId(),
            stockAvailable,
            event.totalCost()
            ,
            message
    );
    kafkaTemplate.send(
            KafkaConfiguration.STOCK_PROCESSED_TOPIC,
            stockProcessedEvent.orderId()
                    .toString(), stockProcessedEvent
    );
    logger.info("Sent PaymentProcessedEvent: {}", stockProcessedEvent);
  }


}
