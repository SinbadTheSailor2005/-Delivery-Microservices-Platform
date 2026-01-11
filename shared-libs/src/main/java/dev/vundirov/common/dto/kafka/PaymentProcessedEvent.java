package dev.vundirov.common.dto.kafka;

import dev.vundirov.common.dto.OrderItemDto;

import java.util.List;

public record PaymentProcessedEvent(
        Integer orderId,
        String messageId,
        boolean paymentSuccessful,
        String comment,
        List<OrderItemDto> items
) {
}
