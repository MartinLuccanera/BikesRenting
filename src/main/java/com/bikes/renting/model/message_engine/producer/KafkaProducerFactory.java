package com.bikes.renting.model.message_engine.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * <p>Kafka producer instantiation as a Singleton.
 * Kafka producers subscribe messages to topics to be read by consumers {@link org.apache.kafka.clients.consumer.KafkaConsumer}.</p>
 */
@Component
public class KafkaProducerFactory {
    private static final Logger logger = Logger.getLogger(KafkaProducerFactory.class);
    private static KafkaProducer<String, String> producer;
    private static String kafkaBootstrapServers;

    /**
     * <p>Instantiates a kafkaProducer if none exists.</p>
     *
     * @return A fully fledged kafkaProducer.
     */
    public static KafkaProducer<String, String> createKafKafkaProducer() {
        if (producer == null) {
            /*
             * Defining producer properties.
             */
            Properties producerProperties = new Properties();
            producerProperties.put("bootstrap.servers", kafkaBootstrapServers);
            producerProperties.put("acks", "all");
            producerProperties.put("retries", 0);
            producerProperties.put("batch.size", 16384);
            producerProperties.put("linger.ms", 1);
            producerProperties.put("reconnect.backoff.ms", "5000");
            producerProperties.put("buffer.memory", 33554432);
            producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            /*
             * Creating a Kafka Producer object with above configuration.
             */
            producer = new KafkaProducer<>(producerProperties);
            logger.debug("Created kafkaProducer with properties:\n" + producerProperties.toString());
        }
        return producer;
    }

    /**
     * Setter to avoid issues while injecting spring @value into static context.
     *
     * @param server Bootstrap server address as is on application.properties.
     */
    @Value("${kafka.bootstrap.servers}")
    public void setBootstrapServer(String server) {
        kafkaBootstrapServers = server;
    }
}