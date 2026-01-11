package dev.vundirov.common.dto.kafka;

import dev.vundirov.common.dto.OrderItemDto;

import java.math.BigDecimal;
import java.util.List;

public record StockProcessedEvent(
        Integer orderId,
        Integer userId,
        String messageId,
        boolean stockAvailable,
        BigDecimal amountToPay,
        String comment,
        List<OrderItemDto>items

) {
}
