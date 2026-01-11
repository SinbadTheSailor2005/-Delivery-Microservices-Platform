package dev.vundirov.paymentservice.app;

import dev.vundirov.common.dto.kafka.StockProcessedEvent;
import dev.vundirov.paymentservice.domain.enities.IdempotencyKey;
import dev.vundirov.paymentservice.domain.repositories.IdempotencyKeyRepository;
import dev.vundirov.paymentservice.domain.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class PaymentProceeder {

  private static final Logger logger = LoggerFactory.getLogger(PaymentProceeder.class);

  private final IdempotencyKeyRepository idempotencyKeyRepository;
  private final UserRepository userRepository;

  public PaymentProceeder(IdempotencyKeyRepository idempotencyKeyRepository,
                          UserRepository userRepository) {
    this.idempotencyKeyRepository = idempotencyKeyRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public boolean proceedPayment(StockProcessedEvent event) {
    if (idempotencyKeyRepository.existsById(event.messageId())) {
      logger.info("Event {} already processed. Skipping.", event.messageId());
      return false;
    }
    idempotencyKeyRepository.save(new IdempotencyKey(event.messageId()));
    int rowsUpdated = userRepository.reduceBalance(event.userId(),
            event.amountToPay());
    if (rowsUpdated == 0) {
      logger.error("Payment failed! User ID: {} has insufficient balance. " +
              "or does not exists", event.userId());
      throw new PaymentFailedException("Insufficient balance for user ID: " + event.userId());
    }
    logger.info("Payment of amount {} successfully processed for user ID: {}",
            event.amountToPay(), event.userId());
    return true;
  }
}
