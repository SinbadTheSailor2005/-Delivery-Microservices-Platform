package dev.vundirov.common.dto.kafka;

import java.math.BigDecimal;

public record StockProcessedEvent(
        Integer orderId,
        String messageId,
        boolean stockAvailable,
        BigDecimal amountToPay,
        String comment

) {
}
