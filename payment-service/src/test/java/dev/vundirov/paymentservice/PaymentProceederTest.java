package dev.vundirov.paymentservice;


import static org.mockito.Mockito.*;

import dev.vundirov.common.dto.kafka.StockProcessedEvent;
import dev.vundirov.paymentservice.app.PaymentProceeder;
import dev.vundirov.paymentservice.domain.enities.IdempotencyKey;
import dev.vundirov.paymentservice.domain.repositories.IdempotencyKeyRepository;
import dev.vundirov.paymentservice.domain.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PaymentProceederTest {
  @Mock
  IdempotencyKeyRepository idempotencyKeyRepository;

  @Mock
  UserRepository userRepository;

  @InjectMocks
  PaymentProceeder paymentProceeder;

  @Test
  void givenDublicateEvent_whenProceedPayment_thenShouldReturnFalse() {
    StockProcessedEvent stockProcessedEvent = new StockProcessedEvent(
            1,
            1, "msg-001", true, null, "", null
    ) ;
  when(idempotencyKeyRepository.existsById(stockProcessedEvent.messageId())).thenReturn(true);

    boolean result = paymentProceeder.proceedPayment(stockProcessedEvent);
    Assertions.assertFalse(result);
    verify(idempotencyKeyRepository,times(0)).save(any(IdempotencyKey.class));
    verify(userRepository,times(0)).reduceBalance(anyInt(),any());
  }


  @Test
  void givenNewStockEvent_whenProceedPayment_thenShouldReduceBalanceAndReturnTrue() {
    StockProcessedEvent stockProcessedEvent = new StockProcessedEvent(
            1,
            1, "msg-001", true, null, "", null
    ) ;
    when(idempotencyKeyRepository.existsById(stockProcessedEvent.messageId())).thenReturn(false);
    when(userRepository.reduceBalance(stockProcessedEvent.userId(),
            stockProcessedEvent.amountToPay())).thenReturn(1);
    boolean result = paymentProceeder.proceedPayment(stockProcessedEvent);

    Assertions.assertTrue(result);
    verify(idempotencyKeyRepository,times(1)).save(any(IdempotencyKey.class));
    verify(userRepository,times(1)).reduceBalance(anyInt(),any());
  }
}
