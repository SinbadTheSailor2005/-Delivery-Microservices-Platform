package dev.vundirov.common.dto.kafka;

public record PaymentProcessedEvent(
        Integer orderId,
        String messageId,
        boolean paymentSuccessful,
        String comment
) {
}
