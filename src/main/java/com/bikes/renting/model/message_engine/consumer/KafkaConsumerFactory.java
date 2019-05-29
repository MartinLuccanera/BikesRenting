package com.bikes.renting.model.message_engine.consumer;

import com.google.common.collect.ImmutableList;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * <p>Kafka consumer which will read the pool of messages based on a topic (rental_type).</p>
 */
@Component
public class KafkaConsumerFactory {
    private static final Logger logger = Logger.getLogger(KafkaConsumerFactory.class);
    private static String kafkaBootstrapServers;
    private static String zookeeperGroupId;

    /**
     * <p>Creating a kafka consumer based on a list of topics {@param rentalTopicName} to which consumer will subscribe.</p>
     *
     * @param rentalTopicName List of topics to subscribe the consumer.
     */
    public static KafkaConsumer<String, String> createKafKafkaConsumer(ImmutableList<String> rentalTopicName) {
        /*
         * Defining Kafka consumer properties.
         */
        Properties consumerProperties = new Properties();
        consumerProperties.put("bootstrap.servers", kafkaBootstrapServers);
        consumerProperties.put("group.id", zookeeperGroupId);
        consumerProperties.put("auto.commit.interval.ms", "1000");
        consumerProperties.put("reconnect.backoff.ms", "5000");
        consumerProperties.put("auto.offset.reset", "earliest");
        consumerProperties.put("max.poll.records", "1");
        consumerProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProperties);
        logger.debug("Created kafkaConsumer with properties:\n" + consumerProperties.toString());
        consumer.subscribe(rentalTopicName);
        logger.debug("Subscribed kafkaConsumer to topic:\n" + rentalTopicName.toString());
        return consumer;
    }

    /**
     * <p>Setter to avoid issues while injecting spring @value into static context.</p>
     *
     * @param server Bootstrap server address as is on application.properties.
     */
    @Value("${kafka.bootstrap.servers}")
    public void setBootstrapServer(String server) {
        kafkaBootstrapServers = server;
    }

    /**
     * <p>Setter to avoid issues while injecting spring @value into static context.</p>
     *
     * @param groupId groupID to which consumer is gonna register to.
     */
    @Value("${zookeeper.groupId}")
    public void setGroupId(String groupId) {
        zookeeperGroupId = groupId;
    }
}