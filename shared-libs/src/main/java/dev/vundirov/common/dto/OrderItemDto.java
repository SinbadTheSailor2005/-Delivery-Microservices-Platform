package dev.vundirov.common.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;


public record OrderItemDto(Integer id, @NotNull BigDecimal priceAtPurchase,
                           @NotNull Integer quantity) {
}