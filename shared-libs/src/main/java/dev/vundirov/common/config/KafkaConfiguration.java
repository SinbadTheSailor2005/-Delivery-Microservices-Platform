package dev.vundirov.common.config;

import dev.vundirov.common.dto.OrderDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
public class KafkaConfiguration {
  public static final String PAYMENT_TOPIC = "payment-requests";
  public static final String PAYMENT_RESULT_TOPIC = "payment-results";
  @Bean
  DefaultKafkaProducerFactory<String, OrderDto> orderDtoProducerFactory(
          KafkaProperties properties) {
    Map<String, Object> producerProperties =
            properties.buildProducerProperties();
    producerProperties.put(
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    producerProperties.put(
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(producerProperties);
  }

  @Bean
  KafkaTemplate<String, OrderDto> orderDtoKafkaTemplate(
          DefaultKafkaProducerFactory<String, OrderDto> orderDtoProducerFactory) {
    return new KafkaTemplate<>(orderDtoProducerFactory);
  }

  @Bean
  public ConsumerFactory<String, OrderDto> orderDtoConsumerFactory(
          KafkaProperties kafkaProperties) {
    Map<String, Object> props = kafkaProperties.buildConsumerProperties();
    props.put(
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class
    );
    props.put(
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            JsonDeserializer.class
    );
    props.put(
            JsonDeserializer.TRUSTED_PACKAGES,
            "dev.vundirov.orderservice.domain.dto"
    );
    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  public KafkaListenerContainerFactory<?> orderDtoListenerFactory(
          ConsumerFactory<String, OrderDto> orderDtoConsumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, OrderDto> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(orderDtoConsumerFactory);
    factory.setBatchListener(false);
    return factory;
  }

  @Bean
  public NewTopic paymentTopic() {
    return TopicBuilder.name(PAYMENT_TOPIC).partitions(3).replicas(1).build();
  }

  @Bean
  public NewTopic paymentResultTopic() {
    return TopicBuilder.name(PAYMENT_RESULT_TOPIC).partitions(3).replicas(1).build();
  }
}