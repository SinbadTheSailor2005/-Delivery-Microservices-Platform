package dev.vundirov.common.dto;


import java.math.BigDecimal;

public record ItemDto(Long id, String name,  Long quantity,
                      BigDecimal cost) {
}