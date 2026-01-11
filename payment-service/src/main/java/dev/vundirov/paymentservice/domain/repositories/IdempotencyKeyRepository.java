package dev.vundirov.paymentservice.domain.repositories;

import dev.vundirov.paymentservice.domain.enities.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, String> {
}