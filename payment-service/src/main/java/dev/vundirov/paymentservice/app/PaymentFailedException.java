package dev.vundirov.paymentservice.app;

public class PaymentFailedException extends RuntimeException {
  public PaymentFailedException(String message) {
    super(message);
  }
}
