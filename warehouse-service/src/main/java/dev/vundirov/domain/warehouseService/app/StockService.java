package dev.vundirov.domain.warehouseService.app;

import dev.vundirov.common.dto.OrderItemDto;
import dev.vundirov.common.dto.kafka.OrderCreatedEvent;
import dev.vundirov.domain.warehouseService.entities.IdempotencyKey;
import dev.vundirov.domain.warehouseService.repositories.IdempotencyKeyRepository;
import dev.vundirov.domain.warehouseService.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class StockService {
  private static final Logger logger =
          LoggerFactory.getLogger(StockService.class);


  private final IdempotencyKeyRepository idempotencyKeyRepository;
  private final ProductRepository productRepository;

  @Transactional
  public boolean processStockReservation(OrderCreatedEvent event) {
    if (idempotencyKeyRepository.existsById(event.messageId())) {
      logger.info("Event {} already processed. Skipping.", event.messageId());
      return false;
    }
    idempotencyKeyRepository.save(new IdempotencyKey(event.messageId()));
    reserveItems(event);
    return true;
  }

  private void reserveItems(OrderCreatedEvent event) {
    for (var item : event.orderItems()) {

      int rowsUpdated =
              productRepository.reserveStock(item.id(), item.quantity());

      if (rowsUpdated == 0) {
        logger.error(
                "Stock mismatch! Product ID: {} is out of stock.", item.id());
        throw new OutOfStockException(
                "Not enough stock for product ID: " + item.id());
      }
    }
  }

  @Transactional
  public void reverseReservation(
          List<OrderItemDto> items) {
    for (var item : items) {
      productRepository.reverseStockReservation(item.id(), item.quantity());
    }
  }
}