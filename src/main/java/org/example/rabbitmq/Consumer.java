package org.example.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.annotation.PostConstruct;
import org.example.rabbitmq.util.RabbitMqUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

@Component
public class Consumer {
    private static final Logger LOG = LoggerFactory.getLogger(Consumer.class);

    @PostConstruct
    private void init() {
        listenForMessages();
    }

    private void listenForMessages() {

        try {
            ConnectionFactory factory = new ConnectionFactory();
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(RabbitMqUtil.getQueue(), false, false, false, null);
            channel.queueBind(RabbitMqUtil.getQueue(), RabbitMqUtil.getExchange(), RabbitMqUtil.getRoutingKey());

            channel.basicConsume(
                    RabbitMqUtil.getQueue(),
                    true,
                    (consumerTag, message) -> LOG.info("Got message '{}'", new String(message.getBody(), StandardCharsets.UTF_8)),
                    (consumerTag, sig) -> LOG.error(sig.getMessage()));


        }  catch (TimeoutException | IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
