package dev.vundirov.common.dto;


import java.math.BigDecimal;
import java.util.Set;


public record OrderDto(Long id, PaymentStatus paymentStatus,
                       BigDecimal total_cost, Set<ItemDto> items) {
}