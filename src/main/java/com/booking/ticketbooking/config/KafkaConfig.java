package com.booking.ticketbooking.config;

import com.booking.ticketbooking.event.BookingConfirmedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public NewTopic bookingEventsTopic() {
        return new NewTopic(
                "booking-events",
                3,
                (short) 1
        );
    }

    @Bean
    public DefaultErrorHandler errorHandler() {

        FixedBackOff backOff =
                new FixedBackOff(2000L, 2L);

        return new DefaultErrorHandler(backOff);
    }

    @Bean
    public ProducerFactory<String, BookingConfirmedEvent>
    producerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        config.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );

        config.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JacksonJsonSerializer.class
        );

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, BookingConfirmedEvent>
    kafkaTemplate(
            ProducerFactory<String, BookingConfirmedEvent> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ConsumerFactory<String, BookingConfirmedEvent>
    consumerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        config.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "notification-service"
        );

        config.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        config.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JacksonJsonDeserializer.class
        );

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                new JacksonJsonDeserializer<>(
                        BookingConfirmedEvent.class
                )
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BookingConfirmedEvent>
    kafkaListenerContainerFactory(
            ConsumerFactory<String, BookingConfirmedEvent> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, BookingConfirmedEvent>
                factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        return factory;
    }
}