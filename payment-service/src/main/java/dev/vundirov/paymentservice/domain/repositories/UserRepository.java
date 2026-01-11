package dev.vundirov.paymentservice.domain.repositories;

import dev.vundirov.paymentservice.domain.enities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface UserRepository extends JpaRepository<User, Integer> {

 @Modifying
 @Query("UPDATE  User u SET u.balance = u.balance - :amount where " +
         "u.id = :userId AND u.balance >= :amount")
  int reduceBalance(Integer userId, BigDecimal amount);
}