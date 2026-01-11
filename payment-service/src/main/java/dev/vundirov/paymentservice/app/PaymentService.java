package dev.vundirov.paymentservice.app;


import dev.vundirov.app.KafkaConfiguration;
import dev.vundirov.common.dto.kafka.PaymentProcessedEvent;
import dev.vundirov.common.dto.kafka.StockProcessedEvent;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentService {
  private static final Logger logger =
          LoggerFactory.getLogger(PaymentService.class);
  private final PaymentProceeder paymentProceeder;
  private final KafkaTemplate<String, Object> kafkaTemplate;


  @KafkaListener(
          topics = KafkaConfiguration.STOCK_PROCESSED_TOPIC,
          groupId = "payment-service-group",
          containerFactory = "objectListenerFactory"
  )

  public void handleStockProcessedEvent(StockProcessedEvent event) {
    if (!event.stockAvailable()) {
      logger.warn(
              "Payment cannot be processed for order {}: stock not available. Comment: {}",
              event.orderId(), event.comment()
      );
    }
    try {
      boolean isPayedSuccessfully = paymentProceeder.proceedPayment(event);
      if (isPayedSuccessfully) {

        sendEvent(event, true, "Payment processed successfully");
      }
    } catch (PaymentFailedException e) {
      logger.warn(
              "Payment failed for order {}: {}", event.orderId(),
              e.getMessage()
      );
      sendEvent(
              event, false,
              "Payment failed: " + e.getMessage()
      );
    }


  }

  private void sendEvent(
          StockProcessedEvent event, boolean result,
          String message) {
    PaymentProcessedEvent paymentProcessedEvent = new PaymentProcessedEvent(
            event.orderId(),
            message,
            result,
            message,
            event.items()
    );
    kafkaTemplate.send(
            KafkaConfiguration.PAYMENT_PROCESS_TOPIC,
            paymentProcessedEvent.orderId()
                    .toString(),
            paymentProcessedEvent
    );
    logger.info(
            "Sent PaymentProcessedEvent for order {}: {}",
            event.orderId(), paymentProcessedEvent
    );


  }

}
