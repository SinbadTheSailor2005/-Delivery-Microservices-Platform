package dev.vundirov.paymentservice;

import dev.vundirov.app.KafkaConfiguration;
import dev.vundirov.common.dto.kafka.PaymentProcessedEvent;
import dev.vundirov.common.dto.kafka.StockProcessedEvent;
import dev.vundirov.paymentservice.app.PaymentFailedException;
import dev.vundirov.paymentservice.app.PaymentProceeder;
import dev.vundirov.paymentservice.app.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceApplicationTest {

  @Mock
  PaymentProceeder paymentProceeder;

  @Mock
  private KafkaTemplate<String, Object> kafkaTemplate;
  @InjectMocks
  PaymentService paymentService;


  @Test
  void givenFailedStockProcessedEvent_whenCallHandler_shouldDoNothing() {

    StockProcessedEvent stockProcessedEvent = new StockProcessedEvent(
            null,
            null, null, false, null, null, null
    );

    paymentService.handleStockProcessedEvent(stockProcessedEvent);

    verify(paymentProceeder, times(0)).proceedPayment(
            any(StockProcessedEvent.class));
  }

  @Test
  void givenSuccessfulStockProcessedEvent_whenCallHandlerSuccess_should_sendPosEventToKafka() {
    StockProcessedEvent stockProcessedEvent = new StockProcessedEvent(
            1,
            1, "", true, BigDecimal.valueOf(1), "", List.of()
    );
    PaymentProcessedEvent paymentProcessedEvent = new PaymentProcessedEvent(
            1,
            "1",
            true,
            "Payment processed successfully",
            List.of()
    );
    when(paymentProceeder.proceedPayment(stockProcessedEvent)).thenReturn(true);

    paymentService.handleStockProcessedEvent(stockProcessedEvent);

    verify(kafkaTemplate, times(1)).send(
            eq(KafkaConfiguration.PAYMENT_PROCESS_TOPIC),
            eq("1"),
            eq(paymentProcessedEvent)


    );

  }

  @Test
  void givenSuccessfulStockProcessedEvent_whenCallHandlerFailed_should_sendNegEventToKafka() {
    StockProcessedEvent stockProcessedEvent = new StockProcessedEvent(
            1,
            1, "", true, BigDecimal.valueOf(1), "", List.of()
    );
    PaymentProcessedEvent paymentProcessedEvent = new PaymentProcessedEvent(
            1,
            "1",
            false,
            "Payment failed: null",
            List.of()
    );
    when(paymentProceeder.proceedPayment(stockProcessedEvent)).thenThrow(
            PaymentFailedException.class);

    paymentService.handleStockProcessedEvent(stockProcessedEvent);

    verify(kafkaTemplate, times(1)).send(
            eq(KafkaConfiguration.PAYMENT_PROCESS_TOPIC),
            eq("1"),
            eq(paymentProcessedEvent)


    );

  }
}
