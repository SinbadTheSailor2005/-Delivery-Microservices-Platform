package dev.vundirov.orderservice.domain.dto;

import dev.vundirov.common.dto.OrderDto;
import dev.vundirov.orderservice.domain.api.dto.PostOrderDto;
import dev.vundirov.orderservice.domain.entities.Order;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
  Order toEntity(OrderDto orderDto);

  @AfterMapping
  default void linkOrderItems(@MappingTarget Order order) {
    order.getOrderItems()
            .forEach(orderItem -> orderItem.setOrder(order));
  }

  OrderDto toOrderDto(Order order);

  Order toEntity(PostOrderDto postOrderDto);

  PostOrderDto toPostOrderDto(Order order);
}