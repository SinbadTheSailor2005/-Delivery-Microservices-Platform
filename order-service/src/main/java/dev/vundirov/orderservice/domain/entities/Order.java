package dev.vundirov.orderservice.domain.entities;

import dev.vundirov.common.dto.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "payment_status", nullable = false, length = Integer.MAX_VALUE)
  private PaymentStatus paymentStatus = PaymentStatus.PENDING;

  @NotNull
  @Column(name = "user_id", nullable = false)
  private Integer userId;

  @NotNull
  @Column(name = "total_cost", nullable = false, precision = 10, scale = 2)
  private BigDecimal totalCost;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<OrderItem> orderItems = new LinkedHashSet<>();

}