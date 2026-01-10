package dev.vundirov.common.dto.kafka;

import dev.vundirov.common.dto.OrderItemDto;

import java.math.BigDecimal;
import java.util.List;

public record OrderCreatedEvent(
        Integer orderId,
        String messageId,
        Integer userId,
        BigDecimal totalCost,
        List<OrderItemDto> orderItems
) {
}
